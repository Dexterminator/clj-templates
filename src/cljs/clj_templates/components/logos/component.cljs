(ns clj-templates.components.logos.component)

(def lein-logo-src "images/leiningen-logo.png")
(def boot-logo-src "images/boot-logo.png")

(defn lein-logo []
  [:img.logo {:class "lein-logo" :src lein-logo-src}])

(defn boot-logo []
  [:img.logo {:class "boot-logo" :src boot-logo-src}])
