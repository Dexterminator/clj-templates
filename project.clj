(defproject clj-templates "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.521"]
                 [io.aviso/pretty "0.1.33"]
                 [integrant "0.4.0"]
                 [ring "1.5.1"]
                 [compojure "1.5.2"]
                 [reagent "0.6.1"]
                 [re-frame "0.9.2"]
                 [binaryage/devtools "0.9.4"]
                 [clj-http "3.5.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [ring-logger-timbre "0.7.5"]
                 [ring/ring-defaults "0.2.3"]
                 [metosin/ring-http-response "0.8.2"]]

  :main ^:skip-aot clj-templates.core
  :target-path "target/%s"

  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["test/clj" "test/cljs"]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-npm "0.6.2"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test/js"]

  :profiles
  {:dev     {:dependencies   [[pjstadig/humane-test-output "0.8.1"]
                              [integrant/repl "0.2.0"]
                              [figwheel-sidecar "0.5.9"]
                              [com.cemerick/piggieback "0.2.1"]
                              [spyscope "0.1.5"]]
             :injections     [(require 'pjstadig.humane-test-output)
                              (pjstadig.humane-test-output/activate!)
                              (require 'spyscope.core)]
             :plugins        [[com.jakemccrary/lein-test-refresh "0.19.0"]
                              [lein-doo "0.1.7"]]
             :preloads       ['devtools.preload]
             :test-refresh   {:quiet        true
                              :changes-only true}
             :source-paths   ["dev/src"]
             :resource-paths ["dev/resources"]
             :repl-options   {:init-ns          user
                              :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
   :uberjar {:aot          :all
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
