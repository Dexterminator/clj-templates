(ns clj-templates.pages.main-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [clj-templates.pages.main.core :refer [page-entered-handler]]
            [pjstadig.humane-test-output]))

(deftest page-entered-test
  (testing "page-entered"
    (testing "updates active page and dispatches event"
      (is (= {:db       {:active-page :templates}
              :dispatch [:templates/page-entered]}
             (page-entered-handler {:db {:active-page nil}} [:templates]))))

    (testing "only updates active page when there is no registered event"
      (is (= {:db {:active-page :about}}
             (page-entered-handler {:db {:active-page nil}} [:about]))))))
