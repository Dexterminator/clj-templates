(ns clj-templates.util.api
  (:require [ajax.core :as ajax]
            [re-frame.core :refer [dispatch reg-fx]]))

(def ajax-opts {:response-format :transit
                :keywords?       true})

(defn GET [route on-response-event params]
  "Performs a GET request to a route with specified query params. The handler for on-response-event is
  to have the signature [cofx [data error?]]"
  (ajax/GET route (merge ajax-opts {:handler       #(dispatch [on-response-event % false])
                                    :error-handler #(dispatch [on-response-event % true])
                                    :params        params})))

(defmulti api-call :endpoint)

(defmethod api-call :templates [{:keys [on-response-event params]}]
  (GET "/templates" on-response-event params))
