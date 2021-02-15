package hr.fer.zemris.math;

import java.util.Arrays;

public class ComplexPolynomial {

    private Complex[] factors;

    public Complex[] getFactors() {
        return factors;
    }

    /**
     * Constructing new complex polynomial
     *
     * @param factors factors of polynomial
     */
    public ComplexPolynomial(Complex ...factors) {
        this.factors = factors;
    }

    /**
     * @return returns order of this polynomial
     */
    public short order() {
        return (short) (this.factors.length - 1);
    }

    /**
     * Computes new polynomial as this * p
     *
     * @param p complex polynomial
     * @return returns new polynomial computed as this * p
     */
    public ComplexPolynomial multiply(ComplexPolynomial p) {
        Complex[] resultFactors = new Complex[this.order() + p.order() + 1];
        Arrays.fill(resultFactors, Complex.ZERO);
        for (int i = 0; i < this.factors.length; i++) {
            for (int j = 0; j < p.getFactors().length; j++) {
                Complex tmp = this.factors[i].multiply(p.getFactors()[j]);
                resultFactors[i + j] = resultFactors[i + j].add(tmp);
            }
        }

        return new ComplexPolynomial(resultFactors);
    }

    /**
     * Computes first derivative of this polynomial
     *
     * @return returns new complex polynomial as derivative of this polynomial
     */
    public ComplexPolynomial derive() {
        Complex[] newFactors = new Complex[this.factors.length - 1];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = this.factors[i + 1].multiply(new Complex(i + 1, 0));
        }

        return new ComplexPolynomial(newFactors);
    }

    /**
     * Computes polynomial value at given point z
     *
     * @param z complex number
     * @return returns new complex number computed as value of polynomial at given point z
     */
    public Complex apply(Complex z) {
        Complex result = Complex.ZERO;
        for (int i = 0; i < this.factors.length; i++) {
            Complex zi = z.power(i);
            Complex factor = this.factors[i].multiply(zi);
            result = result.add(factor);
        }
        return result;
    }

    @Override
    public String toString() {
        String polynomial = "";
        for (int i = this.factors.length - 1; i > 0; i --) {
            polynomial += "(" + this.factors[i].toString() + ")Z^" + i + " + ";
        }
        polynomial += this.factors[0].toString();
        return polynomial;
    }
}
