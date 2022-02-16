(ns {{sanitized}}.service-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [clojure.core.async :refer [<!! >!! <! >! go go-loop] :as async]
            [promesa.core :as p]
            [io.pedestal.http :as pedestal]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor :refer [interceptor]]
            [io.pedestal.interceptor.chain :refer [terminate]]
            [protojure.pedestal.routes :as pedestal.routes]
            [protojure.pedestal.core :as protojure.pedestal]
            [protojure.grpc.client.providers.http2 :as grpc.http2]
            [com.example.addressbook.Greeter.server :as greeter]
            [com.example.addressbook.Greeter.client :as greeter-client]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]
            [taoensso.timbre.tools.logging :refer [use-timbre]])
  (:import [java.nio ByteBuffer])
  (:refer-clojure :exclude [resolve]))

(log/set-config! {:level :error
                  :ns-whitelist ["protojure.*"]
                  :appenders {:println (appenders/println-appender {:stream :auto})}})

(use-timbre)

(deftype Greeter []
  greeter/Service
  (Hello
    [this { {:keys [name]} :grpc-params :as request}]
    {:status 200
     :body {:message (str "Hello, " name)}}))

(def test-env (atom {}))

(defn create-service []
  (let [port (let [socket (java.net.ServerSocket. 0)]
               (.close socket)
               (.getLocalPort socket))
        interceptors [(body-params/body-params)
                      pedestal/html-body]
        server-params {:env                      :prod
                       ::pedestal/routes         (into #{} (concat interceptors
                                                                   (pedestal.routes/->tablesyntax {:rpc-metadata greeter/rpc-metadata
                                                                                                               :interceptors interceptors
                                                                                                               :callback-context (Greeter.)})))
                       ::pedestal/port           port

                       ::pedestal/type           protojure.pedestal/config
                       ::pedestal/chain-provider protojure.pedestal/provider}
        client-params {:port port :idle-timeout -1}]
    (let [server (-> (pedestal/create-server server-params)
                   (pedestal/start))]
      (swap! test-env assoc :port port :server server))))

(defn destroy-service []
  (swap! test-env update :server pedestal/stop))

(defn wrap-service [test-fn]
  (create-service)
  (test-fn)
  (destroy-service))

(use-fixtures :each wrap-service)

(deftest grpc-check
  (testing "Check that a simple grpc call works end to end"
    (is (= "Hello, John Doe"
           (:message @(greeter-client/Hello @(grpc.http2/connect {:uri (str "http://localhost:" (:port @test-env))}) {:name "John Doe"}))))))
