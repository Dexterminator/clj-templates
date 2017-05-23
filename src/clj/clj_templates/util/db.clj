(ns clj-templates.util.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql-postgres.format]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [medley.core :refer [map-keys]]))

(defn exec [db stmt]
  (first (jdbc/execute! db (sql/format stmt))))

(defn query [db q]
  (jdbc/query db (sql/format q) {:row-fn #(map-keys ->kebab-case-keyword %)}))
