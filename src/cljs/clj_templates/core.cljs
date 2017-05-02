(ns clj-templates.core
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clj-templates.pages.main.page :as main]
            [clj-templates.routes :as routes]
            [clj-templates.pages.main.core]
            [clj-templates.pages.templates.core]
            [clj-templates.util.api]
            [devtools.core :as devtools]))

(def debug?
  ^boolean js/goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (devtools/set-pref! :bypass-availability-checks true)
    (devtools/install! [:formatters :hints])
    (set! js/log (.bind js/console.log js/console))))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [main/main-panel]
            (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (routes/app-routes)
  ;(rf/dispatch-sync [:initialize-db])
  (mount-root))
