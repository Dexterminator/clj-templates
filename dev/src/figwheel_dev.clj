(ns figwheel-dev
  (:require [integrant.core :as ig]
            [figwheel-sidecar.repl-api :as ra]))

(defmethod ig/init-key :figwheel [_ opts]
  (ra/start-figwheel! opts))

(defmethod ig/halt-key! :figwheel [_ _]
  (ra/stop-figwheel!))

(defmethod ig/suspend-key! :figwheel [_ _]
  nil)

(defmethod ig/resume-key :figwheel [_ _ _ _]
  nil)
