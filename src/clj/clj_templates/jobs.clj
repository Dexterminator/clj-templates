(ns clj-templates.jobs
  (:require [clj-templates.clojars-data :as clojars]
            [clj-templates.github-data :as github]
            [clj-templates.db.db :as db]
            [taoensso.timbre :as timbre]))

(defn group-by-github [templates]
  (group-by (fn [template]
              (if (some? (:github-id template))
                :github-templates
                :non-github-templates))
            templates))

(defn do-jobs [db]
  (timbre/info "Starting scheduled job: Refresh template info")
  (let [templates (map clojars/adapt-template-to-db (clojars/get-clojars-templates))
        {:keys [github-templates non-github-templates]} (group-by-github templates)
        updated-github-templates (github/update-templates-github-info github-templates)
        updated-rows (->> (concat non-github-templates updated-github-templates)
                          (pmap (partial db/upsert-template db))
                          (reduce +))]
    (timbre/info updated-rows "rows successfully updated after job: Refresh template info")
    updated-rows))
