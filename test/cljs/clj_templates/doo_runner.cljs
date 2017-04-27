(ns clj-templates.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [clj-templates.core-test]))

(doo-tests 'clj-templates.core-test)


