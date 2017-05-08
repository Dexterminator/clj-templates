(ns clj-templates.clojars-data
  (:require [clojure.edn :as edn]
            [org.httpkit.client :as http]
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
  (let [res @(http/get "http://clojars.org/repo/feed.clj.gz" {:as :stream})]
    (extract-templates-from-gzip-stream (:body res))))

(def github-url-re #"^https?://github.com/([^/]+/[^/]+)")

(defn set-github-url [template]
  (let [url (get template :url "")
        scm-url (get-in template [:scm :url] "")
        [github-url github-id] (some (partial re-find github-url-re) [scm-url url])]
    (assoc template
      :github-url github-url
      :github-id (when github-id (str/replace github-id ".git" "")))))

(defn adapt-template-to-db [template]
  (-> template
      (set-github-url)
      (select-keys [:group-id :description :artifact-id :github-url :github-id])
      (set/rename-keys {:group-id    :template-name
                        :artifact-id :build-system})
      (#(merge {:description "" :github-stars nil :github-readme nil} %))
      (update :build-system #(str/replace % "-template" ""))))
