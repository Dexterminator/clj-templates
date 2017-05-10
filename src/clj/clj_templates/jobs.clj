(ns clj-templates.jobs
  (:require [clj-templates.github-data :as github]
            [clj-templates.db.db :as db]
            [taoensso.timbre :as timbre]
            [clj-templates.clojars-data :as clojars-data]))

(defn upsert-rows [db templates]
  (->> templates
       (pmap (partial db/upsert-template db))
       (reduce +)))

(defn do-jobs [db]
  (timbre/info "Starting scheduled job: Refresh template info")
  (let [templates (->> (clojars-data/get-clojars-templates)
                       (clojars-data/update-templates-details-info)
                       (map clojars-data/adapt-template-to-db)
                       (github/update-templates-github-info))
        updated-rows (upsert-rows db templates)]
    (timbre/info updated-rows "rows successfully updated after job: Refresh template info")
    updated-rows))
