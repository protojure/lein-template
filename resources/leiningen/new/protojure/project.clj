(defproject {{name}} "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"
            :year 2019
            :key "apache-2.0"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [io.pedestal/pedestal.service "0.5.8"]

                 ;; -- PROTOC-GEN-CLOJURE --
                 [protojure "1.6.0"]
                 [protojure/google.protobuf "0.9.1"]

                 ;; -- PROTOC_GEN_CLOJURE CLIENT DEPS --
                 [org.eclipse.jetty.http2/http2-client "11.0.1"]
                 [org.eclipse.jetty/jetty-alpn-java-client "11.0.1"]
                 ;; -- Jetty Client Dep --
                 [org.ow2.asm/asm "9.1"]

                 ;; Include Undertow for supporting HTTP/2 for GRPCs
                 [io.undertow/undertow-core "2.2.5.Final"]
                 [io.undertow/undertow-servlet "2.2.5.Final"]
                 ;; And of course, protobufs
                 [com.google.protobuf/protobuf-java "3.15.6"]
                 ;; logging
                 [com.taoensso/timbre "5.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.30"]
                 [org.slf4j/jcl-over-slf4j "1.7.30"]
                 [org.slf4j/log4j-over-slf4j "1.7.30"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "{{name}}.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.8"]]}
             :uberjar {:aot [{{name}}.server]}}
  :main ^{:skip-aot true} {{name}}.server)
