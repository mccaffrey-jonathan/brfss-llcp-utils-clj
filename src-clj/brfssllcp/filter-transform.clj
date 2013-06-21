(ns brfss-llcp.filter-transform
  (:require [clojure.set :as set]
            [clojure.pprint :as pp]
            [brfssllcp.datamodel :as model])
  (:use 
    clojure.java.io
    clojure.string
    [clojure.tools.cli :only [cli]]
    [bytebuffer.buff :as bbb]))

(import 'java.io.File)
(import 'java.io.FileOutputStream)

(defmacro ?
  [val]
  `(let [x# ~val]
     (println '~val " is " x#)
     x#))

(defmacro do-let
  [[binding-form init-expr] & body]
  `(let [~binding-form ~init-expr]
     ~@body
     ~binding-form))

(defn buff-to-relevant-fields-numeric-records
  [buff]
  (into {}
        (for [[k v] relevant-fields
              :let [extractor (record-extraction-map k)
                    field (trim (apply str (for [idx 
                                                 (range (extractor :starting-col)
                                                        (+ (extractor :starting-col)
                                                           (extractor :field-len)))]
                                             (aget buff idx))))]]
          [k (if (= field "")
               blank-num
               (try (parse-numeric field)
                 (catch NumberFormatException nfe blank-num)))])))

(defn parse-int
  [s]
  (Integer/parseInt s))
;  (try (Integer/parseInt s)
;    (catch NumberFormatException nfe nil)))

; BMIs are supposed to be coded with a 4 fixed pointer number with 2 implicit
; decimal places.  Some are miscoded with floating point instead, and the actual
; magnitude.  If < 100, assume it's mis-coded
(defn parse-bmi
  [s]
  (let [f (-> s
            (.replace " " ".")
            (Float/parseFloat))]
    (if (< 100 f) (int (* f 100)) f)))

(defn parse-numeric
  [s]
  (try (Integer/parseInt s)
    ; If at least some of s is numeric, try to parse as BMI, which may be a FP
    ; else just bail and pass up the NFE
    (catch NumberFormatException nfe
      (if (some #(Character/isDigit %) s) (parse-bmi s) (throw nfe)))))

; Some activity frequencies (which use the hundred's place as a code)
; are mis-formatted with blanks instead of a 0 between the hundreds and ones
; place
; Replace the " " with 0's
(defn parse-int-weird-chars-to-zeroes
  [s]
  (-> s
    (.replace " " "0")
    parse-int))

(def numeric-types
  [{:min Byte/MIN_VALUE
    :max Byte/MAX_VALUE
    :name "sbyte" :fn bbb/put-byte :sz 1}
   {:min Short/MIN_VALUE
    :max Short/MAX_VALUE
    :name "sshort" :fn bbb/put-short :sz 2}
   {:min Integer/MIN_VALUE
    :max Integer/MAX_VALUE
    :name "sint" :fn bbb/put-int :sz 4}])

(def parsed (cli *command-line-args*))

(def record-len 1695)

(defn buff-seq
  [rdr]
  (let [buff (char-array record-len)]
    (if (= record-len (.read rdr buff 0 record-len))
      (cons buff (lazy-seq (buff-seq rdr))) [])))

(defn find-smallest-containing-type
  [{mn :min mx :max}]
  (some (fn [{tmin :min tmax :max :as tp}]
          (if (and (<= mx tmax) (>= mn tmin)) tp nil))
        numeric-types))

(defn file-name-for-field
  [prefix kw out-type]
  (str prefix
       (-> kw
         name
         clojure.string/lower-case)
       "-"
       (out-type :name)))

; These functions are used for coalescing the code position tables into clojure
(defn chunk-seq-inner
  [xs]
  (let [next-3 (take 3 xs)
        [starting-col var-name field-len] next-3]
    (if (= 3 (count next-3))
      (cons [(keyword var-name) {:starting-col (- (Integer/parseInt starting-col) 1)
                                 :field-len (Integer/parseInt field-len)}]
            (lazy-seq (chunk-seq-inner (drop 3 xs))))
      [])))

(defn chunk-seq
  [xs]
  (chunk-seq-inner (drop 3 xs)))

(defn open-bbs-for-fields
  [numeric-seq out-types]
  (let [cnt (count numeric-seq)]
    (into {}
          (for [[k v] out-types]
            [k (bbb/byte-buffer (* (get-in out-types [k :sz]) cnt))]))))

(defn copy-numeric-seq-to-bbs
  [numeric-seq out-types]
  (do-let [bbs (open-bbs-for-fields numeric-seq out-types)]
          (doseq [rec numeric-seq [k v] rec]
            ((get-in out-types [k :fn]) (bbs k) v))
          (doseq [[k bb] bbs]
            (.flip bb))))

(defn write-out-bbs-for-fields
  [out-types bbs]
     (doseq [[k bb] bbs]
     (-> (file-name-for-field "raw-out/" k (out-types k))
       (File.)
       (FileOutputStream.)
       (.getChannel)
       (.write bb))))

(with-open [rdr (clojure.java.io/reader (get-in parsed [1 1]))]
  (let [numeric-seq (->> rdr
                      buff-seq
                      (take 10000)
                      (map buff-to-relevant-fields-numeric-records))
        out-types (map-vals find-smallest-containing-type
                            (extremal-values numeric-seq))
        bbs (copy-numeric-seq-to-bbs numeric-seq out-types)]
    (write-out-bbs-for-fields out-types bbs)
  ;  (pp/pprint extremes)
    ))

; ; Chunk up a list of col positions/codes/lengths into clojure table
; (with-open [rdr (clojure.java.io/reader (get-in parsed [1 1]))]
;   (pp/pprint
;     (into {} (chunk-seq (line-seq rdr)))))
