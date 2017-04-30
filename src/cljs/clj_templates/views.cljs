(ns clj-templates.views
  (:require [re-frame.core :refer [subscribe]]
            [clj-templates.components.header.component :refer [header]]
            [clj-templates.components.footer.component :refer [footer]]))

(defn main-panel []
  [:div
   [header]
   [:div#page-wrap
    [:div (str "Hello " @(subscribe [:main/title]))]]
   [footer]])
