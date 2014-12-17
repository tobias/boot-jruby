(set-env!
  :dependencies '[[org.clojure/clojure "1.6.0"]
                  [boot-jruby "0.3.0"]]
  :source-paths #{"resources"})

(require
  '[clojure.java.io :as io]
  '[boot.jruby :refer [jruby make-jruby]])

(deftask haml
  "convert haml to html"
  []
  (let [jrb (make-jruby)]
    (with-pre-wrap fileset
      (jrb fileset
           ;; installs the haml gem, unless it's already installed
           :gem [["haml" "4.0.5"]]
           ;; sets data from clojure in the ruby runtime, as globals
           :set-var {"$haml_files" (->> fileset
                                     input-files
                                     (by-ext [".haml"])
                                     (mapv #(.getAbsolutePath (tmpfile %))))
                     "$data"       {:what_the "nuts"}}
           ;; evaluates resources/render_haml.rb, that does the actual work
           :eval-file [(-> "render_haml.rb" io/resource io/file .getAbsolutePath)]))))

(deftask show-files
  "A debug task to println any files with :ext in the fileset"
  [e ext PATH [str] "extensions to show"]
  (with-pre-wrap fileset
    (doseq [:let [out-f (->> [user-files input-files output-files]
                          (mapcat #(% fileset))
                          set)]
            f (if (seq ext) (by-ext ext out-f) out-f)]
      (println (str "\n" (tmppath f) ":\n\n")
        (slurp (tmpfile f))))
    fileset))
