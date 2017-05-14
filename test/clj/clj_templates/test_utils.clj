(ns clj-templates.test-utils)

(defn add-default-vals [template]
  (merge template {:github-id     nil
                   :github-stars  nil
                   :github-readme nil
                   :homepage      "https://foo"
                   :downloads     10}))

(def example-templates [(add-default-vals {:template-name "Foo" :description "" :build-system "lein" :github-url "https://foo"})
                        (add-default-vals {:template-name "Bar" :description "" :build-system "lein" :github-url "https://foo"})
                        (add-default-vals {:template-name "Baz" :description "" :build-system "lein" :github-url "https://foo"})])
