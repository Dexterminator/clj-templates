(ns clj-templates.db.db
  (:require [integrant.core :as ig]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [hugsql.core :as hugsql]))

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

(defn result-one-snake->kebab
  [this result options]
  (->> (hugsql.adapter/result-one this result options)
       (transform-keys ->kebab-case-keyword)))

(defn result-many-snake->kebab
  [this result options]
  (->> (hugsql.adapter/result-many this result options)
       (map #(transform-keys ->kebab-case-keyword %))))

(defmethod hugsql.core/hugsql-result-fn :1 [sym]
  'clj-templates.db.db/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :one [sym]
  'clj-templates.db.db/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :* [sym]
  'clj-templates.db.db/result-many-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :many [sym]
  'clj-templates.db.db/result-many-snake->kebab)
