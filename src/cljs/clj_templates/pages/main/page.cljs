(ns clj-templates.pages.main.page
  (:require [re-frame.core :refer [subscribe]]
            [clj-templates.components.header.component :refer [header]]
            [clj-templates.components.footer.component :refer [footer]]
            [clj-templates.util.events :refer [listen]]
            [clj-templates.pages.templates.page :refer [templates]]
            [clj-templates.pages.about.page :refer [about]]))

(def page-panels {:templates [templates]
                  :about     [about]})

(defn page-panel [page-name]
  (page-panels page-name))

(defn main-panel []
  (let [active-page (listen [:main/active-page])]
    [:div.app
     [header]
     [:div.main
      [page-panel active-page]]
     [footer]]))
