(ns clj-templates.components.header.component)

(defn header []
  [:header.header
   [:div.content
    [:a.main-logo {:href "#/"} "clj-templates"]]])
