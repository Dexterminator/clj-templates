(ns clj-templates.util.dev
  (:require [devtools.core :as devtools]))

(def debug?
  ^boolean js/goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (devtools/set-pref! :bypass-availability-checks true)
    (devtools/install! [:formatters :hints])
    (set! js/log (.bind js/console.log js/console))))
