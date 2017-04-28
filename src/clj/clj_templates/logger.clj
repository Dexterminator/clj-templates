(ns clj-templates.logger
  (:require [integrant.core :as ig]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

(defmethod ig/init-key :logger/timbre [_ {:keys [appenders]}]
  (println (str "Initializing logging appenders: " (keys appenders)))
  (timbre/merge-config! {:appenders {:println (when-let [opts (:println appenders)] (appenders/println-appender opts))
                                     :spit    (when-let [opts (:spit appenders)] (appenders/spit-appender opts))}}))
