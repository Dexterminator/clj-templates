(ns clj-templates.specs.frontend-db
  (:require [clojure.spec :as s]
            [clj-templates.specs.common :as c]))

(def initial-db {:active-page :templates
                 :templates   []
                 :loading? false})

(s/def ::active-page keyword?)
(s/def ::loading? boolean?)

(s/def ::db (s/keys :req-un [::active-page ::loading? ::c/templates]))
