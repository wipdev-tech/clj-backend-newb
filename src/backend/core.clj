(ns backend.core
  (:require [ring-jetty.adapter.jetty :refer [run-jetty]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [content-type response]]
            [cheshire.core :refer [generate-string]]))

;; basic handler: req (map) => response (map)
(defn handler [request]
  (-> {:greeting (:g (:params request) "unknown")}
      generate-string
      response
      (content-type "application/json")))

(def app (-> #'handler
             wrap-keyword-params
             wrap-params))

(defonce server (atom nil))

(defn -main []
  (reset! server
          (run-jetty #'app {:port 8000
                            :join? false})))

#_(-main)
#_(do (.stop @server) (reset! server nil))
