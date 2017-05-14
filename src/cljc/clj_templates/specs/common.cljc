(ns clj-templates.specs.common
  (:require [clojure.spec :as s]))

(s/def ::template-name string?)
(s/def ::description string?)
(s/def ::build-system #{"lein" "boot"})
(s/def ::github-url (s/nilable string?))
(s/def ::github-id (s/nilable string?))
(s/def ::github-stars (s/nilable integer?))
(s/def ::github-readme (s/nilable string?))
(s/def ::homepage (s/nilable string?))
(s/def ::downloads integer?)
(s/def ::template (s/keys :req-un [::template-name
                                   ::description
                                   ::build-system
                                   ::github-url
                                   ::github-id
                                   ::github-stars
                                   ::github-readme]))
(s/def ::templates (s/coll-of ::template :kind sequential?))


(s/def ::group-id string?)
(s/def ::artifcat-id string?)
(s/def ::scm (s/map-of keyword? string?))
(s/def ::url string?)
(s/def ::versions (s/coll-of string? :kind vector?))
(s/def ::raw-template (s/keys :req-un [::group-id
                                       ::artifact-id
                                       ::description
                                       ::scm
                                       ::homepage
                                       ::url
                                       ::versions]))
(s/def ::raw-templates (s/coll-of ::raw-templates :kind sequential?))

(s/def ::datasource any?)
(s/def ::db (s/keys :req-un [::datasource]))

#?(:clj
   (s/def ::promise #(instance? clojure.lang.IPending %)))
