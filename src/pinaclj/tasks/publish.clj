(ns pinaclj.tasks.publish
  (:require [pinaclj.read :as rd]
            [pinaclj.files :as files]
            [pinaclj.page :as page]
            [pinaclj.page-builder :as pb]))

(def description
  "Publishes a page with the current timestamp.")

(defn- read-page [src-root src-file]
  (rd/read-page (pb/create-page src-root src-file)))

(defn add-header [page time-fn]
  (if (contains? page :published-at)
    page
    (assoc page :published-at (time-fn))))

(defn publish-path [src-root src-path time-fn]
  (-> (read-page src-root (files/resolve-path src-root src-path))
      (add-header time-fn)
      (page/write-page src-root)))
