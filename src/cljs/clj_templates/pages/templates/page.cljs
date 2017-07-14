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

(defn pagination-link [page current-page-index]
  (let [page-active? (= page current-page-index)]
    [:div.pagination-link {:class    (when page-active? "current-page")
                           :on-click (when-not page-active? #(dispatch [:templates/page-change page]))}
     page]))

(defn pagination [page-count]
  (let [current-page-index (listen [:templates/current-page-index])]
    [:div.pagination
     (for [page (range 1 (inc page-count))]
       ^{:key page} [pagination-link page current-page-index])]))

(defn results [templates query-string error?]
  (if (seq templates)
    [:div.templates-listing
     (for [{:keys [template-name build-system] :as template} templates]
       ^{:key (str template-name build-system)} [template-panel template])]
    (if error?
      [:div.results-for (str "Something went wrong when getting templates for \"" query-string "\"")]
      [:div.results-for (str "No results for \"" query-string "\"")])))

(defn results-for-text [templates query-string]
  (when (seq templates)
    (let [result-string (if (str/blank? query-string) "All templates:"
                                                      (str "Results for \"" query-string "\":"))]
      [:div.results-for result-string])))

(defn templates []
  (let [templates (listen [:templates/templates])
        query-string (listen [:templates/query-string])
        page-count (listen [:templates/page-count])
        error? (listen [:templates/error?])]
    [:div.templates
     [search-input]
     (if (pos? page-count)
       [pagination page-count]
       [:div.pagination [:div.pagination-link.current-page ":("]])
     [results-for-text]
     [results templates query-string error?]
     (when (< 1 page-count) [pagination page-count])]))
