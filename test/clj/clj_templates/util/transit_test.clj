(ns clj-templates.util.transit-test
  (:require [clojure.test :refer :all]
            [clj-templates.test-utils :refer [instrument-test]]
            [clj-templates.util.transit :refer [transit-json read-transit-json]]))

(use-fixtures :each instrument-test)

(deftest test-transit-json
  (let [value {:foo {:bar ["baz" 1 2 3]}}
        encoded-value "[\"^ \",\"~:foo\",[\"^ \",\"~:bar\",[\"baz\",1,2,3]]]"]

    (testing "transit-json returns a transit-encoded string"
      (is (= encoded-value
             (transit-json value))))

    (testing "read-transit-json returns the encoded clojure value"
      (is (= value
             (read-transit-json encoded-value))))))
