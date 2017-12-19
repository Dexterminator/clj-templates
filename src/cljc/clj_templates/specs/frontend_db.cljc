(ns clj-templates.specs.frontend-db
  (:require [clojure.spec.alpha :as s]
            [clj-templates.specs.common :as c]))

(def initial-db {:active-page :templates
                 :templates   {:template-list         []
                               :loading?              false
                               :request-query-string  ""
                               :response-query-string ""
                               :error?                false
                               :current-template-page 1
                               :typing?               false}})

(s/def ::template (s/keys :req-un [::c/template-name
                                   ::c/description
                                   ::c/build-system
                                   ::c/homepage
                                   ::c/downloads]))
(s/def ::template-list (s/coll-of ::template :kind sequential?))

(s/def ::active-page keyword?)
(s/def ::loading? boolean?)
(s/def ::response-query-string string?)
(s/def ::request-query-string string?)
(s/def ::error? boolean?)
(s/def ::current-templates-page integer?)
(s/def ::typing? boolean?)

(s/def ::templates (s/keys :req-un [::template-list
                                    ::loading?
                                    ::request-query-string
                                    ::response-query-string
                                    ::error?
                                    ::current-template-page
                                    ::typing?]))

(s/def ::db (s/keys :req-un [::active-page
                             ::templates]))
