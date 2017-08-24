(ns clj-templates.db.db-test
  (:require [clojure.test :refer [use-fixtures]]
            [clj-templates.test-utils :refer [facts fact is= instrument-test test-config]]
            [clj-templates.clojars-data :refer [extract-templates-from-gzip-stream]]
            [clj-templates.db.db :as db]
            [integrant.core :as ig]
            [integrant.repl.state :refer [system]]
            [config.dev-config :refer [dev-config]]))

(def test-db (atom nil))

(defn clear-tables [f]
  (let [system (ig/init (select-keys test-config [:db/postgres]))]
    (reset! test-db (:db/postgres system))
    (f)
    (db/delete-all-templates (:db/postgres system))
    (ig/halt! system)))

(use-fixtures :each clear-tables instrument-test)

(facts "template-table"
  (let [template {:template-name "Foo" :description "Bar" :build-system "lein" :github-url "https://github.com/Dexterminator/clj-templates"
                  :github-id     "Dexterminator/clj-templates" :github-stars nil :github-readme nil :homepage nil :downloads nil}
        changed-template (assoc template :description "Baz")]

    (fact "Upserting a record affects a row"
      (is= 1 (db/upsert-template @test-db template)))

    (fact "Getting all templates returns the inserted template"
      (is= [template] (db/all-templates @test-db)))

    (fact "Upserting again does not result in an error"
      (is= 1 (db/upsert-template @test-db changed-template)))

    (fact "Getting all templates again returns the updated template"
      (is= [changed-template] (db/all-templates @test-db)))))
