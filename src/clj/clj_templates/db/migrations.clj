(ns clj-templates.db.migrations
  (:require [environ.core :refer [env]]
            [migratus.core :as migratus]))

(def migratus-config
  {:store         :database
   :migration-dir "migrations"
   :db            (env :db-url)})

(defn migrate []
  (println (str "Running migrations... \n" (migratus/pending-list migratus-config)))
  (migratus/migrate migratus-config)
  (println "Migrations done."))
