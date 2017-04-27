.PHONY: default all remove-package-json clean npm-install stylusbuild stylusbuild-once cljsbuild uberjar clj-test clj-test-once cljs-test cljs-test-once

default: uberjar
all: uberjar

remove-package-json:
	-rm -f package.json

clean:
	lein clean

npm-install: remove-package-json
	lein npm install

stylusbuild: clean npm-install
	lein npm run watch:stylus

stylusbuild-once: clean npm-install
	lein npm run build:stylus

cljsbuild:
	lein cljsbuild once

uberjar: remove-package-json
	lein uberjar

clj-test: clean
	lein test-refresh

clj-test-once: clean
	lein test

cljs-test: clean
	lein doo phantom test

cljs-test-once: clean
	lein doo phantom test once
