package com.github.kiprobinson.util;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Arbitrary-precision fraction, utilizing BigIntegers for numerator and
 * denominator. Fraction is always kept in lowest terms. Fraction is
 * immutable, and guaranteed not to have a null numerator or denominator.
 * Denominator will always be positive (so sign is carried by numerator,
 * and a zero-denominator is impossible).
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public final class BigFraction extends Number implements Comparable<Number>
{
  private static final long serialVersionUID = 3L; //because Number is Serializable
  private final BigInteger numerator;
  private final BigInteger denominator;
  
  //some constants used
  private final static BigInteger BIGINT_TWO = BigInteger.valueOf(2);
  private final static BigInteger BIGINT_FIVE = BigInteger.valueOf(5);
  
  /** The value 0/1. */
  public final static BigFraction ZERO = new BigFraction(BigInteger.ZERO, BigInteger.ONE, Reduced.YES);
  /** The value 1/1. */
  public final static BigFraction ONE = new BigFraction(BigInteger.ONE, BigInteger.ONE, Reduced.YES);
  /** The value 1/2. */
  public final static BigFraction ONE_HALF = new BigFraction(BigInteger.ONE, BIGINT_TWO, Reduced.YES);
  /** The value 1/10. */
  public final static BigFraction ONE_TENTH = new BigFraction(BigInteger.ONE, BigInteger.TEN, Reduced.YES);
  /** The value 10/1. */
  public final static BigFraction TEN = new BigFraction(BigInteger.TEN, BigInteger.ONE, Reduced.YES);
  
  private static enum Reduced { YES, NO };
  private static enum FareyMode { NEXT, PREV, CLOSEST };
  private static enum RemainderMode { QUOTIENT, REMAINDER, BOTH };
  
  /**
   * <strong>Note:</strong> {@link #valueOf(Number)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param n Number to convert to BigFraction.
   * @see #valueOf(Number)
   */
  public BigFraction(Number n)
  {
    BigFraction bf = valueOf(n);
    this.numerator = bf.numerator;
    this.denominator = bf.denominator;
  }
  
  /**
   * <strong>Note:</strong> {@link #valueOf(Number, Number)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param numerator numerator of new BigFraction
   * @param denominator denominator of new BigFraction
   * @see #valueOf(Number, Number)
   */
  public BigFraction(Number numerator, Number denominator)
  {
    BigFraction bf = valueOf(numerator, denominator);
    this.numerator = bf.numerator;
    this.denominator = bf.denominator;
  }
  
  /**
   * <strong>Note:</strong> {@link #valueOf(String)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param s String to convert to parse as BigFraction
   * @see #valueOf(String)
   */
  public BigFraction(String s)
  {
    BigFraction bf = valueOf(s);
    this.numerator = bf.numerator;
    this.denominator = bf.denominator;
  }
  
  /**
   * Constructs a BigFraction from given number. If the number is not one of the
   * known implementations of Number class, then {@link Number#doubleValue()}
   * will be used for construction.<br>
   * <br>
   * Warning: when using floating point numbers, round-off error can result
   * in answers that are unexpected. For example:<br> 
   * {@code     System.out.println(BigFraction.valueOf(1.1))}<br>
   * will print:<br>
   * {@code     2476979795053773/2251799813685248}<br>
   * <br>
   * This is because 1.1 cannot be expressed exactly in binary form. The
   * computed fraction is exactly equal to the internal representation of
   * the double-precision floating-point number. (Which, for {@code 1.1}, is:
   * {@code (-1)^0 * 2^0 * (1 + 0x199999999999aL / 0x10000000000000L)}.)<br>
   * <br>
   * In many cases, {@code BigFraction.valueOf(Double.toString(d))} may give the result
   * the user expects.
   * 
   * @param n Any Number to be converted to a BigFraction
   * @return a fully reduced fraction equivalent to {@code n}. Guaranteed to be non-null.
   * 
   * @throws IllegalArgumentException if n is null.
   */
  public static BigFraction valueOf(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null parameter.");
    
    if(n instanceof BigFraction)
      return (BigFraction)n;
    else if(isInt(n))
      return new BigFraction(toBigInteger(n), BigInteger.ONE, Reduced.YES);
    else if(n instanceof BigDecimal)
      return valueOfHelper((BigDecimal)n);
    else
      return valueOfHelper(n.doubleValue());
  }
  
  /**
   * Constructs a BigFraction with given numerator and denominator. Fraction
   * will be reduced to lowest terms. If fraction is negative, negative sign will
   * be carried on numerator, regardless of how the values were passed in. The numerator
   * and denominator can both be non-integers.<br>
   * <br>
   * Example: {@code BigFraction.valueOf(8.5, -6.25); //-34/25}<br>
   * <br>
   * Warning: when using floating point numbers, round-off error can result
   * in answers that are unexpected. For example,<br>
   * {@code     System.out.println(BigFraction.valueOf(1.1))}<br>
   * <br>
   * This is because 1.1 cannot be expressed exactly in binary form. The
   * computed fraction is exactly equal to the internal representation of
   * the double-precision floating-point number. (Which, for {@code 1.1}, is:
   * {@code (-1)^0 * 2^0 * (1 + 0x199999999999aL / 0x10000000000000L)}.)<br>
   * <br>
   * In many cases, {@code BigFraction.valueOf(Double.toString(d))} may give the result
   * the user expects.
   * 
   * @param numerator any Number to be used as the numerator. This does not need to be an integer.
   * @param denominator any Number to be used as the denominator. This does not need to be an integer.
   * @return a fully reduced fraction equivalent to {@code numerator/denominator}. Guaranteed to be non-null.
   * 
   * @throws ArithmeticException if denominator == 0.
   * @throws IllegalArgumentException if numerator or denominator is null.
   */
  public static BigFraction valueOf(Number numerator, Number denominator)
  {
    if(numerator == null)
      throw new IllegalArgumentException("Numerator is null.");
    
    if(denominator == null)
      throw new IllegalArgumentException("Denominator is null.");
    
    if(isInt(numerator) && isInt(denominator))
      return new BigFraction(toBigInteger(numerator), toBigInteger(denominator), Reduced.NO);
    else if(isFloat(numerator) && isFloat(denominator))
      return valueOfHelper(numerator.doubleValue(), denominator.doubleValue());
    else if(numerator instanceof BigDecimal && denominator instanceof BigDecimal)
      return valueOfHelper((BigDecimal)numerator, (BigDecimal)denominator);
    
    //else: convert numerator and denominator to fractions, and divide
    //(n1/d1)/(n2/d2) = (n1*d2)/(d1*n2)
    BigFraction f1 = valueOf(numerator);
    BigFraction f2 = valueOf(denominator);
    return new BigFraction(f1.numerator.multiply(f2.denominator), f1.denominator.multiply(f2.numerator), Reduced.NO);
  }
  
  /**
   * Constructs a BigFraction from a String. Expected format is numerator/denominator,
   * but /denominator part is optional. Either numerator or denominator may be a floating-point
   * decimal number, which is in the same format as a parameter to the
   * {@link BigDecimal#BigDecimal(String)} constructor.<br>
   * <br>
   * Examples:<br>
   * {@code BigFraction.valueOf("11"); //11/1}<br>
   * {@code BigFraction.valueOf("22/34"); //11/17}<br>
   * {@code BigFraction.valueOf("2e4/-0.64"); //-174375/4}
   * 
   * @param s a string representation of a number or fraction
   * @return a fully reduced fraction equivalent to the specified string. Guaranteed to be non-null.
   * 
   * @throws NumberFormatException  if the string cannot be properly parsed.
   * @throws ArithmeticException if denominator == 0.
   * @throws IllegalArgumentException if s is null.
   * 
   * @see BigDecimal#BigDecimal(String)
   */
  public static BigFraction valueOf(String s)
  {
    if(s == null)
      throw new IllegalArgumentException("Null argument.");
    
    int slashPos = s.indexOf('/');
    if(slashPos < 0)
    {
      return valueOfHelper(new BigDecimal(s));
    }
    else
    {
      BigDecimal num = new BigDecimal(s.substring(0, slashPos));
      BigDecimal den = new BigDecimal(s.substring(slashPos+1, s.length()));
      return valueOfHelper(num, den);
    }
  }
  
  /**
   * Returns the numerator of this fraction.
   * @return numerator of this fraction.
   */
  public final BigInteger getNumerator()
  {
    return numerator;
  }
  
  /**
   * Returns the denominator of this fraction.
   * @return denominator of this fraction.
   */
  public final BigInteger getDenominator() {
    return denominator;
  }
  
  /**
   * Returns this + n.
   * @param n number to be added to this
   * @return this + n
   * @throws IllegalArgumentException if n is null.
   */
  public BigFraction add(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    if(isInt(n))
    {
      //n1/d1 + n2 = (n1 + d1*n2)/d1
      return new BigFraction(numerator.add(denominator.multiply(toBigInteger(n))),
                             denominator, Reduced.YES);
    }
    else
    {
      BigFraction f = valueOf(n);
      
      //n1/d1 + n2/d2 = (n1*d2 + d1*n2)/(d1*d2)
      return new BigFraction(numerator.multiply(f.denominator).add(denominator.multiply(f.numerator)),
                             denominator.multiply(f.denominator), Reduced.NO);
    }
  }
  
  /**
   * Returns a + b, represented as a BigFraction. Equivalent to {@code BigFraction.valueOf(a).add(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to be added
   * @param b number to be added
   * @return a + b
   * @throws IllegalArgumentException if a or b is null.
   */
  public static BigFraction sum(Number a, Number b)
  {
    return valueOf(a).add(b);
  }
  
  /**
   * Returns this - n.
   * @param n number to be subtracted from this
   * @return this - n
   * @throws IllegalArgumentException if n is null.
   */
  public BigFraction subtract(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    if(isInt(n))
    {
      //n1/d1 - n2 = (n1 - d1*n2)/d1
      return new BigFraction(numerator.subtract(denominator.multiply(toBigInteger(n))),
                             denominator, Reduced.YES);
    }
    else
    {
      BigFraction f = valueOf(n);
      
      //n1/d1 - n2/d2 = (n1*d2 - d1*n2)/(d1*d2)
      return new BigFraction(numerator.multiply(f.denominator).subtract(denominator.multiply(f.numerator)),
                             denominator.multiply(f.denominator), Reduced.NO);
    }
  }
  
  /**
   * Returns n - this. Sometimes this results in cleaner code than
   * rearranging the code to use subtract().
   * 
   * @param n number to subtract this from
   * @return n - this
   * @throws IllegalArgumentException if n is null.
   */
  public BigFraction subtractFrom(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    if(isInt(n))
    {
      //n1 - n2/d2 = (d2*n1 - n2)/d2
      return new BigFraction(denominator.multiply(toBigInteger(n)).subtract(numerator),
                             denominator, Reduced.YES);
    }
    else
    {
      BigFraction f = valueOf(n);
      
      //n1/d1 - n2/d2 = (n1*d2 - d1*n2)/(d1*d2)
      return new BigFraction(f.numerator.multiply(denominator).subtract(f.denominator.multiply(numerator)),
                             f.denominator.multiply(denominator), Reduced.NO);
    }
  }
  
  /**
   * Returns a - b, represented as a BigFraction. Equivalent to {@code BigFraction.valueOf(a).subtract(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to subtract from (minuend)
   * @param b number to be subtracted from a (subtrahend)
   * @return a - b
   * @throws IllegalArgumentException if a or b is null.
   */
  public static BigFraction difference(Number a, Number b)
  {
    return valueOf(a).subtract(b);
  }
  
  /**
   * Returns this * n.
   * @param n number to be multiplied by this
   * @return this * n
   * @throws IllegalArgumentException if n is null.
   */
  public BigFraction multiply(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    BigFraction f = valueOf(n);
    
    //(n1/d1)*(n2/d2) = (n1*n2)/(d1*d2)
    return new BigFraction(numerator.multiply(f.numerator), denominator.multiply(f.denominator), Reduced.NO);
  }
  
  /**
   * Returns a * b, represented as a BigFraction. Equivalent to {@code BigFraction.valueOf(a).multiply(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to be multiplied
   * @param b number to be multiplied
   * @return a * b
   * @throws IllegalArgumentException if a or b is null.
   */
  public static BigFraction product(Number a, Number b)
  {
    return valueOf(a).multiply(b);
  }
  
  /**
   * Returns this / n.
   * 
   * @param n number to divide this by (divisor)
   * @return this / n
   * @throws IllegalArgumentException if n is null.
   * @throws ArithmeticException if n == 0.
   */
  public BigFraction divide(Number n)
  {
    //division is the same thing as constructing new fraction
    return valueOf(this, n);
  }
  
  /**
   * Returns n / this. Sometimes this results in cleaner code than
   * rearranging the code to use divide().
   * 
   * @param n number to be divided by this (dividend)
   * @return n / this
   * @throws IllegalArgumentException if n is null.
   * @throws ArithmeticException if this == 0.
   */
  public BigFraction divideInto(Number n)
  {
    //division is the same thing as constructing new fraction
    return valueOf(n, this);
  }
  
  /**
   * Returns a / b, represented as a BigFraction. Equivalent to {@code BigFraction.valueOf(a).divide(b)}.
   * Also equivalent to {@code BigFraction.valueOf(a, b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to be divided (dividend)
   * @param b number by which to divide (divisor)
   * @return a / b
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   */
  public static BigFraction quotient(Number a, Number b)
  {
    //Note: a/b is the same thing as constructing a new fraction from a and b.
    return valueOf(a, b);
  }
  
  
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
  public BigInteger divideToIntegralValue(Number n)
  {
    return divideToIntegralValue(n, DivisionMode.TRUNCATED);
  }
  
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
  public BigInteger divideToIntegralValue(Number n, DivisionMode divisionMode)
  {
    return (BigInteger)(divideAndRemainderImpl(this, valueOf(n), divisionMode, RemainderMode.QUOTIENT));
  }
  
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
  public BigFraction remainder(Number n)
  {
    return remainder(n, DivisionMode.TRUNCATED);
  }
  
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
  public BigFraction remainder(Number n, DivisionMode divisionMode)
  {
    return (BigFraction)(divideAndRemainderImpl(this, valueOf(n), divisionMode, RemainderMode.REMAINDER));
  }
  
  /**
   * Returns integral quotient and fractional remainder of this/n. Uses {@link DivisionMode#TRUNCATED} division mode.
   * 
   * @param n number to divide this by (dividend)
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
   * 
   * @throws IllegalArgumentException if n is null.
   * @throws ArithmeticException if n == 0.
   * 
   * @see #divideAndRemainder(Number, DivisionMode)
   * @see DivisionMode
   */
  public Number[] divideAndRemainder(Number n)
  {
    return divideAndRemainder(n, DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns integral quotient and fractional remainder of this/n, using specified division mode. If the quotient
   * and remainder are q and r, respectively, then the results satisfy the following equations:<br>
   * <br>
   * {@code     this/n = q + r/n}<br>
   * {@code     this = q*n + r}<br>
   * 
   * @param n  Number to divide {@code this} by.
   * @param divisionMode Division mode to use if dividend or divisor is negative.
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
   * 
   * @throws IllegalArgumentException if n is null.
   * @throws ArithmeticException if n == 0.
   * 
   * @see DivisionMode
   */
  public Number[] divideAndRemainder(Number n, DivisionMode divisionMode)
  {
    return (Number[])(divideAndRemainderImpl(this, valueOf(n), divisionMode, RemainderMode.BOTH));
  }
  
  /**
   * Private method to do all the work of integer division with fractional remainder.
   * Code is optimized for speed moreso than readability, but I've tried to comment.
   * Returns either just the quotient, just the remainder, or an array of both,
   * depending on the remainder mode.
   */
  private static Object divideAndRemainderImpl(BigFraction a, BigFraction b, DivisionMode divisionMode, RemainderMode remainderMode)
  {
    if(b.numerator.equals(BigInteger.ZERO))
      throw new ArithmeticException("Divide by zero.");
    else if(a.numerator.equals(BigInteger.ZERO))
      return divideAndRemainderReturner(BigInteger.ZERO, BigFraction.ZERO, remainderMode);
    
    //First calculate true a/b value. We don't care about reducing to lowest terms
    //yet, so calculate numerator and denominator separately:
    //  a/b = (a.n/a.d)/(b.n/b.d) = (a.n/a.d)*(b.d/b.n) = (a.n*b.d)/(a.d*b.n)
    //also worth noting: sign(a)==sign(num), sign(b)==sign(den)
    BigInteger num = a.numerator.multiply(b.denominator);
    BigInteger den = a.denominator.multiply(b.numerator);
    
    //BigInteger.divideAndRemainder() uses TRUNCATED division to give us values q,r such that:  num/den = q + r/den
    //For other division modes, we may need to adjust q,r to new values q',r'. If we adjust q by adjustment x, i.e. q'=q+x, then:
    //  q + r/den = q' + r'/den
    //  q + r/den = q + x + r'/den
    //      r/den =     x + r'/den
    //     r'/den = r/den - x
    //         r' = r - x*den
    //In actuality, x will either be -1, 0, or 1.
    int adjustment = 0;
    if(divisionMode == DivisionMode.FLOORED && ((num.signum() < 0 || den.signum() < 0) && num.signum() != den.signum()))
    {
      //floor is equivalent to truncation for positive quotient, but for negative quotient we have to subtract one
      adjustment = -1;
    }
    else if(divisionMode == DivisionMode.EUCLIDEAN && num.signum() < 0)
    {
      //Euclidean division picks a quotient to ensure the remainder is always positive.
      // *  b > 0: q = floor(a/b)
      // *  b < 0: q = ciel(a/b)
      
      //For the four different combinations of signs of the operators, two are the same as truncation,
      //and two require additional modification:
      //   + / +: +  =>  floor(q) == trunc(q)
      //   + / -: -  =>  ciel(q)  == trunc(q)
      //   - / +: -  =>  floor(q) == trunc(q) - 1  **modification required
      //   - / -: +  =>  ciel(q)  == trunc(q) + 1  **modification required
      if(den.signum() > 0)
        adjustment = -1;
      else
        adjustment = 1;
    }
    
    
    //we may only need one or the other of q,r. Only compute the ones that we need.
    BigInteger q=null, r=null;
    if(remainderMode == RemainderMode.REMAINDER)
    {
      //if we are in remainder mode, we never care about the quotient
      r = num.remainder(den);
    }
    else if(adjustment == 0 && remainderMode == RemainderMode.QUOTIENT)
    {
      //in quotient mode, if we have an adjustment we have to get both quotient and remainder, because we cancel the adjustment if the
      //remainder is 0. But if adjustment is already 0, we can get only the quotient.
      q = num.divide(den);
    }
    else
    {
      BigInteger[] divmod = num.divideAndRemainder(den);
      q = divmod[0];
      r = divmod[1];
    }
    
    //if the remainder is 0, we don't do any adjustments, and we already know the remainder will
    //be zero, so go ahead and return this.
    if(r != null && r.equals(BigInteger.ZERO))
      return divideAndRemainderReturner(q, BigFraction.ZERO, remainderMode); //or could do: adjustment=0
    
    //avoid doing unnecessary math... at this point if we got both q and r, but only need q, we can drop r.
    if(r != null && remainderMode == RemainderMode.QUOTIENT)
      r = null;
    
    if(adjustment == -1)
    {
      q = (q == null ? null : q.subtract(BigInteger.ONE));  //q' = q + (-1)
      r = (r == null ? null : r.add(den));                  //r' = r - (-1)*den
    }
    else if(adjustment == 1)
    {
      q = (q == null ? null : q.add(BigInteger.ONE));  //q' = q + (1)
      r = (r == null ? null : r.subtract(den));        //r' = r - (1)*den
    }
    
    //At this point we have:
    //  num/den = q + r/den
    //We want to compute q", r", such that:  a/b = q" + r"/b
    //We know that a/b = num/den, and q = q". So we are left with:
    //  (r"/b)=(r/den)
    //  r" = r * b / den = (r * b.n)/(b.d * den)
    
    BigFraction rFract = (r == null ? null : new BigFraction(r.multiply(b.numerator), b.denominator.multiply(den), Reduced.NO));
    
    return divideAndRemainderReturner(q, rFract, remainderMode);
  }
  
  /**
   * Helper method to handle return value for divideAndRemainderImpl.
   */
  private static Object divideAndRemainderReturner(BigInteger q, BigFraction r, RemainderMode remainderMode)
  {
    if(remainderMode == RemainderMode.QUOTIENT)
      return q;
    if(remainderMode == RemainderMode.REMAINDER)
      return r;
    else
      return new Number[]{q, r};
  }
  
  
  
  /**
   * Returns result of integer division a / b, using truncated division mode.
   * Equivalent to {@code BigFraction.valueOf(a).divideToIntegralValue(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @return integral quotient of a / b, using TRUNCATED division mode
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #divideToIntegralValue(Number n)
   * @see DivisionMode
   */
  public static BigInteger integralQuotient(Number a, Number b)
  {
    return valueOf(a).divideToIntegralValue(b);
  }
  
  /**
   * Returns result of integer division a / b, using specified division mode.
   * Equivalent to {@code BigFraction.valueOf(a).divideToIntegralValue(b, divisionMode)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @param divisionMode division mode to use when dividend or divisor is negative.
   * @return integral quotient of a / b, using specified division mode
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #divideToIntegralValue(Number n, DivisionMode divisionMode)
   * @see DivisionMode
   */
  public static BigInteger integralQuotient(Number a, Number b, DivisionMode divisionMode)
  {
    return valueOf(a).divideToIntegralValue(b, divisionMode);
  }
  
  
  /**
   * Returns fractional remainder of integer division a / b, using truncated division mode.
   * Equivalent to {@code BigFraction.valueOf(a).remainder(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @return division remainder (modulus) of a / b, using TRUNCATED division mode
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #remainder(Number n)
   * @see DivisionMode
   */
  public static BigFraction remainder(Number a, Number b)
  {
    return valueOf(a).remainder(b);
  }
  
  /**
   * Returns fractional remainder of integer division a / b, using specified division mode.
   * Equivalent to {@code BigFraction.valueOf(a).remainder(b, divisionMode)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @param divisionMode division mode to use when dividend or divisor is negative.
   * @return division remainder (modulus) of a / b, using specified division mode
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #remainder(Number n, DivisionMode divisionMode)
   * @see DivisionMode
   */
  public static BigFraction remainder(Number a, Number b, DivisionMode divisionMode)
  {
    return valueOf(a).remainder(b, divisionMode);
  }
  
  /**
   * Returns integral quotient and fractional remainder of integer division a / b, using truncated division mode.
   * Equivalent to {@code BigFraction.valueOf(a).divideAndRemainder(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #divideAndRemainder(Number n)
   * @see DivisionMode
   */
  public static Number[] quotientAndRemainder(Number a, Number b)
  {
    return valueOf(a).divideAndRemainder(b);
  }
  
  /**
   * Returns integral quotient and fractional remainder of integer division a / b, using specified division mode.
   * Equivalent to {@code BigFraction.valueOf(a).divideAndRemainder(b, divisionMode)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @param divisionMode division mode to use when dividend or divisor is negative.
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #divideAndRemainder(Number n, DivisionMode divisionMode)
   * @see DivisionMode
   */
  public static Number[] quotientAndRemainder(Number a, Number b, DivisionMode divisionMode)
  {
    return valueOf(a).divideAndRemainder(b, divisionMode);
  }
  
  /**
   * Returns the greatest common divisor (also called greatest common factor) of
   * {@code this} and {@code n}.<br>
   * <br>
   * If {@code this} and {@code n} are both zero, returns {@code 0/1}.<br>
   * <br>
   * Note: The result will always be nonnegative, regardless of the signs of the inputs.<br>
   * <br>
   * When dealing with fractions, the divisors of a/b are: (a/b)/1, (a/b)/2, (a/b)/3, ... (a/b)/n.
   * Thus gcd(n1/d1, n2/d2) gives the largest fraction n3/d3, such that (n1/d1)/(n3/d3) is an integer,
   * and (n2/d2)/(n3/d3) is an integer.
   * 
   * @param n other value to compute gcd from.
   * @return greatest common divisor of {@code this} and {@code n}.
   */
  public BigFraction gcd(Number n) {
    BigFraction f = valueOf(n);
    
    if(this.numerator.equals(BigInteger.ZERO))
      return f.abs();
    if(f.numerator.equals(BigInteger.ZERO))
      return this.abs();
    
    //gcd((a/b),(c/d)) = gcd(a,c) / lcm(b,d)
    //                 = gcd(a,c) / (|b*d|/gcd(b,d))
    //Note: this result is guaranteed to be a reduced fraction.
    //If you try to further simplify this to: (gcd(a,c) * gcd(b,d)) / (|b*d|), then the
    //result will not be reduced, and the operation actually takes about 60% longer.
    BigInteger num = this.numerator.gcd(f.numerator);
    BigInteger den = this.denominator.multiply(f.denominator).abs().divide(this.denominator.gcd(f.denominator));
    
    return new BigFraction(num, den, Reduced.YES);
  }
  
  /**
   * Returns least common multiple of {@code this} and {@code n}.<br>
   * <br>
   * If {@code this} or {@code n} is zero, returns {@code 0/1}.<br>
   * <br>
   * Note: The result will always be nonnegative, regardless of the signs of the inputs.<br>
   * <br>
   * When dealing with fractions, the multiples of a/b are: (a/b)*1, (a/b)*2, (a/b)*3, ... (a/b)*n.
   * Thus lcm(n1/d1, n2/d2) gives the smallest fraction n3/d3, such that (n3/d3)/(n1/d1) is an integer,
   * and (n3/d3)/(n2/d2) is an integer.
   * 
   * @param n other value to compute lcm from.
   * @return least common multiple of {@code this} and {@code n}
   */
  public BigFraction lcm(Number n) {
    BigFraction f = valueOf(n);
    
    if(this.numerator.equals(BigInteger.ZERO) || f.numerator.equals(BigInteger.ZERO))
      return BigFraction.ZERO;
    
    //lcm((a/b),(c/d)) = lcm(a,c) / gcd(b,d)
    //                 = (|a*c| / gcd(a,c)) / gcd(b,d)
    //Note: this result is guaranteed to be a reduced fraction.
    //If you try to further simplify this to: |a*c| / (gcd(a,c) * gcd(b,d)), then the
    //result will not be reduced, and the operation actually takes about 60% longer.
    BigInteger num = this.numerator.multiply(f.numerator).abs().divide(this.numerator.gcd(f.numerator));
    BigInteger den = this.denominator.gcd(f.denominator);
    
    return new BigFraction(num, den, Reduced.YES);
  }
  
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
   */
  public BigFraction pow(int exponent)
  {
    if(exponent < 0 && numerator.equals(BigInteger.ZERO))
      throw new ArithmeticException("Divide by zero: raising zero to negative exponent.");
    
    if(exponent == 0)
      return BigFraction.ONE;
    else if (exponent == 1)
      return this;
    else if (exponent > 0)
      return new BigFraction(numerator.pow(exponent), denominator.pow(exponent), Reduced.YES);
    else
      return new BigFraction(denominator.pow(-exponent), numerator.pow(-exponent), Reduced.YES);
  }
  
  /**
   * Returns 1/this.
   * 
   * @return 1/this
   * 
   * @throws ArithmeticException if this == 0.
   */
  public BigFraction reciprocal()
  {
    if(numerator.equals(BigInteger.ZERO))
      throw new ArithmeticException("Divide by zero: reciprocal of zero.");
    
    return new BigFraction(denominator, numerator, Reduced.YES);
  }
  
  /**
   * Returns the complement of this fraction, which is equal to 1 - this.
   * Useful for probabilities/statistics.
   * 
   * @return 1-this
   */
  public BigFraction complement()
  {
    //1 - n/d == d/d - n/d == (d-n)/d
    return new BigFraction(denominator.subtract(numerator), denominator, Reduced.YES);
  }
  
  /**
   * Returns -this. If this is zero, returns zero.
   * @return equivalent of {@code this.multiply(-1)}
   */
  public BigFraction negate()
  {
    return withSign(-numerator.signum());
  }
  
  /**
   * Returns the absolute value of this.
   * @return absolute value of this.
   */
  public BigFraction abs()
  {
    return withSign(1);
  }
  
  /**
   * Returns this, with sign set to the sign of {@code sgn} parameter.
   * Another way of saying it: returns the equivalent of {@code this.abs().multiply(Math.signum(sgn))}.<br>
   * <br>
   * Important Note: If this is zero, always returns zero. No exception thrown, even if we are trying
   * to set the sign of 0 to positive or negative.
   * 
   * @param sgn an integer less than, equal to, or greater than 0, whose sign will be assigned to the returned fraction.
   * @return equivalent of {@code this.abs().multiply(Math.signum(sgn))}.
   */
  public BigFraction withSign(int sgn)
  {
    if(sgn == 0 || numerator.equals(BigInteger.ZERO))
      return BigFraction.ZERO;
    
    int thisSignum = numerator.signum();
    if((thisSignum < 0 && sgn > 0) || (thisSignum > 0 && sgn < 0))
      return new BigFraction(numerator.negate(), denominator, Reduced.YES);
    
    return this;
  }
  
  /**
   * Returns -1, 0, or 1, representing the sign of this fraction.
   * @return -1, 0, or 1, representing the sign of this fraction.
   */
  public int signum()
  {
    return numerator.signum();
  }
  
  /**
   * Returns the integer part of this fraction; that is, the part that
   * would come before the decimal point if this were written as a decimal
   * number. Carries the same sign as this fraction.<br>
   * <br>
   * Equivalent to {@code getIntegerPart(DivisionMode.TRUNCATED)}
   * 
   * @return integer part of this fraction (numerator/denominator), using TRUCATED division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public BigInteger getIntegerPart()
  {
    return getIntegerPart(DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns the integer part of this fraction; that is, the part that
   * would come before the decimal point if this were written as a decimal
   * number. Carries the same sign as this fraction.
   * 
   * @param divisionMode Division mode to use when computing quotient. Only relevant if this is negative.
   * @return integer part of this fraction (numerator/denominator), using specified division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public BigInteger getIntegerPart(DivisionMode divisionMode)
  {
    if(denominator.equals(BigInteger.ONE))
      return numerator;
    
    BigInteger iPart = numerator.divide(denominator);
    
    if(numerator.signum() < 0 && divisionMode != DivisionMode.TRUNCATED)
      iPart = iPart.subtract(BigInteger.ONE);
    
    return iPart;
  }
  
  /**
   * Returns the fraction part of this fraction; that is, the fraction that
   * represents the part that would come after the decimal point if this
   * were written as a decimal number. Carries the same sign as this
   * fraction, unless the fraction part is zero.<br>
   * <br>
   * Equivalent to {@code getFractionPart(DivisionMode.TRUNCATED)}
   * 
   * @return fractional part of this fraction (i.e. {@code (numerator%denominator)/denominator)}), using TRUNCATED division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public BigFraction getFractionPart()
  {
    return getFractionPart(DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns the fraction part of this fraction; that is, the remainder when
   * the numerator is divided by the denominator when using the specified
   * division mode.
   * 
   * @param divisionMode Division mode to use when computing remainder. Only relevant if this is negative.
   * @return fractional part of this fraction (i.e. {@code (numerator%denominator)/denominator)}), using specified division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public BigFraction getFractionPart(DivisionMode divisionMode)
  {
    if(denominator.equals(BigInteger.ONE))
      return BigFraction.ZERO;
    
    BigInteger fPart = numerator.remainder(denominator);
    
    if(numerator.signum() < 0 && divisionMode != DivisionMode.TRUNCATED)
      fPart = fPart.add(denominator);
    
    return new BigFraction(fPart, denominator, Reduced.YES);
  }
  
  /**
   * Returns the integer and fraction parts of this fraction. The return
   * array is guaranteed to have exactly two elements. The first is guaranteed
   * to be a BigInteger, equivalent to the result of getIntegerPart().
   * The second element is guaranteed to be a BigFraction, equivalent to
   * the result of getFractionPart().<br>
   * <br>
   * Equivalent to {@code getParts(DivisionMode.TRUNCATED)}
   * 
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
   *         These represent the part that would be written before the decimal, and the part that would be after the decimal, if this fraction
   *         were written in decimal format.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public Number[] getParts()
  {
    return getParts(DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns the integer and fraction parts of this fraction. The return
   * array is guaranteed to have exactly two elements. The first is guaranteed
   * to be a BigInteger, and the second is guaranteed to be a BigFraction.<br>
   * <br>
   * The first element is the result of integer division of numerator by
   * denominator, using the supplied division mode. The second element is
   * the fraction given by numerator mod denominator, using the given
   * division mode.<br>
   * <br>
   * Note that the division mode only matters if this fraction is negative.
   * Because the sign of a BigFraction is always carried by the numerator,
   * the FLOOR and EUCLIDEAN division modes will always produce the same result.<br>
   * <br>
   * Some examples:<br>
   * <br>
   * <table summary="Examples of rounding modes" border="1" cellpadding="4">
   *   <tr><th>Fraction</th><th>TRUNCATE</th><th>FLOOR</th><th>EUCLIDEAN</th></tr>
   *   <tr><td> 4/1</td><td>[ 4,  0/1]</td><td>[ 4, 0/1]</td><td>[ 4, 0/1]</td></tr>
   *   <tr><td> 4/3</td><td>[ 1,  1/3]</td><td>[ 1, 1/3]</td><td>[ 1, 1/3]</td></tr>
   *   <tr><td> 2/7</td><td>[ 0,  2/7]</td><td>[ 0, 2/7]</td><td>[ 0, 2/7]</td></tr>
   *   <tr><td>-2/7</td><td>[ 0, -2/7]</td><td>[-1, 5/7]</td><td>[-1, 5/7]</td></tr>
   *   <tr><td>-4/3</td><td>[-1, -1/3]</td><td>[-2, 2/3]</td><td>[-2, 2/3]</td></tr>
   *   <tr><td>-4/1</td><td>[-4,  0/1]</td><td>[-4, 0/1]</td><td>[-4, 0/1]</td></tr>
   * </table>
   * 
   * @param divisionMode Division mode to use when computing parts. Only relevant if this is negative.
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. First is a {@code BigInteger}, second is a {@code BigFraction}.
   *         These represent the part that would be written before the decimal, and the part that would be after the decimal, if this fraction
   *         were written in decimal format.
   * 
   * @see DivisionMode
   */
  public Number[] getParts(DivisionMode divisionMode) {
    if(denominator.equals(BigInteger.ONE))
      return new Number[]{numerator, BigFraction.ZERO};
    
    final BigInteger[] divmod = numerator.divideAndRemainder(denominator);
    
    BigInteger iPart = divmod[0];
    BigInteger fPart = divmod[1];
    
    if(numerator.signum() < 0 && divisionMode != DivisionMode.TRUNCATED) {
      iPart = iPart.subtract(BigInteger.ONE);
      fPart = fPart.add(denominator);
    }
    
    return new Number[]{iPart, new BigFraction(fPart, denominator, Reduced.YES)};
  }
  
  /**
   * Returns this rounded to the nearest whole number, using
   * RoundingMode.HALF_UP as the default rounding mode.
   * 
   * @return this fraction rounded to nearest whole number, using RoundingMode.HALF_UP.
   */
  public BigInteger round()
  {
    return round(RoundingMode.HALF_UP);
  }
  
  /**
   * Returns this fraction rounded to a whole number, using
   * the given rounding mode.
   * 
   * @param roundingMode rounding mode to use
   * @return this fraction rounded to a whole number, using the given rounding mode.
   * 
   * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but
   *         this fraction does not exactly represent an integer.
   */
  public BigInteger round(RoundingMode roundingMode)
  {
    //Since fraction is always in lowest terms, this is an exact integer
    //iff the denominator is 1.
    if(denominator.equals(BigInteger.ONE))
      return numerator;
    
    //If the denominator was not 1, rounding will be required.
    if(roundingMode == RoundingMode.UNNECESSARY)
      throw new ArithmeticException("Rounding necessary");
    
    final Set<RoundingMode> ROUND_HALF_MODES = EnumSet.of(RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN);
    
    BigInteger intVal = null;
    BigInteger remainder = null;
    
    //Note:  The remainder is only needed if we are using HALF_X rounding mode, and the
    //       remainder is not one-half. Since computing the remainder can be a bit
    //       expensive, only compute it if necessary.
    if(ROUND_HALF_MODES.contains(roundingMode) && !denominator.equals(BIGINT_TWO))
    {
      BigInteger[] divMod = numerator.divideAndRemainder(denominator);
      intVal = divMod[0];
      remainder = divMod[1];
    }
    else
    {
      intVal = numerator.divide(denominator);
    }
    
    //For HALF_X rounding modes, convert to either UP or DOWN.
    if(ROUND_HALF_MODES.contains(roundingMode))
    {
      //Since fraction is always in lowest terms, the remainder is exactly
      //one-half iff the denominator is 2.
      if(denominator.equals(BIGINT_TWO))
      {
        if(roundingMode == RoundingMode.HALF_UP || (roundingMode == RoundingMode.HALF_EVEN && intVal.testBit(0)))
        {
          roundingMode = RoundingMode.UP;
        }
        else
        {
          roundingMode = RoundingMode.DOWN;
        }
      }
      else if (remainder.abs().compareTo(denominator.shiftRight(1)) <= 0)
      {
        //note:  x.shiftRight(1) === x.divide(2)
        roundingMode = RoundingMode.DOWN;
      }
      else
      {
        roundingMode = RoundingMode.UP;
      }
    }
    
    //For ceiling and floor, convert to up or down (based on sign).
    if(roundingMode == RoundingMode.CEILING || roundingMode == RoundingMode.FLOOR)
    {
      //Use numerator.signum() instead of intVal.signum() to get correct answers
      //for values between -1 and 0.
      if(numerator.signum() > 0)
      {
        if(roundingMode == RoundingMode.CEILING)
          roundingMode = RoundingMode.UP;
        else
          roundingMode = RoundingMode.DOWN;
      }
      else
      {
        if(roundingMode == RoundingMode.CEILING)
          roundingMode = RoundingMode.DOWN;
        else
          roundingMode = RoundingMode.UP;
      }
    }
    
    //Sanity check... at this point all possible values should be turned to up or down.
    if(roundingMode != RoundingMode.UP && roundingMode != RoundingMode.DOWN)
      throw new IllegalArgumentException("Unsupported rounding mode: " + roundingMode);
    
    if(roundingMode == RoundingMode.UP)
    {
      if (numerator.signum() > 0)
        intVal = intVal.add(BigInteger.ONE);
      else
        intVal = intVal.subtract(BigInteger.ONE);
    }
    
    return intVal;
  }
  
  
  /**
   * Rounds the given fraction to the nearest fraction having the given denominator,
   * using HALF_UP rounding method, and returns the numerator of that fraction.
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
  public BigInteger roundToDenominator(BigInteger newDenominator)
  {
    return this.roundToDenominator(newDenominator, RoundingMode.HALF_UP);
  }
  
  /**
   * Rounds the given fraction to the nearest fraction having the given denominator,
   * using the given rounding method, and returns the numerator of that fraction.<br>
   * <br>
   * For example, given the fraction 7/15, if you wanted to know the nearest fraction
   * with denominator 6, it would be 2.8/6, which rounds to 3/6. This function would
   * return 3.<br>
   * <br>
   * Note: this is not reduced--3/6 is equivalent to 1/2, but this
   * function would still return 3. If newDenominator is 1, this method is equivalent
   * to round(). If this object is negative, the returned numerator will also be
   * negative.
   * 
   * @param newDenominator denominator of fraction to round to.
   * @param roundingMode rounding mode to use if the answer must be rounded.
   * 
   * @return numerator of rounded fraction (unreduced)
   * 
   * @throws IllegalArgumentException If newDenominator is null.
   * @throws ArithmeticException If newDenominator is zero or negative.
   * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but
   *         this fraction cannot be represented exactly as a fraction with the
   *         given denominator.
   */
  public BigInteger roundToDenominator(BigInteger newDenominator, RoundingMode roundingMode)
  {
    if(newDenominator == null)
      throw new IllegalArgumentException("Null argument");
    
    if(newDenominator.compareTo(BigInteger.ZERO) <= 0)
      throw new ArithmeticException("newDenominator must be positive");
    
    //n1/d1 = x/d2  =>   x = (n1/d1)*d2
    return this.multiply(newDenominator).round(roundingMode);
  }
  
  /**
   * Returns a string representation of this, in the form
   * numerator/denominator. Note that the denominator will
   * always be included, even if it is 1.
   * 
   * @return This fraction, represented as a string in the format {@code numerator/denominator}.
   */
  @Override
  public String toString()
  {
    return toString(false);
  }
  
  /**
   * Returns a string representation of this, in the form of
   * numerator/denominator. Optionally, "/denominator" part
   * can be ommitted for whole numbers.
   * 
   * @param denominatorOptional If true, the denominator will be ommitted
   *        when it is unnecessary. For example, "7" instead of "7/1".
   * @return This fraction, represented as a string in the format {@code numerator/denominator}.
   */
  public String toString(boolean denominatorOptional)
  {
    if(denominatorOptional && denominator.equals(BigInteger.ONE))
      return numerator.toString();
    return numerator.toString() + "/" + denominator.toString();
  }
  
  /**
   * Returns string representation of this object as a mixed fraction.
   * For example, 4/3 would be "1 1/3". For negative fractions, the
   * sign is carried only by the whole number and assumed to be distributed
   * across the whole value. For example, -4/3 would be "-1 1/3". For
   * fractions that are equal to whole numbers, only the whole number will
   * be displayed. For fractions which have absolute value less than 1,
   * this will be equivalent to toString().
   * 
   * @return String representation of this fraction as a mixed fraction.
   */
  public String toMixedString()
  {
    if(denominator.equals(BigInteger.ONE))
      return numerator.toString();
    
    if(numerator.abs().compareTo(denominator) < 0)
      return toString();
    
    BigInteger[] divmod = numerator.divideAndRemainder(denominator);
    
    return divmod[0] + " " + divmod[1].abs() + "/" + denominator;
  }
  
  
  /**
   * Returns decimal string representation of the fraction with the given number
   * of decimal digits using roundingMode ROUND_HALF_UP.
   * 
   * @param numDecimalDigits number of digits to be displayed after the decimal
   * @return decimal string representation of this fraction.
   * 
   * @throws IllegalArgumentException if numDecimalDigits is negative
   * @throws ArithmeticException if roundingMode is UNNECESSARY but rounding is required.
   */
  public String toDecimalString(int numDecimalDigits)
  {
    return toDecimalString(numDecimalDigits, RoundingMode.HALF_UP);
  }
  
  /**
   * Converts the fraction to a string with the given number of decimal digits.
   * For example, if f is 1/3, f.toDecimalString(1): 0.3; f.toDecimalString(4): 0.3333.
   * If numDecimalDigits is 0, this method is equivalent to round().toString().
   * Will append trailing 0s as needed: (1/2).toDecimalString(3) is 0.500.
   * 
   * @param numDecimalDigits number of digits to be displayed after the decimal
   * @param roundingMode how to round the number if necessary
   * @return decimal string representation of this fraction.
   * 
   * @throws IllegalArgumentException if numDecimalDigits is negative
   * @throws ArithmeticException if roundingMode is UNNECESSARY but rounding is required.
   */
  public String toDecimalString(int numDecimalDigits, RoundingMode roundingMode)
  {
    if(numDecimalDigits < 0)
      throw new IllegalArgumentException("numDecimalDigits must be nonnegative");
    
    //shortcut - if we don't want any decimal digits, this is equivalent to round()
    if(numDecimalDigits == 0)
      return this.round(roundingMode).toString();
    
    //multiply by 10^(digits), then round to integer
    BigInteger rounded = this.multiply(BigInteger.TEN.pow(numDecimalDigits)).round(roundingMode);
    
    //get the actual digits (ignoring the sign bit)
    String digits = rounded.abs().toString();
    
    String beforeDecimal = "0";
    String afterDecimal = digits;
    int padLen = 0; //number of zeros we need to pad afterDecimal with with
    
    if(digits.length() > numDecimalDigits)
    {
      //we got too many digits... need to split into before/after decimal parts
      beforeDecimal = digits.substring(0, digits.length() - numDecimalDigits);
      afterDecimal = digits.substring(digits.length() - numDecimalDigits);
    }
    else if (digits.length() < numDecimalDigits)
    {
      //we don't have enough digits. We will have to pad with zeros
      padLen = numDecimalDigits - digits.length();
    }
    //else: we got exactly the right number of digits. nothing to do!
    
    //create string builder to hold result. init buffer to max possible size: lenth of parts plus length of padding plus space for . and -
    StringBuilder sb = new StringBuilder(beforeDecimal.length() + afterDecimal.length() + padLen + 2);
    
    if(rounded.signum() < 0)
      sb.append('-');
    
    sb.append(beforeDecimal).append('.');
    
    for(int i = 0; i < padLen; i++)
      sb.append('0');
    
    sb.append(afterDecimal);
    
    return sb.toString();
  }
  
  /**
   * Returns if this object is equal to another object. In order to maintain symmetry,
   * this will *only* return true if the other object is a BigFraction. For looser
   * comparison to other Number objects, use the equalsNumber(Number) method.
   * 
   * @param o Object to compare to this
   * @return true if {@code o instanceof BigFraction} and is equal to this.
   * 
   * @see #equalsNumber(Number)
   */
  @Override
  public boolean equals(Object o)
  {
    if(this == o)
      return true;
    
    if(!(o instanceof BigFraction))
      return false;
    
    BigFraction f = (BigFraction)o;
    return numerator.equals(f.numerator) && denominator.equals(f.denominator);
  }
  
  /**
   * Returns if this object is equal to another Number object. Equivalent
   * to: {@code this.equals(BigFraction.valueOf(n))}
   * 
   * @param n number to compare this to
   * @return true if this is equivalent to {@code valueof(n)}
   */
  public boolean equalsNumber(Number n)
  {
    if(n == null)
      return false;
    return equals(valueOf(n));
  }
  
  /**
   * Returns a hash code for this object.
   * @return hash code for this object.
   */
  @Override
  public int hashCode()
  {
    //using the method generated by Eclipse, but streamlined a bit..
    return (31 + numerator.hashCode())*31 + denominator.hashCode();
  }
  
  /**
   * Returns a negative, zero, or positive number, indicating if this object
   * is less than, equal to, or greater than n, respectively.
   * 
   * @param n number to compare this to
   * @return integer indicating how this compares to given number
   * @throws IllegalArgumentException if n is null
   */
  @Override
  public int compareTo(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    BigFraction f = valueOf(n);
    
    //easy case: this and f have different signs
    if(signum() != f.signum())
      return signum() - f.signum();
    
    //next easy case: this and f have the same denominator
    if(denominator.equals(f.denominator))
      return numerator.compareTo(f.numerator);
    
    //not an easy case, so first make the denominators equal then compare the numerators
    return numerator.multiply(f.denominator).compareTo(denominator.multiply(f.numerator));
  }
  
  /**
   * Returns the next fraction in the Farey sequence with denominator less than
   * or equal to the given denominator. This is the smallest fraction that is
   * larger than this, with a denominator less than or equal to maxDenominator.
   * Algorithm is O(maxDenominator), and not optimized for generating entire
   * sequence by sequentially calling this function.
   * 
   * @param maxDenominator maximum denominator to use for computing next value
   *        in Farey sequence.
   * @return the smallest fraction that is larger than this, with a denominator less
   *         than or equal to maxDenominator.
   * @throws IllegalArgumentException if maxDenominator is non-positive.
   */
  public BigFraction fareyNext(int maxDenominator)
  {
    return fareyImpl(maxDenominator, FareyMode.NEXT);
  }
  
  /**
   * Returns the previous fraction in the Farey sequence with denominator less than
   * or equal to the given denominator. This is the largest fraction that is
   * smaller than this, with a denominator less than or equal to maxDenominator.
   * Algorithm is O(maxDenominator), and not optimized for generating entire
   * sequence by sequentially calling this function.
   * 
   * @param maxDenominator maximum denominator to use for computing previous value
   *        in Farey sequence.
   * @return the largest fraction that is smaller than this, with a denominator less
   *         than or equal to maxDenominator.
   * @throws IllegalArgumentException if maxDenominator is non-positive.
   */
  public BigFraction fareyPrev(int maxDenominator)
  {
    return fareyImpl(maxDenominator, FareyMode.PREV);
  }
  
  /**
   * Returns the closest fraction with denominator less than or equal to
   * the given denominator. Algorithm is O(maxDenominator).
   * 
   * @param maxDenominator maximum denominator to use for computing closest value
   *        in Farey sequence.
   * @return the closest fraction that to this, with a denominator less
   *         than or equal to maxDenominator.
   * @throws IllegalArgumentException if maxDenominator is non-positive.
   */
  public BigFraction fareyClosest(int maxDenominator)
  {
    return fareyImpl(maxDenominator, FareyMode.CLOSEST);
  }
  
  /**
   * Common private function for handling the Farey Sequence methods.
   * 
   * @throws IllegalArgumentException if maxDenominator is non-positive.
   */
  private BigFraction fareyImpl(int maxDenominator, FareyMode fareyMode)
  {
    if(maxDenominator <= 0)
      throw new IllegalArgumentException("maxDenominator must be positive");
    
    //shortcut - if we are finding closest, but we are actually already in the sequence, just return this
    if(fareyMode == FareyMode.CLOSEST && denominator.compareTo(BigInteger.valueOf(maxDenominator)) <= 0)
      return this;
    
    //shortcut - if this is a whole number, and we want next/prev, we just add or subtract 1/maxDenominator
    if(denominator.equals(BigInteger.ONE))
    {
      BigInteger bigMaxDenominator = BigInteger.valueOf(maxDenominator);
      
      // a/1 + 1/b = ab/b + 1/b = (ab+1)/b
      if(fareyMode == FareyMode.NEXT)
        return new BigFraction(numerator.multiply(bigMaxDenominator).add(BigInteger.ONE), bigMaxDenominator, Reduced.YES);
      else if(fareyMode == FareyMode.PREV)
        return new BigFraction(numerator.multiply(bigMaxDenominator).subtract(BigInteger.ONE), bigMaxDenominator, Reduced.YES);
    }
    
    //For negatives, we call negate this then call the sequence on the opposite mode, then negate the result
    if(numerator.signum() < 0)
    {
      if(fareyMode == FareyMode.NEXT)
        return this.negate().fareyImpl(maxDenominator, FareyMode.PREV).negate();
      else if(fareyMode == FareyMode.PREV)
        return this.negate().fareyImpl(maxDenominator, FareyMode.NEXT).negate();
      else
        return this.negate().fareyImpl(maxDenominator, fareyMode).negate();
    }
    
    //The algorithm needs a number between 0 and 1. If this is an improper fraction, get the sequence value for
    //the fraction part, then add back the whole number
    if(numerator.compareTo(denominator) > 0)
    {
      BigInteger[] divmod = numerator.divideAndRemainder(denominator);
      
      BigFraction fPartSeq = new BigFraction(divmod[1], denominator, Reduced.YES).fareyImpl(maxDenominator, fareyMode);
      
      // n + a/b = nb/b + a/b = (nb + a)/b
      return new BigFraction(divmod[0].multiply(fPartSeq.denominator).add(fPartSeq.numerator), fPartSeq.denominator, Reduced.YES);
    }
    
    //Now... do the actual algorithm. We have lower bound a/b (initally 0/1), and upper bound c/d (initially 1/0).
    //We repeatedly take the mediant of a/b and c/d -- that is, (a+c)/(b+d). This is guaranteed to be a fraction
    //between a/b and c/d. Then we see which side of the mediant this is on, and set either upper bound or lower
    //bound to the mediant, and repeat until denominator is greater than maxDenominator
    long a=0, b=1, c=1, d=1;
    while(b+d <= maxDenominator)
    {
      long med_n = a+c, med_d = b+d;
      int cmp = BigInteger.valueOf(med_n).multiply(this.denominator).compareTo(BigInteger.valueOf(med_d).multiply(this.numerator));
      if(cmp < 0 || (cmp == 0 && fareyMode == FareyMode.NEXT))
      {
        a = med_n;
        b = med_d;
      }
      else
      {
        c = med_n;
        d = med_d;
      }
    }
    
    if(fareyMode == FareyMode.NEXT)
      return new BigFraction(BigInteger.valueOf(c), BigInteger.valueOf(d), Reduced.YES);
    if(fareyMode == FareyMode.PREV)
      return new BigFraction(BigInteger.valueOf(a), BigInteger.valueOf(b), Reduced.YES);
    
    //else: we need to determine whether lowerbound or upper bound is closer to this
    BigFraction lower = new BigFraction(BigInteger.valueOf(c), BigInteger.valueOf(d), Reduced.YES);
    BigFraction upper = new BigFraction(BigInteger.valueOf(a), BigInteger.valueOf(b), Reduced.YES);
    
    if(this.subtract(lower).compareTo(upper.subtract(this)) > 0)
      return lower;
    else
      return upper;
  }
  
  /**
   * Returns the smaller of this and n. If they have equal value, this is returned.
   * Worth noting: if n is smaller, the returned Number is n, <i>not</i> a BigFraction
   * representing n.
   * 
   * @param n number to compare to this
   * @return smaller of this and n
   * @throws IllegalArgumentException if n is null
   */
  public Number min(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    return (this.compareTo(n) <= 0 ? this : n);
  }
  
  /**
   * Returns the smaller of a and b. If they have equal value, a is returned.
   * Worth noting: the returned Number is always one of the two arguments, not
   * necessarily a BigFraction.
   * 
   * @param a one number to compare
   * @param b another number to compare
   * @return smaller of a and b
   * @throws IllegalArgumentException if a or b is null
   */
  public static Number min(Number a, Number b)
  {
    if(a == null || b == null)
      throw new IllegalArgumentException("Null argument");
    
    return (valueOf(a).compareTo(b) <= 0 ? a : b);
  }
  
  
  /**
   * Returns the larger of this and n. If they have equal value, this is returned.
   * Worth noting: if n is larger, the returned Number is n, <i>not</i> a BigFraction
   * representing n.
   * 
   * @param n number to compare to this
   * @return larger of this and n
   * @throws IllegalArgumentException if n is null
   */
  public Number max(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    return (this.compareTo(n) >= 0 ? this : n);
  }
  
  /**
   * Returns the larger of a and b. If they have equal value, a is returned.
   * Worth noting: the returned Number is always one of the two arguments, not
   * necessarily a BigFraction.
   * 
   * @param a one number to compare
   * @param b another number to compare
   * @return larger of a and b
   * @throws IllegalArgumentException if a or b is null
   */
  public static Number max(Number a, Number b)
  {
    if(a == null || b == null)
      throw new IllegalArgumentException("Null argument");
    
    return (valueOf(a).compareTo(b) >= 0 ? a : b);
  }
  
  
  /**
   * Returns the mediant of this and n. The mediant of a/b and c/d is
   * (a+c)/(b+d). It is guaranteed to be between a/b and c/d. Not to
   * be confused with the median!
   * 
   * @param n other number to use to compute mediant
   * @return mediant of this and n
   * @throws IllegalArgumentException if n is null
   */
  public BigFraction mediant(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    BigFraction f = valueOf(n);
    
    //if the two fractions are equal, we can avoid the math
    if(this.equals(f))
      return this;
    
    return new BigFraction(this.numerator.add(f.numerator), this.denominator.add(f.denominator), Reduced.NO);
  }
  
  /**
   * Returns the mediant of a and b. Provided as static method for convenience.
   * 
   * @param a one number to use to compute mediant
   * @param b other number to use to compute mediant
   * @return mediant of a and b
   * @throws IllegalArgumentException if a or b is null
   * @see #mediant(Number)
   */
  public static BigFraction mediant(Number a, Number b)
  {
    return valueOf(a).mediant(b);
  }
  
  /**
   * Returns a BigDecimal representation of this fraction.<br>
   * <br>
   * If possible, the returned value will be exactly equal to the fraction. If not,
   * the BigDecimal will have a scale large enough to hold the same number of
   * significant figures as both numerator and denominator, or the equivalent of a
   * double-precision number, whichever is more.
   * 
   * @return This fraction represented as a BigDecimal (exactly, if possible).
   */
  public BigDecimal toBigDecimal()
  {
    //Implementation note:  A fraction can be represented exactly in base-10 iff its
    //denominator is of the form 2^a * 5^b, where a and b are nonnegative integers.
    //(In other words, if there are no prime factors of the denominator except for
    //2 and 5, or if the denominator is 1). So to determine if this denominator is
    //of this form, continually divide by 2 to get the number of 2's, and then
    //continually divide by 5 to get the number of 5's. Afterward, if the denominator
    //is 1 then there are no other prime factors.
    
    //Note: number of 2's is given by the number of trailing 0 bits in the number
    int twos = denominator.getLowestSetBit();
    BigInteger tmpDen = denominator.shiftRight(twos); // x / 2^n === x >> n
    
    int fives = 0;
    BigInteger[] divMod = null;
    
    //while(tmpDen % 5 == 0) { tmpDen /= 5; fives++; }
    while(BigInteger.ZERO.equals((divMod = tmpDen.divideAndRemainder(BIGINT_FIVE))[1]))
    {
      tmpDen = divMod[0];
      fives++;
    }
    
    if(tmpDen.equals(BigInteger.ONE))
    {
      //This fraction will terminate in base 10, so it can be represented exactly as
      //a BigDecimal. We would now like to make the fraction of the form
      //unscaled / 10^scale. We know that 2^x * 5^x = 10^x, and our denominator is
      //in the form 2^twos * 5^fives. So use max(twos, fives) as the scale, and
      //multiply the numerator and deminator by the appropriate number of 2's or 5's
      //such that the denominator is of the form 2^scale * 5^scale. (Of course, we
      //only have to actually multiply the numerator, since all we need for the
      //BigDecimal constructor is the scale.)
      BigInteger unscaled = numerator;
      int scale = Math.max(twos, fives);
      
      if(twos < fives)
        unscaled = unscaled.shiftLeft(fives - twos); //x * 2^n === x << n
      else if (fives < twos)
        unscaled = unscaled.multiply(BIGINT_FIVE.pow(twos - fives));
      
      return new BigDecimal(unscaled, scale);
    }
    
    //else: this number will repeat infinitely in base-10. So try to figure out
    //a good number of significant digits. Start with the number of digits required
    //to represent the numerator and denominator in base-10, which is given by
    //bitLength / log[2](10). (bitLenth is the number of digits in base-2).
    final double LG10 = 3.321928094887362; //Precomputed ln(10)/ln(2), a.k.a. log[2](10)
    int precision = Math.max(numerator.bitLength(), denominator.bitLength());
    precision = (int)Math.ceil(precision / LG10);
    
    //If the precision is less than that of a double, use double-precision so
    //that the result will be at least as accurate as a cast to a double. For
    //example, with the fraction 1/3, precision will be 1, giving a result of
    //0.3. This is quite a bit different from what a user would expect.
    if(precision < MathContext.DECIMAL64.getPrecision() + 2)
      precision = MathContext.DECIMAL64.getPrecision() + 2;
    
    return toBigDecimal(precision);
  }
  
  /**
   * Returns a BigDecimal representation of this fraction, with a given precision.
   * @param precision  the number of significant figures to be used in the result.
   * @return BigDecimal representation of this fraction, to the given precision.
   */
  public BigDecimal toBigDecimal(int precision)
  {
    return new BigDecimal(numerator).divide(new BigDecimal(denominator), new MathContext(precision, RoundingMode.HALF_EVEN));
  }
  
  //--------------------------------------------------------------------------
  //  IMPLEMENTATION OF NUMBER INTERFACE
  //--------------------------------------------------------------------------
  /**
   * Returns a long representation of this fraction. This value is
   * obtained by integer division of numerator by denominator. If
   * the value is greater than {@link Long#MAX_VALUE}, {@link Long#MAX_VALUE} will be
   * returned. Similarly, if the value is below {@link Long#MIN_VALUE},
   * {@link Long#MIN_VALUE} will be returned.
   * 
   * @return long representation of this fraction
   */
  @Override
  public long longValue()
  {
    BigInteger rounded = this.round(RoundingMode.DOWN);
    if(rounded.bitLength() > 63)
      return rounded.signum() < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    
    return rounded.longValue();
  }
  
  /**
   * Returns an exact long representation of this fraction.
   * 
   * 
   * @return exact long representation of this fraction
   * @throws ArithmeticException if this has a nonzero fractional
   *                             part, or will not fit in a long.
   */
  public long longValueExact()
  {
    if(!denominator.equals(BigInteger.ONE) || numerator.bitLength() > 63)
      throw new ArithmeticException("Value does not have an exact long representation");
    
    return numerator.longValue();
  }
  
  /**
   * Returns an int representation of this fraction. This value is
   * obtained by integer division of numerator by denominator. If
   * the value is greater than {@link Integer#MAX_VALUE}, {@link Integer#MAX_VALUE} will be
   * returned. Similarly, if the value is below {@link Integer#MIN_VALUE},
   * {@link Integer#MIN_VALUE} will be returned.
   * 
   * @return int representation of this fraction
   */
  @Override
  public int intValue()
  {
    return (int)Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, longValue()));
  }
  
  /**
   * Returns an exact int representation of this fraction.
   * 
   * @return exact int representation of this fraction
   * @throws ArithmeticException if this has a nonzero fractional
   *                             part, or will not fit in an int.
   */
  public int intValueExact()
  {
    if(!denominator.equals(BigInteger.ONE) || numerator.bitLength() > 31)
      throw new ArithmeticException("Value does not have an exact int representation");
    
    return numerator.intValue();
  }
  
  /**
   * Returns a short representation of this fraction. This value is
   * obtained by integer division of numerator by denominator. If
   * the value is greater than {@link Short#MAX_VALUE}, {@link Short#MAX_VALUE} will be
   * returned. Similarly, if the value is below {@link Short#MIN_VALUE},
   * {@link Short#MIN_VALUE} will be returned.
   * 
   * @return short representation of this fraction
   */
  @Override
  public short shortValue()
  {
    return (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, longValue()));
  }
  
  /**
   * Returns an exact short representation of this fraction.
   * 
   * @return exact short representation of this fraction
   * @throws ArithmeticException if this has a nonzero fractional
   *                             part, or will not fit in a short.
   */
  public short shortValueExact()
  {
    if(!denominator.equals(BigInteger.ONE) || numerator.bitLength() > 15)
      throw new ArithmeticException("Value does not have an exact short representation");
    
    return numerator.shortValue();
  }
  
  /**
   * Returns a byte representation of this fraction. This value is
   * obtained by integer division of numerator by denominator. If
   * the value is greater than {@link Byte#MAX_VALUE}, {@link Byte#MAX_VALUE} will be
   * returned. Similarly, if the value is below {@link Byte#MIN_VALUE},
   * {@link Byte#MIN_VALUE} will be returned.
   * 
   * @return byte representation of this fraction
   */
  @Override
  public byte byteValue()
  {
    return (byte)Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, longValue()));
  }
  
  /**
   * Returns an exact byte representation of this fraction.
   * 
   * @return exact byte representation of this fraction
   * @throws ArithmeticException if this has a nonzero fractional
   *                             part, or will not fit in a byte.
   */
  public byte byteValueExact()
  {
    if(!denominator.equals(BigInteger.ONE) || numerator.bitLength() > 7)
      throw new ArithmeticException("Value does not have an exact byte representation");
    
    return numerator.byteValue();
  }
  
  /**
   * Returns the value of this fraction. If this value is beyond the
   * range of a double, {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY} will
   * be returned.
   * 
   * @return double representation of this fraction
   */
  @Override
  public double doubleValue()
  {
    //TODO: UNOPTIMIZED! Currently converting to BigDecimal then converting that to double.
    
    //note: must use precision+2 so that  new BigFraction(d).doubleValue() == d,
    //      for all possible double values.
    return toBigDecimal(MathContext.DECIMAL64.getPrecision() + 2).doubleValue();
  }
  
  /**
   * Returns an exact double representation of this fraction.
   * 
   * @return exact double representation of this fraction
   * @throws ArithmeticException if this cannot be represented exactly as a double.
   */
  public double doubleValueExact()
  {
    //TODO: UNOPTIMIZED! Algorithm is simply to convert to double, then convert back to BigFraction,
    //then make sure the copy of copy is identical to the original.
    //One test: if denominator.bitLength() != 1, then this cannot be represented exactly
    
    double doubleVal = this.doubleValue();
    if(DoubleUtil.isFinite(doubleVal)) {
      BigFraction copy = valueOfHelper(doubleVal);
      if(this.equals(copy))
        return doubleVal;
    }
    
    throw new ArithmeticException("Value does not have an exact double representation");
  }
  
  /**
   * Returns the value of this fraction. If this value is beyond the
   * range of a float, {@link Float#POSITIVE_INFINITY} or {@link Float#NEGATIVE_INFINITY} will
   * be returned.
   * 
   * @return float representation of this fraction
   */
  @Override
  public float floatValue()
  {
    //TODO: UNOPTIMIZED! Currently converting to BigDecimal then converting that to float.
    
    //note: must use precision+2 so that  new BigFraction(f).floatValue() == f,
    //      for all possible float values.
    return toBigDecimal(MathContext.DECIMAL32.getPrecision() + 2).floatValue(); 
  }
  
  /**
   * Returns an exact float representation of this fraction.
   * 
   * @return exact float representation of this fraction
   * @throws ArithmeticException if this cannot be represented exactly as a float.
   */
  public float floatValueExact()
  {
    //TODO: UNOPTIMIZED! Algorithm is simply to convert to float, then convert back to BigFraction,
    //then make sure the copy of copy is identical to the original.
    
    float floatVal = this.floatValue();
    if(DoubleUtil.isFinite(floatVal)) {
      BigFraction copy = valueOfHelper(floatVal);
      if(this.equals(copy))
        return floatVal;
    }
    
    throw new ArithmeticException("Value does not have an exact float representation");
  }
  
  
  //--------------------------------------------------------------------------
  //  PRIVATE FUNCTIONS
  //--------------------------------------------------------------------------
  
  
  /**
   * Constructs a BigFraction from a floating-point number.
   */
  private static BigFraction valueOfHelper(double d)
  {
    if(Double.isInfinite(d))
      throw new IllegalArgumentException("double val is infinite");
    if(Double.isNaN(d))
      throw new IllegalArgumentException("double val is NaN");
    
    //special case - math below won't work right for 0.0 or -0.0
    if(d == 0)
      return BigFraction.ZERO;
    
    //Per IEEE spec...
    final int sign = DoubleUtil.getSign(d);
    final int exponent = DoubleUtil.getExponent(d);
    final long mantissa = DoubleUtil.getMantissa(d);
    final boolean isSubnormal = DoubleUtil.isSubnormal(d);
    
    //Number is: (-1)^sign * 2^(exponent) * 1.mantissa
    //Neglecting sign bit, this gives:
    //           2^(exponent) * 1.mantissa
    //         = 2^(exponent) * (1 + mantissa/2^52)
    //         = 2^(exponent) * (2^52 + mantissa)/2^52
    // Letting tmpNumerator=(2^52 + mantissa):
    //         = 2^(exponent) * tmpNumerator/2^52
    //  
    //  For exponent > 52:
    //         = tmpNumerator * 2^(exponent - 52)
    //  For exponent = 52:
    //         = tmpNumerator
    //  For exponent < 52:
    //         = tmpNumerator / 2^(52 - exponent)
    //
    //SPECIAL CASE: Subnormals - if all exponent bits are 0 (in my code, this
    //would mean exponent is -0x3ff, or -1023), then the formula is:
    //    (-1)^sign * 2^(exponent+1) * 0.mantissa
    //    
    //Again neglecting sign bit, this gives:
    //           2^(exponent + 1) * 0.mantissa
    //         = 2^(-1022) * (mantissa/2^52)
    //         = mantissa / 2^1074
    // Letting tmpNumerator = mantissa:
    //         = tmpNumerator / 2^1074
    
    BigInteger tmpNumerator = BigInteger.valueOf((isSubnormal ? 0L : 0x10000000000000L) + mantissa);
    BigInteger tmpDenominator = BigInteger.ONE;
    
    if(exponent > 52)
    {
      //numerator * 2^(exponent - 52) === numerator << (exponent - 52)
      tmpNumerator = tmpNumerator.shiftLeft(exponent - 52);
    }
    else if (exponent < 52)
    {
      if(!isSubnormal)
      {
        //The gcd of (2^52 + mantissa) / 2^(52 - exponent)  must be of the form 2^y,
        //since the only prime factors of the denominator are 2. In base-2, it is
        //easy to determine how many factors of 2 a number has--it is the number of
        //trailing "0" bits at the end of the number. (This is the same as the number
        //of trailing 0's of a base-10 number indicating the number of factors of 10
        //the number has).
        int y = Math.min(tmpNumerator.getLowestSetBit(), 52 - exponent);
        
        //Now 2^y = gcd( 2^52 + mantissa, 2^(52 - exponent) ), giving:
        // (2^52 + mantissa) / 2^(52 - exponent)
        //      = ((2^52 + mantissa) / 2^y) / (2^(52 - exponent) / 2^y)
        //      = ((2^52 + mantissa) / 2^y) / (2^(52 - exponent - y))
        //      = ((2^52 + mantissa) >> y) / (1 << (52 - exponent - y))
        tmpNumerator = tmpNumerator.shiftRight(y);
        tmpDenominator = tmpDenominator.shiftLeft(52 - exponent - y);
      }
      else
      {
        //using the same logic as above, except now we are finding gcd of tmpNumerator/2^1074
        int y = Math.min(tmpNumerator.getLowestSetBit(), 1074);
        
        tmpNumerator = tmpNumerator.shiftRight(y);
        tmpDenominator = tmpDenominator.shiftLeft(1074 - y);
      }
    }
    //else: exponent == 52: do nothing
    
    //Set sign bit if needed
    if(sign != 0)
      tmpNumerator = tmpNumerator.negate();
    
    //Guaranteed there is no gcd, so fraction is in lowest terms
    return new BigFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Constructs a BigFraction from two floating-point numbers.<br>
   * <br>
   * Warning: round-off error in IEEE floating point numbers can result
   * in answers that are unexpected. See {@link #valueOf(Number)} for more
   * information.<br>
   * <br>
   * NOTE: In many cases, BigFraction(Double.toString(numerator) + "/" + Double.toString(denominator))
   * may give a result closer to what the user expects.
   * 
   * @throws ArithmeticException if denominator == 0.
   */
  private static BigFraction valueOfHelper(double numerator, double denominator)
  {
    if(denominator == 0)
      throw new ArithmeticException("Divide by zero: fraction denominator is zero.");
    
    if(denominator < 0)
    {
      numerator = -numerator;
      denominator = -denominator;
    }
    
    BigFraction numFract = valueOfHelper(numerator);
    BigFraction denFract = valueOfHelper(denominator);
    
    //We can avoid the check for gcd here because we know that a fraction created from
    //a double will be of the form n/2^x, where x >= 0. So we have:
    //     (n1/2^x1)/(n2/2^x2)
    //   = (n1/n2) * (2^x2 / 2^x1).
    //
    //Now, we only have to check for gcd(n1,n2), and we know gcd(2^x2, 2^x1) = 2^(abs(x2 - x1)).
    //This gives us the following:
    // For x1 < x2 :  (n1 * 2^(x2 - x1)) / n2  =  (n1 << (x2 - x1)) / n2
    // For x1 = x2 :  n1 / n2
    // For x1 > x2 :  n1 / (n2 * 2^(x1 - x2))  =  n1 / (n2 << (x1 - x2))
    //
    //Further, we know that if x1 > 0, n1 is not divisible by 2 (likewise for x2 > 0 and n2).
    //This guarantees that the GCD for any of the above three cases is equal to gcd(n1,n2).
    //Since it is easier to compute GCD of smaller numbers, this can speed us up a bit.
    
    BigInteger gcd = numFract.numerator.gcd(denFract.numerator);
    BigInteger tmpNumerator = numFract.numerator.divide(gcd);
    BigInteger tmpDenominator = denFract.numerator.divide(gcd);
    
    int x1 = numFract.denominator.getLowestSetBit();
    int x2 = denFract.denominator.getLowestSetBit();
    
    //Note:  a * 2^b === a << b
    if(x1 < x2)
      tmpNumerator = tmpNumerator.shiftLeft(x2 - x1);
    else if (x1 > x2)
      tmpDenominator = tmpDenominator.shiftLeft(x1 - x2);
    //else: x1 == x2: do nothing
    
    return new BigFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Constructs a new BigFraction from the given BigDecimal object.
   */
  private static BigFraction valueOfHelper(BigDecimal d)
  {
    //BigDecimal format: unscaled / 10^scale.
    BigInteger tmpNumerator = d.unscaledValue();
    BigInteger tmpDenominator = BigInteger.ONE;
    
    //Special case for d == 0 (math below won't work right)
    //Note:  Cannot use d.equals(BigDecimal.ZERO), because BigDecimal.equals()
    //       does not consider numbers equal if they have different scales. So,
    //       0.00 is not equal to BigDecimal.ZERO.
    if(tmpNumerator.equals(BigInteger.ZERO))
      return BigFraction.ZERO;
    
    if(d.scale() < 0)
    {
      tmpNumerator = tmpNumerator.multiply(BigInteger.TEN.pow(-d.scale()));
    }
    else if (d.scale() > 0)
    {
      //Now we have the form:  unscaled / 10^scale = unscaled / (2^scale * 5^scale)
      //We know then that gcd(unscaled, 2^scale * 5^scale) = 2^commonTwos * 5^commonFives
      
      //Easy to determine commonTwos
      int commonTwos = Math.min(d.scale(), tmpNumerator.getLowestSetBit());
      tmpNumerator = tmpNumerator.shiftRight(commonTwos);
      tmpDenominator = tmpDenominator.shiftLeft(d.scale() - commonTwos);
      
      //Determining commonFives is a little trickier..
      int commonFives = 0;
      
      BigInteger[] divMod = null;
      //while(commonFives < d.scale() && tmpNumerator % 5 == 0) { tmpNumerator /= 5; commonFives++; }
      while(commonFives < d.scale() && BigInteger.ZERO.equals((divMod = tmpNumerator.divideAndRemainder(BIGINT_FIVE))[1]))
      {
        tmpNumerator = divMod[0];
        commonFives++;
      }
      
      if(commonFives < d.scale())
        tmpDenominator = tmpDenominator.multiply(BIGINT_FIVE.pow(d.scale() - commonFives));
    }
    //else: d.scale() == 0: do nothing
    
    //Guaranteed there is no gcd, so fraction is in lowest terms
    return new BigFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Constructs a new BigFraction from two BigDecimals.
   * 
   * @throws ArithmeticException if denominator == 0.
   */
  private static BigFraction valueOfHelper(BigDecimal numerator, BigDecimal denominator)
  {
    //Note:  Cannot use .equals(BigDecimal.ZERO), because "0.00" != "0.0".
    if(denominator.unscaledValue().equals(BigInteger.ZERO))
      throw new ArithmeticException("Divide by zero: fraction denominator is zero.");
    
    //Format of BigDecimal: unscaled / 10^scale
    BigInteger tmpNumerator = numerator.unscaledValue();
    BigInteger tmpDenominator = denominator.unscaledValue();
    
    // (u1/10^s1) / (u2/10^s2) = u1 / (u2 * 10^(s1-s2)) = (u1 * 10^(s2-s1)) / u2
    if(numerator.scale() > denominator.scale())
      tmpDenominator = tmpDenominator.multiply(BigInteger.TEN.pow(numerator.scale() - denominator.scale()));
    else if(numerator.scale() < denominator.scale())
      tmpNumerator = tmpNumerator.multiply(BigInteger.TEN.pow(denominator.scale() - numerator.scale()));
    //else: scales are equal, do nothing.
    
    BigInteger gcd = tmpNumerator.gcd(tmpDenominator);
    tmpNumerator = tmpNumerator.divide(gcd);
    tmpDenominator = tmpDenominator.divide(gcd);
    
    if(tmpDenominator.signum() < 0)
    {
      tmpNumerator = tmpNumerator.negate();
      tmpDenominator = tmpDenominator.negate();
    }
    
    return new BigFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Private constructor, used when you can be certain that the fraction is already in
   * lowest terms. No check is done to reduce numerator/denominator. A check is still
   * done to maintain a positive denominator.
   * 
   * @param isReduced  Indicates whether or not the fraction is already known to be
   *                   reduced to lowest terms.
   */
  private BigFraction(BigInteger numerator, BigInteger denominator, Reduced reduced)
  {
    if(numerator == null)
      throw new IllegalArgumentException("Numerator is null");
    if(denominator == null)
      throw new IllegalArgumentException("Denominator is null");
    if(denominator.equals(BigInteger.ZERO))
      throw new ArithmeticException("Divide by zero: fraction denominator is zero.");
    
    //if numerator is zero, we don't care about the denominator. force it to 1.
    if(numerator.equals(BigInteger.ZERO))
    {
      denominator = BigInteger.ONE;
      reduced = Reduced.YES;
    }
    
    //only numerator should be negative.
    if(denominator.signum() < 0)
    {
      numerator = numerator.negate();
      denominator = denominator.negate();
    }
    
    if(reduced == Reduced.NO)
    {
      //create a reduced fraction
      BigInteger gcd = numerator.gcd(denominator);
      if(!gcd.equals(BigInteger.ONE))
      {
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
      }
    }
    
    this.numerator = numerator;
    this.denominator = denominator;
  }
  
  /**
   * Converts a Number to a BigInteger. Assumes that a check on the type of n
   * has already been performed.
   */
  private static BigInteger toBigInteger(Number n)
  {
    if(n instanceof BigInteger)
      return (BigInteger)n;
    
    if(n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte || n instanceof AtomicInteger || n instanceof AtomicLong)
      return BigInteger.valueOf(n.longValue());
    
    if(n instanceof BigFraction)
      return ((BigFraction)n).numerator;
    
    if(n instanceof Double || n instanceof Float)
    {
      final double d = n.doubleValue();
      
      if(d == 0.0)
        return BigInteger.ZERO;
      
      //This is similar to valueOfHelper(double), except we know that the exponent is greater than 52. See the comments
      //in valueOfHelper(double) for much more detailed information
      final int sign = DoubleUtil.getSign(d);
      final int exponent = DoubleUtil.getExponent(d);
      final long mantissa = DoubleUtil.getMantissa(d);
      
      BigInteger ret = BigInteger.valueOf(0x10000000000000L + mantissa).shiftLeft(exponent - 52);
      return sign == 0 ? ret : ret.negate();
    }
    
    if(n instanceof BigDecimal)
    {
      final BigDecimal bd = (BigDecimal)n;
      return bd.unscaledValue().multiply(BigInteger.TEN.pow(-bd.scale()));
    }
    
    //we should never get here...
    assert false : "Critical error in toBigInteger. n=" + n.toString() + " [" + n.getClass().getName() + "]";
    return null;
  }
  
  /**
   * Returns true if the given type can be converted to a BigInteger without loss
   * of precision. Returns true for the primitive integer types (Long, Integer, Short,
   * Byte, AtomicInteger, AtomicLong, or BigInteger).<br>
   * <br>
   * For BigFraction, returns true if denominator is 1.<br>
   * <br>
   * For double, float, and BigDecimal, analyzes the data. Otherwise returns false.<br>
   * <br>
   * Used to determine if a Number is appropriate to be passed into toBigInteger() method.
   */
  private static boolean isInt(Number n)
  {
    if(n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte || n instanceof BigInteger || n instanceof AtomicInteger || n instanceof AtomicLong)
      return true;
    
    if(n instanceof BigFraction)
      return ((BigFraction)n).denominator.equals(BigInteger.ONE);
    
    if(n instanceof Double || n instanceof Float)
    {
      final double d = n.doubleValue();
      if(d == 0.0)
        return true;
      
      if(Double.isInfinite(d) || Double.isNaN(d))
        return false;
      
      return (DoubleUtil.getExponent(d) >= 52);
    }
    
    //BigDecimal format: unscaled / 10^scale
    if(n instanceof BigDecimal)
      return (((BigDecimal)n).scale() <= 0);
    
    //unknown type... play it safe...
    return false;
  }
  
  /**
   * Returns true if n is a floating-point primitive type (Double or Float).
   */
  private static boolean isFloat(Number n)
  {
    return n instanceof Double || n instanceof Float;
  }
}
