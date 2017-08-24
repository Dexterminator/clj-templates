(ns clj-templates.pages.main-test
  (:require [clj-templates.test-utils-frontend :refer-macros [facts fact is=]]
            [clj-templates.pages.main.core :as main]
            [pjstadig.humane-test-output]))

(facts "page-entered-handler"
  (fact "updates active page and dispatches event"
    (is= {:db       {:active-page :templates}
          :dispatch [:templates/search "" 1]}
         (main/page-entered-handler {:db {:active-page nil}} [:templates])))

  (fact "only updates active page when there is no registered event"
    (is= {:db {:active-page :about}}
         (main/page-entered-handler {:db {:active-page nil}} [:about]))))
