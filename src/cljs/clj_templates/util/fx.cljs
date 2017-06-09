(ns clj-templates.util.fx
  (:require [re-frame.core :refer [reg-fx]]
            [clj-templates.util.api :as api]))

(reg-fx
  :scroll-to-top
  #(js/window.scrollTo 0 0))

(reg-fx
  :api-call
  api/api-call)
