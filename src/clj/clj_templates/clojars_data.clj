(ns clj-templates.clojars-data
  (:require [clojure.edn :as edn]
            [org.httpkit.client :as http]
            [clojure.set :as set]
            [clojure.string :as str]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [clojure.spec.alpha :as s]
            [clj-templates.specs.common :as c]
            [clj-templates.util.template :as template-utils])
  (:import (java.util.zip GZIPInputStream)
           (java.io PushbackReader InputStreamReader)))

(def edn-opts {:eof :eof})

(defn read-gzip-edn [stream]
  (let [in (-> stream
               (GZIPInputStream.)
               (InputStreamReader.)
               (PushbackReader.))]
    (doall
     (take-while #(not= :eof %) (repeatedly #(edn/read edn-opts in))))))

(defn is-boot? [artifact-id]
  (= artifact-id "boot-template"))

(defn is-lein? [artifact-id]
  (and (str/includes? artifact-id "lein-template")
       (not= artifact-id "lein-templater")))

(defn extract-templates-from-gzip-stream [stream]
  (filterv (fn [{:keys [artifact-id]}]
             (or (is-boot? artifact-id) (is-lein? artifact-id)))
           (read-gzip-edn stream)))

(defn get-clojars-templates []
  (with-open [stream (:body @(http/get "http://repo.clojars.org/feed.clj.gz" {:as :stream}))]
    (extract-templates-from-gzip-stream stream)))

(def github-url-re #"^https?://github.com/([^/]+/[^/]+)")

(defn set-github-url [template]
  (let [url (get template :url "")
        scm-url (get-in template [:scm :url] "")
        [github-url github-id] (some (partial re-find github-url-re) [scm-url url])]
    (assoc template
           :github-url github-url
           :github-id (when github-id (str/replace github-id ".git" "")))))

(def clojars-details-url "https://clojars.org/api/artifacts/")

(defn get-template-details [template]
  (timbre/info (str "Getting clojars details for template " (template-utils/abbreviate-raw template)))
  (http/get (str clojars-details-url (:group-id template) "/" (:artifact-id template))))

(defn update-template-details-info [template details-req]
  (let [res @details-req]
    (if (= 200 (:status res))
      (let [template-details (json/parse-string (:body res) true)]
        (assoc template :homepage (:homepage template-details)
               :downloads (:downloads template-details)))
      (do (timbre/warn (str "Something went wrong when getting template detail info for template "
                            (template-utils/abbreviate-raw template) ": "
                            (:body res)))
          template))))

(defn update-templates-details-info [templates]
  (map update-template-details-info
       templates
       (map get-template-details templates)))

(defn fix-homepage [{:keys [homepage github-url] :as template}]
  (let [no-homepage? (or (nil? homepage) (str/includes? homepage "FIXME"))
        has-github? (some? github-url)]
    (cond
      (and no-homepage? has-github?) (assoc template :homepage github-url)
      no-homepage? (assoc template :homepage nil)
      :else template)))

(defn fix-name [{:keys [group-id artifact-id] :as template}]
  (let [legacy-template? (or (= artifact-id "lein-template")
                             (= artifact-id "boot-template"))
        template-name (cond
                        legacy-template? group-id
                        (str/includes? artifact-id "lein-template.") (str/replace artifact-id "lein-template." "")
                        (str/includes? artifact-id ".lein-template") (str/replace artifact-id ".lein-template" "")
                        :else artifact-id)]
    (-> template
        (assoc :template-name template-name)
        (assoc :usage (cond
                        legacy-template? template-name
                        :else (str group-id "/" template-name))))))

(defn adapt-template-to-db [{:keys [artifact-id] :as template}]
  (-> template
      (set-github-url)
      (select-keys [:group-id :description :artifact-id :github-url :github-id :homepage :downloads])
      (assoc :build-system (cond
                             (is-boot? artifact-id) "boot"
                             (is-lein? artifact-id) "lein"
                             :else ""))
      (fix-name)
      (fix-homepage)
      (#(merge {:description "" :github-stars nil :github-readme nil} %))))

(s/fdef extract-templates-from-gzip-stream
  :args (s/cat :stream #(instance? java.io.InputStream %))
  :ret ::c/raw-templates)

(s/fdef adapt-template-to-db
  :args (s/cat :template ::c/raw-template)
  :ret ::c/template)
