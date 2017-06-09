(ns clj-templates.handler-test
  (:require [integrant.core :as ig]
            [clojure.test :refer :all]
            [ring.mock.request :refer [request]]
            [clj-templates.util.transit :as t]
            [clj-templates.test-utils :refer [example-templates instrument-test test-config index-example-templates]]
            [clj-templates.search :as search]))

(def test-handler (atom nil))

(defn reset-system [f]
  (with-redefs [search/base-url [:clj_templates_dev]
                search/index-url [:clj_templates_dev :templates]
                search/search-url [:clj_templates_dev :templates :_search]]
    (let [system (ig/init test-config)
          handler (:handler/main system)
          es-client (:search/elastic system)]
      (reset! test-handler handler)
      (index-example-templates es-client)
      (f)
      (search/delete-index es-client)
      (ig/halt! system))))

(use-fixtures :each reset-system instrument-test)

(deftest test-template-route
  (let [res (-> (request :get "/templates?from=0&size=30") (@test-handler))]

    (testing "Returns templates as transit"
      (is (= 200 (:status res)))
      (is (= #{{:build-system  "lein",
                :description   "",
                :downloads     10,
                :homepage      "https://foo",
                :template-name "Bar"}
               {:build-system  "lein",
                :description   "",
                :downloads     10,
                :homepage      "https://foo",
                :template-name "Baz"}
               {:build-system  "lein",
                :description   "",
                :downloads     10,
                :homepage      "https://foo",
                :template-name "Foo"}}
             (-> res :body t/read-transit-json :templates set)))))

  (let [res (-> (request :get "/templates?q=Foo&from=0&size=30") (@test-handler))]

    (testing "Returns a search result when query-string is provided"
      (is (= 200 (:status res)))
      (is (= 1 (-> res :body t/read-transit-json :templates count))))))
