(ns live-node.server.bootstrap
  (:require [live-node.server.core :refer [start-server!]]))

;; This file bootstraps the server. We put it in a separate directory since
;; we don't want to include it in our tests as it will stomp on the main
;; function.

(defn ^:export main []
  ;; Fixes exception strack traces with source maps
  (-> "source-map-support" js/require .install)
  (start-server! {}))

(set! *main-cli-fn* main)
