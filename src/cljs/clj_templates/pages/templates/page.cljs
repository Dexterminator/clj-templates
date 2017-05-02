(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]))

(defn template-panel [{:keys [template-name description build-system]}]
  [:div.template
   [:hr]
   [:div [:b template-name] " (" build-system ")"]
   [:div description]])

(defn templates []
  (let [templates (listen [:templates/templates])]
    [:div.templates
     [:h1 "Templates"]
     [:input.search-input {:type        "text"
                           :placeholder (str "Search " (count templates) " templates")}]
     (for [template templates]
       ^{:key (:template-name template)} [template-panel template])]))
