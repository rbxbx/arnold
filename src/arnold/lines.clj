(ns arnold.lines
  (:use quil.core
        [quil.helpers.seqs :only [steps seq->stream range-incl tap tally indexed-range-incl]]
        [quil.helpers.calc :only [mul-add]]))

;; Inspired by an example from the excellent book "Generative Art" by Matt Pearson

(defn draw-point
  [x y noise-factor]
  (push-matrix)
  (translate x y)
  (rotate (* noise-factor (radians 360)))
  (stroke 100 )
  (line 0 0 500 0)
  (pop-matrix))

(defn draw-all-points
  [x-start y-start step-size]
  (dorun
   (for [[x-idx x] (indexed-range-incl 0 (width) step-size)
         [y-idx y] (indexed-range-incl 0 (height) step-size)]
     (let [x-noise-shift (* x-idx 0.1)
           y-noise-shift (* y-idx 0.1)
           x-noise (+ x-start x-noise-shift)
           y-noise (+ y-start y-noise-shift)]
       (draw-point x y (noise x-noise y-noise))))))

(defn starts-seq []
  (let [noise-steps (steps (random 20) 0.01)
        noises      (map noise noise-steps)
        noises      (mul-add noises 0.5 -0.25)
        noise-tally (tally noises)]
    (map +
         (steps (random 10) 0.01)
         noise-tally)))

(defn setup []
  (smooth)
  (background 255)
  (frame-rate 60)

  (let [x-starts      (starts-seq)
        y-starts      (starts-seq)
        starts-str    (seq->stream (map list x-starts y-starts))]
    (set-state! :starts-str starts-str)))

(defn draw []
  (background 0)
  (stroke-weight 20)
  (let [[x-start y-start] ((state :starts-str))]
    (draw-all-points x-start y-start 80)))
