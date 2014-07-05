(ns live-node.client.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as tdom :include-macros true]
            [ajax.core :refer [GET POST]]
            [cljs.reader :as cr]
            [goog.net.XhrIo :as XhrIo])
  (:require-macros [schema.macros :refer [defschema]]))

(def app-state (atom {:features []
                      :count 100}))

(defn log [expr]
  (.log js/console expr)
  expr)

(defn change-count [owner f n]
  (om/set-state! owner :n (f n))
  )

(defcomponent widget [data owner]
  (will-mount [_]
    (om/set-state! owner :n (:count data)))
  (render-state [_ {:keys [n]}]
    (tdom/div
      (tdom/span (str "Count: " n))
      (tdom/h1 (:text data))
      (tdom/button
        {:on-click #(change-count owner inc n)}
        "+")
      (tdom/button
        {:on-click #(change-count owner dec n)}
        "-")
      (tdom/button
        {:on-click #(change-count owner (fn [a] (* a a)) n)}
        "^2")
      (tdom/button
        {:on-click #(change-count owner (fn [a] (* a 0)) n)}
        "Reset"))))

(defn load-features! []
  (XhrIo/send "/api/get-count"
              (fn [event]
                (let [xhr (.-target event)]
                  (if (.isSuccess xhr)
                    (do 
                      (log "success!")
                    (swap! app-state assoc :count (.getResponseJson xhr))
                      )
                    (.log js/console "Could not load features.")))))) 

(defn insert-root-component! [target]
  (om/root widget 
           app-state
           {:target target}))
