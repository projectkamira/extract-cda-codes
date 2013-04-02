(ns extract_cda_codes.core
  (:gen-class)
  (:use [clojure.tools.cli :only (cli)]
        [clojure.string :only (split)]
        [clojure.data.json :only (write-str write)]
        [clojure.data.csv :only (write-csv)])
  (:import [org.xml.sax InputSource]
           [org.xml.sax.helpers DefaultHandler]
           [java.io FileReader]
           [javax.xml.parsers SAXParserFactory])
)

; Map used to hold summary counts of codeSet/code occurence as {oid {'code' count}}
(def code-count (atom {}))

; Utility functions for working with summary counts structure
(defn merge-code-counts
  "Merge two maps expected to contain code counts as {'code' count}"
  [m1 m2]
  (merge-with + m1 m2))
  
(defn merge-oid-maps
  "Merge two maps expected to contain oids and code counts as {oid {'code' count}}"
  [m1 m2]
  (merge-with merge-code-counts m1 m2))
  
(defn log-code
  "Increment the count for the supplied code"
  [code-system code]
  (swap! code-count merge-oid-maps {(or code-system "unknown") {code 1}}))
  
(defn to-rows
  "Convert occurence data for a code set to a list of rows of the form ([codeSystem code count] ...). Append resulting rows to supplied list"
  [codes rows]
  (let [code-system (first codes)
        code-counts (second codes)
        code-names (keys code-counts)
        new-rows (map #(do [code-system % (code-counts %)]) code-names)]
    (swap! rows into new-rows)))

(defn to-csv
  "Convert occurence data to 2D array"
  [codes]
  (let [rows (atom '())]
    (doseq [i codes] (to-rows i rows))
    (deref rows)))

; XML parsing code
(defn logging-start-element-handler
  "Returns a DefaultHandler subclass that logs the values of codeSystem/code for each element"
  []
  (proxy [DefaultHandler] []
    (startElement [uri local qname atts]
       (if (. atts getValue "code")
          (log-code (. atts getValue "codeSystem") (. atts getValue "code"))))))

(defn extract-codes
  "Extract all the codes for a given CDA file"
  [file]
  (let [handle-element (logging-start-element-handler)]
    (.. SAXParserFactory newInstance newSAXParser
      (parse (InputSource. (FileReader. file)) handle-element))))

; File handling
(defn xml-suffix-filter
  "Returns a FilenameFilter that matches *.xml"
  []
  (reify 
    java.io.FilenameFilter
    (accept [_ dir name]
      (not (nil? (re-find #"\.xml$" name))))))

(defn list-files
  "List all of the '*.xml' files in the supplied directories"
  [dirs]
  (flatten (map #(seq (.listFiles (clojure.java.io/file %) (xml-suffix-filter))) dirs)))

; Iterate over all the XML files in the supplied directories and extract a summary of 
; the codeSystems and codes used in those documents.
(defn -main
  "Extract a frequency count of clinical codes from a directory of CDA documents"
  [& args]
  (let [[options args banner] (cli args
                                   ["-h" "--help" "Show help" :default false :flag true]
                                   ["-o" "--output" "A file into which the output will optionally be written, default is stdout"])]
    (when (:help options)
      (println banner)
      (System/exit 0))
      
    (dorun (pmap extract-codes (list-files args)))
    
    (if (:output options)
      (do
        (with-open [w (clojure.java.io/writer (:output options))]
          (.write w (write-str (deref code-count))))
        (with-open [w (clojure.java.io/writer (clojure.string/join "." [(:output options) "csv"]))]
          (write-csv w (to-csv (deref code-count)))))
      (do
        (println (write-str (deref code-count)))))
      
    (shutdown-agents)))
      


