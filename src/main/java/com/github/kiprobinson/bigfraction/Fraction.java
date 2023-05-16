package com.github.kiprobinson.bigfraction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import com.github.kiprobinson.bigfraction.util.DoubleUtil;

/**
 * Fraction interface
 * 
 * @author Wayne Zhang
 */
public interface Fraction<V extends Number, T extends Fraction<V, T>> {
	static enum Reduced {
		YES, NO
	};

	static enum FareyMode {
		NEXT, PREV, CLOSEST
	};

	static enum RemainderMode {
		QUOTIENT, REMAINDER, BOTH
	};
	
   /*
    * Check if a number is Java integer type, that is
    *   byte, short, int, long, AtomicInteger, AmoticLong, 
    *   LongAdder and LongAccumlator
    */
    static boolean isJavaInteger(Number n){
        return n instanceof Long || 
               n instanceof Integer || 
               n instanceof Short || 
               n instanceof Byte || 
               n instanceof AtomicInteger || 
               n instanceof AtomicLong || 
               n instanceof LongAdder || 
               n instanceof LongAccumulator;
    }	
    
    /**
     * Returns true if n is a type that can be converted to a double without loss of precision (Float, Double, DoubleAdder, and DoubleAccumulator)
     */
    static boolean isFloat(Number n)
    {
      return n instanceof Double ||
      	   n instanceof Float || 
      	   n instanceof DoubleAdder || 
      	   n instanceof DoubleAccumulator;
    }
    
    /**
     * Returns true if the given type can be converted to a BigInteger without loss
     * of precision. Returns true for the primitive integer types (Long, Integer, Short,
     * Byte, AtomicInteger, AtomicLong, LongAdder, LongAccumulator, or BigInteger).<br>
     * <br>
     * For LongFraction, returns true if denominator is 1.<br>
     * <br>
     * For double, float, DoubleAdder, DoubleAccumulator, and BigDecimal, analyzes the data. Otherwise returns false.<br>
     * <br>
     * Used to determine if a Number is appropriate to be passed into toBigInteger() method.
     */
    static boolean isInt(Number n)
    {
      if(isJavaInteger(n) || n instanceof BigInteger)
        return true;
      
      if(n instanceof BigFraction)
        return ((BigFraction)n).getDenominator().equals(BigInteger.ONE);
      
      if(n instanceof LongFraction)
        return ((LongFraction)n).getDenominator() == 1L;
      
      //BigDecimal format: unscaled / 10^scale
      if(n instanceof BigDecimal)
        return (((BigDecimal)n).scale() <= 0);
      
      //unknown type - use the doubleValue()
      final double d = n.doubleValue();
      if(d == 0.0)
        return true;
      
      if(Double.isInfinite(d) || Double.isNaN(d))
        return false;
      
      return (DoubleUtil.getExponent(d) >= 52);
    }
    
    // Exception helpers
    static ArithmeticException noExactValueException(String type){
        return new ArithmeticException(
          String.format("Value does not have an exact %s representation", type)
        );
    } 
    
    // short hand of new ArithmeticException
    static ArithmeticException arithExp(Object... msgs){
        StringBuilder message = new StringBuilder();
        for(Object msg : msgs){
            message.append(msg);
        }
        
        return new ArithmeticException(message.toString());
    }    
  
    byte byteValueExact();

    short shortValueExact();

    int intValueExact();

    long longValueExact();

    float floatValueExact();

    double doubleValueExact();
    
    /**
     * Returns the numerator of this fraction.
     * @return numerator of this fraction.
     */
    V getNumerator();
    
    /**
     * Returns the denominator of this fraction.
     * @return denominator of this fraction.
     */
    V getDenominator();
    
    /**
     * Returns this + n.
     * @param n number to be added to this
     * @return this + n
     * @throws IllegalArgumentException if n is null.
     */
    T add(Number n);
    
    /**
     * Returns this - n.
     * @param n number to be subtracted from this
     * @return this - n
     * @throws IllegalArgumentException if n is null.
     */
    T subtract(Number n);
    
    /**
     * Returns n - this. Sometimes this results in cleaner code than
     * rearranging the code to use subtract().
     * 
     * @param n number to subtract this from
     * @return n - this
     * @throws IllegalArgumentException if n is null.
     */
    T subtractFrom(Number n);
    
    /**
     * Returns this * n.
     * @param n number to be multiplied by this
     * @return this * n
     * @throws IllegalArgumentException if n is null.
     */
    T multiply(Number n);
    
    /**
     * Returns this / n.
     * 
     * @param n number to divide this by (divisor)
     * @return this / n
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     */
    T divide(Number n);
    
    /**
     * Returns n / this. Sometimes this results in cleaner code than
     * rearranging the code to use divide().
     * 
     * @param n number to be divided by this (dividend)
     * @return n / this
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if this == 0.
     */
    T divideInto(Number n);
    
    /**
     * Divides to an integral value, using {@link DivisionMode#TRUNCATED} division mode.
     * 
     * @param n number to divide this by (dividend)
     * @return integral quotient of this / n, using truncated division
     * 
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     * 
     * @see #divideAndRemainder(Number, DivisionMode)
     * @see DivisionMode
     */
    V divideToIntegralValue(Number n);
    
    /**
     * Divides to an integral value, using the specified division mode.
     * 
     * @param n number to divide this by (dividend)
     * @param divisionMode division mode to use if dividend or divisor is negative.
     * @return integral quotient of this / n, using specified division mode.
     * 
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     * 
     * @see #divideAndRemainder(Number, DivisionMode)
     * @see DivisionMode
     */
    V divideToIntegralValue(Number n, DivisionMode divisionMode);
    
    /**
     * Computes division remainder (modulus), using {@link DivisionMode#TRUNCATED} division mode.
     * 
     * @param n number to divide this by (dividend)
     * @return division remainder (modulus) of this / n, using truncated division.
     * 
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     * 
     * @see #divideAndRemainder(Number, DivisionMode)
     * @see DivisionMode
     */
    T remainder(Number n);
    
    /**
     * Computes division remainder (modulus), using the specified division mode.
     * 
     * @param n number to divide this by (dividend)
     * @param divisionMode division mode to use if dividend or divisor is negative.
     * @return division remainder (modulus) of this / n, using specified division mode.
     * 
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     * 
     * @see #divideAndRemainder(Number, DivisionMode)
     * @see DivisionMode
     */
    T remainder(Number n, DivisionMode divisionMode);
    
    /**
     * Returns integral quotient and fractional remainder of this/n. 
     * Uses {@link DivisionMode#TRUNCATED} division mode.
     * 
     * @param n number to divide this by (dividend)
     * @return Two {@code Number} objects. Guaranteed to be two non-null elements. 
     *         First is a {@code BigInteger}, second is a {@code LongFraction}.
     * 
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     * 
     * @see #divideAndRemainder(Number, DivisionMode)
     * @see DivisionMode
     */
    Number[] divideAndRemainder(Number n);
    
    /**
     * Returns integral quotient and fractional remainder of this/n, using 
     * specified division mode. If the quotient and remainder are q and r, 
     * respectively, then the results satisfy the following equations:
     * <br>
     * <br>
     * {@code     this/n = q + r/n}<br>
     * {@code     this = q*n + r}<br>
     * 
     * @param n  Number to divide {@code this} by.
     * @param divisionMode Division mode to use if dividend or divisor is negative.
     * @return Two {@code Number} objects. Guaranteed to be two non-null elements. 
     *         First is a {@code BigInteger}, second is a {@code LongFraction}.
     * 
     * @throws IllegalArgumentException if n is null.
     * @throws ArithmeticException if n == 0.
     * 
     * @see DivisionMode
     */
    Number[] divideAndRemainder(Number n, DivisionMode divisionMode);

    /**
     * Returns the smaller of this and n. If they have equal value, this is
     * returned. Worth noting: if n is smaller, the returned Number is n,
     * <i>not</i> a BigFraction representing n.
     *
     * @param n number to compare to this
     * @return smaller of this and n
     * @throws IllegalArgumentException if n is null
     */
    Number min(Number n);

    /**
     * Returns the larger of this and n. If they have equal value, this is
     * returned. Worth noting: if n is larger, the returned Number is n,
     * <i>not</i> a BigFraction representing n.
     *
     * @param n number to compare to this
     * @return larger of this and n
     * @throws IllegalArgumentException if n is null
     */
    Number max(Number n);

    /**
     * Returns the mediant of this and n. The mediant of a/b and c/d is
     * (a+c)/(b+d). It is guaranteed to be between a/b and c/d. Not to be
     * confused with the median!
     *
     * @param n other number to use to compute mediant
     * @return mediant of this and n
     * @throws IllegalArgumentException if n is null
     */
    T mediant(Number n);

    /**
     * Returns 1/this.
     *
     * @return 1/this
     *
     * @throws ArithmeticException if this == 0.
     */
    T reciprocal();

    /**
     * Returns the complement of this fraction, which is equal to 1 - this.
     * Useful for probabilities/statistics.
     *
     * @return 1-this
     */
    T complement();

    /**
     * Returns -this. If this is zero, returns zero.
     *
     * @return equivalent of {@code this.multiply(-1)}
     */
    T negate();

    /**
     * Returns the absolute value of this.
     *
     * @return absolute value of this.
     */
    T abs();

    /**
     * Returns this, with sign set to the sign of {@code sgn} parameter. Another
     * way of saying it: returns the equivalent of
     * {@code this.abs().multiply(Math.signum(sgn))}.<br>
     * <br>
     * Important Note: If this is zero, always returns zero. No exception
     * thrown, even if we are trying to set the sign of 0 to positive or
     * negative.
     *
     * @param sgn an integer less than, equal to, or greater than 0, whose sign
     * will be assigned to the returned fraction.
     * @return equivalent of {@code this.abs().multiply(Math.signum(sgn))}.
     */
    T withSign(int sgn);

    /**
     * Returns -1, 0, or 1, representing the sign of this fraction.
     *
     * @return -1, 0, or 1, representing the sign of this fraction.
     */
    int signum();

    /**
     * Returns the integer part of this fraction; that is, the part that would
     * come before the decimal point if this were written as a decimal number.
     * Carries the same sign as this fraction.<br>
     * <br>
     * Equivalent to {@code getIntegerPart(DivisionMode.TRUNCATED)}
     *
     * @return integer part of this fraction (numerator/denominator), using
     * TRUCATED division mode.
     *
     * @see #getParts(DivisionMode divisionMode)
     */
    V getIntegerPart();

    /**
     * Returns the integer part of this fraction; that is, the part that would
     * come before the decimal point if this were written as a decimal number.
     * Carries the same sign as this fraction.
     *
     * @param divisionMode Division mode to use when computing quotient. Only
     * relevant if this is negative.
     * @return integer part of this fraction (numerator/denominator), using
     * specified division mode.
     *
     * @see #getParts(DivisionMode divisionMode)
     */
    V getIntegerPart(DivisionMode divisionMode);

    /**
     * Returns the fraction part of this fraction; that is, the fraction that
     * represents the part that would come after the decimal point if this were
     * written as a decimal number. Carries the same sign as this fraction,
     * unless the fraction part is zero.<br>
     * <br>
     * Equivalent to {@code getFractionPart(DivisionMode.TRUNCATED)}
     *
     * @return fractional part of this fraction (i.e.
     * {@code (numerator%denominator)/denominator)}), using TRUNCATED division
     * mode.
     *
     * @see #getParts(DivisionMode divisionMode)
     */
    T getFractionPart();

    /**
     * Returns the fraction part of this fraction; that is, the remainder when
     * the numerator is divided by the denominator when using the specified
     * division mode.
     *
     * @param divisionMode Division mode to use when computing remainder. Only
     * relevant if this is negative.
     * @return fractional part of this fraction (i.e.
     * {@code (numerator%denominator)/denominator)}), using specified division
     * mode.
     *
     * @see #getParts(DivisionMode divisionMode)
     */
    T getFractionPart(DivisionMode divisionMode);

    /**
     * Returns the integer and fraction parts of this fraction. The return array
     * is guaranteed to have exactly two elements. The first is guaranteed to be
     * a BigInteger, equivalent to the result of getIntegerPart(). The second
     * element is guaranteed to be a BigFraction, equivalent to the result of
     * getFractionPart().<br>
     * <br>
     * Equivalent to {@code getParts(DivisionMode.TRUNCATED)}
     *
     * @return Two {@code Number} objects. Guaranteed to be two non-null
     * elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
     * These represent the part that would be written before the decimal, and
     * the part that would be after the decimal, if this fraction were written
     * in decimal format.
     *
     * @see #getParts(DivisionMode divisionMode)
     */
    Number[] getParts();

    /**
     * Returns the integer and fraction parts of this fraction. The return array
     * is guaranteed to have exactly two elements. The first is guaranteed to be
     * a BigInteger, and the second is guaranteed to be a BigFraction.<br>
     * <br>
     * The first element is the result of integer division of numerator by
     * denominator, using the supplied division mode. The second element is the
     * fraction given by numerator mod denominator, using the given division
     * mode.<br>
     * <br>
     * Note that the division mode only matters if this fraction is negative.
     * Because the sign of a BigFraction is always carried by the numerator, the
     * FLOOR and EUCLIDEAN division modes will always produce the same
     * result.<br>
     * <br>
     * Some examples:<br>
     * <br>
     * <table summary="Examples of rounding modes" border="1" cellpadding="4">
     * <tr><th>Fraction</th><th>TRUNCATE</th><th>FLOOR</th><th>EUCLIDEAN</th></tr>
     * <tr><td> 4/1</td><td>[ 4, 0/1]</td><td>[ 4, 0/1]</td><td>[ 4,
     * 0/1]</td></tr>
     * <tr><td> 4/3</td><td>[ 1, 1/3]</td><td>[ 1, 1/3]</td><td>[ 1,
     * 1/3]</td></tr>
     * <tr><td> 2/7</td><td>[ 0, 2/7]</td><td>[ 0, 2/7]</td><td>[ 0,
     * 2/7]</td></tr>
     * <tr><td>-2/7</td><td>[ 0, -2/7]</td><td>[-1, 5/7]</td><td>[-1,
     * 5/7]</td></tr>
     * <tr><td>-4/3</td><td>[-1, -1/3]</td><td>[-2, 2/3]</td><td>[-2,
     * 2/3]</td></tr>
     * <tr><td>-4/1</td><td>[-4, 0/1]</td><td>[-4, 0/1]</td><td>[-4,
     * 0/1]</td></tr>
     * </table>
     *
     * @param divisionMode Division mode to use when computing parts. Only
     * relevant if this is negative.
     * @return Two {@code Number} objects. Guaranteed to be two non-null
     * elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
     * These represent the part that would be written before the decimal, and
     * the part that would be after the decimal, if this fraction were written
     * in decimal format.
     *
     * @see DivisionMode
     */
    Number[] getParts(DivisionMode divisionMode);

    /**
     * Returns this rounded to the nearest whole number, using
     * RoundingMode.HALF_UP as the default rounding mode.
     *
     * @return this fraction rounded to nearest whole number, using
     * RoundingMode.HALF_UP.
     */
    V round();

    /**
     * Returns this fraction rounded to a whole number, using the given rounding
     * mode.
     *
     * @param roundingMode rounding mode to use
     * @return this fraction rounded to a whole number, using the given rounding
     * mode.
     *
     * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but this
     * fraction does not exactly represent an integer.
     */
    public V round(RoundingMode roundingMode);

    /**
     * Rounds this fraction to the nearest multiple of the given number, using
     * HALF_UP rounding method.
     *
     * @param n number to which we will round to the nearest multiple
     *
     * @return this value, rounded to the nearest multiple of n
     *
     * @throws IllegalArgumentException If n is null.
     * @throws ArithmeticException If n is zero or negative.
     */
    T roundToNumber(Number n);

    /**
     * Rounds this fraction to the nearest multiple of the given number, using
     * the specified rounding method.<br>
     * <br>
     * Note for HALF_EVEN rounding method: this rounds to the nearest even
     * multiple of n, which may or may not be even. For example, if rounding to
     * the nearest 2, every result will be even. So 9 rounded to nearest 2 with
     * HALF_EVEN will round to 8, since 8=2*4 (4 being an even number), whereas
     * 10=2*5 (5 being odd).
     *
     * @param n number to which we will round to the nearest multiple
     * @param roundingMode rounding mode to use if the answer must be rounded
     *
     * @return this value, rounded to the nearest multiple of n
     *
     * @throws IllegalArgumentException If n is null.
     * @throws ArithmeticException If n is zero or negative.
     * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but this
     * fraction is not an exact multiple of the given value.
     */
    T roundToNumber(Number n, RoundingMode roundingMode);

    /**
     * Rounds the given fraction to the nearest fraction having the given
     * denominator, using HALF_UP rounding method, and returns the numerator of
     * that fraction.
     *
     * @param newDenominator denominator of fraction to round to.
     *
     * @return numerator of rounded fraction (unreduced)
     *
     * @throws IllegalArgumentException If newDenominator is null.
     * @throws ArithmeticException If newDenominator is zero or negative.
     *
     * @see #roundToDenominator(BigInteger, RoundingMode)
     */
    V roundToDenominator(V newDenominator);

    /**
     * Rounds the given fraction to the nearest fraction having the given
     * denominator, using the given rounding method, and returns the numerator
     * of that fraction.<br>
     * <br>
     * For example, given the fraction 7/15, if you wanted to know the nearest
     * fraction with denominator 6, it would be 2.8/6, which rounds to 3/6. This
     * function would return 3.<br>
     * <br>
     * Note: this is not reduced--3/6 is equivalent to 1/2, but this function
     * would still return 3. If newDenominator is 1, this method is equivalent
     * to round(). If this object is negative, the returned numerator will also
     * be negative.
     *
     * @param newDenominator denominator of fraction to round to.
     * @param roundingMode rounding mode to use if the answer must be rounded.
     *
     * @return numerator of rounded fraction (unreduced)
     *
     * @throws IllegalArgumentException If newDenominator is null.
     * @throws ArithmeticException If newDenominator is zero or negative.
     * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but this
     * fraction cannot be represented exactly as a fraction with the given
     * denominator.
     */
    V roundToDenominator(V newDenominator, RoundingMode roundingMode);
    
    
    /**
     * Returns the greatest common divisor (also called greatest common factor) of
     * {@code this} and {@code n}.<br>
     * <br>
     * If {@code this} and {@code n} are both zero, returns {@code 0/1}.<br>
     * <br>
     * Note: The result will always be nonnegative, regardless of the signs of the inputs.<br>
     * <br>
     * When dealing with fractions, the divisors of a/b are: 
     *   (a/b)/1, (a/b)/2, (a/b)/3, ... (a/b)/n.
     * Thus gcd(n1/d1, n2/d2) gives the largest fraction n3/d3, 
     * such that (n1/d1)/(n3/d3) is an integer, and (n2/d2)/(n3/d3) is an integer.
     * 
     * @param n other value to compute gcd from.
     * @return greatest common divisor of {@code this} and {@code n}.
     */
    T gcd(Number n);
    
    /**
     * Returns least common multiple of {@code this} and {@code n}.<br>
     * <br>
     * If {@code this} or {@code n} is zero, returns {@code 0/1}.<br>
     * <br>
     * Note: The result will always be nonnegative, regardless of the signs of the inputs.<br>
     * <br>
     * When dealing with fractions, the multiples of a/b are: 
     *   (a/b)*1, (a/b)*2, (a/b)*3, ... (a/b)*n.
     * Thus lcm(n1/d1, n2/d2) gives the smallest fraction n3/d3, 
     * such that (n3/d3)/(n1/d1) is an integer, and (n3/d3)/(n2/d2) is an integer.
     * 
     * @param n other value to compute lcm from.
     * @return least common multiple of {@code this} and {@code n}
     */
    T lcm(Number n);
    
    /**
     * Returns this^exponent.<br>
     * <br>
     * Note: 0^0 will return 1/1. This is consistent with {@link Math#pow(double, double)},
     * {@link BigInteger#pow(int)}, and {@link BigDecimal#pow(int)}.
     * 
     * @param exponent power to raise this fraction to.
     * @return this^exponent
     * 
     * @throws ArithmeticException if {@code this == 0 && exponent < 0}.
     * @throws ArithmeticException if {@code exponent == Integer.MIN_VALUE}.
     */
    T pow(int exponent);
    
    /**
     * Returns a value approximately equal to this^exponent, where the exponent itself may be a fraction.<br>
     * <br>
     * Equivalent to: {@code this.pow(exponent.getNumerator()).nthRoot(exponent.getDenominator(), epsilon)}<br>
     * <br>
     * <b>WARNING</b>: Read important notes in {@link #nthRoot(int n, BigFraction epsilon)}.
     * 
     * @param exponent power to raise this fraction to.
     * @param epsilon value used in the <a href="https://en.wikipedia.org/wiki/Nth_root_algorithm">nth-root algorithm</a>.
     * 
     * @return an approximation of {@code this^(exponent)}
     * 
     * @throws ArithmeticException if {@code this == 0 && exponent < 0}.
     * @throws ArithmeticException if {@code abs(exponent.numerator) >= Integer.MAX_VALUE 
     *                            || abs(exponent.numerator) >= Integer.MAX_VALUE}.
     * @throws ArithmeticException if {@code this < 0 && (exponent.denominator is even)}.
     * @throws IllegalArgumentException if {@code epsilon == null || epsilon <= 0}.
     * 
     * @see #pow(int n)
     * @see #nthRoot(int n, BigFraction epsilon)
     */
//    T pow(T exponent, T epsilon);
    
    /**
     * Returns a value approximately equal to the nth root of this, i.e. {@code this^(1/n)}.
     * If the solution to this is rational, then that exact value will be returned. Otherwise,
     * returns an approximation.<br>
     * <br>
     * If this is negative, and n is odd, then the result is equivalent to: {@code -(this.nthRoot(n, epsilon))}.<br>
     * If this is negative, and n is even, then an exception is thrown because there is no valid answer.<br>
     * <br>
     * <b>WARNING</b>: This implementation is very slow. The mathematics of this are beyond my
     * comfort zone and any contributions would be appreciated. This implementation starts by
     * doing something like a binary search to find the closest BigInteger to the nth root of
     * the numerator and denominator, separately. If both of those are exact answers, then that
     * value is returned. Otherwise, it is used as the initial guess to the
     * <a href="https://en.wikipedia.org/wiki/Nth_root_algorithm">nth-root algorithm</a>.
     * 
     * @param n root to find.
     * @param epsilon value used in the <a href="https://en.wikipedia.org/wiki/Nth_root_algorithm">nth-root algorithm</a>.
     * 
     * @return an approximation of {@code this^(1/n)}
     * 
     * @throws ArithmeticException if {@code n == 0}.
     * @throws ArithmeticException if {@code n == Integer.MIN_VALUE}.
     * @throws ArithmeticException if {@code this < 0 && (n is even)}.
     * @throws IllegalArgumentException if {@code epsilon == null || epsilon <= 0}.
     */
//    T nthRoot(int n, T epsilon);
}
