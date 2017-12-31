(defproject clj-templates "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [io.aviso/pretty "0.1.34"]
                 [integrant "0.6.3"]
                 [ring "1.6.3"]
                 [compojure "1.6.0"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.3-alpha1"]
                 [binaryage/devtools "0.9.8"]
                 [http-kit "2.2.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [ring-logger-timbre "0.7.6"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-mock "0.3.2"]
                 [metosin/ring-http-response "0.9.0"]
                 [org.postgresql/postgresql "42.1.4"]
                 [com.layerware/hugsql "0.4.8"]
                 [migratus "1.0.1"]
                 [environ "1.1.0"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs "0.8.243"]
                 [camel-snake-kebab "0.4.0"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.7.3"]
                 [hikari-cp "2.0.0"]
                 [medley "1.0.0"]
                 [cheshire "5.8.0"]
                 [clj-time "0.14.2"]
                 [jarohen/chime "0.2.2"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [nilenso/honeysql-postgres "0.2.3"]
                 [cc.qbits/spandex "0.5.5"]
                 [cljsjs/clipboard "1.6.1-1"]]

  :min-lein-version "2.0.0"

  :main ^:skip-aot clj-templates.core
  :target-path "target/%s"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj" "test/cljs" "test/cljc"]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-npm "0.6.2"]
            [lein-environ "1.1.0"]
            [migratus-lein "0.4.4"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test/js"]

  :migratus {:store         :database
             :migration-dir "migrations"
             :db            ~(get (System/getenv) "DB_URL")}

  :profiles
  {:dev           [:project/dev :profiles/dev]
   :test          [:project/dev :profiles/test]

   :project/dev   {:dependencies   [[pjstadig/humane-test-output "0.8.3"]
                                    [integrant/repl "0.2.0"]
                                    [figwheel-sidecar "0.5.14"]
                                    [com.cemerick/piggieback "0.2.2"]
                                    [spyscope "0.1.6"]
                                    [day8.re-frame/trace "0.1.14"]]
                   :injections     [(require 'pjstadig.humane-test-output)
                                    (pjstadig.humane-test-output/activate!)
                                    (require 'spyscope.core)]
                   :plugins        [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                    [lein-doo "0.1.8"]
                                    [lein-pdo "0.1.1"]]
                   :test-refresh   {:quiet        true
                                    :changes-only true}
                   :source-paths   ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :repl-options   {:init-ns          user
                                    :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

   :profiles/dev  {}
   :profiles/test {}

   :uberjar       {:aot          :all
                   :omit-source  true
                   :uberjar-name "clj-templates.jar"
                   :prep-tasks   [["npm" "install"] ["npm" "run" "prod:stylus"] ["cljsbuild" "once" "min"] "compile"]}}

  :cljsbuild
  {:builds
   [;; "dev" build is located under :figwheel in config.dev-config

    {:id           "min"
     :source-paths ["src/cljs"]
     :jar          true
     :compiler     {:main            clj-templates.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs" "test/cljc"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :main          clj-templates.doo-runner
                    :optimizations :none}}]}


  :npm {:dependencies [[:stylus "0.54.5"]
                       [:nib "1.1.2"]]
        :package      {:scripts {:clean        "rm -rf resources/public/css && mkdir -p resources/public/css",
                                 :prod:stylus  "npm run clean && node_modules/.bin/stylus --include-css src/cljs/clj_templates/style/main.styl --out resources/public/css/style.css --compress --use ./node_modules/nib",
                                 :build:stylus "npm run clean && node_modules/.bin/stylus --include-css src/cljs/clj_templates/style/main.styl --out resources/public/css/style.css --compress --use ./node_modules/nib --sourcemap --sourcemap-inline ",
                                 :watch:stylus "npm run clean && node_modules/.bin/stylus --include-css src/cljs/clj_templates/style/main.styl --out resources/public/css/style.css --compress --use ./node_modules/nib --sourcemap --sourcemap-inline --watch"}}})
