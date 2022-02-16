;; Copyright © 2019 State Street Bank and Trust Company.  All rights reserved
;;
;; SPDX-License-Identifier: Apache-2.0ns leiningen.new.protojure

(ns leiningen.new.protojure
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "protojure"))

(defn protojure
  "FIXME: write documentation"
  [name]
  (let [sanitized (name-to-path name)
        data {:name name
              :sanitized sanitized}]
    (main/info "Generating fresh 'lein new' protojure project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             ["Makefile" (render "Makefile" data)]
             ["README.md" (render "README.md" data)]
             ["src/{{sanitized}}/server.clj" (render "server.clj" data)]
             ["src/{{sanitized}}/service.clj" (render "service.clj" data)]
             ["test/{{sanitized}}/service_test.clj" (render "service_test.clj" data)]
             ["resources/addressbook.proto" (render "addressbook.proto" data)])))
