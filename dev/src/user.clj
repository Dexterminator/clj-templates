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
            [clj-templates.db.db :as db]
            [clj-templates.logger]
            [clojure.java.io :as io]
            [clj-templates.clojars-feed :refer [extract-templates-from-gzip-stream adapt-template-to-db]]))

(defn get-config []
  (merge main-config dev-config))

(defn cljs-repl []
  (ra/cljs-repl))

(integrant.repl/set-prep! get-config)

(defn bootstrap []
  (let [db (:db/postgres system)
        templates (extract-templates-from-gzip-stream (io/input-stream "dev/resources/test_feed_big.clj.gz"))]
    (doseq [template templates]
      (db/upsert-template db (adapt-template-to-db template)))))

(comment
  (bootstrap))
