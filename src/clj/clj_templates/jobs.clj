(ns clj-templates.jobs
  (:require [clj-templates.github-data :as github]
            [clj-templates.db.db :as db]
            [taoensso.timbre :as timbre]
            [clj-templates.clojars-data :as clojars-data]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [chime :refer [chime-at]]
            [integrant.core :as ig]
            [environ.core :refer [env]]
            [clj-templates.search :as search]))

(defn upsert-rows [db templates]
  (let [updated-rows (db/upsert-templates db templates)]
    (timbre/info updated-rows "rows updated.")))

(defn assemble-templates []
  (->> (if (= "prod" (env :deployment-env))
         (clojars-data/get-clojars-templates)
         (take 5 (clojars-data/get-clojars-templates)))
       (clojars-data/update-templates-details-info)
       (map clojars-data/adapt-template-to-db)
       (github/update-templates-github-info)))

(defn log-github-rate []
  (let [{:keys [remaining] :as rate} (github/get-github-rate-limit)]
    (timbre/info "GitHub rate:" rate)
    (when (< remaining 2000)
      (timbre/warn "Remaining GitHub rate is low:" remaining))))

(defn index-templates [es-client templates]
  (timbre/info "Starting job: Index templates")
  (doseq [template templates]
    (search/index-template es-client template))
  (timbre/info "Job finished: Index templates"))

(defn do-jobs [es-client]
  (timbre/info "Starting job: Refresh template info")
  (let [templates (assemble-templates)]
    (timbre/info "Job finished: Refresh template info")
    (log-github-rate)
    (index-templates es-client templates)))

(defn init-scheduled-jobs [es-client hours-between-jobs]
  (chime-at (rest (periodic-seq (t/now) (t/hours hours-between-jobs)))
            (fn [time] (do-jobs es-client))))

(defmethod ig/init-key :jobs/scheduled-jobs [_ {:keys [es-client hours-between-jobs]}]
  (init-scheduled-jobs es-client hours-between-jobs))

(defmethod ig/halt-key! :jobs/scheduled-jobs [_ cancel-jobs-fn]
  (cancel-jobs-fn))
