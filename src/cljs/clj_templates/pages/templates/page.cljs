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
   [:div.description description]
   (when (= build-system "lein") [:div.template-attribute [:div.keyword ":lein-usage "] [:div.code "lein new " template-name " my-app"]])
   [:div.template-attribute [:div.keyword ":boot-usage "] [:div.code "boot -d boot/new new -t " template-name "-n my-app"]]
   [:div.template-attribute [:div.keyword ":downloads "] [:div.code downloads]]])

(defn search-input []
  [:input.search-input {:type        "text"
                        :placeholder (str "Search templates")
                        :on-change   #(dispatch [:templates/search (target-value %)])}])

(defn templates []
  (let [templates (listen [:templates/templates])
        loading? (listen [:templates/loading?])]
    [:div.templates
     [search-input]
     (when (and loading? (zero? (count templates))) [:div.spinner.templates-spinner])
     [:div.templates-listing
      (for [{:keys [template-name build-system] :as template} templates]
        ^{:key (str template-name build-system)} [template-panel template])]]))
