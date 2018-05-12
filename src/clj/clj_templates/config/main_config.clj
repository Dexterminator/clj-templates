(ns clj-templates.config.main-config
  (:require [integrant.core :as ig]
            [environ.core :refer [env]]
            [clojure.string :as str])
  (:import (java.net URI)))

(defn get-jdbc-url []
  (let [uri (URI. (env :database-url))
        [name pwd] (str/split (.getUserInfo uri) #":")]
    (str "jdbc:postgresql://" (.getHost uri) (.getPath uri) "?user=" name "&password=" pwd)))

(def main-config
  {:handler/main        {:es-client (ig/ref :search/elastic)}
   :server/jetty        {:handler (ig/ref :handler/main)
                         :opts    {:join? false}}
   :db/postgres         {:jdbc-url          (get-jdbc-url)
                         :driver-class-name "org.postgresql.Driver"}
   :logger/timbre       {:appenders {:println {:stream :auto}}}
   :jobs/scheduled-jobs {:hours-between-jobs 8
                         :db                 (ig/ref :db/postgres)
                         :es-client          (ig/ref :search/elastic)}
   :search/elastic      {:hosts    [(env :bonsai-url)]
                         :user     (env :bonsai-user)
                         :password (env :bonsai-password)}})
