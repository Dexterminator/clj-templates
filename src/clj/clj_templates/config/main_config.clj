(ns clj-templates.config.main-config
  (:require [integrant.core :as ig]
            [environ.core :refer [env]]))

(def main-config
  {:handler/main        {:es-client (ig/ref :search/elastic)}
   :server/jetty        {:handler (ig/ref :handler/main)
                         :opts    {:join? false}}
   :db/postgres         {:jdbc-url          (env :db-url)
                         :driver-class-name "org.postgresql.Driver"}
   :logger/timbre       {:appenders {:println {:stream :auto}}}
   :jobs/scheduled-jobs {:hours-between-jobs 2
                         :db                 (ig/ref :db/postgres)
                         :es-client          (ig/ref :search/elastic)}
   :search/elastic      {:hosts    [(env :bonsai-url)]
                         :user     (env :bonsai-user)
                         :password (env :bonsai-password)}})
