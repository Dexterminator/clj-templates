(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-sub]]
            [clj-templates.util.events :refer [reg-event]]
            [clj-templates.util.api :as api]))

(reg-event
  :templates/page-entered
  (fn [{:keys [db]} _]
    {:api-call {:endpoint          :templates
                :on-response-event :templates/templates-loaded}}))

(reg-event
  :templates/templates-loaded
  (fn [{:keys [db]} [{:keys [templates]}]]
    {:db (assoc db :templates templates)}))

(reg-event
  :templates/tab-clicked
  (fn [{:keys [db]} [tab]]
    {:db (assoc db :active-tab tab)}))

(reg-sub
  :templates/templates
  (fn [db]
    (:templates db)))

(reg-sub
  :templates/active-tab
  (fn [db]
    (:active-tab db)))
