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
   (when (= build-system "lein") [:div [:span.keyword ":lein-usage "] [:span.usage "lein new " template-name " my-app"]])
   [:div [:span.keyword ":boot-usage "] [:span.usage "boot -d boot/new new -t " template-name "-n my-app"]]
   [:div [:span.keyword ":downloads "] [:span.usage downloads]]])

(defn search-input []
  [:input.search-input {:type        "text"
                        :placeholder (str "Search templates")
                        :on-change   #(dispatch [:templates/search (target-value %)])}])

(defn templates []
  (let [templates (listen [:templates/templates])
        loading? (listen [:templates/loading?])]
    [:div.templates
     [search-input]
     [:div.templates-listing
      (for [{:keys [template-name build-system] :as template} templates]
        ^{:key (str template-name build-system)} [template-panel template])]]))
