(ns clj-templates.util.dev)

(def debug?
  ^boolean js/goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (set! js/log (.bind js/console.log js/console))))
