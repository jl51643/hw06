package hr.fer.zemris.math;

import java.util.Arrays;

public class ComplexRootedPolynomial {

    /**
     * Constant of polynomial
     */
    private Complex constant;

    /**
     * Roots of polynomial
     */
    private Complex[] roots;

    /**
     * Constructing new complex rooted polynomial
     *
     * @param constant constant of polynomial
     * @param roots roots od polynomial
     */
    public ComplexRootedPolynomial(Complex constant, Complex ... roots) {
        this.constant = constant;
        this.roots = roots;
    }

    /**
     * Computes polynomial value at given point z
     *
     * @param z complex number
     * @return returns complex number that is value of polynomial at given point z
     */
    public Complex apply(Complex z) {
        Complex result = this.constant;
        for (int i = 0; i < this.roots.length; i++) {
            result = result.multiply(z.sub(roots[i]));
        }

        return result;
    }

    /**
     * @return returns new ComplexPolynomial from complex rooted polynomial
     */
    public ComplexPolynomial toComplexPolynom() {
        ComplexPolynomial[] rootedPolynomials = new ComplexPolynomial[this.roots.length];
        for (int i = 0; i < this.roots.length; i++) {
            rootedPolynomials[i] = new ComplexPolynomial(this.roots[i].negate(), Complex.ONE);
        }
        //ComplexPolynomial result = new ComplexPolynomial(this.constant);
        ComplexPolynomial result = new ComplexPolynomial(Complex.ONE);
        for (ComplexPolynomial p : rootedPolynomials)
            result = result.multiply(p);

        return result;
    }

    @Override
    public String toString() {
        String polynomial = "f(z) = " + this.constant.toString();
        for (int i = 0; i < this.roots.length; i++) {
            polynomial += " * (z - (" + this.roots[i].negate().toString() + "))";
        }
        return polynomial;
    }

    /**
     * Finds index of closest root for given complex number z that is within
     * treshold. If there is no such root, returns -1
     *
     * @param z complex number
     * @param treshold treshold
     * @return returns index of closest root for given complex number z that is within.
     * If there is no such root, returns -1
     */
    public int indexOfClosestRootFor(Complex z, double treshold) {
        int index = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.roots.length; i++) {
            double distance = Math.sqrt((this.roots[i].getRe() - z.getRe()) * (this.roots[i].getRe() - z.getRe()) + (this.roots[i].getIm() - z.getIm()) * (this.roots[i].getIm() - z.getIm()));
            if (distance < treshold && distance < minDistance) {
                index = i;
                minDistance = distance;
            }
        }
        return index;
    }
}
