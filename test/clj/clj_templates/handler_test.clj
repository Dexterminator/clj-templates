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
  (testing "Returns templates as transit"
    (let [res (-> (request :get "/templates?q=&from=0&size=30") (@test-handler))
          body (-> res :body t/read-transit-json)]
      (is (= 200 (:status res)))
      (is (= {:templates    [{:build-system  "lein",
                              :description   "",
                              :downloads     10,
                              :homepage      "https://foo",
                              :template-name "Foo"}
                             {:build-system  "lein",
                              :description   "",
                              :downloads     9,
                              :homepage      "https://foo",
                              :template-name "Bar"}
                             {:build-system  "lein",
                              :description   "",
                              :downloads     8,
                              :homepage      "https://foo",
                              :template-name "Baz"}]
              :hit-count    3
              :query-string ""}
             body))))

  (testing "Returns a search result when query-string is provided"
    (let [res (-> (request :get "/templates?q=Foo&from=0&size=30") (@test-handler))
          body (-> res :body t/read-transit-json)]
      (is (= 200 (:status res)))
      (is (= 1 (count (:templates body))))))

  (testing "Returns 400 bad request when bad query input is supplied"
    (let [res (-> (request :get "/templates?q=Foo&from=one") (@test-handler))]
      (is (= 400 (:status res)))
      (is (= (str "Missing parameter: size" "\n"
                  "Incorrect value for parameter \"from\": one") (:body res))))))
