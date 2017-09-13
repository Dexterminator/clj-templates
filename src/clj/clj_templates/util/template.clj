(ns clj-templates.util.template)

(defn abbreviate-raw [{:keys [group-id artifact-id]}]
  (str "\"" group-id " (" artifact-id ")\""))

(defn abbreviate [{:keys [template-name build-system]}]
  (str "\"" template-name " (" build-system ")\""))
