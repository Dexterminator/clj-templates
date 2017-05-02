(ns clj-templates.pages.templates.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :refer [dispatch reg-event-fx]]))

(reg-event-fx
  :templates/page-entered
  (fn [_ _]
    (js/log "Templates page entered")))
