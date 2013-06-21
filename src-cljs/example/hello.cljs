(ns example.hello
  (:require [clojure.string :as string]
            [brfssllcp.datamodel :as dm])
  (:use [jayq.core :only [$ css inner]]))

(def file-list
  ["_bmi5-sint"
   "_state-sbyte"
   "addepev2-sbyte"
   "age-sbyte"
   "alcday5-sshort"
   "chccopd-sbyte"
   "chcocncr-sbyte"
   "chcscncr-sbyte"
   "cvdcrhd4-sbyte"
   "cvdinfr4-sbyte"
   "diabete3-sbyte"
   "educa-sbyte"
   "employ-sbyte"
   "exeroft1-sshort"
   "genhlth-sbyte"
   "havarth3-sbyte"
   "income2-sbyte"
   "orace2-sbyte"
   "smokday2-sbyte"])

; TODO get gzip working
(def gz-file-list
  ["_bmi5-sint.gz"
   "_state-sbyte.gz"
   "addepev2-sbyte.gz"
   "age-sbyte.gz"
   "alcday5-sshort.gz"
   "chccopd-sbyte.gz"
   "chcocncr-sbyte.gz"
   "chcscncr-sbyte.gz"
   "cvdcrhd4-sbyte.gz"
   "cvdinfr4-sbyte.gz"
   "diabete3-sbyte.gz"
   "educa-sbyte.gz"
   "employ-sbyte.gz"
   "exeroft1-sshort.gz"
   "genhlth-sbyte.gz"
   "havarth3-sbyte.gz"
   "income2-sbyte.gz"
   "orace2-sbyte.gz"
   "smokday2-sbyte.gz"])

(defn console-log
  [& ss]
  (doseq [s ss] (.log js/console s)))

(defn console-log-one
  [s]
  (console-log s)
  s)

(console-log "loading hello.cljs")

(defn console-log-clj
  [& ss]
  (doseq [s ss] 
    (->> s
      (clj->js)
      (.log js/console))))

(defn split-file-name-to-kw-buffer
  [file-name]
  (let [[_ s-kw s-type] (.exec #"([a-zA-Z0-9_]*)-([a-zA-Z]*)" file-name)]
    [(-> s-kw
       string/upper-case
       keyword)
     (case s-type
       "sbyte" #(js/Int8Array. %)
       "sshort" #(js/Int16Array. %)
       "sint" #(js/Int32Array. %))]))

(defn keyword-typed-array-pair
  [file-name array-buffer]
  (let [[kw array-factory] (split-file-name-to-kw-buffer file-name)]
    [kw (array-factory array-buffer)]))

(defn files-and-buffers-to-map-of-typed-arrays
  [file-list array-buffers]
  (into {} (map keyword-typed-array-pair file-list array-buffers)))

(defn package-array-buffers-to-aos
  [file-list array-buffers]
  (let [map-o-arrs (files-and-buffers-to-map-of-typed-arrays
                     file-list
                     array-buffers)]
    (-> map-o-arrs
      keys
      clj->js
      console-log)
    (for [idx (-> map-o-arrs
                first 
                (nth 1)
                (.-length) 
                range)]
      (into {}
            (for [[kw arr] map-o-arrs]
              [kw (aget arr idx)])))))

(def brfss-llcp-chart-group "brfss-llcp-group")

(defn count-to-percentage-fn
  [group-all]
  (fn [d]
    (-> d
      (.-value)
      (* 100)
      (/ (.value group-all)))))

(defn percentage-scale
  ([]
   (percentage-scale 100))
  ([top]
   (-> js/d3
     (.-scale)
     (.linear)
     (.domain
       (clj->js [0 top])))))

(def histogram-margins
  (clj->js {:top 10 :right 10 :bottom 30 :left 50}))

(defn print-dimension-min-max
  [tag dim]
  (console-log-clj
    {:tag tag
     :min (aget (aget (.bottom dim 1) 0) tag)
     :max (aget (aget (.top dim 1) 0) tag)}))

(defn update-count
  [anchor-tag group-all]
  (fn [_]
    (-> (str anchor-tag" > .valid-count")
      $
      (inner (.value group-all)))))

(defn make-age-histogram
  [anchor-tag chart-group age-dim age-group-count age-group-all-count]
  (print-dimension-min-max "AGE" age-dim)
  (doto (.barChart js/dc anchor-tag chart-group)
    (.dimension age-dim)
    (.group age-group-count)
    (.valueAccessor (count-to-percentage-fn age-group-all-count))
    (.width 1000)
    (.height 150)
    (.margins histogram-margins)
    (.x (-> js/d3 (.-scale) (.linear) (.domain
                                        (clj->js [-1 100]))))
    (.y (percentage-scale 50))
    (.centerBar true)
    (.title #(str (.-value %) " years old"))
    (.on "preRender" (update-count anchor-tag age-group-all-count)))

(defn make-income-histogram
  [anchor-tag chart-group income-dim income-group-count income-group-all-count]
  (print-dimension-min-max "INCOME2" income-dim)
  (doto (.barChart js/dc anchor-tag chart-group)
    (.dimension income-dim)
    (.group income-group-count)
    (.valueAccessor (count-to-percentage-fn income-group-all-count))
    (.width 1000)
    (.height 150)
    (.margins histogram-margins)
    ; TODO stretch higher if needed...
    (.x (-> js/d3 (.-scale) (.linear) (.domain
                                        (clj->js [-1 100]))))
    (.y (percentage-scale 50))
    ; TODO finish
    (.title (fn [d]
              (console-log (dm/int-encoding-to-str :INCOME2 (.-key d)))
              (dm/int-encoding-to-str :INCOME2 (.-key d))
              ))
    (.renderTitle true)
    (.centerBar true)
    (.on "preRender" (update-count anchor-tag income-group-all-count)))))

(defn make-bmi-histogram
  [anchor-tag chart-group bmi-dim bmi-group-count bmi-group-all-count]
  (print-dimension-min-max "_BMI5" bmi-dim)
  (doto (.barChart js/dc anchor-tag chart-group)
    (.dimension bmi-dim)
    (.group bmi-group-count)
    (.valueAccessor (count-to-percentage-fn bmi-group-all-count))
    (.width 1000)
    (.height 150)
    (.margins histogram-margins)
    (.x (-> js/d3 (.-scale) (.linear) (.domain
                                        (clj->js [-1 100]))))
    (.y (percentage-scale 50))
    ; TODO finish
    (.centerBar true)
    (.on "preRender" (update-count anchor-tag bmi-group-all-count))))

; TODO chronic conditions chart; draw at work

(defn make-basic-pie
  [anchor-tag chart-group dim group group-all]
  (doto (.pieChart js/dc anchor-tag chart-group)
    (.width 200)
    (.height 200)
    (.radius 90)
    (.innerRadius 40)
    (.dimension dim)
    (.group group)
    ; TODO pretty-print
    (.label #(-> %1 (.-data) (.-key)))
    (.renderLabel true)
    ; TODO pretty-print
    (.title #(-> %1 (.-data) (.-key)))
    (.renderTitle true)
    (.on "preRender" (update-count anchor-tag group-all))))

(defn str-for-pie-slice
  [kw]
  #(->> %
     (.-data)
     (.-key)
     (dm/int-encoding-to-str kw)))
 
(defn make-kw-labeled-pie
  [kw]
  (comp 
    (fn [pie]
      (let [slice-to-str-via-kw (str-for-pie-slice kw)]
        (doto pie
          (.label slice-to-str-via-kw)
          (.title #(str (slice-to-str-via-kw %1) 
                        " (" (-> %1 (.-data) (.-key)) ") "
                        ": " (-> %1 (.-data) (.-value)))))))
    make-basic-pie))

(def make-race-pie (make-kw-labeled-pie :ORACE2))
(def make-employment-pie (make-kw-labeled-pie :EMPLOY))
(def make-education-pie (make-kw-labeled-pie :EDUCA))

(defn reduceValidCount
  [dim-fn]
  (fn [grp]
    (.reduce grp
             ; add
             (fn [p v]
               (if (= dm/blank-num (dim-fn v))
                 p
                 (inc p)))
             ; remove
             (fn [p v]
               (if (= dm/blank-num (dim-fn v))
                 p
                 (dec p)))
             ; initial
             (fn [] 0))))

(defn dim-count
  [ndx dim-fn]
  (let [dim (.dimension ndx dim-fn)]
    [dim
     (-> dim
       (.group)
       ((reduceValidCount dim-fn) ))]))

; TODO!  Group-all should not ignore all filters!
(defn dim-count-all-count
  [ndx dim-fn]
  (let [[dim & _ :as inner-vec] (dim-count ndx dim-fn)]
    (conj inner-vec (-> dim
                      (.groupAll)
                      ((reduceValidCount dim-fn) )))))

(defn make-data-count
  [anchor-tag chart-group ndx group]
  (doto (.dataCount js/dc anchor-tag chart-group)
    (.dimension ndx)
    (.group group)))

; TODO factory function for dim/group/group-all creation
(defn setup-crossfilter
  [data]
  (let [ndx (js/crossfilter data)
        all (.groupAll ndx)
        [age-dim age-group-count age-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "AGE")))
        [income-dim income-group-count income-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "INCOME2")))
        [bmi-dim bmi-group-count bmi-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "_BMI5")))
        [race-dim race-group-count race-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "ORACE2")))
        [education-dim education-group-count education-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "EDUCA")))
        [employment-dim employment-group-count employment-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "EMPLOY")))
        [exercise-dim exercise-group-count exercise-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "EXEROFT1")))
        [alcohol-dim alcohol-group-count alcohol-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "ALCDAY5")))
        [smoking-dim smoking-group-count smoking-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "SMOKDAY2")))
        ;
        ]

    ; TODO make this less like code and more like data
    (make-age-histogram "#age-histogram" brfss-llcp-chart-group
      age-dim age-group-count age-group-all-count)
    (make-income-histogram "#income-histogram" brfss-llcp-chart-group
      income-dim income-group-count income-group-all-count)
    (make-bmi-histogram "#bmi-histogram" brfss-llcp-chart-group
      bmi-dim bmi-group-count bmi-group-all-count)

    (make-race-pie "#race-pie" brfss-llcp-chart-group
      race-dim race-group-count race-group-all-count)
    (make-education-pie "#education-pie" brfss-llcp-chart-group
      education-dim education-group-count education-group-all-count)
    (make-employment-pie "#employment-pie" brfss-llcp-chart-group
      employment-dim employment-group-count employment-group-all-count)

    ((make-kw-labeled-pie :EXEROFT1) "#exercise-pie" brfss-llcp-chart-group
      race-dim race-group-count race-group-all-count)
    ((make-kw-labeled-pie :ALCDAY5) "#alcohol-pie" brfss-llcp-chart-group
      education-dim education-group-count education-group-all-count)
    ((make-kw-labeled-pie :SMOKDAY2) "#smoking-pie" brfss-llcp-chart-group
      employment-dim employment-group-count employment-group-all-count)

    (make-data-count "#data-count" brfss-llcp-chart-group ndx all)
    (.renderAll js/dc brfss-llcp-chart-group)
    (console-log "Done with crossfilter.")
    ))

(console-log "About to try loading a bunch of data")

(.map js/async
      (clj->js file-list)
      (fn [file-name cb]
        (let [xhr2 (js/XMLHttpRequest. )]
          (.open xhr2 "GET" file-name true)
          (set! (.-responseType xhr2) "arraybuffer")
          (set! (.-onload xhr2)
                (fn [e]
                  (this-as this
                           (cb nil(.-response this)))))
          (.send xhr2)))
      (fn [err res]
        (setup-crossfilter
          (clj->js
            (package-array-buffers-to-aos file-list res)))))

(console-log "At end of hello.cljs")

; TODO: add per-chart counts and filters to drop missing data!
