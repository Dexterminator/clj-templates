(ns clj-templates.specs.api
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.reader.edn :as edn]))

(s/def ::int-string (s/and
                      #(re-matches #"\d+" %)
                      (s/conformer edn/read-string)))
(s/def ::from ::int-string)
(s/def ::size ::int-string)
(s/def ::q (s/and string? #(< (count %) 100)))

(s/def ::templates-params (s/keys :req-un [::from ::size ::q]))
