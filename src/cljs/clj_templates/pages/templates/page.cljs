(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [re-frame.core :refer [dispatch]]))

(defn template-panel [{:keys [template-name description build-system]}]
  [:div.template
   [:hr]
   [:div [:b template-name] " (" build-system ")"]
   [:div description]])

(defn tab [id text active-tab]
  [:div.tab {:class (when (= active-tab id) "active")
             :on-click #(dispatch [:templates/tab-clicked id])} text])

(defn templates []
  (let [templates (listen [:templates/templates])
        active-tab (listen [:templates/active-tab])]
    [:div.templates
     [:h1 "Templates"]
     [:div.build-system-tabs
      [tab :lein "Leiningen" active-tab]
      [tab :boot "Boot" active-tab]]
     [:input.search-input {:type        "text"
                           :placeholder (str "Search " (count templates) " templates")}]
     (for [{:keys [template-name build-system] :as template} templates]
       ^{:key (str template-name build-system)} [template-panel template])]))
