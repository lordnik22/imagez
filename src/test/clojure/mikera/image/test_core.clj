(ns mikera.image.test-core
  (:use mikera.image.core)
  (:use clojure.test)
  (:require [mikera.image.colours :refer [long-colour]]
            [clojure.java.io :refer [as-file resource input-stream]])
  (:import java.awt.image.BufferedImage
           javax.imageio.ImageIO))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(deftest test-new-image
  (is (instance? BufferedImage (new-image 10 10))))

(deftest test-scale-image
  (let [^BufferedImage bi (new-image 10 10)
        bi (scale-image bi 5 6)]
    (is (instance? BufferedImage bi))
    (is (== 5 (.getWidth bi)))
    (is (== 6 (.getHeight bi)))))

(deftest test-scale
  (let [^BufferedImage bi (new-image 10 10)
        bi (scale bi 2.0 3.0)]
    (is (instance? BufferedImage bi))
    (is (== 20 (.getWidth bi)))
    (is (== 30 (.getHeight bi)))))

(deftest test-zoom-image
  (let [^BufferedImage bi (new-image 10 10)
        bi (zoom bi 2.0)]
    (is (instance? BufferedImage bi))
    (is (== 20 (.getWidth bi)))
    (is (== 20 (.getHeight bi)))
    (.setRGB bi 0 0 (unchecked-int 0xFFFFFFFF))
    (let [si (sub-image bi 0 0 3 4)]
      (is (== 3 (.getWidth si)))
      (is (== 4 (.getHeight si)))
      (is (== 0xFFFFFFFF (long-colour (.getRGB si 0 0)))))))

(deftest test-get-pixels
  (let [bi (new-image 1 1)
        pxs (get-pixels bi)]
    (is (instance? (Class/forName "[I") pxs))
    (is (= [0] (seq pxs)))
    (aset pxs 0 0xFFFFFFFF)
    (set-pixels bi pxs)
    (is (== 0xFFFFFFFF (long-colour (.getRGB bi 0 0))))))

(deftest test-load-image
  (are [r] (instance? BufferedImage (load-image r))
       "src/test/resources/mikera/image/samples/Clojure_300x300.png"
       (as-file "src/test/resources/mikera/image/samples/Clojure_300x300.png")
       (ImageIO/read (as-file "src/test/resources/mikera/image/samples/Clojure_300x300.png"))
       (resource "mikera/image/samples/Clojure_300x300.png")
       (input-stream "src/test/resources/mikera/image/samples/Clojure_300x300.png")))
