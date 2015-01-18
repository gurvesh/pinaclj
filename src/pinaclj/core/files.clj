(ns pinaclj.core.files
  (:require [pinaclj.core.nio :as nio]))

(defn- as-bytes [st]
  (bytes (byte-array (map byte st))))

(def fs-root (atom ""))

(defn init [filesystem root]
  (reset! fs-root (nio/get-path filesystem root)))

(defn init-default []
  (init (nio/default-file-system) (System/getProperty "user.dir")))

(defn read-lines [path]
  (nio/read-all-lines @fs-root path))

(defn content [path]
  (clojure.string/join "\n" (read-lines path)))

(defn relativize [path]
  (nio/relativize @fs-root path))

(defn create [path content]
  (nio/create-parent-directories @fs-root path)
  (nio/create-file @fs-root path (as-bytes content)))

(defn all-in [path]
  (with-open [dir (nio/directory-stream @fs-root path)]
    (into [] dir)))