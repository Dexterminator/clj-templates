(ns clj-templates.components.tooltip.component)

(defn tooltip
  ([trigger-element tooltip-content]
   [tooltip {} trigger-element tooltip-content])
  ([attrs trigger-element tooltip-content]
   [:div.tooltip attrs
    trigger-element
    [:div.tooltip-content tooltip-content]]))
