(ns clj-templates.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [clj-templates.pages.main-test]))

(doo-tests 'clj-templates.pages.main-test)


