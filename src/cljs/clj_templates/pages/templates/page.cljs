(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [re-frame.core :refer [dispatch]]))

(defn template-panel [{:keys [template-name description build-system]}]
  [:div.template
   [:hr]
   [:div [:b template-name] " (" build-system ")"]
   [:div description]])

(defn search-input [templates]
  [:input.search-input {:type        "text"
                        :placeholder (str "Search " (count templates) " templates")}])

(defn templates []
  (let [templates (listen [:templates/templates])]
    [:div.templates
     [:h1 "Templates"]
     [search-input templates]
     (for [{:keys [template-name build-system] :as template} templates]
       ^{:key (str template-name build-system)} [template-panel template])]))
