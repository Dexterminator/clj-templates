(ns clj-templates.github-data
  (:require [org.httpkit.client :as http]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [cheshire.core :as json])
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

(defn request-stars [template]
  (let [url (str repos-url (:github-id template))]
    (do (timbre/info "Getting GitHub stars for " template ". url: " url)
        (http/get url http-opts))))

(defn request-readme [template]
  (let [url (str repos-url (:github-id template) "/readme")]
    (do (timbre/info "Getting GitHub readme for " template ". url: " url)
        (http/get url http-opts))))

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
      (do (timbre/error "Something went wrong when getting info for template " template ": " (:body star-res))
          (assoc template
            :github-url nil
            :github-id nil)))))

(defn update-templates-github-info [templates]
  (doall (map update-template-github-info
              templates
              (map request-stars templates)
              (map request-readme templates))))
