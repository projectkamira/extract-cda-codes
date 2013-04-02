(defproject extract_cda_codes "0.1.0-SNAPSHOT"
  :description "Script to extract a frequency count of clinical codes from a set of CDA documents"
  :url "https://github.com/projectkamira/extract_cda_codes"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [org.clojure/clojure "1.4.0"]
    [org.clojure/tools.cli "0.2.2"]
    [org.clojure/data.json "0.2.1"]
    [org.clojure/data.csv "0.1.2"]]
  :main extract_cda_codes.core)
