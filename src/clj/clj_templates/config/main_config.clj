(ns clj-templates.config.main-config
  (:require [integrant.core :as ig]
            [environ.core :refer [env]]))

(def main-config
  {:handler/main {:db (ig/ref :db/postgres)}
   :server/jetty {:handler (ig/ref :handler/main)
                  :opts    {:port 3000 :join? false}}
   :db/postgres  (env :database-url)
   :logger/timbre {:appenders {:println {:stream :auto}}}})
