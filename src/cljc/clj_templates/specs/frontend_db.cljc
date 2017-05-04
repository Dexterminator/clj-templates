(ns clj-templates.specs.frontend-db
  (:require [clojure.spec :as s]
            [clj-templates.specs.common :as c]))

(def initial-db {:active-page :templates
                 :active-tab  :lein
                 :templates   []})

(s/def ::active-page keyword?)
(s/def ::active-tab keyword?)

(s/def ::db (s/keys :req-un [::active-page ::active-tab ::c/templates]))
