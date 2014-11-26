(set-env!
  :dependencies '[[org.clojure/clojure "1.6.0" :scope "provided"]
                  [clj.rb "0.2.1"]]
  :src-paths    ["src"])

(task-options!
  pom [:project 'boot-jruby
       :version "0.1.0"
       :description "Boot task to execute JRuby code."
       :url "https://github.com/tobias/boot-jruby"
       :scm {:url "https://github.com/tobias/boot-jruby"}
       :license {:name "Apache Software License - v 2.0"
                 :url "http://www.apache.org/licenses/LICENSE-2.0"}]
  push [:gpg-sign true
        :gpg-user-id "toby@tcrawley.org"
        :repo "clojars"])

(deftask build
  "Build and install the artifact."
  []
  (comp (pom) (add-src) (jar) (install)))
