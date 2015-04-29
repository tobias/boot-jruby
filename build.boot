(set-env!
  :dependencies   '[[org.clojure/clojure "1.6.0" :scope "provided"]
                    [clj.rb "0.3.0"]
                    [adzerk/bootlaces "0.1.5" :scope "test"]]
  :resource-paths #{"src"})

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.3.0")

(bootlaces! +version+)

(task-options!
  pom {:project 'boot-jruby
       :version +version+
       :description "Boot task to execute JRuby code."
       :url "https://github.com/tobias/boot-jruby"
       :scm {:url "https://github.com/tobias/boot-jruby"}
       :license {"Apache Software License - v 2.0"
                 "http://www.apache.org/licenses/LICENSE-2.0"}})
