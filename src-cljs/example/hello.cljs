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

(defn setup-crossfilter
  [data]
  (let [ndx (js/crossfilter data)
        age-dim (.dimension ndx (fn [d] (aget d "AGE")))]
    (console-log (.top age-dim 10))
    (console-log (.bottom age-dim 10))))

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
