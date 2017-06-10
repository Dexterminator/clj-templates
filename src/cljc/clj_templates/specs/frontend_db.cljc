(ns clj-templates.specs.frontend-db
  (:require [clojure.spec :as s]
            [clj-templates.specs.common :as c]))

(def initial-db {:active-page           :templates
                 :templates             []
                 :loading?              false
                 :query-string          ""
                 :error?                false
                 :current-template-page 1
                 :timeout               nil})

(s/def ::template (s/keys :req-un [::c/template-name
                                   ::c/description
                                   ::c/build-system
                                   ::c/homepage
                                   ::c/downloads]))
(s/def ::templates (s/coll-of ::template :kind sequential?))

(s/def ::active-page keyword?)
(s/def ::loading? boolean?)
(s/def ::query-string string?)
(s/def ::error? boolean?)
(s/def ::current-templates-page integer?)
(s/def ::timeout (s/nilable integer?))

(s/def ::db (s/keys :req-un [::active-page
                             ::loading?
                             ::templates
                             ::query-string
                             ::error?
                             ::current-template-page
                             ::timeout]))
