(ns clj-templates.server
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler opts]}]
  (run-jetty handler opts))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))
