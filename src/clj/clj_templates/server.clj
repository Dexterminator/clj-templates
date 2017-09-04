(ns clj-templates.server
  (:require [integrant.core :as ig]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defmethod ig/init-key :server/jetty [_ {:keys [handler opts]}]
  (run-jetty handler (assoc opts :port (Integer/parseInt (env :port)))))

(defmethod ig/halt-key! :server/jetty [_ server]
  (.stop server))
