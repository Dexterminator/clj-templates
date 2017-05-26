(defproject clj-templates "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.521"]
                 [io.aviso/pretty "0.1.33"]
                 [integrant "0.4.0"]
                 [ring "1.6.0"]
                 [compojure "1.6.0"]
                 [reagent "0.6.1"]
                 [re-frame "0.9.2"]
                 [binaryage/devtools "0.9.4"]
                 [http-kit "2.2.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [ring-logger-timbre "0.7.5"]
                 [ring/ring-defaults "0.3.0"]
                 [ring/ring-mock "0.3.0"]
                 [metosin/ring-http-response "0.8.2"]
                 [org.postgresql/postgresql "42.0.0"]
                 [com.layerware/hugsql "0.4.7"]
                 [migratus "0.9.2"]
                 [environ "1.1.0"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [camel-snake-kebab "0.4.0"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.5.9"]
                 [hikari-cp "1.7.5"]
                 [medley "1.0.0"]
                 [cheshire "5.7.1"]
                 [clj-time "0.13.0"]
                 [jarohen/chime "0.2.1" :exclusions [[org.clojure/core.async]]]
                 [org.clojure/java.jdbc "0.6.2-alpha3"]
                 [nilenso/honeysql-postgres "0.2.2"]
                 [cc.qbits/spandex "0.3.10"]]

  :main ^:skip-aot clj-templates.core
  :target-path "target/%s"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj" "test/cljs"]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-npm "0.6.2"]
            [lein-environ "1.1.0"]
            [migratus-lein "0.4.4"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test/js"]

  :migratus {:store         :database
             :migration-dir "migrations"
             :db            ~(get (System/getenv) "DATABASE_URL")}

  :profiles
  {:dev           [:project/dev :profiles/dev]
   :test          [:project/dev :profiles/test]

   :project/dev   {:dependencies   [[pjstadig/humane-test-output "0.8.1"]
                                    [integrant/repl "0.2.0"]
                                    [figwheel-sidecar "0.5.10"]
                                    [com.cemerick/piggieback "0.2.1"]
                                    [spyscope "0.1.6"]]
                   :injections     [(require 'pjstadig.humane-test-output)
                                    (pjstadig.humane-test-output/activate!)
                                    (require 'spyscope.core)]
                   :plugins        [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                    [lein-doo "0.1.7"]]
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
                   :prep-tasks   [["npm" "run" "prod:stylus"] ["cljsbuild" "once" "min"] "compile"]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :compiler     {:main                 clj-templates.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}
    {:id           "min"
     :source-paths ["src/cljs"]
     :jar          true
     :compiler     {:main            clj-templates.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :main          clj-templates.doo-runner
                    :optimizations :none}}]}


  :npm {:dependencies [[:stylus "0.54.5"]
                       [:nib "1.1.2"]]
        :package      {:scripts {:clean        "rm -rf resources/public/css && mkdir -p resources/public/css",
                                 :prod:stylus  "npm run clean && node_modules/.bin/stylus --include-css src/cljs/clj_templates/style/main.styl --out resources/public/css/style.css --compress --use ./node_modules/nib",
                                 :build:stylus "npm run clean && node_modules/.bin/stylus --include-css src/cljs/clj_templates/style/main.styl --out resources/public/css/style.css --compress --use ./node_modules/nib --sourcemap --sourcemap-inline ",
                                 :watch:stylus "npm run clean && node_modules/.bin/stylus --include-css src/cljs/clj_templates/style/main.styl --out resources/public/css/style.css --compress --use ./node_modules/nib --sourcemap --sourcemap-inline --watch"}}})
