(ns config.dev-config
  (:require [integrant.core :as ig]))

(def dev-config
  {:figwheel          {:figwheel-options {:css-dirs ["resources/public/css"]}
                       :build-ids        ["dev"]
                       :all-builds       [{:id           "dev"
                                           :figwheel     {:on-jsload "clj-templates.core/mount-root"}
                                           :source-paths ["src/cljs" "src/cljc"]
                                           :compiler     {:main                 "clj-templates.core"
                                                          :output-to            "resources/public/js/compiled/app.js"
                                                          :output-dir           "resources/public/js/compiled/out"
                                                          :asset-path           "js/compiled/out"
                                                          :source-map-timestamp true
                                                          :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                                                          :preloads             '[day8.re-frame-10x.preload
                                                                                  devtools.preload]}}]}
   :server/jetty      {:handler (ig/ref :handler/main)
                       :opts    {:port 3456 :join? false}}
   :pretty/exceptions {}
   :logger/timbre     {:appenders {:spit {:fname "logs/dev.log"}}}})
