(ns clj-templates.core
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clj-templates.pages.main.page :as main]
            [clj-templates.routes :as routes]
            [clj-templates.pages.main.core]
            [clj-templates.pages.templates.core]
            [clj-templates.util.fx]
            [clj-templates.util.dev :as dev]
            [clj-templates.util.events :refer [reg-event]]
            [clj-templates.specs.frontend-db :as db]))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [main/main-panel]
            (.getElementById js/document "app")))

(defn ^:export init []
  (dev/dev-setup)
  (routes/app-routes)
  (rf/dispatch-sync [:initialize-db])
  (mount-root))

(reg-event
  :initialize-db
  (fn [_ _]
    {:db db/initial-db}))
