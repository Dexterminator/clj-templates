(ns clj-templates.test-utils-frontend
  (:require [cljs.test :refer [deftest testing is]]))

(defmacro facts
  [name & body]
  (let [test-name (symbol (str "test-" (clojure.string/replace name #"\W" "-")))]
    `(deftest ~test-name (testing ~name ~@body))))

(defmacro fact
  [description & body]
  `(testing ~description ~@body))

(defmacro is=
  [x y]
  `(is (= ~x ~y)))
