(ns clj-templates.pages.about.page
  (:require [clj-templates.components.logos.component :refer [lein-logo boot-logo]]))

(defn about []
  [:div.about
   [:h4 "What is clj-templates?"]
   [:p "A search engine for Clojure project templates. The templates are
    fetched from the popular " [:a {:href "https://clojars.org/"} "Clojars"] " repository.
    While Clojars has its own search function, clj-templates makes it easier to find project
     templates by searching only the template population rather than all artficats. "]
   [:p "In addition, it indexes any GitHub README:s that the template has, making it possible to search any
      term mentioned in either the template name, its description, or its GitHub README. This enables
      searching for technologies and other topics, such as \"sass\", \"music\", or \"game\"."]

   [:h4 "Which templates work with which build system?"]
   [:p.logos "The icons on the search results (" [lein-logo] " for "
    [:a {:href "https://leiningen.org/" :target "_blank"} "Leiningen"]
    " and " [boot-logo] " for "
    [:a {:href "http://boot-clj.com/" :target "_blank"} "Boot"] ") indicate which
    build systems the template is compatible with. Clicking the icon copies the command line
    usage for starting a new project with the template."]

   [:h4 "I found a bug/I have a suggestion, where do I send it?"]
   [:p "You can create a GitHub issue " [:a {:href "https://github.com/Dexterminator/clj-templates/issues"} "here"] "."]])
