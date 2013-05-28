(ns example.routes
  (:use compojure.core
        example.views
        [hiccup.middleware :only (wrap-base-url)]
        [ring.middleware reload]
        [ring.middleware file file-info])
  (:require 
    [compojure.route :as route]
    [compojure.handler :as handler]
    [compojure.response :as response]
    [ring.middleware.logger :as logger]))

(defroutes main-routes
  ; (GET "/" [{:header {"Content-Type" }}] (index-page))
  (GET "/" [] (index-page))
  (route/resources "/")
  (route/not-found "Page not found"))

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

; Probably not the right way to do this but oh well
(defn wrap-gz-with-content-encoding
  [handler]
  (fn [request]
    (let [response (handler request)
          uri (request :uri)]
      (if (and (<= (count "gz") (count uri))
               (= "gz" (subs uri (- (count uri) (count "gz")))))
        (-> response
          ; TODO figure out write content-length
          (update-in [:headers] assoc
                     "Content-Encoding" "gzip"
                     "Content-Length" "1337"
                     "Hit-GZ" "yes"))
          ;(update-in [:headers] dissoc "Content-Length"))
        response))))

(defn wrap-log-uri-and-headers
  [handler]
  (fn [request]
    (do-let [response (handler request)]
      (println (request :uri) " got headers " (response :headers)))))

(def app
  (-> (handler/site main-routes)
    (wrap-reload '(example.routes example.views))
    (wrap-file "raw-out")
    (wrap-file-info)
    ; (wrap-gz-with-content-encoding)
    (logger/wrap-with-logger)
    (wrap-base-url)
    (wrap-log-uri-and-headers)))
