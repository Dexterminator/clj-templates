(ns clj-templates.pages.templates.page
  (:require [clj-templates.util.events :refer [listen]]
            [re-frame.core :refer [dispatch]]))

(defn template-panel [{:keys [template-name description build-system]}]
  [:div.template
   [:hr]
   [:div [:b template-name] " (" build-system ")"]
   [:div description]])

(defn tab [id text active-tab]
  [:div.tab {:class    (when (= active-tab id) "active")
             :on-click #(dispatch [:templates/tab-clicked id])} text])

(defn tabs [active-tab]
  [:div.build-system-tabs
   [tab :lein "Leiningen" active-tab]
   [tab :boot "Boot" active-tab]])

(def descriptions
  {:lein {:text    "Leiningen is for automating Clojure projects without setting your hair on fire."
          :image   "images/leiningen.jpg"
          :command "lein new app"}
   :boot {:text    "Builds are programs. Let's start treating them that way."
          :image   "images/boot-logo.png"
          :command "boot -d boot/new new -t app -n myapp"}})

(defn build-system-info [active-tab]
  (let [{:keys [image text command]} (descriptions active-tab)]
    [:div.build-system-info
     [:img.build-system-logo {:src image}]
     [:div.build-system-text [:i text] [:pre command]]]))

(defn search-input [templates]
  [:input.search-input {:type        "text"
                        :placeholder (str "Search " (count templates) " templates")}])

(defn templates []
  (let [templates (listen [:templates/templates])
        active-tab (listen [:templates/active-tab])]
    [:div.templates
     [:h1 "Templates"]
     [build-system-info active-tab]
     [tabs active-tab]
     [search-input templates]
     (for [{:keys [template-name build-system] :as template} templates]
       ^{:key (str template-name build-system)} [template-panel template])]))
