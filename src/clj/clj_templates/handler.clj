(ns clj-templates.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response response]]
            [ring.logger.timbre :refer [wrap-with-logger]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.util.http-response :refer [content-type ok]]
            [clj-templates.db.db :as db]
            [clj-templates.util.transit :as t]
            [integrant.core :as ig]))

(defn home-page []
  (-> (resource-response "index.html" {:root "public"})
      (content-type "text/html; charset=utf-8")))

(defn templates [db]
  (let [all-templates {:templates (vec (db/all-templates db))}
        transit-templates (t/transit-json all-templates)]
    (-> (response transit-templates)
        (content-type "application/transit+json"))))

(defn app-routes [db]
  (routes
    (GET "/templates" [] (templates db))
    (GET "/" [] (home-page))
    (resources "/")))

(defmethod ig/init-key :handler/main [_ {:keys [db]}]
  (-> (app-routes db)
      (wrap-defaults site-defaults)
      wrap-with-logger))
