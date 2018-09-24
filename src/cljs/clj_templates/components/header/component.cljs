(ns clj-templates.components.header.component
  (:require [clj-templates.components.logos.component :refer [clj-templates-logo clojure-logo cljs-logo]]))


(defn header []
  [:header.header
   [:div.content
    [clj-templates-logo {:class "main-logo"}]
    [:div.external-links
     [:a.clojure-logo {:href "https://clojure.org/" :target "_blank"} [clojure-logo]]
     [:a {:href "https://clojurescript.org/" :target "_blank"} [cljs-logo]]]]])
