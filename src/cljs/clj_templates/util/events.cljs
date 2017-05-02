(ns clj-templates.util.events
  (:require [re-frame.core :refer [reg-event-fx subscribe trim-v debug]]))

(defn listen
  [query-v]
  @(subscribe query-v))

(def standard-interceptors [debug trim-v])

(defn reg-event
  ([event handler]
   (reg-event event nil handler))
  ([event interceptors handler]
   (reg-event-fx event [interceptors standard-interceptors] handler)))
