(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [clj-templates.util.js :refer [target-value]]
            [re-frame.core :refer [dispatch]]
            [clojure.string :as str]))

(defn template-panel [{:keys [template-name description build-system homepage downloads]}]
  [(if homepage :a.template
                :div.template)
   (when homepage {:href homepage})
   [:div.title template-name]
   [:div.description description]
   (when (= build-system "lein") [:div.template-attribute [:div.keyword ":lein-usage "] [:div.code "lein new " template-name " my-app"]])
   [:div.template-attribute [:div.keyword ":boot-usage "] [:div.code "boot -d boot/new new -t " template-name "-n my-app"]]
   [:div.template-attribute [:div.keyword ":downloads "] [:div.code downloads]]])

(defn search-input []
  [:input.search-input {:type        "text"
                        :placeholder (str "Search templates")
                        :on-change   #(dispatch [:templates/delayed-search (target-value %)])}])

(defn pagination [page-count]
  (let [current-page-index (listen [:templates/current-page-index])]
    [:div.pagination
     (for [page (range 1 (inc page-count))]
       (let [page-active? (= page current-page-index)]
         ^{:key page} [:div.pagination-link
                       {:class    (when page-active? "current-page")
                        :on-click (when-not page-active? #(dispatch [:templates/page-change page]))}
                       page]))]))

(defn templates []
  (let [templates (listen [:templates/templates])
        loading? (listen [:templates/loading?])
        query-string (listen [:templates/query-string])
        page-count (listen [:templates/page-count])]
    [:div.templates
     [search-input]
     [pagination page-count]
     (when (and (not (str/blank? query-string))
                (seq templates))
       [:div.results-for (str "Results for \"" query-string "\":")])
     (when (and loading? (zero? (count templates))) [:div.spinner.templates-spinner])
     (if (seq templates)
       [:div.templates-listing
        (for [{:keys [template-name build-system] :as template} templates]
          ^{:key (str template-name build-system)} [template-panel template])]
       [:div.no-results (str "No results for \"" query-string "\"")])
     (when (< 1 page-count) [pagination page-count])]))
