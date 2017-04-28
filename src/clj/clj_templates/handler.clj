(ns clj-templates.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [ring.logger.timbre :refer [wrap-with-logger]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [integrant.core :as ig]))

(defn app-routes [options]
  (routes
    (GET "/hello" [] (str "hello " (:name options)))
    (GET "/" [] (resource-response "index.html" {:root "public"}))
    (resources "/")))

(defmethod ig/init-key :handler/main [_ options]
  (-> (app-routes options)
      (wrap-defaults site-defaults)
      wrap-with-logger))
