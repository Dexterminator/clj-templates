(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-sub reg-event-db path]]
            [clj-templates.util.events :refer [reg-event]]))

(def results-per-page 30)

(defn templates-loaded-handler [{:keys [db]} [{:keys [templates]}]]
  {:db (assoc db :templates templates
                 :loading? false)})

(defn search-templates-handler [{:keys [db]} [query-string page]]
  {:api-call {:endpoint          :templates
              :on-response-event :templates/templates-loaded
              :params            {:q    query-string
                                  :from (* (dec page) results-per-page)
                                  :size results-per-page}}
   :db       (assoc db :loading? true
                       :query-string query-string)})

(defn page-change-handler [{:keys [db]} [page]]
  {:dispatch [:templates/search (:query-string db) page]})

(reg-event :templates/templates-loaded templates-loaded-handler)
(reg-event :templates/search search-templates-handler)
(reg-event :templates/page-change page-change-handler)

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
