(ns clj-templates.util.transit
  (:require [cognitect.transit :as transit])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)))

(defn transit-json [v]
  (let [out (ByteArrayOutputStream. 4096)
        _ (-> out
              (transit/writer :json)
              (transit/write v))
        res (.toString out)]
    (.reset out)
    res))

(defn read-transit-json [s]
  (-> s
      (.getBytes "UTF-8")
      (ByteArrayInputStream.)
      (transit/reader :json)
      transit/read))
