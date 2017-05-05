(ns clj-templates.specs.common
  (:require [clojure.spec :as s]))

(s/def ::template-name string?)
(s/def ::description string?)
(s/def ::build-system #{"lein" "boot"})
(s/def ::template (s/keys :req-un [::template-name
                                   ::description
                                   ::build-system]))
(s/def ::templates (s/coll-of ::template :kind sequential?))

(s/def ::datasource any?)
(s/def ::db (s/keys :req-un [::datasource]))
