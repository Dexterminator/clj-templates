(ns clj-templates.handler-test
  (:require [integrant.core :as ig]
            [clojure.test :refer :all]
            [ring.mock.request :refer [request]]
            [clj-templates.config.main-config :refer [main-config]]
            [clj-templates.db.db :as db]
            [clj-templates.util.transit :as t]
            [clj-templates.test-utils :refer [example-templates instrument-test test-config]]))

(defn insert-test-templates [db]
  (doseq [template example-templates]
    (db/upsert-template db template)))

(def test-handler (atom nil))

(defn reset-system [f]
  (let [system (ig/init test-config)
        db (:db/postgres system)
        handler (:handler/main system)]
    (reset! test-handler handler)
    (insert-test-templates db)
    (f)
    (db/delete-all-templates (:db/postgres system))
    (ig/halt! system)))

(use-fixtures :each reset-system instrument-test)

(deftest test-template-route
  (let [res (-> (request :get "/templates") (@test-handler))]

    (testing "Returns templates as transit"
      (is (= 200 (:status res)))
      (is (= (set example-templates) (-> res :body t/read-transit-json :templates set))))))
