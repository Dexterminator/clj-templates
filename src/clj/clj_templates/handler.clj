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
            [clj-templates.search :as search]))

(defn adapt-template-to-api [template]
  (dissoc template :github-url :github-id :github-stars :github-readme))

(defn home-page []
  (-> (resource-response "index.html" {:root "public"})
      (content-type "text/html; charset=utf-8")))

(defn search-templates [es-client query-string]
  (let [templates {:templates (mapv adapt-template-to-api
                                    (if query-string
                                      (search/search-templates es-client query-string)
                                      (search/match-all-templates es-client)))}
        transit-templates (t/transit-json templates)]
    (-> (response transit-templates)
        (content-type "application/transit+json"))))

(defn app-routes [es-client]
  (routes
    (GET "/templates" [q] (search-templates es-client q))
    (GET "/" [] (home-page))
    (resources "/")))

(defmethod ig/init-key :handler/main [_ {:keys [es-client]}]
  (-> (app-routes es-client)
      (wrap-defaults site-defaults)
      wrap-with-logger))

(s/fdef templates
        :args (s/cat :db ::c/es-client)
        :ret integer?)
