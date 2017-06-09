(ns clj-templates.pages.templates-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [clj-templates.pages.templates.core :refer [search-templates-handler page-count]]
            [pjstadig.humane-test-output]))

(deftest search-test
  (testing "templates/search"
    (testing "sets loading? to true and produces an api call fx"
      (is (= {:db       {:loading?              true
                         :query-string          "foo"
                         :current-template-page 1}
              :api-call {:endpoint          :templates
                         :on-response-event :templates/templates-loaded
                         :params            {:q    "foo"
                                             :from 0
                                             :size 30}}}
             (search-templates-handler {} ["foo" 1]))))

    (testing "supports pagination"
      (is (= {:db       {:loading?              true
                         :query-string          "foo"
                         :current-template-page 3}
              :api-call {:endpoint          :templates
                         :on-response-event :templates/templates-loaded
                         :params            {:q    "foo"
                                             :from 60
                                             :size 30}}}
             (search-templates-handler {} ["foo" 3]))))))

(deftest page-count-test
  (testing "page-count"
    (testing "returns an appropriate number of pages for a number of hits"
      (are
        [expected-count page-count-result]
        (= expected-count page-count-result)

        1 (page-count 1)
        1 (page-count 30)
        2 (page-count 31)
        2 (page-count 60)
        3 (page-count 61)
        3 (page-count 90)))))
