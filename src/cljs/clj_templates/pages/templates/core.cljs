(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-sub reg-event-db path]]
            [clj-templates.util.events :refer [reg-event]]))

(def results-per-page 30)
(def search-delay 300)

(defn templates-loaded-handler [{:keys [db]} [{:keys [templates hit-count query-string]} error?]]
  (if error?
    {:db (assoc db :error? true
                   :templates []
                   :hit-count 0
                   :loading? false)}
    (when (= (:request-query-string db) query-string)
      {:db (assoc db :template-list templates
                     :hit-count (:value hit-count)
                     :response-query-string query-string
                     :loading? false)})))

(defn search-templates-handler [{:keys [db]} [query-string page]]
  {:api-call {:endpoint          :templates
              :on-response-event :templates/templates-loaded
              :params            {:q    query-string
                                  :from (* (dec page) results-per-page)
                                  :size results-per-page}}
   :db       (assoc db :loading? true
                       :request-query-string query-string
                       :current-template-page page
                       :typing? false)})

(defn page-change-handler [{:keys [db]} [page]]
  {:dispatch [:templates/search (:request-query-string db) page]})

(defn delayed-search-handler [{:keys [db]} [query-string]]
  {:dispatch-debounce [{:id       :search
                        :dispatch [:templates/search query-string 1]
                        :ms       search-delay}]
   :db                (assoc db :typing? true)})

(defn page-count [hit-count]
  (js/Math.ceil (/ hit-count results-per-page)))

(reg-event :templates/templates-loaded (path :templates) templates-loaded-handler)
(reg-event :templates/search (path :templates) search-templates-handler)
(reg-event :templates/page-change (path :templates) page-change-handler)
(reg-event :templates/delayed-search (path :templates) delayed-search-handler)

(reg-sub :templates/templates (fn [db] (get-in db [:templates :template-list])))
(reg-sub :templates/active-tab (fn [db] (get-in db [:templates :active-tab])))
(reg-sub :templates/loading? (fn [db] (get-in db [:templates :loading?])))
(reg-sub :templates/hit-count (fn [db] (get-in db [:templates :hit-count])))
(reg-sub :templates/current-page-index (fn [db] (get-in db [:templates :current-template-page])))
(reg-sub :templates/response-query-string (fn [db] (get-in db [:templates :response-query-string])))
(reg-sub :templates/error? (fn [db] (get-in db [:templates :error?])))
(reg-sub :templates/typing? (fn [db] (get-in db [:templates :typing?])))

(reg-sub
  :templates/page-count
  :<- [:templates/hit-count]
  (fn [hit-count]
    (page-count hit-count)))
