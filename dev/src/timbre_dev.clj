(ns timbre-dev
  (:require [integrant.core :as ig]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

(defmethod ig/init-key :logging/timbre [_ {:keys [fname]}]
  (timbre/merge-config! {:appenders {:spit (appenders/spit-appender {:fname fname})
                                     :println nil}}))
