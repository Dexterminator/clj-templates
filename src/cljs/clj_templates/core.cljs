(ns clj-templates.core
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clj-templates.views :as views]
            [clj-templates.events]
            [clj-templates.subs]))

(def debug?
  ^boolean js/goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [views/main-panel]
            (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (rf/dispatch-sync [:initialize-db])
  (mount-root))
