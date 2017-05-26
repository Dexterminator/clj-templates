(ns clj-templates.search
  (:require [integrant.core :as ig]
            [qbits.spandex :as es]
            [qbits.spandex.utils :as es-utils]
            [clojure.spec :as s]
            [clj-templates.specs.common :as c]))

(def base-url [:clj_templates])
(def index-url (conj base-url :template))
(def search-url (conj base-url :_search))

(defn templates-from-es-response [es-response]
  (map :_source (get-in es-response [:body :hits :hits])))

(defn index-template [es-client {:keys [template-name build-system] :as template}]
  (es/request es-client {:url    (es-utils/url (conj index-url (str template-name "-" build-system)))
                         :method :post
                         :body   template}))

(defn match-all-templates [es-client]
  (templates-from-es-response
    (es/request es-client {:url    (es-utils/url search-url)
                           :method :get
                           :body   {:query {:function_score {:query              {:match_all {}}
                                                             :field_value_factor {:field "downloads"}}}
                                    :from  0
                                    :size  50}})))

(defn search-templates [es-client search-string]
  (templates-from-es-response
    (es/request es-client {:url    (es-utils/url search-url)
                           :method :get
                           :body   {:query {:function_score
                                            {:query              {:multi_match {:query  search-string
                                                                                :type   :best_fields
                                                                                :fields ["template-name^3" "github-readme"]}}
                                             :field_value_factor {:field    "downloads"
                                                                  :modifier "log1p"}}}
                                    :from  0
                                    :size  50}})))

(defn delete-index [es-client]
  (es/request es-client {:url    (es-utils/url base-url)
                         :method :delete
                         :body   {}}))

(defmethod ig/init-key :search/elastic [_ {:keys [hosts default-headers]}]
  (es/client {:default-headers default-headers
              :hosts           hosts}))

(defmethod ig/halt-key! :search/elastic [_ es-client]
  (es/close! es-client))

(s/fdef index-template
        :args (s/cat :es-client ::c/es-client :template ::c/template)
        :ret ::c/spandex-response)

(s/fdef match-all-templates
        :args (s/cat :es-client ::c/es-client)
        :ret ::c/spandex-response)

(s/fdef search-templates
        :args (s/cat :es-client ::c/es-client :search-string string?)
        :ret ::c/spandex-response)
