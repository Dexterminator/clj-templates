(ns clj-templates.util.api
  (:require [ajax.core :as ajax]
            [re-frame.core :refer [dispatch reg-fx]]))

(def ajax-opts {:response-format :transit
                :keywords?       true})

(defn GET [route on-response-event params]
  (ajax/GET route (merge ajax-opts {:handler       #(dispatch [on-response-event %])
                                    :error-handler #(js/log %)
                                    :params        params})))

(defmulti api-call :endpoint)

(defmethod api-call :templates [{:keys [on-response-event params]}]
  (GET "/templates" on-response-event params))
