(ns clj-templates.util.fx
  (:require [re-frame.core :refer [reg-fx]]
            [clj-templates.util.api :as api]))

(reg-fx
  :api-call
  api/api-call)
