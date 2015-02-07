(ns pinaclj.test-templates
  (:require [pinaclj.test-fs :as test-fs]
            [pinaclj.templates :as templates]))

(defn- page-stream []
  (test-fs/resource-stream "example_theme/post.html"))

(defn- page-list-stream []
  (test-fs/resource-stream "example_theme/index.html"))

(def page-link
  (templates/build-link-func (page-list-stream)))

(def page-list
  (templates/build-list-func (page-list-stream) page-link))

(def page
  (templates/build-page-func (page-stream)))
