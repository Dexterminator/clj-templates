(ns clj-templates.db.db
  (:require [integrant.core :as ig]
            [hugsql.core :as hugsql]
            [clj-templates.db.format]
            [clojure.spec :as s]
            [clj-templates.specs.common :as c]
            [hikari-cp.core :as hikari]))

(def db-fns (hugsql/map-of-db-fns "sql/queries.sql"))

(defn exec
  ([name db]
   ((get-in db-fns [name :fn]) db))
  ([name db m]
   ((get-in db-fns [name :fn]) db m)))

(defn upsert-template [db template]
  (exec :upsert-template db template))

(defn all-templates [db]
  (exec :all-templates db))

(defn delete-all-templates [db]
  (exec :delete-all-templates db))

(defn insert-templates [db templates]
  (count (pmap (fn [template] (upsert-template db template))
               templates)))

(defmethod ig/init-key :db/postgres [_ db-config]
  {:datasource (hikari/make-datasource db-config)})

(defmethod ig/halt-key! :db/postgres [_ {:keys [datasource]}]
  (hikari/close-datasource datasource))

(s/fdef upsert-template
        :args (s/cat :db ::c/db :template ::c/template)
        :ret int?)

(s/fdef all-templates
        :args (s/cat :db ::c/db)
        :ret ::c/templates)

(s/fdef delete-all-templates
        :args (s/cat :db ::c/db)
        :ret int?)

(s/fdef insert-templates
        :args (s/cat :db ::c/db :templates ::c/templates)
        :ret int?)
