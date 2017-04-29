(ns clj-templates.db.db
  (:require [integrant.core :as ig]
            [hugsql.core :as hugsql]))

(def queries (hugsql/map-of-db-fns "clj_templates/db/queries.sql"))

(defn query
  ([name db]
   ((get-in queries [name :fn]) db))
  ([name db q]
   ((get-in queries [name :fn]) db q)))

(defn insert-user [db q]
  (query :insert-user db q))

(defn all-users [db]
  (query :all-users db))

(defmethod ig/init-key :db/postgres [_ db]
  db)
