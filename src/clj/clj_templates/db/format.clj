(ns clj-templates.db.format
  (:require [camel-snake-kebab.extras :refer [transform-keys]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]))

(defn result-one-snake->kebab
  [this result options]
  (->> (hugsql.adapter/result-one this result options)
       (transform-keys ->kebab-case-keyword)))

(defn result-many-snake->kebab
  [this result options]
  (->> (hugsql.adapter/result-many this result options)
       (map #(transform-keys ->kebab-case-keyword %))))

(defmethod hugsql.core/hugsql-result-fn :1 [sym]
  'clj-templates.db.format/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :one [sym]
  'clj-templates.db.format/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :* [sym]
  'clj-templates.db.format/result-many-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :many [sym]
  'clj-templates.db.format/result-many-snake->kebab)
