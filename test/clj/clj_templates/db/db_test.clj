(ns clj-templates.db.db-test
  (:require [clojure.test :refer :all]
            [clj-templates.clojars-feed :refer [extract-templates-from-gzip-stream]]
            [clj-templates.db.db :as db]
            [clojure.java.io :as io]
            [integrant.core :as ig]
            [integrant.repl.state :refer [system]]
            [config.dev-config :refer [dev-config]]
            [clj-templates.config.main-config :refer [main-config]]))

(def test-config {:server/jetty {:handler (ig/ref :handler/main)
                                 :db      (ig/ref :db/postgres)
                                 :opts    {:port 3457 :join? false}}})

(defn setup-test-config []
  (-> (merge main-config test-config)
      (dissoc :logging/timbre)))

(def test-db (atom nil))

(defn system-fixture [f]
  (let [system (ig/init (setup-test-config))]
    (reset! test-db (:db/postgres system))
    (f)
    (db/delete-all-templates (:db/postgres system))
    (ig/halt! system)))

(use-fixtures :each system-fixture)

(deftest test-template-table
  (let [template {:template-name "Foo" :description "Bar" :build-system "lein"}]

    (testing "Upserting a record affects a row"
      (is (= (db/upsert-template @test-db template)
             1)))

    (testing "Getting all templates returns the inserted template"
      (is (= (db/all-templates @test-db)
             [template])))

    (let [changed-template (assoc template :description "Baz")]

      (testing "Upserting again does not result in an error"
        (is (= (db/upsert-template @test-db changed-template)
               1)))

      (testing "Getting all templates again returns the updated template"
        (is (= (db/all-templates @test-db)
               [changed-template]))))))
