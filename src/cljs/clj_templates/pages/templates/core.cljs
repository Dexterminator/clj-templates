(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-sub]]
            [clj-templates.util.events :refer [reg-event]]))

(defn template-call-fx-params [tab-id]
  {:endpoint          :templates
   :on-response-event :templates/templates-loaded
   :params            {:build-system tab-id}})

(defn page-entered-handler [{:keys [db]} _]
  {:api-call (template-call-fx-params (name (:active-tab db)))})

(defn tab-clicked-handler [{:keys [db]} [tab]]
  (when-not (= (:active-tab db) tab)
    {:db       (assoc db :active-tab tab)
     :api-call (template-call-fx-params (name tab))}))

(defn templates-loaded-handler [{:keys [db]} [{:keys [templates]}]]
  {:db (assoc db :templates templates)})

(reg-event :templates/page-entered page-entered-handler)
(reg-event :templates/tab-clicked tab-clicked-handler)
(reg-event :templates/templates-loaded templates-loaded-handler)

(reg-sub
  :templates/templates
  (fn [db]
    (:templates db)))

(reg-sub
  :templates/active-tab
  (fn [db]
    (:active-tab db)))
