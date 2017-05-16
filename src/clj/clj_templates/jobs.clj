(ns clj-templates.jobs
  (:require [clj-templates.github-data :as github]
            [clj-templates.db.db :as db]
            [taoensso.timbre :as timbre]
            [clj-templates.clojars-data :as clojars-data]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [chime :refer [chime-at]]
            [integrant.core :as ig]
            [environ.core :refer [env]]))

(defn upsert-rows [db templates]
  (let [updated-rows (->> templates
                          (pmap (partial db/upsert-template db))
                          (reduce +))]
    (timbre/info updated-rows "rows updated.")
    updated-rows))

(defn assemble-templates []
  (->> (if (= "prod" (env :deployment-env))
         (clojars-data/get-clojars-templates)
         (take 5 (clojars-data/get-clojars-templates)))
       (clojars-data/update-templates-details-info)
       (map clojars-data/adapt-template-to-db)
       (github/update-templates-github-info)))

(defn log-github-rate []
  (let [{:keys [remaining] :as rate} (github/get-github-rate-limit)]
    (timbre/info "GithHb rate:" rate)
    (when (< remaining 2000)
      (timbre/warn "Remaining GitHub rate is low:" remaining))))

(defn do-jobs [db]
  (timbre/info "Starting job: Refresh template info")
  (->> (assemble-templates)
       (upsert-rows db))
  (log-github-rate))

(defn init-scheduled-jobs [db hours-between-jobs]
  (chime-at (rest (periodic-seq (t/now) (t/hours hours-between-jobs)))
            (fn [time] (do-jobs db))))

(defmethod ig/init-key :jobs/scheduled-jobs [_ {:keys [hours-between-jobs db]}]
  (init-scheduled-jobs db hours-between-jobs))

(defmethod ig/halt-key! :jobs/scheduled-jobs [_ cancel-jobs-fn]
  (cancel-jobs-fn))
