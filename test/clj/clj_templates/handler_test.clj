(ns clj-templates.handler-test
  (:require [integrant.core :as ig]
            [clojure.test :refer :all]
            [ring.mock.request :refer [request]]
            [clj-templates.config.main-config :refer [main-config]]
            [clj-templates.db.db :as db]
            [clj-templates.util.transit :as t]))

(defn add-default-vals [template]
  (merge template {:github-id     nil
                   :github-stars  nil
                   :github-readme nil
                   :homepage      nil
                   :downloads     nil}))

(def templates #{(add-default-vals {:template-name "Foo" :description "" :build-system "lein" :github-url "https://foo"})
                 (add-default-vals {:template-name "Bar" :description "" :build-system "lein" :github-url "https://foo"})
                 (add-default-vals {:template-name "Baz" :description "" :build-system "lein" :github-url "https://foo"})})

(defn insert-test-templates [db]
  (doseq [template templates]
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

    (testing "Returns templates as transit"
      (is (= 200 (:status res)))
      (is (= templates (-> res :body t/read-transit-json :templates set))))))
