(ns clj-templates.specs.frontend-db
  (:require [clojure.spec :as s]
            [clj-templates.specs.common :as c]))

(def initial-db {:active-page :templates
                 :templates   []})

(s/def ::active-page keyword?)

(s/def ::db (s/keys :req-un [::active-page ::c/templates]))
