(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [clj-templates.util.js :refer [target-value]]
            [re-frame.core :refer [dispatch]]
            [clojure.string :as str]))

(def lein-logo "images/leiningen.jpg")
(def boot-logo "images/boot-logo.png")

(defn boot-usage [template-name]
  (str "boot -d boot/new new -t " template-name "-n my-app"))

(defn lein-usage [template-name]
  (str "lein new " template-name "my-app"))

(defn template-panel [{:keys [template-name description build-system homepage downloads]}]
  [:div.template
   [:div
    [(if homepage :a.title :div.title)
     (when homepage {:href homepage}) template-name]
    [:div.description description]]
   [:div.info-row
    [:div.template-attribute [:div.keyword ":downloads "] [:div.code downloads]]
    [:div.template-icons
     (when (= build-system "lein")
       [:img {:src lein-logo :width "20px"}])
     [:img {:src boot-logo :width "23px"}]]]])

(defn search-input [hit-count query-string]
  [:input.search-input {:type        "text"
                        :placeholder (when (and hit-count (str/blank? query-string)) (str "Search " hit-count " templates"))
                        :on-change   #(dispatch [:templates/delayed-search (target-value %)])}])

(defn pagination-link [page current-page-index]
  (let [page-active? (= page current-page-index)]
    [:a.pagination-link {:class    (when page-active? "current-page")
                         :on-click (when-not page-active? #(dispatch [:templates/page-change page]))}
     page]))

(defn pagination [page-count]
  (let [current-page-index (listen [:templates/current-page-index])]
    [:div.pagination
     (for [page (range 1 (inc page-count))]
       ^{:key page} [pagination-link page current-page-index])]))

(defn templates-listing [templates typing? loading?]
  [:div.templates-listing {:class (when (or typing? loading?) "loading")}
   (for [{:keys [template-name build-system] :as template} templates]
     ^{:key (str template-name build-system)} [template-panel template])])

(defn results [templates error? typing? loading?]
  (when (not error?)
    [templates-listing templates typing? loading?]))

(defn results-for-text [templates query-string hit-count typing? loading? error?]
  (let [result-string (cond
                        typing? "Typing..."
                        loading? "Loading..."
                        error? (str "Something went wrong when getting templates for \"" query-string "\"")
                        (str/blank? query-string) "Results:"
                        (seq templates) (str hit-count " results for \"" query-string "\":")
                        :else (str "No results for \"" query-string "\"."))]
    [:div.results-for result-string]))

(defn intro-text []
  [:div.intro-text "Find Clojure templates for "
   [:a {:href "https://leiningen.org/" :target "_blank"}
    "Leiningen" [:img {:src lein-logo :width "20px"}]]
   " and "
   [:a {:href "http://boot-clj.com/" :target "_blank"}
    "Boot" [:img {:src boot-logo :width "23px"}]]
   ". "])

(defn templates []
  (let [templates (listen [:templates/templates])
        query-string (listen [:templates/response-query-string])
        hit-count (listen [:templates/hit-count])
        page-count (listen [:templates/page-count])
        error? (listen [:templates/error?])
        typing? (listen [:templates/typing?])
        loading? (listen [:templates/loading?])]
    [:div.templates
     [intro-text]
     [search-input hit-count query-string]
     [results-for-text templates query-string hit-count typing? loading? error?]
     (when (pos? page-count) [pagination page-count])
     [results templates error? typing? loading?]
     (when (< 1 page-count) [pagination page-count])]))
