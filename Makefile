.PHONY: default all remove-package-json clean npm-install stylusbuild stylusbuild-once cljsbuild uberjar
.PHONY: clj-test clj-test-once cljs-test cljs-test-once ci auto-dev

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

cljs-test: clean npm-install cljsbuild
	lein pdo karma start, cljsbuild auto

cljs-test-once: clean npm-install cljsbuild
	lein karma start --single-run

auto-dev: clean remove-package-json npm-install cljsbuild
	lein pdo karma start, test-refresh, cljsbuild auto, npm run watch:stylus

ci: clj-test-once cljs-test-once
