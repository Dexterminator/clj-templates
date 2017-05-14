(ns clj-templates.util.transit
  (:require [cognitect.transit :as transit]
            [clojure.spec :as s])
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

(s/fdef transit-json
        :args (s/cat :v any?)
        :ret string?)

(s/fdef read-transit-json
        :args (s/cat :s string?)
        :ret any?)
