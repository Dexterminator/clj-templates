(ns clj-templates.clojars-feed
  (:require [clojure.edn :as edn]
            [clj-http.client :as http]
            [clojure.set :as set]
            [clojure.string :as str])
  (:import (java.util.zip GZIPInputStream)
           (java.io PushbackReader InputStreamReader)))

(def edn-opts {:eof :eof})

(defn gzip-seq [stream]
  (let [in (-> stream
               (GZIPInputStream.)
               (InputStreamReader.)
               (PushbackReader.))]
    (take-while #(not= :eof %) (repeatedly #(edn/read edn-opts in)))))

(defn extract-templates-from-gzip-stream [stream]
  (filterv (fn [{:keys [artifact-id]}] (#{"lein-template" "boot-template"} artifact-id))
           (gzip-seq stream)))

(defn get-clojars-templates []
  (let [res (http/get "http://clojars.org/repo/feed.clj.gz" {:as :stream})]
    (extract-templates-from-gzip-stream (:body res))))

(defn adapt-template-to-db [template]
  (-> template
      (select-keys [:group-id :description :artifact-id])
      (set/rename-keys {:group-id :template-name :artifact-id :build-system})
      (#(merge {:description ""} %))
      (update :build-system #(str/replace % "-template" ""))))
