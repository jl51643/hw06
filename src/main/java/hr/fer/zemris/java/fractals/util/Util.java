package hr.fer.zemris.java.fractals.util;

import hr.fer.zemris.math.Complex;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class with utility methods
 */
public class Util {

    /**
     * @return returns array of complex numbers from system input
     */
    public static Complex[] getFactors() {
        Scanner sc = new Scanner(System.in);

        ArrayList<Complex> roots = new ArrayList<>();
        int i = 1;
        while (true) {
            System.out.print("Root " + i + "> ");
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("done"))
                break;
            Complex root = Util.parseComplex(line);
            roots.add(root);
            i++;
        }
        System.out.println("Image of fractal will appear shortly. Thank you.");

        Complex[] factors = new Complex[roots.size()];
        factors = roots.toArray(factors);
        return factors;
    }

    /**
     * Tries to parse given string into complex number
     *
     * @param line input string
     * @return returns complex number from given string if possible
     * @throws IllegalArgumentException if can not parse string into complex number
     */
    public static Complex parseComplex(String line) {
        char[] number = line.toCharArray();
        String re = "", im = "";
        int i = 0;
        while (i < number.length && Character.isWhitespace(number[i])) i++;
        if (Character.isDigit(number[i])) {
            re = determineNumber(number, i);
            i += re.length();
        } else if (number[i] == '-' && i + 1 < number.length && Character.isDigit(number[i+1])) {
            i++;
            re = determineNumber(number, i);
            i += re.length();
            re = "-" + re;

        }
        while (i < number.length && Character.isWhitespace(number[i])) i++;
        String sign = "";
        if (i < number.length && (number[i] == '+' || number[i] == '-'))
            sign += number[i++];
        while (i < number.length && Character.isWhitespace(number[i])) i++;
        if (i < number.length && number[i] == 'i') {
            i++;
            im = determineNumber(number, i);
            i += im.length();
            im = sign + im;
            im = im + "i";
        }
        if (re.equals(""))
            re = "0.0";


        if (im.equals(""))
            im = "0.0";
        if (im.equals("i") || im.equals("+i"))
            im = "1.0";
        if (im.equals("-i"))
            im = "-1.0";
        if (im.endsWith("i"))
            im = im.substring(0, im.length()-1);

        if (i != number.length)
            throw new IllegalArgumentException("Can not turn string " + line + " into complex number");

        return new Complex(Double.parseDouble(re), Double.parseDouble(im));
    }

    private static String determineNumber(char[] line, int index) {
        String number = "";
        String tmp = readNumbers(line, index);
        number += tmp;
        index += tmp.length();
        /*if there is more elements in array and current element is "." and i we already have any digit saved into number and if next element is digit*/
        if (index < line.length-1 && line[index] == '.' && !number.equals("") && Character.isDigit(line[index+1])) {
            number += line[index++];
        }
        tmp = readNumbers(line, index);
        number += tmp;
        index += tmp.length();

        return number;
    }

    /**
     * returns string of sequential digits in input string
     *
     * @param line input string
     * @param index current position in input string
     * @return returns string of sequential digits in input string
     */
    private static String readNumbers(char[] line, int index) {
        String number = "";
        while (index < line.length && Character.isDigit(line[index])) {
            number += line[index++];
        }
        return number;
    }

    /**
     * Extracts number from command line argument
     *
     * @param arg command line argument
     * @return returns integer value from argument
     */
    public static int parseArguments(String arg) {
        char[] argument = arg.toCharArray();
        int i = 0;
        String number = "";
        while (i < argument.length && !Character.isDigit(argument[i])) i++;
        while (i < argument.length && Character.isDigit(argument[i])) number += argument[i++];

        if (i != argument.length || number.equals(""))
            throw new IllegalArgumentException("Wrong format of argument : " + arg);

        return Integer.parseInt(number);
    }
}
