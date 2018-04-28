(ns clj-templates.components.error-boundary.component
  (:require [reagent.core :as r]))

(defn error-boundary [component]
  (let [error (r/atom nil)]
    (r/create-class
      {:component-did-catch (fn [this info e]
                              (reset! error e))
       :reagent-render      (fn [component]
                              (if @error
                                [:div.error
                                 [:div.error-message "Something went wrong :("]]
                                component))})))
