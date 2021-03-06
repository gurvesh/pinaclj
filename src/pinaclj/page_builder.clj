(ns pinaclj.page-builder
  (:require [pinaclj.page :as page]
            [pinaclj.date-time :as dt]
            [pinaclj.transforms.transforms :as transforms]
            [pinaclj.group :as group]))

(defn create-page [src path]
  (transforms/apply-all {:path path :src-root src}))

(defn generate-page [url]
  (assoc (create-page nil nil)
         :modified (System/currentTimeMillis)
         :parent "index.html"
         :generated true
         :path "index.md"
         :url url
         :raw-content ""
         :published-at (dt/now)))

(defn- chronological-sort [pages]
  (sort-by :published-at pages))

(defn- build-group-page [[group pages] url-func category]
  (assoc (generate-page (url-func group))
         :category category
         :pages (page/to-page-urls (chronological-sort pages))
         :title (name group)))

(defn- build-group-pages [pages url-func category]
  (map #(build-group-page % url-func category) pages))

(defn build-tag-pages [pages]
  (build-group-pages (group/pages-by-tag pages) page/tag-url "tags"))

(defn build-category-pages [pages]
  (build-group-pages (group/pages-by-category pages) page/category-url "category"))

(defn- split-page-url [page]
  (.split (page/retrieve-value page :destination) "\\."))

(defn- build-url-fn [page page-count]
  (let [[start ext] (split-page-url page)]
    (fn [page-num]
      (cond
        (zero? page-num) (page/retrieve-value page :destination)
        (and (pos? page-num) (< page-num page-count))
        (str start "-" (inc page-num) "." ext)))))

(defn- duplicate-page [page child-pages page-num url-fn total-pages]
  (let [new-url (url-fn page-num)]
    [new-url (assoc page
                    :raw-content ""
                    :url new-url
                    :pages child-pages
                    :next (url-fn (dec page-num))
                    :prev (url-fn (inc page-num))
                    :page-sequence-number (inc page-num)
                    :total-pages total-pages)]))

(defn divide [[destination page :as kv] {max-pages :max-pages}]
  (if (or (nil? max-pages) (empty? (:pages page)))
    [kv]
    (let [partitions (partition-all max-pages (:pages page))
          total-pages (count partitions)
          url-fn (build-url-fn page total-pages)]
      (map #(duplicate-page page %1 %2 url-fn total-pages) partitions (range)))))
