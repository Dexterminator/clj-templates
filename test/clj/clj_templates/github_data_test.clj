(ns clj-templates.github-data-test
  (:require [clojure.test :refer :all]
            [clj-templates.test-utils :refer [example-templates]]
            [clj-templates.github-data :refer [update-template-github-info]]))

(deftest test-update-template-github-info
  (testing "Updates template github stars and readme on successful requests"
    (let [stars-req (promise)
          readme-req (promise)]
      (deliver stars-req {:body "{\"stargazers_count\": 10}" :status 200})
      (deliver readme-req {:body "{\"content\": \"Zm9v\"}" :status 200}) ; base64-encoded "foo"
      (is (= {:template-name "Foo"
              :description   ""
              :build-system  "lein"
              :github-url    "https://foo"
              :github-readme "foo"
              :github-id     nil
              :github-stars  10
              :homepage      "https://foo"
              :downloads     10}
             (update-template-github-info (first example-templates) stars-req readme-req)))))
  (testing "Removes github-url for template when stars request fails"
    (let [stars-req (promise)
          readme-req (promise)]
      (deliver stars-req {:status 404})
      (deliver readme-req {})
      (is (= {:template-name "Foo"
              :description   ""
              :build-system  "lein"
              :github-url    nil
              :github-readme nil
              :github-id     nil
              :github-stars  nil
              :homepage      nil
              :downloads     10}
             (update-template-github-info (first example-templates) stars-req readme-req))))))
