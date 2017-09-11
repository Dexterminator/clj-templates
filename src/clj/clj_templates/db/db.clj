(ns clj-templates.db.db
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [clj-templates.specs.common :as c]
            [clj-templates.util.db :refer [exec query]]
            [medley.core :refer [map-keys]]
            [camel-snake-kebab.core :refer [->snake_case_keyword]]
            [honeysql.core :as sql]
            [hikari-cp.core :as hikari]))

(defn upsert-templates [db templates]
  (let [db-templates (map #(map-keys ->snake_case_keyword %) templates)
        conflict-columns #{:template_name :build_system}
        update-columns (remove conflict-columns (keys (first db-templates)))]
    (exec db {:insert-into   :templates
              :values        db-templates
              :on-conflict   conflict-columns
              :do-update-set update-columns})))

(defn upsert-template [db template]
  (upsert-templates db [template]))

(defn all-templates [db]
  (query db {:select [:*] :from [:templates] :order-by [[:downloads :desc]]}))

(defn delete-all-templates [db]
  (exec db {:delete-from :templates}))

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

(s/fdef upsert-templates
        :args (s/cat :db ::c/db :templates ::c/templates)
        :ret int?)
