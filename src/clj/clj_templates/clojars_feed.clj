(ns clj-templates.clojars-feed
  (:require [clojure.edn :as edn]
            [clj-http.client :as http]
            [clojure.set :as set]
            [clojure.string :as str]
            [medley.core :refer [find-first]]
            [clojure.java.io :as io])
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

(def github-url-re #"^https?://github.com/[^/]+/[^/]+")

(defn set-github-url [template]
  (let [url (get template :url "")
        scm-url (get-in template [:scm :url] "")
        github-url (find-first (partial re-matches github-url-re) [scm-url url])]
    (assoc template :github-url github-url)))

(defn adapt-template-to-db [template]
  (-> template
      (set-github-url)
      (select-keys [:group-id :description :artifact-id :github-url])
      (set/rename-keys {:group-id    :template-name
                        :artifact-id :build-system})
      (#(merge {:description ""} %))
      (update :build-system #(str/replace % "-template" ""))))
