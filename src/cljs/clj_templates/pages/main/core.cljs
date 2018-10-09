(ns clj-templates.pages.main.core
  (:require [re-frame.core :refer [reg-event-fx reg-sub]]
            [clj-templates.util.events :refer [reg-event]]))

(def page-entered-events
  {:templates [:templates/search "" 1]})

(defn page-entered-handler [{:keys [db]} [page]]
  (let [event (page-entered-events page)]
    (cond-> {:db (assoc db :active-page page)}
            event (merge {:dispatch event}))))

(defn reload-app-handler [_ _]
  {:dispatch-n [[:initialize-db]
                [:main/page-entered :templates]]})

(reg-event :main/page-entered page-entered-handler)
(reg-event :main/reload-app reload-app-handler)

(reg-sub
  :main/active-page
  (fn [db]
    (:active-page db)))
