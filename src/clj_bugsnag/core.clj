(ns clj-bugsnag.core
  (:require [clj-stacktrace.core :refer [parse-exception]]
            [clj-stacktrace.repl :refer [method-str]]
            [clojure.java.shell :refer [sh]]
            [clj-http.client :as http]
            [clojure.data.json :as json]))

(defn transform-stacktrace
  [trace-elems project-ns]
  (vec (for [{:keys [file line ns] :as elem} trace-elems
             :let [project? (.startsWith (or ns "_") project-ns)]]
          {:file file :lineNumber line :method (method-str elem) :inProject project?})))

(defn post-data
  [exception data]
  (let [ex (parse-exception exception)
        class-name (.getName (:class ex))
        project-ns (get data :project-ns "")
        base-meta (if-let [d (ex-data exception)]
                    {"ex–data" d}
                    {})]
    {:apiKey (:api-key data)
     :notifier {:name "clj-bugsnag"
                :version "0.1.2"
                :url "http://github.com/bscofield/clj-bugsnag"}
     :events [{:payloadVersion "2"
               :exceptions [{:errorClass class-name
                             :message (:message ex)
                             :stacktrace (transform-stacktrace (:trace-elems ex) project-ns)}]
               :groupingHash (or (:group data)
                               (if (isa? (type exception) clojure.lang.ExceptionInfo)
                                 (:message ex)
                                 class-name))
               :severity (or (:severity data) "error")
               :app {:version (clojure.string/trim (:out (sh "git" "rev-parse" "HEAD")))
                     :releaseStage (or (:environment data) "production")}
               :device {:hostname (.. java.net.InetAddress getLocalHost getHostName)}
               :metaData (merge base-meta (:meta data))}]}))


(defn notify
  [exception data]
  (let [params (post-data exception data)
        url "https://notify.bugsnag.com/"]
    (http/post url {:form-params params
                    :content-type :json})))
