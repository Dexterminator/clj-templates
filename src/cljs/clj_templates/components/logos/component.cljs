(ns clj-templates.components.logos.component)

(def lein-logo-src "images/leiningen-logo.png")
(def boot-logo-src "images/boot-logo.png")
(def clojure-logo-src "images/clojure-logo.png")
(def cljs-logo-src "images/cljs-logo.png")

(defn lein-logo []
  [:img.logo {:class "lein-logo" :src lein-logo-src}])

(defn boot-logo []
  [:img.logo {:class "boot-logo" :src boot-logo-src}])

(defn clojure-logo []
  [:img {:src clojure-logo-src}])

(defn cljs-logo []
  [:img {:src cljs-logo-src}])

(defn clj-templates-logo [attrs]
  [:span.clj-templates-logo attrs [:span.paren "("] [:span.logo-text "clj-templates"] [:span.paren ")"]])
