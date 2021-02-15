package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.util.Util;
import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Newton {


    public static void main(String[] args) {

        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.\nPlease enter at least two roots, one root per line. Enter 'done' when done.");

        FractalViewer.show(new FractalProducerImpl(Util.getFactors()));
    }

    public static class FractalProducerImpl implements IFractalProducer {

        Complex[] factors;

        public FractalProducerImpl(Complex[] factors) {
            this.factors = factors;
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNO, IFractalResultObserver observer, AtomicBoolean cancel) {

            System.out.println("Starting calculation...");
            int m = 16*16*16;
            int offset = 0;
            short[] data = new short[width * height];
            ComplexPolynomial polynomial = null;
            for (int y = 0; y < height; y++) {
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
            System.out.println("Calculation ended");
            observer.acceptResult(data, (short)(polynomial.order() + 1), requestNO);
        }
    }


}
