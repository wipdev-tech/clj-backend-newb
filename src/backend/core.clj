(ns backend.core
  (:require [ring-jetty.adapter.jetty :refer [run-jetty]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.pprint :refer [pprint]]))

;; basic handler: req (map) => response (map)
(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body  (str
           "<pre>"
           (with-out-str (pprint request))
           "</pre>")})

;; middleware produces functions that wrap the original handler
(defn wrap-content-type [handler content-type]
  (fn [request]
    (assoc-in (handler request)
              [:headers "Content-Type"]
              content-type)))

(def app (-> handler
             (wrap-content-type "text/plain")
             wrap-keyword-params
             wrap-params))

(defonce server (atom nil))

(defn -main []
  (reset! server
          (run-jetty #'app {:port 8000
                            :join? false})))

#_(-main)
#_(do (.stop @server) (reset! server nil))
