(ns clj-templates.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response response]]
            [ring.logger.timbre :refer [wrap-with-logger]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.util.http-response :refer [content-type ok]]
            [clj-templates.db.db :as db]
            [clj-templates.util.transit :as t]
            [integrant.core :as ig]
            [clojure.spec :as s]
            [clj-templates.specs.common :as c]
            [clj-templates.search :as search]
            [clojure.tools.reader.edn :as edn]
            [clojure.string :as str]))

(defn home-page []
  (-> (resource-response "index.html" {:root "public"})
      (content-type "text/html; charset=utf-8")))

(defn search-templates [es-client query-string from size]
  (let [templates (if (str/blank? query-string)
                    (search/match-all-templates es-client from size)
                    (search/search-templates es-client query-string from size))
        transit-templates (t/transit-json templates)]
    (-> (response transit-templates)
        (content-type "application/transit+json"))))

(defn app-routes [es-client]
  (routes
    (GET "/templates" [q from size] (search-templates es-client q (edn/read-string from) (edn/read-string size)))
    (GET "/" [] (home-page))
    (resources "/")))

(defmethod ig/init-key :handler/main [_ {:keys [es-client]}]
  (-> (app-routes es-client)
      (wrap-defaults site-defaults)
      wrap-with-logger))

(s/fdef search-templates
        :args (s/cat :es-client ::c/es-client
                     :query-string (s/nilable string?)
                     :from integer?
                     :size integer?)
        :ret integer?)
