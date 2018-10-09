(ns clj-templates.components.about.component
  (:require [clj-templates.components.logos.component :refer [clj-templates-logo lein-logo boot-logo]]))

(defn about []
  [:div.about
   [:p [clj-templates-logo {:class "inline-logo"}] " is a search engine for Clojure project templates. The templates are
    fetched from the popular " [:a {:href "https://clojars.org/" :target "_blank"} "Clojars"] " repository.
    While Clojars has its own search function, clj-templates makes it easier to find project
     templates by searching only the template population rather than all artficats. "]
   [:p "In addition, it indexes any GitHub README:s that the template has, making it possible to search any
      term mentioned in either the template name, its description, or its GitHub README. This enables
      searching for technologies and other topics, such as \"sass\", \"music\", or \"game\"."]
   [:p.logos "The icons on the search results (" [lein-logo] " for "
    [:a {:href "https://leiningen.org/" :target "_blank"} "Leiningen"]
    " and " [boot-logo] " for "
    [:a {:href "http://boot-clj.com/" :target "_blank"} "Boot"] ") indicate which
    build systems the template is compatible with. Clicking the icon copies the command line
    usage for starting a new project with the template."]
   [:p "If you find a bug or have a suggestion, can create a GitHub issue "
    [:a {:href "https://github.com/Dexterminator/clj-templates/issues" :target "_blank"} "here"] "."]])
