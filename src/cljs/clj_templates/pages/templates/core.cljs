(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-sub reg-event-db]]
            [clj-templates.util.events :refer [reg-event]]
            [clojure.string :as str]))

(defn page-entered-handler [{:keys [db]} _]
  {:api-call {:endpoint          :templates
              :on-response-event :templates/templates-loaded}
   :db       (assoc db :loading? true)})

(defn templates-loaded-handler [{:keys [db]} [{:keys [templates]}]]
  {:db (assoc db :templates templates
                 :loading? false)})

(defn search-templates-handler [{:keys [db]} [query-string]]
  {:api-call {:endpoint          :templates
              :on-response-event :templates/templates-loaded
              :params            (when-not (str/blank? query-string) {:q query-string})}
   :db       (assoc db :loading? true)})

(reg-event :templates/page-entered page-entered-handler)
(reg-event :templates/templates-loaded templates-loaded-handler)
(reg-event :templates/search search-templates-handler)

(reg-sub
  :templates/templates
  (fn [db]
    (:templates db)))

(reg-sub
  :templates/active-tab
  (fn [db]
    (:active-tab db)))

(reg-sub
  :templates/loading?
  (fn [db]
    (:loading? db)))
