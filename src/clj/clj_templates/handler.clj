(ns clj-templates.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [ring.logger.timbre :refer [wrap-with-logger]]
            [integrant.core :as ig]))

(defn app-routes [options]
  (routes
    (GET "/hello" [] (str "hello " (:name options)))
    (GET "/" [] (resource-response "index.html" {:root "public"}))
    (resources "/")))

(defmethod ig/init-key :handler/main [_ options]
  (-> (app-routes options)
      wrap-with-logger))
