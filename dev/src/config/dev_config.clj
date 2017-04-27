(ns config.dev-config)

(def dev-config
  {:figwheel {:figwheel-options {:css-dirs ["resources/public/css"]}
              :build-ids        ["dev"]
              :all-builds       [{:id           "dev"
                                  :figwheel     {:on-jsload "clj-templates.core/mount-root"}
                                  :source-paths ["src/cljs"]
                                  :compiler     {:main                 "clj-templates.core"
                                                 :output-to            "resources/public/js/compiled/app.js"
                                                 :output-dir           "resources/public/js/compiled/out"
                                                 :asset-path           "js/compiled/out"
                                                 :source-map-timestamp true}}]}
   :pretty/exceptions {}
   :logging/timbre {:fname "logs/dev.log"}})
