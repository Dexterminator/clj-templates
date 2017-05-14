(ns clj-templates.jobs
  (:require [clj-templates.github-data :as github]
            [clj-templates.db.db :as db]
            [taoensso.timbre :as timbre]
            [clj-templates.clojars-data :as clojars-data]))

(defn upsert-rows [db templates]
  (let [updated-rows (->> templates
                          (pmap (partial db/upsert-template db))
                          (reduce +))]
    (timbre/info updated-rows "rows updated.")
    updated-rows))

(defn assemble-templates []
  (->> (take 5 (clojars-data/get-clojars-templates))
       (clojars-data/update-templates-details-info)
       (map clojars-data/adapt-template-to-db)
       (github/update-templates-github-info)))

(defn do-jobs [db]
  (timbre/info "Starting scheduled job: Refresh template info")
  (->> (assemble-templates)
       (upsert-rows db)))
