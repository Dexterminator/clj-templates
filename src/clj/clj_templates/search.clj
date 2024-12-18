(ns clj-templates.search
  (:require [integrant.core :as ig]
            [qbits.spandex :as es]
            [clojure.spec.alpha :as s]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [clj-templates.specs.common :as c]))

(def base-url [:clj_templates])
(def index-url (conj base-url :_doc))
(def search-url (conj index-url :_search))

(defn adapt-template-to-api [template]
  (dissoc template :github-url :github-id :github-stars :github-readme))

(defn adapt-to-api [es-response]
  {:hit-count (get-in es-response [:body :hits :total])
   :templates (mapv (comp adapt-template-to-api :_source) (get-in es-response [:body :hits :hits]))})

(defn search-query [search-string]
  {:function_score
   {:query              {:multi_match {:query    search-string
                                       :type     "cross_fields"
                                       :operator "and"
                                       :fields   ["template-name.raw^3"
                                                  "template-name^3"
                                                  "description^2"
                                                  "github-readme"]}}
    :field_value_factor {:field    "downloads"
                         :modifier "log1p"}}})

(def match-all-query
  {:function_score
   {:query              {:match_all {}}
    :field_value_factor {:field "downloads"}}})

(def ngram-analysis
  {:index {:max_ngram_diff 20}
   :analysis
   {:filter   {:autocomplete_filter
               {:type     "ngram"
                :min_gram 1
                :max_gram 20}}
    :analyzer {:autocomplete
               {:type      "custom"
                :tokenizer "standard"
                :filter    ["lowercase" "autocomplete_filter"]}}}})

(def mappings
   {:properties
    {:template-name {:type            "text"
                     :analyzer        "autocomplete"
                     :search_analyzer "standard"
                     :fields          {:raw {:type "keyword"}}}
     :description   {:type            "text"
                     :analyzer        "autocomplete"
                     :search_analyzer "standard"}
     :build-system  {:type "keyword"}
     :github-url    {:type "keyword"}
     :github-id     {:type "keyword"}
     :github-stars  {:type "integer"}
     :github-readme {:type            "text"
                     :analyzer        "autocomplete"
                     :search_analyzer "standard"}
     :homepage      {:type "keyword"}
     :downloads     {:type "integer"}}})

(defn search-templates [es-client search-string from size]
  (adapt-to-api
    (es/request es-client {:url    search-url
                           :method :get
                           :body   {:query (search-query search-string)
                                    :from  from
                                    :size  size}})))

(defn match-all-templates [es-client from size]
  (adapt-to-api
    (es/request es-client {:url    search-url
                           :method :get
                           :body   {:query match-all-query
                                    :from  from
                                    :size  size}})))

(defn index-template
  ([es-client {:keys [template-name build-system] :as template} {:keys [refresh?]}]
   (es/request es-client {:url          (conj index-url
                                              (str template-name "-" build-system))
                          :query-string (when refresh? {:refresh true})
                          :method       :post
                          :body         template}))
  ([es-client template]
   (index-template es-client template {})))

(defn delete-index [es-client]
  (es/request es-client {:url    base-url
                         :method :delete
                         :body   {}}))

(defn create-index [es-client]
  (es/request es-client {:url    base-url
                         :method :put
                         :body   {:settings ngram-analysis
                                  :mappings mappings}}))

(defmethod ig/init-key :search/elastic [_ {:keys [hosts user password]}]
  (let [es-config (merge {:hosts hosts}
                         (when (= "prod" (env :deployment-env))
                           {:http-client {:basic-auth {:user     user
                                                       :password password}}}))

        es-client (es/client es-config)]
    (try
      (create-index es-client)
      (catch Exception e
        (timbre/warn e)))
    es-client))

(defmethod ig/halt-key! :search/elastic [_ es-client]
  (es/close! es-client))

(s/fdef index-template
        :args (s/cat :es-client ::c/es-client
                     :template ::c/template
                     :opts (s/? map?))
        :ret ::c/spandex-response)

(s/fdef match-all-templates
        :args (s/cat :es-client ::c/es-client
                     :from integer?
                     :size integer?)
        :ret ::c/spandex-response)

(s/fdef search-templates
        :args (s/cat :es-client ::c/es-client
                     :search-string string?
                     :from integer?
                     :size integer?)
        :ret ::c/spandex-response)
