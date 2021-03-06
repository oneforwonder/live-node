(defproject live-node "0.0.0-SNAPSHOT"
  :description "live-node"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [om "0.6.4"]
                 [prismatic/om-tools "0.2.2"]
                 [prismatic/schema "0.2.4"]
                 [cljs-ajax "0.2.6"]
                 [specljs "2.9.1"]]

  :plugins [[lein-cljsbuild "1.0.1"]
            [specljs "2.9.1"]
            [lein-pprint "1.1.1"]]

  :profiles {:production
             {:cljsbuild
              {:builds
               {:client {:compiler {:optimizations :advanced
                                    :pretty-print false
                                    :externs ["app/bower_components/react/react.js"]}}
                :client-test {:compiler {:optimizations :advanced
                                         :pretty-print false
                                         :externs ["app/bower_components/react/react.js"]}}}}}}

  :cljsbuild {:builds {:server
                       {:source-paths ["src/common" "src/server" "src/server-bootstrap"]
                        :compiler {:output-to "target/server/main.js"
                                   :target :nodejs
                                   :output-dir "target/server/out"
                                   :source-map "target/server/main.js.map"
                                   ; pending: http://dev.clojure.org/jira/browse/CLJS-743
                                   ;:language-in :ecmascript5
                                   :optimizations :simple}}
                       :server-test
                       {:source-paths ["src/common" "src/server" "test/server"]
                        :notify-command ["node" "target/test/server/main.js"]
                        :compiler {:output-to "target/test/server/main.js"
                                   :target :nodejs
                                   :output-dir "target/test/server/out"
                                   :source-map "target/test/server/main.js.map"
                                   :optimizations :simple}}
                       :client
                       {:source-paths ["src/common" "src/client" "src/client-bootstrap"]
                        :compiler {:output-to "target/public/scripts/main.js"
                                   :output-dir "target/public/scripts/out"
                                   :source-map "target/public/scripts/main.js.map"
                                   :optimizations :whitespace}}
                       :client-test
                       {:source-paths ["src/common" "src/client" "test/client"]
                        :notify-command ["./node_modules/.bin/phantomjs"
                                         "scripts/phantomjs-specljs-runner.js"
                                         "scripts/phantomjs-shims.js"
                                         "app/bower_components/react/react.js"
                                         "target/test/client/main.js"]
                        :compiler {:output-to "target/test/client/main.js"
                                   :output-dir "target/test/client/out"
                                   :source-map "target/test/client/main.js.map"
                                   :optimizations :whitespace}}}})
