(ns clj-templates.db.db
  (:require [integrant.core :as ig]
            [hugsql.core :as hugsql]
            [clj-templates.db.format]))

(def db-fns (hugsql/map-of-db-fns "clj_templates/db/queries.sql"))

(defn exec
  ([name db]
   ((get-in db-fns [name :fn]) db))
  ([name db m]
   ((get-in db-fns [name :fn]) db m)))

(defn upsert-template [db m]
  (exec :upsert-template db m))

(defn all-templates [db]
  (exec :all-templates db))

(defn delete-all-templates [db]
  (exec :delete-all-templates db))

(defmethod ig/init-key :db/postgres [_ db]
  db)
