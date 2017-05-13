(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-sub reg-event-db]]
            [clj-templates.util.events :refer [reg-event]]))

(defn page-entered-handler [_ _]
  {:api-call {:endpoint          :templates
              :on-response-event :templates/templates-loaded}})

(defn templates-loaded-handler [{:keys [db]} [{:keys [templates]}]]
  {:db (assoc db :templates templates)})

(reg-event :templates/page-entered page-entered-handler)
(reg-event :templates/templates-loaded templates-loaded-handler)

(reg-sub
  :templates/templates
  (fn [db]
    (:templates db)))

(reg-sub
  :templates/active-tab
  (fn [db]
    (:active-tab db)))
