(ns clj-templates.karma-runner
  (:require [jx.reporter.karma :as karma :include-macros true]
            [clj-templates.pages.main-test]
            [clj-templates.pages.templates-test]))

(defn ^:export run-tests [karma]
  (karma/run-tests
    karma
    'clj-templates.pages.main-test
    'clj-templates.pages.templates-test))
