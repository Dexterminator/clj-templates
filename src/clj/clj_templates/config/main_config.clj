(ns clj-templates.config.main-config
  (:require [integrant.core :as ig]
            [environ.core :refer [env]]))

(def main-config
  {:handler/main        {:db (ig/ref :db/postgres)}
   :server/jetty        {:handler (ig/ref :handler/main)
                         :opts    {:port 3000 :join? false}}
   :db/postgres         {:jdbc-url          (env :database-url)
                         :driver-class-name "org.postgresql.Driver"}
   :logger/timbre       {:appenders {:println {:stream :auto}}}
   :jobs/scheduled-jobs {:hours-between-jobs 1
                         :db                 (ig/ref :db/postgres)
                         :es-client          (ig/ref :search/elastic)}
   :search/elastic      {:hosts           [(env :elastic-url)]
                         :default-headers {:content-type "application/json"}}})
