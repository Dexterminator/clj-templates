# clj-templates

A website for finding Leiningen and Boot templates.

## Development

In order to launch the app in dev mode, setting up a server on `localhost:3456`, run:

```
lein repl
=> (go)
```

You should now see:

```
=> :initiated
```

This means that the app is up and running, and that figwheel is listening to changes in your ClojureScript files.



To start a ClojureScript REPL, run:
```
=> (cljs-repl)
```
To go back to the regular Clojure REPL, type:
```
=> :cljs/quit
```

To restart the system and reload changed namespaces (in the regular Clojure REPL), run:
```
=> (reset)
```

Check out the [Integrant](https://github.com/weavejester/integrant) README as well as
[Integrant-REPL](https://github.com/weavejester/integrant-repl) for more information on how to
interact with the system in development mode.

### Build CSS

In order to start the stylus autobuilder, run:
```
make stylusbuild
```


### Run tests:

#### Clojure

Autorun (tests run as changes to files are detected):
```
make clj-test
```

Once:
```
make clj-test-once
```

#### ClojureScript
Make sure [phantomjs](http://phantomjs.org/) is installed.

Autorun (tests run as changes to files are detected):
```
make cljs-test
```
Once: 
```
make cljs-test-once
```

## Production Build

Run:
```
make
```

See the `Procfile` for the production run command.
