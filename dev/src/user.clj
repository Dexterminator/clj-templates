(ns user
  (:require [clj-templates.core]
            [clj-templates.config.main-config :refer [main-config]]
            [config.dev-config :refer [dev-config]]
            [integrant.repl :refer [clear go halt init reset reset-all set-prep!]]
            [integrant.repl.state :refer [config system]]
            [clojure.repl :refer [apropos doc dir find-doc pst]]
            [integrant.core :as ig]
            [figwheel-sidecar.repl-api :as ra]
            [figwheel-dev]
            [pretty-dev]
            [clj-templates.logger]
            [clojure.java.io :as io]
            [clj-templates.clojars-data :refer [extract-templates-from-gzip-stream adapt-template-to-db]]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as s]
            [clj-templates.github-data :as github-data]
            [clj-templates.clojars-data :as clojars-data]
            [clj-templates.jobs :as jobs]
            [cheshire.core :as json]
            [clojure.string :as str]
            [qbits.spandex :as es]
            [qbits.spandex.utils :as es-utils]
            [clj-templates.search :as search]))

(defn get-config []
  (merge main-config dev-config))

(integrant.repl/set-prep! get-config)

(comment
  (go)
  (reset)
  (search/create-index (:search/elastic system))
  (search/delete-index (:search/elastic system))
  (time (jobs/do-jobs (:search/elastic system)))
  (search/match-all-templates (:search/elastic system) 0 30)
  (github-data/get-github-rate-limit)
  (stest/instrument)
  (stest/unstrument))
