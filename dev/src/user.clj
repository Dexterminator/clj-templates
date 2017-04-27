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
            [timbre-dev]))

(defn get-config []
  (merge main-config dev-config))

(defn cljs-repl []
  (ra/cljs-repl))

(integrant.repl/set-prep! get-config)
