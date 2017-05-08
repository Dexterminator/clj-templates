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

(s/fdef upsert-template
        :args (s/cat :db ::c/db :template ::c/template)
        :ret int?)

(defn all-templates [db]
  (exec :all-templates db))

(s/fdef all-templates
        :args (s/cat :db ::c/db)
        :ret ::c/templates)

(defn templates [db q]
  (exec :templates db q))

(s/fdef templates
        :args (s/cat :db ::c/db :q (s/keys :req-un [::c/build-system]))
        :ret ::c/templates)

(defn delete-all-templates [db]
  (exec :delete-all-templates db))

(s/fdef delete-all-templates
        :args (s/cat :db ::c/db)
        :ret int?)

(defn insert-templates [db templates]
  (count (pmap (fn [template] (upsert-template db template))
               templates)))

(s/fdef insert-templates
        :args (s/cat :db ::c/db :templates ::c/templates)
        :ret int?)

(defmethod ig/init-key :db/postgres [_ db-config]
  {:datasource (hikari/make-datasource db-config)})

(defmethod ig/halt-key! :db/postgres [_ {:keys [datasource]}]
  (hikari/close-datasource datasource))
