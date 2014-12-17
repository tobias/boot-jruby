(ns boot.jruby
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [boot.core :as boot :refer [deftask]]
            [boot.pod :as pod])
  (:import java.util.Properties))

(defonce ^:private clj-rb-version
  (let [props (doto (Properties.)
                (.load (-> "META-INF/maven/clj.rb/clj.rb/pom.properties"
                         io/resource
                         io/reader)))]
    (.getProperty props "version")))

(defonce ^:private pods
  (pod/pod-pool
    (assoc-in (boot/get-env) [:dependencies] [['clj.rb clj-rb-version]])))

(defn make-jruby
  []
  ;; TODO: figure out what keys (if any) these should have
  ;; TODO: replace with new tempfile mechanism when it is released
  (let [pod (pods :refresh)
        tgt (boot/temp-dir!)
        src (boot/temp-dir!)
        rsc (boot/temp-dir!)
        tmp (boot/temp-dir!)]
    (fn [fileset & {:keys [eval eval-file env gem gem-path load-path set-var]}]
      (boot/empty-dir! tgt)
      (pod/with-eval-in pod
        (require
          '[clj.rb :as rb]
          '[clojure.java.io :as io])
        (let [rt (rb/runtime {:gem-paths ~gem-path
                              :load-paths ~load-path
                              :preserve-locals? true})]
          (try
            (rb/setenv rt "BOOT_TGT_PATH" ~(.getAbsolutePath tgt))
            (rb/setenv rt "BOOT_SRC_PATH" ~(.getAbsolutePath src))
            (rb/setenv rt "BOOT_RSC_PATH" ~(.getAbsolutePath rsc))
            (rb/setenv rt "BOOT_TMP_PATH" ~(.getAbsolutePath tmp))
            (doseq [[k v] ~env]
              (rb/setenv rt k v))
            (doseq [[name version] ~gem]
              (rb/install-gem rt name version))
            (doseq [[v-name v] ~set-var]
              (rb/setvar rt v-name v))
            (doseq [f ~eval-file]
              (rb/eval-file rt (io/file f)))
            (doseq [e ~eval]
              (rb/eval rt e))
            (finally
              (rb/shutdown-runtime rt)))))
      (-> fileset
          (boot/add-resource tgt)
          (boot/add-resource rsc)
          (boot/add-source   src)
          boot/commit!))))

(deftask jruby
  "Evaluate JRuby snippets or files.

   Any requested gems are installed (if need be) before eval'ing.

   Files are evaluated before snippets.

   Any top-level local variables created during an eval expression
   will be visible in subsequent eval expressions within the same task
   invocation.

   Temporary rsc, src, tgt, and tmp dirs are created for each
   invocation, and the paths are made available to ruby in ENV under
   BOOT_RSC_PATH, BOOT_SRC_PATH, BOOT_TGT_PATH, and BOOT_TMP_PATH,
   respectively."

  [e eval      CODE         [str]       "code snippets to eval"
   f eval-file PATH         [str]       "files to eval"
   E env       KEY=VALUE    {str str}   "values to set in ENV"
   g gem       NAME:VERSION [[str str]] "gems to install before eval"
   p gem-path  PATH         [str]       "additional gem paths to use"
   l load-path PATH         [str]       "additional load paths to use"
   S set-var   NAME=VALUE   {str edn}   "variables (local or global) to set in the runtime before eval"]

  (boot/with-pre-wrap fileset
    (let [jrb (make-jruby)]
      (apply jrb fileset (mapcat identity *opts*)))))
