(ns clj-templates.pages.main.core
  (:require [re-frame.core :refer [reg-event-fx reg-sub]]
            [clj-templates.util.events :refer [reg-event]]))

(def page-entered-events
  {:templates [:templates/page-entered]})

(defn page-entered [{:keys [db]} [page]]
  (let [event (page-entered-events page)]
    (cond-> {:db (assoc db :active-page page)}
            event (merge {:dispatch event}))))

(reg-event
  :main/page-entered
  page-entered)

(reg-sub
  :main/active-page
  (fn [db]
    (:active-page db)))
