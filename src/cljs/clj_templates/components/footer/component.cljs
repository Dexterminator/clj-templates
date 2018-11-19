(ns clj-templates.components.footer.component)

(defn footer []
  [:footer
   [:div.content
    [:div [:span "By " [:a.dxtr-link {:href "http://www.dxtr.se/" :target "_blank"} "Dexter Gramfors"]]]]])
