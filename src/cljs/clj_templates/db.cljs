(ns clj-templates.db
  (:require [cljs.spec :as s]))

(def initial-db {:active-page :templates
                 :active-tab  :lein
                 :templates   []})

(s/def ::template-name string?)
(s/def ::description string?)
(s/def ::build-system #{"lein" "boot"})
(s/def ::template (s/keys :req-un [::template-name
                                   ::description
                                   ::build-system]))
(s/def ::templates (s/coll-of ::template :kind vector?))

(s/def ::active-page keyword?)
(s/def ::active-tab keyword?)

(s/def ::db (s/keys :req-un [::active-page ::templates ::active-tab]))
