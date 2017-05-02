(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch]]
            [clj-templates.util.events :refer [reg-event]]))

(reg-event
  :templates/page-entered
  (fn [_ _]
    (js/log "Templates page entered")))
