(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [clj-templates.util.js :refer [target-value]]
            [re-frame.core :refer [dispatch]]
            [clojure.string :as str]
            [reagent.core :as r]
            [clj-templates.components.tooltip.component :refer [tooltip]]
            [clj-templates.components.logos.component :refer [lein-logo boot-logo]]
            [clj-templates.components.about.component :refer [about]]
            [cljsjs.clipboard]))

(defn boot-usage [template-name]
  (str "boot -d boot/new new -t " template-name " -n my-app"))

(defn lein-usage [template-name]
  (str "lein new " template-name " my-app"))

(def max-description-length 150)

(defn abbreviate-description [description should-abbreviate?]
  (if (and should-abbreviate?
           (< max-description-length (count description)))
    (str (subs description 0 max-description-length) "...")
    description))

(def build-system-config {:lein {:full-name     "Leiningen"
                                 :usage-fn      lein-usage
                                 :img-component lein-logo}
                          :boot {:full-name     "Boot"
                                 :usage-fn      boot-usage
                                 :img-component boot-logo}})

(defn template-icon [build-system template-name]
  (let [{:keys [full-name usage-fn img-component]} (build-system-config build-system)
        usage-text (usage-fn template-name)
        default-instruction-text (str "Click to copy " full-name " usage: ")
        instruction-text (r/atom default-instruction-text)]
    (fn []
      [tooltip {:ref                 (fn [this] (when this (js/Clipboard. this)))
                :data-clipboard-text usage-text
                :on-click            #(reset! instruction-text "Copied!")
                :on-mouse-leave      #(reset! instruction-text default-instruction-text)}
       [img-component]
       [:span @instruction-text [:pre usage-text]]])))

(defn template-panel []
  (let [hovered? (r/atom false)]
    (fn [{:keys [template-name description build-system homepage downloads]}]
      [:div.template {:on-mouse-enter #(reset! hovered? true)
                      :on-mouse-leave #(reset! hovered? false)}
       [:div
        [(if homepage :a.title :div.title)
         (when homepage {:href homepage}) template-name]
        [:div.description (abbreviate-description description (not @hovered?))]]
       [:div.info-row
        [:div.template-attribute [:div.keyword ":downloads "] [:div.code downloads]]
        [:div.template-icons
         (when (= build-system "lein")
           [template-icon :lein template-name])
         [template-icon :boot template-name]]]])))

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

(defn status-text [typing? loading?]
  (let [status-string (cond typing? "Typing..."
                            loading? "Loading...")]
    [:div.status-text status-string]))

(defn results-for-text [query-string hit-count error?]
  (let [result-string (cond
                        error? (str "Something went wrong when getting templates for \"" query-string "\"")
                        (str/blank? query-string) "Results:"
                        (pos? hit-count) (str hit-count " results for \"" query-string "\":")
                        :else (str "No results for \"" query-string "\"."))]
    [:div.results-for result-string]))

(defn intro-text []
  (let [expand? (r/atom false)]
    (fn []
      [:div.intro-text
       [:div "Find Clojure templates for "
        [:a {:href "https://leiningen.org/" :target "_blank"}
         "Leiningen" [lein-logo]]
        " and "
        [:a {:href "http://boot-clj.com/" :target "_blank"}
         "Boot" [boot-logo]]
        ". "
        [:div [:a.read-more {:on-click #(swap! expand? not)}  "More " [:span.expand-arrow (if @expand? "▼" "▶")]]]]
       [:div (when @expand? [about])]])) )

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
     [status-text typing? loading?]
     [results-for-text query-string hit-count error?]
     (when (pos? page-count) [pagination page-count])
     [results templates error? typing? loading?]
     (when (< 1 page-count) [pagination page-count])]))
