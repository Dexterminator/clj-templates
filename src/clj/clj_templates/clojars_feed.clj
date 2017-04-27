(ns clj-templates.clojars-feed
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.util.zip GZIPInputStream)
           (java.io PushbackReader InputStreamReader)))

(def edn-opts {:eof :eof})

(defn gzip-seq [file-name]
  (let [in (-> (io/input-stream file-name)
               (GZIPInputStream.)
               (InputStreamReader.)
               (PushbackReader.))]
    (take-while #(not= :eof %) (repeatedly #(edn/read edn-opts in)))))

(defn extract-templates-from-gzip [file-name]
  (filter (fn [{:keys [artifact-id]}] (#{"lein-template" "boot-template"} artifact-id))
          (gzip-seq file-name)))
