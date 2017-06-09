(ns clj-templates.search-test
  (:require [integrant.core :as ig]
            [clojure.test :refer :all]
            [clj-templates.test-utils :refer [example-templates instrument-test test-config index-example-templates]]
            [clj-templates.search :as search :refer [base-url]]))


(def test-es-client (atom nil))

(def api-example-templates (mapv search/adapt-template-to-api example-templates))

(defn reset-system [f]
  (with-redefs [search/base-url [:clj_templates_dev]
                search/index-url [:clj_templates_dev :templates]
                search/search-url [:clj_templates_dev :templates :_search]]
    (let [system (ig/init (select-keys test-config [:search/elastic]))
          es-client (:search/elastic system)]
      (reset! test-es-client es-client)
      (index-example-templates es-client)
      (f)
      (search/delete-index es-client)
      (ig/halt! system))))

(use-fixtures :each reset-system instrument-test)

(deftest search
  (testing "We can find all templates"
    (let [result (search/match-all-templates @test-es-client 0 10)]
      (is (= 3 (:hit-count result)))
      (is (= (set api-example-templates) (set (:templates result))))))

  (testing "We can limit number of results"
    (let [result (search/match-all-templates @test-es-client 1 2)]
      (is (= 3 (:hit-count result)))
      (is (= (set (subvec api-example-templates 1 3)) (set (:templates result))))))

  (testing "Searching gives a relevant result"
    (let [result (search/search-templates @test-es-client "Foo" 0 10)]
      (is (= 1 (:hit-count result)))
      (is (= [(first api-example-templates)] (:templates result))))))
