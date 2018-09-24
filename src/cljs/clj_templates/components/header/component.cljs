(ns clj-templates.components.header.component)

(def clojure-logo-src "images/clojure-logo.png")
(def cljs-logo-src "images/cljs-logo.png")

(defn header []
  [:header.header
   [:div.content
    [:div.main-logo "(clj-templates)"]
    [:div.external-links
     [:a.clojure-logo {:href "https://clojure.org/" :target "_blank"} [:img {:src clojure-logo-src}]]
     [:a {:href "https://clojurescript.org/" :target "_blank"} [:img {:src cljs-logo-src}]]]]])
