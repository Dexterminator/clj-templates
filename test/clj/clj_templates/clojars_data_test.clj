(ns clj-templates.clojars-data-test
  (:require [clojure.test :refer [use-fixtures]]
            [clj-templates.test-utils :refer [facts fact is= instrument-test]]
            [clj-templates.core :refer :all]
            [clj-templates.clojars-data :as clojars]
            [clojure.java.io :as io]))

(use-fixtures :each instrument-test)

(facts "extract-templates-from-gzip-stream"
  (fact "reads the gzip stream and returns maps with template artifacts"
    (is= [{:group-id    "ajom",
           :artifact-id "lein-template",
           :description "atom plugins in clojurescript",
           :scm         {:tag "HEAD", :url "https://github.com/dvcrn/ajom"},
           :homepage    "https://github.com/dvcrn/ajom",
           :url         "https://github.com/dvcrn/ajom",
           :versions    ["0.3.2" "0.3.1" "0.3.0" "0.2.0" "0.1.1" "0.1.0"]}
          {:group-id    "capstan",
           :artifact-id "lein-template",
           :description "generate a Capstan clojure project skeleton",
           :scm         {:connection           "scm:git:git://github.com/tzach/capstan-lein-plugin.git",
                         :developer-connection "scm:git:ssh://git@github.com/tzach/capstan-lein-plugin.git",
                         :tag                  "4bbeea5fcbe8bd7a1a71996f98a5d94a2d3a3ec7",
                         :url                  "https://github.com/tzach/capstan-lein-plugin"},
           :homepage    "https://github.com/tzach/capstan-lein-plugin",
           :url         "https://github.com/tzach/capstan-lein-plugin",
           :versions    ["0.1.0"]}
          {:group-id    "nrepl-figwheel-node",
           :artifact-id "lein-template",
           :description "DEPRECATED: Leiningen template for Figwheel on nREPL and Node.js",
           :scm         {:tag "HEAD", :url ""},
           :homepage    "",
           :url         "",
           :versions    ["0.1.6" "0.1.5" "0.1.4" "0.1.3" "0.1.2" "0.1.1-SNAPSHOT" "0.1.0"]}
          {:group-id    "fw1",
           :artifact-id "boot-template",
           :description "FW/1 template for Boot new",
           :scm         {:tag "c8449a35cde2b162e5c8d47fb4369b2db8482dd5", :url "https://github.com/framework-one/fw1-template/"},
           :homepage    "https://github.com/framework-one/fw1-template/",
           :url         "https://github.com/framework-one/fw1-template/",
           :versions    ["0.8.0" "0.5.2" "0.5.1" "0.5.0"]}
          {:group-id    "provisdom-clj",
           :artifact-id "boot-template",
           :description "The provisdom boot-new template",
           :scm         {:tag "c7cf590021ebff82e63b4f721ff1d9ebd29b5be5", :url "https://github.com/Provisdom/clj-boot-template"},
           :homepage    "https://github.com/Provisdom/clj-boot-template",
           :url         "https://github.com/Provisdom/clj-boot-template",
           :versions    ["0.2.4" "0.2.3" "0.2.2" "0.2.1" "0.2.0" "0.2.0-SNAPSHOT" "0.1.1" "0.1.1-SNAPSHOT" "0.1.0"]}]
         (clojars/extract-templates-from-gzip-stream (io/input-stream "dev/resources/test_feed.clj.gz")))))

(facts "adapt-template-to-db"
  (fact "extracts and renames correct keys and values for storage in the db"
    (is= {:template-name "ajom"
          :description   "atom plugins in clojurescript"
          :build-system  "lein"
          :github-url    "https://github.com/dvcrn/ajom"
          :github-id     "dvcrn/ajom"
          :github-stars  nil
          :github-readme nil
          :homepage      "https://github.com/dvcrn/ajom"
          :downloads     1}
         (clojars/adapt-template-to-db {:group-id    "ajom",
                                        :artifact-id "lein-template",
                                        :description "atom plugins in clojurescript",
                                        :scm         {:tag "HEAD", :url "https://github.com/dvcrn/ajom"},
                                        :homepage    "https://github.com/dvcrn/ajom",
                                        :downloads   1
                                        :url         "https://github.com/dvcrn/ajom",
                                        :versions    ["0.3.2" "0.3.1" "0.3.0" "0.2.0" "0.1.1" "0.1.0"]}))

    (is= {:template-name "fw1"
          :description   "FW/1 template for Boot new"
          :build-system  "boot"
          :github-url    "https://github.com/framework-one/fw1-template"
          :github-id     "framework-one/fw1-template"
          :github-stars  nil
          :github-readme nil
          :homepage      "https://github.com/framework-one/fw1-template"
          :downloads     1}
         (clojars/adapt-template-to-db {:group-id    "fw1",
                                        :artifact-id "boot-template",
                                        :description "FW/1 template for Boot new",
                                        :scm         {:tag "c8449a35cde2b162e5c8d47fb4369b2db8482dd5", :url "https://github.com/framework-one/fw1-template/"},
                                        :homepage    "https://github.com/framework-one/fw1-template",
                                        :url         "https://github.com/framework-one/fw1-template",
                                        :downloads   1
                                        :versions    ["0.8.0" "0.5.2" "0.5.1" "0.5.0"]}))))

(facts "fix-homepage"
  (let [template {:template-name "fw1"
                  :description   "FW/1 template for Boot new"
                  :build-system  "boot"
                  :github-url    "https://github.com/framework-one/fw1-template"
                  :github-id     "framework-one/fw1-template"
                  :github-stars  nil
                  :github-readme nil
                  :homepage      "https://github.com/framework-one/fw1-template"
                  :downloads     1}]

    (fact "does nothing if homepage exists"
      (is= template
           (clojars/fix-homepage template)))

    (fact "sets homepage to github url if it exists"
      (is= template
           (clojars/fix-homepage (assoc template :homepage nil))))

    (fact "removes homepage if homepage is placeholder value"
      (is= (assoc template :homepage nil
                           :github-url nil)
           (clojars/fix-homepage (assoc template :homepage "http://example.com/FIXME"
                                                 :github-url nil))))))
