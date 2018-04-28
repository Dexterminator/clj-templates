(ns clj-templates.util.events
  (:require [re-frame.core :refer [reg-event-fx subscribe trim-v debug ->interceptor get-effect get-coeffect]]
            [cljs.spec.alpha :as s]
            [clj-templates.util.dev :as dev]
            [clj-templates.specs.frontend-db :as db]))

(defn listen
  [query-v]
  @(subscribe query-v))

(defn check-spec
  [a-spec db event]
  (when-not (nil? db)
    (when-let [problems (::s/problems (s/explain-data a-spec db))]
      (js/console.group "Spec errors after event: " event)
      (doseq [{:keys [pred val via in path]} problems]
        (js/console.error "Spec check failed")
        (js/console.log "val: " val)
        (js/console.log "in: " in)
        (js/console.log "failed spec: " (last via))
        (js/console.log "on predicate: " pred)
        (js/console.log "path: " path))
      (js/console.groupEnd))))

(def check-spec-interceptor
  (->interceptor
    :id :check-spec
    :after (fn check-spec-after [context]
             (let [db (get-effect context :db :not-found)
                   event (get-coeffect context :event)]
               (when-not (= :not-found db)
                 (check-spec ::db/db db event))
               context))))

(def standard-interceptors [(when dev/debug? [check-spec-interceptor]) trim-v])

(defn reg-event
  ([event handler]
   (reg-event event nil handler))
  ([event interceptors handler]
   (reg-event-fx event [standard-interceptors interceptors] handler)))
