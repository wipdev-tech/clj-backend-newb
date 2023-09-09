(ns backend.core
  (:require [ring-jetty.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [content-type response]]
            [cheshire.core :refer [generate-string]]
            [reitit.ring :as ring]))

(defn now [] (new java.util.Date))

(defn handler [request]
  (prn request)
  (-> {:message (str "Hello "
                     (:name (:params request) "world")
                     "! You've successfully accessed the API!")
       :at       (now)}
      generate-string
      response
      (content-type "application/json")))

(def router
  (ring/router ["/ping" {:get #(handler %)}]))

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
