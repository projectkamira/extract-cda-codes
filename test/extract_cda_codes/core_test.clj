(ns extract-cda-codes.core-test
  (:use clojure.test
        extract_cda_codes.core))

(deftest test-code-map-merge
  (testing "Merging code counts"
    (let [
          merged (merge-code-counts {:a 1 :b 2 :c 3} {:a 2 :b 2})]
      (is (= 3 (:a merged)))
      (is (= 4 (:b merged)))
      (is (= 3 (:c merged)))
    )
  )
)

(deftest test-oid-maps-merge
  (testing "Merging code counts"
    (let [
          merged (merge-oid-maps {:oid {:a 1 :b 2 :c 3}} {:oid {:a 2 :b 2} :oid2 {:c 4}})]
      (is (= 3 (:a (:oid merged))))
      (is (= 4 (:b (:oid merged))))
      (is (= 3 (:c (:oid merged))))
      (is (= 4 (:c (:oid2 merged))))
    )
  )
)

(deftest test-list-files
  (testing "File listing"
    (let [
          files (list-files '("./test/fixtures" "."))
          filenames (map #(.getName %) files)]
      (is (= 2 (count files)))
      (is (every? #{"pom.xml" "NISTExampleC32.xml"} filenames))
    )
  )
)

(deftest test-code-counting
  (testing "Code counting"
    (dosync
      (alter code-count {}))
    (extract-codes "./test/fixtures/NISTExampleC32.xml")
    (is (= 1 ((code-count "2.16.840.1.113883.6.1") "30954-2")))
    (is (= 15 ((code-count "2.16.840.1.113883.6.1") "33999-4")))
    (is (= 4 ((code-count "2.16.840.1.113883.6.96") "404684003")))
    (is (= 11 ((code-count "2.16.840.1.113883.6.96") "55561003")))
  )
)
