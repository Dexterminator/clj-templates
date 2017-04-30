(ns clj-templates.handler-test
  (:require [integrant.core :as ig]
            [clojure.test :refer :all]
            [ring.mock.request :refer [request]]
            [clj-templates.config.main-config :refer [main-config]]
            [clj-templates.db.db :as db]
            [clj-templates.util.transit :as t]))

(def test-templates #{{:template-name "Foo" :description "" :build-system "lein"}
                      {:template-name "Bar" :description "" :build-system "lein"}
                      {:template-name "Baz" :description "" :build-system "lein"}})

(defn insert-test-templates [db]
  (doseq [template test-templates]
    (db/upsert-template db template)))

(def test-handler (atom nil))

(defn reset-system [f]
  (let [system (ig/init (dissoc main-config :logging/timbre))
        db (:db/postgres system)
        handler (:handler/main system)]
    (reset! test-handler handler)
    (insert-test-templates db)
    (f)
    (db/delete-all-templates (:db/postgres system))
    (ig/halt! system)))

(use-fixtures :each reset-system)

(deftest test-template-route
  (let [res (-> (request :get "/templates") (@test-handler))]

    (testing "Returns all templates as transit"
      (is (= 200 (:status res)))

      (is (= test-templates (-> res :body t/read-transit-json :templates set))))))
