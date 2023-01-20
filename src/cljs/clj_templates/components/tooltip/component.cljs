(ns clj-templates.components.tooltip.component)

(defn adjust [elem]
  (let [x (.-x (.getBoundingClientRect elem))]
    (when (neg? x)
      (set! (-> elem .-style .-left) "auto")
      (set! (-> elem .-style .-right) "0")
      (set! (-> elem .-style .-transform) (str "translateX(" (- x) "px)")))))

(defn tooltip
  ([trigger-element tooltip-content]
   [tooltip {} trigger-element tooltip-content])
  ([attrs trigger-element tooltip-content]
   [:div.tooltip attrs
    trigger-element
    [:div.tooltip-content {:ref adjust} tooltip-content]]))
