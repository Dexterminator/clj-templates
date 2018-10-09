(ns clj-templates.components.header.component
  (:require [clj-templates.components.logos.component :refer [clj-templates-logo clojure-logo cljs-logo]]
            [re-frame.core :refer [dispatch]]))

(defn header []
  [:header.header
   [:div.content
    [clj-templates-logo {:class "main-logo" :on-click #(dispatch [:main/reload-app])}]
    [:div.external-links
     [:a.clojure-logo {:href "https://clojure.org/" :target "_blank"} [clojure-logo]]
     [:a {:href "https://clojurescript.org/" :target "_blank"} [cljs-logo]]]]])
