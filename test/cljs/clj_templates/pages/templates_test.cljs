(ns clj-templates.pages.templates-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [clj-templates.pages.templates.core :refer [search-templates-handler]]
            [pjstadig.humane-test-output]))

(deftest search-test
  (testing "templates/search"
    (testing "sets loading? to true and produces an api call fx"
      (is (= {:db       {:loading? true
                         :query-string "foo"}
              :api-call {:endpoint          :templates
                         :on-response-event :templates/templates-loaded
                         :params            {:q    "foo"
                                             :from 0
                                             :size 30}}}
             (search-templates-handler {} ["foo" 1]))))

    (testing "supports pagination"
      (is (= {:db {:loading? true
                   :query-string "foo"}
              :api-call {:endpoint :templates
                         :on-response-event :templates/templates-loaded
                         :params {:q "foo"
                                  :from 60
                                  :size 30}}}
             (search-templates-handler {} ["foo" 3]))))))
