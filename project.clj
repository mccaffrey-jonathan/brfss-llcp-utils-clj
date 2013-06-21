(defproject cljsbuild-example-simple "0.3.2"
  :description "A simple example of how to use lein-cljsbuild"
  :source-paths ["src-clj"]
  :dependencies [[bytebuffer "0.2.0"]
                 [compojure "1.2.0-SNAPSHOT"]
                 [hiccup "1.0.3"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.2"]
                 [ring "1.1.8"]
                 [ring.middleware.logger "0.4.0"]
                 [jayq "2.3.0"] ]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-exec "0.3.0"]
            [lein-ring "0.8.5"]]
  :cljsbuild {
    :crossovers [brfssllcp.datamodel]

    :builds [{:source-paths ["src-cljs"]
              ; :incremental false
              :compiler {:output-to "resources/public/js/main.js"
                         :optimizations :whitespace
                         :pretty-print true}}]}
  :ring {:handler example.routes/app
         :auto-reload? true
         :auto-refresh? true
         :stacktraces? true})
