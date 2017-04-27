(ns clj-templates.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response]]
            [integrant.core :as ig]))

(defn app-routes [options]
  (routes
    (GET "/hello" [] (str "hello " (:name options)))
    (GET "/" [] (resource-response "index.html" {:root "public"}))
    (resources "/")))

(def handler app-routes)

(defmethod ig/init-key :handler/main [_ options]
  (app-routes options))
