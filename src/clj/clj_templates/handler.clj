(ns clj-templates.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response response]]
            [ring.logger.timbre :refer [wrap-with-logger]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.util.http-response :refer [content-type ok bad-request!]]
            [ring.middleware.http-response :refer [wrap-http-response]]
            [clj-templates.util.transit :as t]
            [integrant.core :as ig]
            [clojure.spec :as s]
            [clj-templates.specs.common :as c]
            [clj-templates.search :as search]
            [clojure.string :as str]
            [medley.core :refer [map-keys]]
            [clj-templates.specs.api :as api-spec]))

(defn params-problems-string [problems]
  (->> problems
       (map (fn [{:keys [val in pred]}]
              (if (seq in)
                (str "Incorrect value for parameter \"" (name (first in)) "\": " val)
                (str "Missing parameter: " (name (last pred))))))
       (str/join "\n")))

(defn parse-query-params [query-params params-spec]
  (let [query-params (map-keys keyword query-params)
        problems (::s/problems (s/explain-data params-spec query-params))]
    (if problems
      (bad-request! (params-problems-string problems))
      (s/conform params-spec query-params))))

(defn home-page []
  (-> (resource-response "index.html" {:root "public"})
      (content-type "text/html; charset=utf-8")))

(defn search-templates [es-client query-string from size]
  (let [templates (if (str/blank? query-string)
                    (search/match-all-templates es-client from size)
                    (search/search-templates es-client query-string from size))
        res (assoc templates :query-string query-string)]
    (-> (response (t/transit-json res))
        (content-type "application/transit+json"))))

(defn app-routes [es-client]
  (routes
    (GET "/api/templates" {:keys [query-params]}
      (let [{:keys [q from size]} (parse-query-params query-params ::api-spec/templates-params)]
        (search-templates es-client q from size)))
    (GET "/" [] (home-page))
    (resources "/")))

(defmethod ig/init-key :handler/main [_ {:keys [es-client]}]
  (-> (app-routes es-client)
      (wrap-defaults site-defaults)
      (wrap-http-response)
      wrap-with-logger))

(s/fdef search-templates
        :args (s/cat :es-client ::c/es-client
                     :query-string ::api-spec/q
                     :from integer?
                     :size integer?)
        :ret integer?)
