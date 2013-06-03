(ns example.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]))

(defn index-page []
  (html5
    [:head
     [:title "Hello World!"]
     ; (include-js "/js/d3.min.js")
     ; (include-js "/js/crossfilter.min.js")
     ; (include-js "/js/dc.min.js")
     (include-js "/js/d3.js")
     (include-js "/js/crossfilter.js")
     (include-js "/js/dc.js")
     (include-js "/js/jquery-1.7.1.js")
     (include-js "/js/async.js")
     (include-js "/js/main.js")]
    [:body
     [:h1 "caliente Hello World"]
     [:div {:id "age-histogram"}
      [:span "Age"
       ;; TODO look at all that filter business
; <!-- A div anchor that can be identified by id -->
; <div id="your-chart">
;     <!-- Title or anything you want to add above the chart -->
;     <span>Days by Gain or Loss</span>
;     <!--
;         if a link with css class "reset" is present then the chart
;         will automatically turn it on/off based on whether there is filter
;         set on this chart (slice selection for pie chart and brush
;         selection for bar chart)
;      -->
;     <a class="reset" href="javascript:gainOrLossChart.filterAll();dc.redrawAll();" style="display: none;">reset</a>
;     <!--
;         dc.js will also automatically inject applied current filter value into
;         any html element with css class set to "filter"
;     -->
;     <span class="reset" style="display: none;">Current filter: <span class="filter"></span></span>
; </div>

            ]]

      [:div {:id "income-histogram"}
       [:span "Household Income"]]
      [:div {:id "bmi-histogram"}
       [:span "Body Mass Index (BMI)"]]

     ; Demographic Pies
     [:div {:display "inline-block"}
      [:div {:id "race-pie"}
       [:span "Race"]]
      [:div {:id "employment-pie"}
       [:span "Employment Status"]]
      [:div {:id "education-pie"}
       [:span "Educational Attainment"]]
      ]

     ]))

