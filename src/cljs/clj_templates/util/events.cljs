(ns clj-templates.util.events
  (:require [re-frame.core :refer [subscribe]]))

(defn listen
  [query-v]
  @(subscribe query-v))
