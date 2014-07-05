(ns live-node.util)

(defn ->json [obj]
  (js/JSON.stringify (clj->js obj)))
