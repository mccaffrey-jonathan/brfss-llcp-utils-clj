(ns example.hello
  (:require [clojure.string :as string]))

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
  []
  (-> js/d3
    (.-scale)
    (.linear)
    (.domain
      (clj->js [0 100]))))

(def histogram-margins
  (clj->js {:top 10 :right 10 :bottom 30 :left 50}))

(defn print-dimension-min-max
  [tag dim]
  (console-log-clj
    {:tag tag
     :min (aget (aget (.bottom dim 1) 0) tag)
     :max (aget (aget (.top dim 1) 0) tag)}))

(defn make-age-histogram
  [anchor-tag chart-group age-dim age-group-count age-group-all-count]
  (print-dimension-min-max "AGE" age-dim)
  (doto (.barChart js/dc anchor-tag chart-group)
    (.dimension age-dim)
    (.group age-group-count)
    (.valueAccessor (count-to-percentage-fn age-group-all-count))
    (.width 1000)
    (.height 300)
    (.margins histogram-margins)
    (.x (-> js/d3 (.-scale) (.linear) (.domain
                                        (clj->js [-1 100]))))
    (.y (percentage-scale))
    (.centerBar true)
    (.title #(str (.-value %) " years old"))))

(defn make-income-histogram
  [anchor-tag chart-group income-dim income-group-count income-group-all-count]
  (print-dimension-min-max "INCOME2" income-dim)
  (doto (.barChart js/dc anchor-tag chart-group)
    (.dimension income-dim)
    (.group income-group-count)
    (.valueAccessor (count-to-percentage-fn income-group-all-count))
    (.width 1000)
    (.height 300)
    (.round (fn [x]
              (if (< x 0)
                x
                (-> x (/ 10) int (* 10)))))
    (.margins histogram-margins)
    (.x (-> js/d3 (.-scale) (.linear) (.domain
                                        (clj->js [-1 100]))))
    (.y (percentage-scale))
    ; TODO finish
    (.centerBar true)
  ))

(defn make-basic-pie
  [anchor-tag chart-group dim group]
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
    (.renderTitle true)))

(def make-race-pie make-basic-pie)
(def make-employment-pie make-basic-pie)
(def make-education-pie make-basic-pie)

(defn dim-count
  [ndx dim-fn]
  (let [dim (.dimension ndx dim-fn)]
    [dim
     (-> dim
       (.group)
       (.reduceCount))]))

; TODO!  Group-all should not ignore all filters!
(defn dim-count-all-count
  [ndx dim-fn]
  (let [[dim & _ :as inner-vec] (dim-count ndx dim-fn)]
    (conj inner-vec (-> dim
                      (.groupAll)
                      (.reduceCount)))))

; TODO factory function for dim/group/group-all creation
(defn setup-crossfilter
  [data]
  (let [ndx (js/crossfilter data)
        [age-dim age-group-count age-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "AGE")))
        [income-dim income-group-count income-group-all-count]
        (dim-count-all-count ndx (fn [d] (aget d "INCOME2")))
        [race-dim race-group-count]
        (dim-count ndx (fn [d] (aget d "RACE")))
      ;  [education-dim education-group-count]
      ;  (dim-count ndx (fn [d] (aget d "EDUCA")))
        [employment-dim employment-group-count]
        (dim-count ndx (fn [d] (aget d "EMPLOY")))
        ]
    (make-age-histogram "#age-histogram" brfss-llcp-chart-group
      age-dim age-group-count age-group-all-count)
    (make-income-histogram "#income-histogram" brfss-llcp-chart-group
      income-dim income-group-count income-group-all-count)
    (make-race-pie "#race-pie" brfss-llcp-chart-group
      race-dim race-group-count)
;    (make-education-pie "#education-pie" brfss-llcp-chart-group
;      education-dim education-group-count)
    (make-employment-pie "#employment-pie" brfss-llcp-chart-group
      employment-dim employment-group-count)
    (.renderAll js/dc brfss-llcp-chart-group)
    (console-log "Done with crossfilter.")
    ))

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
