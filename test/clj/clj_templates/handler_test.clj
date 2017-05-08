(ns clj-templates.handler-test
  (:require [integrant.core :as ig]
            [clojure.test :refer :all]
            [ring.mock.request :refer [request]]
            [clj-templates.config.main-config :refer [main-config]]
            [clj-templates.db.db :as db]
            [clj-templates.util.transit :as t]))

(def lein-test-templates #{{:template-name "Foo" :description "" :build-system "lein" :github-url "https://foo" :github-id nil :github-stars nil :github-readme nil}
                           {:template-name "Bar" :description "" :build-system "lein" :github-url "https://foo" :github-id nil :github-stars nil :github-readme nil}
                           {:template-name "Baz" :description "" :build-system "lein" :github-url "https://foo" :github-id nil :github-stars nil :github-readme nil}})

(def boot-test-templates #{{:template-name "Foo" :description "" :build-system "boot" :github-url "https://foo" :github-id nil :github-stars nil :github-readme nil}
                           {:template-name "Boo" :description "" :build-system "boot" :github-url "https://foo" :github-id nil :github-stars nil :github-readme nil}})

(defn insert-test-templates [db]
  (doseq [template (clojure.set/union lein-test-templates boot-test-templates)]
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
  (let [lein-res (-> (request :get "/templates?build-system=lein") (@test-handler))
        boot-res (-> (request :get "/templates?build-system=boot") (@test-handler))]

    (testing "Returns templates for build system as transit"
      (is (= 200 (:status lein-res)))
      (is (= lein-test-templates (-> lein-res :body t/read-transit-json :templates set)))

      (is (= 200 (:status boot-res)))
      (is (= boot-test-templates (-> boot-res :body t/read-transit-json :templates set))))))
