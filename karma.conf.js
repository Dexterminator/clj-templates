module.exports = function (config) {
    var root = 'resources/public/js/compiled/test'; // same as :output-dir

    config.set({
        frameworks: ['cljs-test'],

        browsers: ['ChromeHeadlessNoSandbox'],

        customLaunchers: {
            ChromeHeadlessNoSandbox: {
                base: 'ChromeHeadless',
                flags: ['--disable-gpu', '--no-sandbox']
            }
        },

        files: [
            root + '/goog/base.js',
            root + '/cljs_deps.js',
            root + '/test.js', // same as :output-to
            {pattern: root + '/*.js', included: false},
            {pattern: root + '/**/*.js', included: false}
        ],

        autoWatchBatchDelay: 500,

        client: {
            // main function
            args: ['clj_templates.karma_runner.run_tests']
        }
    })
};