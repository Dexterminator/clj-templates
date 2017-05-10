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

(s/def ::datasource any?)
(s/def ::db (s/keys :req-un [::datasource]))
