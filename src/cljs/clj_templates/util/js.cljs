(ns clj-templates.util.js)

(defn target-value [event]
  (-> event .-target .-value))
