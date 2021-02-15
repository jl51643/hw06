package hr.fer.zemris.math;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Model of complex number
 */
public class Complex {

    /**
     * Real part of complex number
     */
    private double re;

    /**
     * Imaginary part of complex number
     */
    private double im;

    public static final Complex ZERO = new Complex(0,0);
    public static final Complex ONE = new Complex(1,0);
    public static final Complex ONE_NEG = new Complex(-1,0);
    public static final Complex IM = new Complex(0,1);
    public static final Complex IM_NEG = new Complex(0,-1);

    /**
     * Constructing new complex number in center of complex plane (0, 0i)
     */
    public Complex() {
        this(0, 0);
    }

    /**
     * Constructing new complex number with real and imaginary part
     *
     * @param re real part of complex number
     * @param im imaginary part of complex number
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * @return returns real part of complex number
     */
    public double getRe() {
        return re;
    }

    /**
     * @return returns imaginary part of complex number
     */
    public double getIm() {
        return im;
    }


    /**
     * @return returns module of complex number
     */
    public double module() {
        return Math.sqrt(this.re * this.re + this.im * this.im);
    }

    /**
     * Returns new complex number as this * c
     *
     * @param c complex number
     * @return returns this * c
     */
    public Complex multiply(Complex c) {
        return new Complex(this.re * c.re - (this.im * c.im),
                this.re * c.im + this.im * c.re);
    }

    /**
     * Returns new complex number as this / c
     *
     * @param c complex number
     * @return returns this / c
     */
    public Complex divide(Complex c) {

        Complex conjugateOfC = new Complex(c.re, -c.im);

        Complex dividend = this.multiply(conjugateOfC);
        Complex divisor = c.multiply(conjugateOfC);

        return new Complex(dividend.re / divisor.re, dividend.im / divisor.re);
    }

    /**
     * Returns new complex number as this + c
     *
     * @param c complex number
     * @return returns this+c
     */
    public Complex add(Complex c) {
        return new Complex(this.re + c.re, this.im + c.im);
    }

    /**
     * Returns new complex number as this - c
     *
     * @param c complex number
     * @return returns this - c
     */
    public Complex sub(Complex c) {
        return new Complex(this.re - c.re, this.im - c.im);
    }

    /**
     * Returns new complex number as -this
     *
     * @return returns -this
     */
    public Complex negate() {
        return new Complex(-this.re, - this.im);
    }

    /**
     * Returns new complex number as this^n
     *
     * @param n non-negative integer
     * @return returns this^n, n is non-negative integer
     * @throws IllegalArgumentException if n is negative
     */
    public Complex power(int n) {
        if (n < 0)
            throw new IllegalArgumentException("n must be non-negative integer");
        if (n == 0)
            return ONE;
        Complex c = new Complex(this.re, this.im);
        double magnitude = Math.pow(c.module(), n);
        double angle = Math.atan2(c.im, c.re);
        if (angle < 0)
            angle += (2 * PI);
        angle *= n;

        return new Complex(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
    }

    // returns n-th root of this, n is positive integer
    public List<Complex> root(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n mus be positive integer");
        List<Complex> roots = new LinkedList<Complex>();
        Complex c = new Complex(this.re, this.im);
        double magnitude = Math.pow(c.module(), 1f/n);
        double angle = Math.atan2(c.im, c.re);
        for (int i = 0; i < n; i++) {
            roots.add(new Complex(magnitude * Math.cos((angle + 2 * i * PI) / n), magnitude * Math.sin((angle + 2 * i * PI) / n)));
        }

        return roots;
    }

    @Override
    public String toString() {
        if (this.re != 0.0 && this.im != 0.0) {
            if (this.im > 0)
                return this.re + "+" + this.im + "i";
            else {
                return "" + this.re + this.im + "i";
            }
        }
        if (abs(this.im - 0.0) <= 1E-10)
            return Double.toString(this.re);
        if (abs(this.re - 0.0) <= 1E-10)
            return this.im + "i";
        return null;
    }
}
