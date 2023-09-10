(ns backend.core
  (:require [ring-jetty.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [content-type response]]
            [cheshire.core :refer [generate-string]]
            [reitit.ring :as ring]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [backend.utils :as u]))

(defn ping-handler [request]
  (-> {:message (str "Hello "
                     (:name (:params request) "world")
                     "! You've successfully accessed the API!")
       :at      (u/now)}
      generate-string
      response
      (content-type "application/json")))

(defn hepatitis-handler [_]
  (let [url "https://gist.githubusercontent.com/waseem-medhat/c5b5583f9423cf5f4fccf089395aa7f0/raw/hep_c.csv"]
    (with-open [reader (io/reader url)]
      (doall
       (-> reader
           csv/read-csv
           u/csv-data->maps
           (#(take 5 %))
           generate-string
           response
           (content-type "application/json"))))))

(def router
  (ring/router [["/ping" {:get #(ping-handler %)}]
                ["/hepatitis" {:get #(hepatitis-handler %)}]]))

(def app (-> #((ring/ring-handler router) %)
             wrap-keyword-params
             wrap-params))

(defonce server (atom nil))

(defn -main []
  (reset! server
          (run-jetty #'app {:port 8080
                            :join? false})))

#_(-main)
#_(do (.stop @server) (reset! server nil))
