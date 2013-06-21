(ns example.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js include-css]]]))

(def filter-span
  [:span {:class "reset"}
          "Current filter: " [:span {:class "filter"}]])

(def count-span
  [:span [:span {:class "valid-count"}] " valid records"])

(defn index-page []
  (html5
    [:head
     [:title "Hello World!"]
     ; (include-js "/js/d3.min.js")
     ; (include-js "/js/crossfilter.min.js")
     ; (include-js "/js/dc.min.js")
     (include-css "/css/dc.css")

     (include-js "/js/d3.js")
     (include-js "/js/crossfilter.js")
     (include-js "/js/dc.js")
     (include-js "/js/jquery-1.7.1.js")
     (include-js "/js/async.js")
     (include-js "/js/main.js")]
    [:body
     [:h1 "caliente Hello World"]
     [:div
      [:div {:id "age-histogram"}
       [:div "Age"
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
       [:div "Household Income"]]
      [:div {:id "bmi-histogram"}
       [:div "Body Mass Index (BMI)"]]]

     ; Demographic Pies
     [:div ; div to stack the rows of pie charts
      [:div {:style "display: inline-block"}
       [:div {:id "race-pie"}
        [:div "Race"] 
        count-span ]
       [:div {:id "employment-pie"}
        [:div "Employment Status"] 
        count-span ]
       [:div {:id "education-pie"}
        [:div "Educational Attainment"]
        count-span ] ]

      ; Lifestyle pies
      [:div {:style "display: inline-block"}
       [:div {:id "exercise-pie"}
        [:div "Primary Exercise Frequency"]
        count-span ]
       [:div {:id "alcohol-pie"}
        [:div "Alcohol Consumption"] 
        count-span ]
       [:div {:id "smoking-pie"}
        [:div "Smoking Frequency"] 
        count-span ] ] ]

     ; Data count
     [:div {:id "data-count"}
      [:span {:class "filter-count"}]
       " selected out of "
      [:span {:class "total-count"}]
      " | "
      [:a {:href "javascript:dc.filterAll(\"brfss-llcp-group\"); dc.renderAll(\"brfss-llcp-group\");"}
       "Reset All"]]
     ]))

