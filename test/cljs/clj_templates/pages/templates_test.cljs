(ns clj-templates.pages.templates-test
  (:require [cljs.test :refer-macros [use-fixtures are]]
            [clj-templates.test-utils-frontend :refer-macros [facts fact is=]]
            [clj-templates.pages.templates.core :as templates]
            [pjstadig.humane-test-output]))

(facts "search-templates-handler"
  (fact "sets loading? to true and produces an api call fx"
    (is= {:db       {:loading?              true
                     :query-string          "foo"
                     :current-template-page 1}
          :api-call {:endpoint          :templates
                     :on-response-event :templates/templates-loaded
                     :params            {:q    "foo"
                                         :from 0
                                         :size 30}}}
         (templates/search-templates-handler {} ["foo" 1])))

  (fact "supports pagination"
    (is= {:db       {:loading?              true
                     :query-string          "foo"
                     :current-template-page 3}
          :api-call {:endpoint          :templates
                     :on-response-event :templates/templates-loaded
                     :params            {:q    "foo"
                                         :from 60
                                         :size 30}}}
         (templates/search-templates-handler {} ["foo" 3]))))

(facts "page-count"
  (fact "returns an appropriate number of pages for a number of hits"
    (are
      [expected-count page-count-result]
      (= expected-count page-count-result)

      1 (templates/page-count 1)
      1 (templates/page-count 30)
      2 (templates/page-count 31)
      2 (templates/page-count 60)
      3 (templates/page-count 61)
      3 (templates/page-count 90))))
