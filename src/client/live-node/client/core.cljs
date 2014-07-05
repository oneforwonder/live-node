(ns live-node.client.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as tdom :include-macros true]
            [cljs.reader :as cr]
            [goog.net.XhrIo :as XhrIo])
  (:require-macros [schema.macros :refer [defschema]]))

(def app-state (atom {:features []
                      :init 100}))

(defn log [expr]
  (.log js/console expr)
  expr)

(defn change-count [owner f n]
  (om/set-state! owner :n (f n))
  )

(defcomponent widget [data owner]
  (will-mount [_]
    (om/set-state! owner :n (:init data)))
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


;; Initial shizz
(defn row-major->columns
  "Given coll, representing a matrix in row-major order, and col-count,
  indicating the number of columns in the matrix, produces a sequence where
  each ith item of the sequence represents the ith column of matrix data. "
  [coll col-count]
  (->> coll
       (map vector (range (count coll)))
       (group-by (fn [[k _]] (mod k col-count)))
       (map (fn [[k v]] [k (map second v)]))
       (sort-by first)
       (map (fn [[k v]] v))))

(defn feature-view [{:keys [title description]} owner]
  (reify
    om/IRender
    (render [this]
            (dom/div nil
                     (dom/h4 nil title)
                     (dom/p nil description)))))

(defn feature-column-view [col-count column owner]
  (reify
    om/IRender
    (render [this]
            (apply dom/div #js {:className (str "col-lg-" (int (/ 12 col-count)))}
                   (om/build-all feature-view column)))))

(defn feature-list-view [{:keys [features]} owner]
  (reify
    om/IRender
    (render [this]
            (let [col-count 2
                  feature-count (count features)
                  columns (row-major->columns features col-count)]
              (apply dom/div #js {:className "row marketing"}
                     (om/build-all (partial feature-column-view col-count)
                                   columns))))))

(defn load-features! []
  (XhrIo/send "/api/features"
              (fn [event]
                (let [xhr (.-target event)]
                  (if (.isSuccess xhr)
                    (swap! app-state assoc :features (js->clj (.getResponseJson xhr)
                                                              :keywordize-keys true))
                    (.log js/console "Could not load features."))))))                                   
(defn insert-root-component! [target]
  (om/root widget ;feature-list-view
           @app-state
           {:target target}))
