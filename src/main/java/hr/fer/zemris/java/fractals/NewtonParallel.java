package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.util.Util;
import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import javax.swing.table.TableRowSorter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewtonParallel {

    public static void main(String[] args) {

        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.\nPlease enter at least two roots, one root per line. Enter 'done' when done.");
        int workers = Runtime.getRuntime().availableProcessors();
        int tracks = 4 * workers;
        if (args.length != 0) {
            if (args[0].startsWith("--workers") || args[0].startsWith("-w"))
                workers = Util.parseArguments(args[0]);
            if (args[0].startsWith("--tracks") || args[0].startsWith("-t"))
                tracks = Util.parseArguments(args[0]);
            if (args.length == 2) {
                if (args[1].startsWith("--workers") || args[1].startsWith("-w"))
                    workers = Util.parseArguments(args[1]);
                if (args[1].startsWith("--tracks") || args[0].startsWith("-t"))
                    tracks = Util.parseArguments(args[1]);
                if (tracks < 1)
                    throw new IllegalArgumentException("Number of tracks must be 1 or greater, was "+ tracks);
            }
        }

        //FractalViewer.show(new FractalProducerImpl(2, 1, Util.getFactors()));
        FractalViewer.show(new FractalProducerImpl(tracks, workers, Util.getFactors()));

    }

    /**
     * Model of one calculation work
     */
    public static class Worker implements Runnable {

        double reMin;
        double reMax;
        double imMin;
        double imMax;
        int width;
        int height;
        int yMin;
        int yMax;
        int m;
        short[] data;
        AtomicBoolean cancel;
        Complex[] factors;
        public static Worker NO_JOB = new Worker();

        private Worker() {}

        public Worker(double reMin, double reMax, double imMin,
                      double imMax, int width, int height, int yMin,
                      int yMax, int m, short[] data, AtomicBoolean cancel, Complex[] factors) {
            this.reMin = reMin;
            this.reMax = reMax;
            this.imMin = imMin;
            this.imMax = imMax;
            this.width = width;
            this.height = height;
            this.yMin = yMin;
            this.yMax = yMax;
            this.m = m;
            this.data = data;
            this.cancel = cancel;
            this.factors = factors;
        }

        @Override
        public void run() {

            Thread current = Thread.currentThread();


            int offset = width * yMin;
            ComplexPolynomial polynomial = null;
            for (int y = yMin; y < yMax + 1; y++) {
                if (cancel.get()) break;
                for (int x = 0; x < width; x++) {
                    double cRe = x / (width - 1.0) * (reMax - reMin) + reMin;
                    double cIm = (height - 1.0 - y) / (height - 1) * (imMax - imMin) + imMin;
                    Complex zn = new Complex(cRe, cIm);
                    ComplexRootedPolynomial rootedPolynomial = new ComplexRootedPolynomial(zn, factors);
                    polynomial = rootedPolynomial.toComplexPolynom();
                    double module;
                    int iter = 0;
                    do {
                        Complex numerator = polynomial.apply(zn);
                        ComplexPolynomial derived = polynomial.derive();
                        Complex denominator = derived.apply(zn);
                        Complex znold = zn;
                        Complex fraction = numerator.divide(denominator);
                        zn = zn.sub(fraction);
                        module = znold.sub(zn).module();
                        iter++;
                    } while (module > 0.001 && iter < m);
                    int index = rootedPolynomial.indexOfClosestRootFor(zn, 0.002) + 1;
                    data[offset++] = (short) index;
                }
            }

        }
    }

    public static class FractalProducerImpl implements IFractalProducer {

        private int tracks;
        private final int workers;
        private Complex[] factors;

        public FractalProducerImpl(int tracks, int workers, Complex[] factors) {
            this.tracks = tracks;
            this.workers = workers;
            this.factors = factors;
        }


        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {

            if(this.tracks > height)
                this.tracks = height;

            System.out.println("Number of threads : " + workers + "\nNumber of jobs : " + tracks);

            System.out.println("Starting calculation");
            int m = 16*16*16;
            short[] data = new short[width * height];
            int numberYPerTrack = height / this.tracks;

            final BlockingQueue<Worker> queue = new LinkedBlockingQueue<>();

            Thread[] workers = new Thread[this.workers];
            for (int i = 0; i < workers.length; i++) {
                workers[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Worker w = null;
                            try {
                                w = queue.take();
                                if (w == Worker.NO_JOB) break;
                            } catch (InterruptedException e) {
                                continue;
                            }
                            w.run();
                        }
                    }
                });
            }
            for (int i = 0; i < workers.length; i++) {
                workers[i].start();
            }
            for (int i = 0; i < this.tracks; i++) {
                int yMin = i * numberYPerTrack;
                int yMax = (i + 1) * numberYPerTrack - 1;
                if (i == this.tracks - 1) {
                    yMax = height - 1;
                }
                Worker worker = new Worker(reMin, reMax, imMin, imMax, width, height, yMin, yMax, m, data, cancel, factors);
                while (true) {
                    try {
                        queue.put(worker);
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }
            for (int i = 0; i < workers.length; i++) {
                while (true) {
                    try {
                        queue.put(Worker.NO_JOB);
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }
            for (int i = 0; i < workers.length; i++) {
                while (true) {
                    try {
                        workers[i].join();
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }

            System.out.println("Calculation ended. Alarm GUI");
            observer.acceptResult(data, (short) (factors.length + 1), requestNo);
        }
    }
}
