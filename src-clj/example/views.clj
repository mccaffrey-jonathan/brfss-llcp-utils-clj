(ns example.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]))

(defn index-page []
  (html5
    [:head
     [:title "Hello World!"]
     (include-js "/js/d3.min.js")
     (include-js "/js/crossfilter.min.js")
     (include-js "/js/dc.min.js")
     (include-js "/js/jquery-1.7.1.js")
     (include-js "/js/async.js")
     (include-js "/js/main.js")]
    [:body
     [:h1 "caliente Hello World"]]))
