(ns clj-templates.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [integrant.core :as ig]
            [environ.core :refer [env]]
            [clj-templates.handler]
            [clj-templates.server]
            [clj-templates.logger]
            [clj-templates.db.db]
            [clj-templates.search]
            [clj-templates.db.migrations :refer [migrate]]
            [clj-templates.config.main-config :refer [main-config]])
  (:gen-class))

(defn -main
  [& args]
  (when (#{"staging" "prod"} (env :deployment-env))
    (migrate))
  (ig/init main-config)
  (println "Initialized config."))
