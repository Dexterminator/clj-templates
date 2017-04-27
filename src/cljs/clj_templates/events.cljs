(ns clj-templates.events
  (:require [re-frame.core :refer [reg-event-fx]]))

(reg-event-fx
  :initialize-db
  (fn [_]
    {:db {:test "test"}}))
