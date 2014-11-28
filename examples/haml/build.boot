(set-env!
  :dependencies '[[org.clojure/clojure "1.6.0"]
                  [boot-jruby "0.2.0"]]
  :rsc-paths    #{"resources"})

(require
  '[clojure.java.io :as io]
  '[boot.jruby :refer [jruby]])

(deftask haml
  "convert haml to html"
  []
  (jruby
    ;; installs the haml gem, unless it's already installed
    :gem [["haml" "4.0.5"]]
    ;; sets data from clojure in the ruby runtime, as globals
    :set-var {"$haml_files" (mapv (memfn getAbsolutePath) (by-ext [".haml"] (all-files)))
              "$data"       {:what_the "nuts"}}
    ;; evaluates resources/render_haml.rb, that does the actual work
    :eval-file [(-> "render_haml.rb" io/resource io/file .getAbsolutePath)]))

(deftask show-files
  "A debug task to println any files with :ext in the fileset"
  [e ext PATH [str] "extensions to show"]
  (with-pre-wrap
    (doseq [:let [all-f (all-files)]
            f (if (seq ext) (by-ext ext all-f) all-f)]
      (println (str "\n" f ":\n\n")
        (slurp f)))))
