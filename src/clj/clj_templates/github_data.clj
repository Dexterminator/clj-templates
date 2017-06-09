(ns clj-templates.github-data
  (:require [org.httpkit.client :as http]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [cheshire.core :as json]
            [clojure.spec :as s]
            [clj-templates.specs.common :as c])
  (:import (java.util Base64)))

(def base-url "https://api.github.com/")
(def rate-limit-url (str base-url "rate_limit"))
(def repos-url (str base-url "repos/"))
(def http-opts {:basic-auth [(env :github-user) (env :github-access-token)]})

(defn decode [to-decode]
  (String. (.decode (Base64/getMimeDecoder) to-decode)))

(defn get-github-rate-limit []
  (let [{:keys [body]} @(http/get rate-limit-url http-opts)]
    (get-in (json/parse-string body true) [:resources :core])))

(defn group-by-github [templates]
  (group-by (fn [template]
              (if (some? (:github-id template))
                :github-templates
                :non-github-templates))
            templates))

(defn request-stars [template]
  (let [url (str repos-url (:github-id template))]
    (timbre/info "Getting GitHub stars for " template ". url: " url)
    (http/get url http-opts)))

(defn request-readme [template]
  (let [url (str repos-url (:github-id template) "/readme")]
    (timbre/info "Getting GitHub readme for " template ". url: " url)
    (http/get url http-opts)))

(defn clear-temlate-github-info [template]
  (cond-> (assoc template
            :github-url nil
            :github-id nil)
          (= (:github-url template) (:homepage template)) (assoc :homepage nil)))

(defn update-template-github-info [template stars-req readme-req]
  (let [star-res @stars-req
        readme-res @readme-req]
    (if (= 200 (:status star-res))
      (assoc template
        :github-stars (:stargazers_count (json/parse-string (:body star-res) true))
        :github-readme (when (= 200 (:status readme-res))
                         (-> (:body readme-res)
                             (json/parse-string true)
                             :content
                             (decode))))
      (do (timbre/warn "Something went wrong when getting github info for template " template ": " (:body star-res))
          (clear-temlate-github-info template)))))

(defn update-templates-github-info [templates]
  (let [{:keys [github-templates non-github-templates]} (group-by-github templates)
        updated-github-templates (doall (map update-template-github-info
                                             github-templates
                                             (map request-stars github-templates)
                                             (map request-readme github-templates)))]
    (concat non-github-templates updated-github-templates)))

(s/fdef update-templates-github-info
        :args (s/cat :templates ::c/templates)
        :ret ::c/templates)

(s/fdef update-template-github-info
        :args (s/cat :template ::c/template :stars-req ::c/promise :readme-req ::c/promise)
        :ret ::c/template)
