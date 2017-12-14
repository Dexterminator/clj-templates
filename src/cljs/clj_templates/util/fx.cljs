(ns clj-templates.util.fx
  (:require [re-frame.core :refer [reg-fx] :as rf]
            [clj-templates.util.api :as api]))

(defonce timeouts (atom {}))

;; Identical api to re-frame's :dispatch-later, but with the added key :id.
;; Dispatches each event after a delay of :ms, unless another :dispatch-debounce with the same id
;; has been issued within the delay, in which case the delay resets.
(reg-fx
  :dispatch-debounce
  (fn [value]
    (doseq [{:keys [id ms dispatch]} value]
      (js/clearTimeout (@timeouts id))
      (swap! timeouts assoc id
             (js/setTimeout (fn []
                              (rf/dispatch dispatch)
                              (swap! timeouts dissoc id))
                            ms)))))

(reg-fx
  :api-call
  api/api-call)
