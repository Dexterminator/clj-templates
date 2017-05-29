(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [clj-templates.util.js :refer [target-value]]
            [re-frame.core :refer [dispatch]]))

(defn template-panel [{:keys [template-name description build-system homepage downloads]}]
  [:div.template
   [:div.title
    (if homepage
      [:a {:href homepage} template-name]
      template-name)]
   [:div description]
   (when (= build-system "lein") [:div [:pre.keyword ":lein-usage "] [:pre.usage "lein new " template-name " my-app"]])
   [:div [:pre.keyword ":boot-usage "] [:pre.usage "boot -d boot/new new -t " template-name "-n my-app"]]
   [:div [:pre.keyword ":downloads"] [:pre.usage downloads]]])

(defn search-input [templates]
  (let [template-count (count templates)]
    [:input.search-input {:type        "text"
                          :placeholder (when (pos? template-count) (str "Search templates"))
                          :on-change   #(dispatch [:templates/search (target-value %)])}]))

(defn templates []
  (let [templates (listen [:templates/templates])
        loading? (listen [:templates/loading?])]
    [:div.templates
     [:div.template-title
      [:h1 "Templates"]
      (when loading?
        [:div.spinner.templates-spinner])]
     [search-input templates]
     [:div

      (for [{:keys [template-name build-system] :as template} templates]
        ^{:key (str template-name build-system)} [template-panel template])]]))
