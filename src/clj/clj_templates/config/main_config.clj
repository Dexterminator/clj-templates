(ns clj-templates.config.main-config
  (:require [integrant.core :as ig]))

(def main-config
  {:handler/main  {:name "test"}
   :adapter/jetty {:handler (ig/ref :handler/main)
                   :opts    {:port 3456 :join? false}}})
