(ns pretty-dev
  (:require [integrant.core :as ig]
            [io.aviso.repl :as pretty]))

(defmethod ig/init-key :pretty/exceptions [_ _]
  (pretty/install-pretty-exceptions))
