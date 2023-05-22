package com.github.kiprobinson.bigfraction;

import static com.github.kiprobinson.bigfraction.Fraction.arithmeticException;
import static com.github.kiprobinson.bigfraction.Fraction.isFloat;
import static com.github.kiprobinson.bigfraction.Fraction.isInt;
import static com.github.kiprobinson.bigfraction.Fraction.isJavaInteger;
import static com.github.kiprobinson.bigfraction.Fraction.noExactValueException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.kiprobinson.bigfraction.util.DoubleUtil;
/**
 * Arbitrary-precision fraction, utilizing {@code long}s for numerator and
 * denominator. Fraction is always kept in lowest terms. Fraction is
 * immutable, and guaranteed not to have a null numerator or denominator.
 * Denominator will always be positive (so sign is carried by numerator,
 * and a zero-denominator is impossible).<br>
 * <br>
 * Because mathematical operations are done negatively, they will perform
 * much better than with {@link BigFraction}, but with the risk of overflow.<br>
 * <br>
 * Any operations that overflow throw an ArithmeticException.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public final class LongFraction extends Number 
        implements Fraction<Long, LongFraction>, Comparable<Number>
{
  private static final long serialVersionUID = 3L; //because Number is Serializable
  
  private final long numerator;
  private final long denominator;
  
  //some constants used
  private final static BigInteger BIGINT_FIVE = BigInteger.valueOf(5);
  
  /** The value 0/1. */
  public final static LongFraction ZERO = new LongFraction(0L, 1L, Reduced.YES);
  /** The value 1/1. */
  public final static LongFraction ONE = new LongFraction(1L, 1L, Reduced.YES);
  /** The value 1/2. */
  public final static LongFraction ONE_HALF = new LongFraction(1L, 2L, Reduced.YES);
  /** The value 1/10. */
  public final static LongFraction ONE_TENTH = new LongFraction(1L, 10L, Reduced.YES);
  /** The value 10/1. */
  public final static LongFraction TEN = new LongFraction(10L, 1L, Reduced.YES);

  
  /**
   * <strong>Note:</strong> {@link #valueOf(Number)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param n Number to convert to LongFraction.
   * @see #valueOf(Number)
   */
  public LongFraction(Number n)
  {
    LongFraction bf = valueOf(n);
    this.numerator = bf.numerator;
    this.denominator = bf.denominator;
  }
  
  /**
   * <strong>Note:</strong> {@link #valueOf(Number, Number)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param numerator numerator of new LongFraction
   * @param denominator denominator of new LongFraction
   * @see #valueOf(Number, Number)
   */
  public LongFraction(Number numerator, Number denominator)
  {
    LongFraction bf = valueOf(numerator, denominator);
    this.numerator = bf.numerator;
    this.denominator = bf.denominator;
  }
  
  /**
   * <strong>Note:</strong> {@link #valueOf(String)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param s String to convert to parse as LongFraction
   * @see #valueOf(String)
   */
  protected LongFraction(String s)
  {
    this(s, 10);
  }
  
  /**
   * <strong>Note:</strong> {@link #valueOf(String, int)} should be preferred for performance reasons.
   * This constructor is provided for convenience.
   * 
   * @param s String to convert to parse as LongFraction
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @see #valueOf(String, int)
   */
  protected LongFraction(String s, int radix)
  {
    LongFraction bf = valueOf(s, radix);
    this.numerator = bf.numerator;
    this.denominator = bf.denominator;
  }
  
  /**
   * Constructs a LongFraction from given number. If the number is not one of the
   * known implementations of Number class, then {@link Number#doubleValue()}
   * will be used for construction.<br>
   * <br>
   * Warning: when using floating point numbers, round-off error can result
   * in answers that are unexpected. For example:<br> 
   * {@code     System.out.println(LongFraction.valueOf(1.1))}<br>
   * will print:<br>
   * {@code     2476979795053773/2251799813685248}<br>
   * <br>
   * This is because 1.1 cannot be expressed exactly in binary form. The
   * computed fraction is exactly equal to the internal representation of
   * the double-precision floating-point number. (Which, for {@code 1.1}, is:
   * {@code (-1)^0 * 2^0 * (1 + 0x199999999999aL / 0x10000000000000L)}.)<br>
   * <br>
   * In many cases, {@code LongFraction.valueOf(Double.toString(d))} may give the result the user expects.
   * 
   * @param n Any Number to be converted to a LongFraction
   * @return a fully reduced fraction equivalent to {@code n}. Guaranteed to be non-null.
   * 
   * @throws IllegalArgumentException if n is null.
   */
  public static LongFraction valueOf(Number n)
  {
    if(n == null)
      throw new IllegalArgumentException("Null parameter.");
    
    if(n instanceof LongFraction)
      return (LongFraction)n;
    else if(n instanceof BigFraction)
      return new LongFraction(((BigFraction)n).getNumerator().longValueExact(), 
              ((BigFraction)n).getDenominator().longValueExact(), Reduced.YES);
    else if(isInt(n))
      return new LongFraction(toLong(n), 1L, Reduced.YES);
    else if(n instanceof BigDecimal)
      return valueOfHelper((BigDecimal)n);
    else
      return valueOfHelper(n.doubleValue());
  }
  
  /**
   * Constructs a LongFraction with given numerator and denominator. Fraction
   * will be reduced to lowest terms. If fraction is negative, negative sign will
   * be carried on numerator, regardless of how the values were passed in. The numerator
   * and denominator can both be non-integers.<br>
   * <br>
   * Example: {@code LongFraction.valueOf(8.5, -6.25); //-34/25}<br>
   * <br>
   * Warning: when using floating point numbers, round-off error can result
   * in answers that are unexpected. For example,<br>
   * {@code     System.out.println(LongFraction.valueOf(1.1))}<br>
   * <br>
   * This is because 1.1 cannot be expressed exactly in binary form. The
   * computed fraction is exactly equal to the internal representation of
   * the double-precision floating-point number. (Which, for {@code 1.1}, is:
   * {@code (-1)^0 * 2^0 * (1 + 0x199999999999aL / 0x10000000000000L)}.)<br>
   * <br>
   * In many cases, {@code LongFraction.valueOf(Double.toString(d))} may give the result
   * the user expects.
   * 
   * @param numerator any Number to be used as the numerator. 
   *        This does not need to be an integer.
   * @param denominator any Number to be used as the denominator. 
   *        This does not need to be an integer.
   * @return a fully reduced fraction equivalent to {@code numerator/denominator}.
   *        Guaranteed to be non-null.
   * 
   * @throws ArithmeticException if denominator == 0.
   * @throws IllegalArgumentException if numerator or denominator is null.
   */
  public static LongFraction valueOf(Number numerator, Number denominator)
  {
    if(numerator == null)
      throw new IllegalArgumentException("Numerator is null.");
    
    if(denominator == null)
      throw new IllegalArgumentException("Denominator is null.");
    
    if(isInt(numerator) && isInt(denominator))
      return new LongFraction(toLong(numerator), toLong(denominator), Reduced.NO);
    else if(isFloat(numerator) && isFloat(denominator))
      return valueOfHelper(numerator.doubleValue(), denominator.doubleValue());
    else if(numerator instanceof BigDecimal && denominator instanceof BigDecimal)
      return valueOfHelper((BigDecimal)numerator, (BigDecimal)denominator);
    
    //else: convert numerator and denominator to fractions, and divide
    //(n1/d1)/(n2/d2) = (n1*d2)/(d1*n2)
    LongFraction f1 = valueOf(numerator);
    LongFraction f2 = valueOf(denominator);
    return new LongFraction(mulAndCheck(f1.numerator, f2.denominator), 
            mulAndCheck(f1.denominator, f2.numerator), Reduced.NO);
  }
  
  
  /**
   * Constructs a LongFraction from a String. Expected format is {@code numerator/denominator},
   * but "{@code /denominator}" part is optional. Either numerator or denominator may be a floating-point
   * decimal number, which is in the same format as a parameter to the
   * {@link BigDecimal#BigDecimal(String)} constructor.<br>
   * <br>
   * Numerator or denominator can also be expressed as a repeating decimal, such as 0.(1) = 0.1111...
   * Scientific notation is not allowed when using repeating digits.<br>
   * <br>
   * Examples:<br>
   * {@code LongFraction.valueOf("11"); //11/1}<br>
   * {@code LongFraction.valueOf("22/34"); //11/17}<br>
   * {@code LongFraction.valueOf("2e4/-0.64"); //-174375/4}<br>
   * {@code LongFraction.valueOf("0.(1)"); //1/9}<br>
   * {@code LongFraction.valueOf("12.34(56)"); //122222/9900}<br>
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
  public static LongFraction valueOf(String s)
  {
    return valueOf(s, 10);
  }
  
  /**
   * Constructs a LongFraction from a String. Expected format is {@code numerator/denominator},
   * but "{@code /denominator}" part is optional.<br>
   * <br>
   * If {@code radix == 10}: either numerator or denominator may be a floating-point
   * decimal number, which is in the same format as a parameter to the
   * {@link BigDecimal#BigDecimal(String)} constructor.<br>
   * <br>
   * If {@code radix != 10}: the numerator and denominator may be a radixed string
   * string in that base, but <b>cannot</b> contain a scientific notation exponent.
   * The numerator and denominator must be in a format that, with the radix point removed,
   * can be parsed by the {@link BigInteger#BigInteger(String, int)} constructor. This means
   * that scientific notation is <em>not</em> allowed in bases other than 10.<br>
   * <br>
   * Numerator or denominator can also be expressed as a radixed string with a repeating
   * digit, such as 0.(1) = 0.1111... This is allowed with any radix. Scientific notation
   * is not allowed when using repeating digits.<br>
   * <br>
   * Examples:<br>
   * {@code LongFraction.valueOf("11", 10); //11/1}<br>
   * {@code LongFraction.valueOf("22/34", 10); //11/17}<br>
   * {@code LongFraction.valueOf("2e4/-0.64", 10); //-174375/4}<br>
   * {@code LongFraction.valueOf("dead/beef", 16); //57005/48879}<br>
   * {@code LongFraction.valueOf("lazy.fox", 36); //15459161339/15552}<br>
   * {@code LongFraction.valueOf("0.(1)", 10); //1/9}<br>
   * {@code LongFraction.valueOf("12.34(56)", 10); //122222/9900}<br>
   * {@code LongFraction.valueOf("0.(1)", 16); //1/15}<br>
   * {@code LongFraction.valueOf("the.lazy(fox)", 36); //2994276908470787/78362484480}<br>
   * 
   * 
   * @param s a string representation of a number or fraction
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @return a fully reduced fraction equivalent to the specified string. Guaranteed to be non-null.
   * 
   * @throws NumberFormatException  if the string cannot be properly parsed.
   * @throws ArithmeticException if denominator == 0.
   * @throws IllegalArgumentException if s is null.
   * 
   * @see BigDecimal#BigDecimal(String)
   * @see BigInteger#BigInteger(String, int)
   */
  public static LongFraction valueOf(String s, int radix)
  {
    if(s == null)
      throw new IllegalArgumentException("Null argument.");
    
    if(radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      radix = 10;
    
    String num = null;
    String den = null;
    
    int slashPos = s.indexOf('/');
    if(slashPos < 0)
    {
      num = s;
    }
    else
    {
      num = s.substring(0, slashPos);
      den = s.substring(slashPos+1, s.length());
    }
    
    int parenPos = s.indexOf('(');
    if(radix == 10 && parenPos < 0)
    {
      //if radix is 10, and we don't have repeating digits, we piggy-back on BigDecimal
      if(den == null)
        return valueOfHelper(new BigDecimal(num));
      else
        return valueOfHelper(new BigDecimal(num), new BigDecimal(den));
    }
    else
    {
      //otherwise we use the helper method to parse each part.
      if(den == null)
        return valueOfHelper(num, radix);
      else
        return valueOfHelper(num, radix).divide(valueOfHelper(den, radix));
    }
  }
  
  /**
   * Returns the numerator of this fraction.
   * @return numerator of this fraction.
   */
  @Override
  public final Long getNumerator()
  {
    return numerator;
  }
  
  /**
   * Returns the denominator of this fraction.
   * @return denominator of this fraction.
   */
  @Override
  public final Long getDenominator() {
    return denominator;
  }
  
  /**
   * Returns this + n.
   * @param n number to be added to this
   * @return this + n
   * @throws IllegalArgumentException if n is null.
   */
  @Override
  public LongFraction add(Number n)
  {
    if(isZero(n))
      return this;
    
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    if(isInt(n))
    {
      //n1/d1 + n2 = (n1 + d1*n2)/d1
      return new LongFraction(
              addAndCheck(numerator, mulAndCheck(denominator, toLong(n))),
              denominator, Reduced.YES
      );
    }
    else
    {
      LongFraction f = valueOf(n);
      
      //n1/d1 + n2/d2 = (n1*(lcm/d1) + n2*(lcm/d2))/lcm
      long lcm = lcm(denominator, f.denominator);
      return new LongFraction(
              addAndCheck(mulAndCheck(numerator, lcm/denominator), 
              mulAndCheck(f.numerator, lcm/f.denominator)),
              lcm, Reduced.NO
      );
    }
  }
  
  /**
   * Returns a + b, represented as a LongFraction. Equivalent to {@code LongFraction.valueOf(a).add(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to be added
   * @param b number to be added
   * @return a + b
   * @throws IllegalArgumentException if a or b is null.
   */
  public static LongFraction sum(Number a, Number b)
  {
    return valueOf(a).add(b);
  }
  
  /**
   * Returns this - n.
   * @param n number to be subtracted from this
   * @return this - n
   * @throws IllegalArgumentException if n is null.
   */
  @Override
  public LongFraction subtract(Number n)
  {
    if(isZero(n))
      return this;
    
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    if(isInt(n))
    {
      //n1/d1 - n2 = (n1 - d1*n2)/d1
      return new LongFraction(
              subAndCheck(numerator, mulAndCheck(denominator, toLong(n))),
              denominator, Reduced.YES
      );
    }
    else
    {
      LongFraction f = valueOf(n);
      
      //n1/d1 - n2/d2 = (n1*(lcm/d1) - n2*(lcm/d2))/lcm
      long lcm = lcm(denominator, f.denominator);
      return new LongFraction(
              subAndCheck(mulAndCheck(numerator, lcm/denominator), 
              mulAndCheck(f.numerator, lcm/f.denominator)),
              lcm, Reduced.NO
      );
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
  @Override
  public LongFraction subtractFrom(Number n)
  {
    if(isZero(n))
      return this.negate();
    
    if(n == null)
      throw new IllegalArgumentException("Null argument");
    
    if(isInt(n))
    {
      //n1 - n2/d2 = (d2*n1 - n2)/d2
      return new LongFraction(
              subAndCheck(mulAndCheck(denominator, toLong(n)), numerator),
              denominator, Reduced.YES
      );
    }
    else
    {
      LongFraction f = valueOf(n);
      
      //n1/d1 - n2/d2 = (n1*(lcm/d1) - n2*(lcm/d2))/lcm
      long lcm = lcm(denominator, f.denominator);
      return new LongFraction(
              subAndCheck(mulAndCheck(f.numerator, lcm/f.denominator), 
              mulAndCheck(numerator, lcm/denominator)),
              lcm, Reduced.NO
      );
    }
  }
  
  /**
   * Returns a - b, represented as a LongFraction. Equivalent to {@code LongFraction.valueOf(a).subtract(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to subtract from (minuend)
   * @param b number to be subtracted from a (subtrahend)
   * @return a - b
   * @throws IllegalArgumentException if a or b is null.
   */
  public static LongFraction difference(Number a, Number b)
  {
    return valueOf(a).subtract(b);
  }
  
  /**
   * Returns this * n.
   * @param n number to be multiplied by this
   * @return this * n
   * @throws IllegalArgumentException if n is null.
   */
  @Override
  public LongFraction multiply(Number n)
  {
    if(isZero(n))
      return LongFraction.ZERO;
    if(isOne(n))
      return this;
    
    LongFraction f = valueOf(n);
    
    //in order to reduce chance of overflow, we need to compute as a reduced fraction. This means computing gcd twice,
    //instead of just once, but we will have smaller in between values. 
    //(n1/d1)*(n2/d2) = (n1/d2)*(n2/d1) = ((n1/gcd(n1,d2))/(d2/gcd(n1,d2))) * 
    //((n2/gcd(n2,d1))/(d1/gcd(n2,d1))) = (n1'/d2')*(n2'/d1') = (n1'*n2')/(d1'*d2')
    long n1 = numerator, d1 = denominator, n2 = f.numerator, d2 = f.denominator;
    long gcd1 = gcd(n1, d2);
    n1 /= gcd1;
    d2 /= gcd1;
    long gcd2 = gcd(n2, d1);
    n2 /= gcd2;
    d1 /= gcd2;
    
    return new LongFraction(mulAndCheck(n1, n2), mulAndCheck(d1, d2), Reduced.YES);
  }
  
  /**
   * Returns a * b, represented as a LongFraction. Equivalent to {@code LongFraction.valueOf(a).multiply(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to be multiplied
   * @param b number to be multiplied
   * @return a * b
   * @throws IllegalArgumentException if a or b is null.
   */
  public static LongFraction product(Number a, Number b)
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
  @Override
  public LongFraction divide(Number n)
  {
    if(isOne(n))
      return this;
    
    //division is the same thing as constructing new fraction
    return valueOf(this, n);
  }
  
  /**
   * Returns n / this. Sometimes this results in cleaner code than rearranging the code to use divide().
   * 
   * @param n number to be divided by this (dividend)
   * @return n / this
   * @throws IllegalArgumentException if n is null.
   * @throws ArithmeticException if this == 0.
   */
  @Override
  public LongFraction divideInto(Number n)
  {
    if(isOne(n))
      return this.reciprocal();
    if(isZero(n) && !isZero(this))
      return LongFraction.ZERO;
    
    //division is the same thing as constructing new fraction
    return valueOf(n, this);
  }
  
  /**
   * Returns a / b, represented as a LongFraction. Equivalent to {@code LongFraction.valueOf(a).divide(b)}.
   * Also equivalent to {@code LongFraction.valueOf(a, b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number to be divided (dividend)
   * @param b number by which to divide (divisor)
   * @return a / b
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   */
  public static LongFraction quotient(Number a, Number b)
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
  @Override
  public Long divideToIntegralValue(Number n)
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
  @Override
  public Long divideToIntegralValue(Number n, DivisionMode divisionMode)
  {
    return (long)
       (divideAndRemainderImpl(this, n, divisionMode, RemainderMode.QUOTIENT));
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
  @Override
  public LongFraction remainder(Number n)
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
  @Override
  public LongFraction remainder(Number n, DivisionMode divisionMode)
  {
    return (LongFraction)
        (divideAndRemainderImpl(this, n, divisionMode, RemainderMode.REMAINDER));
  }
  
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
  @Override
  public Number[] divideAndRemainder(Number n)
  {
    return divideAndRemainder(n, DivisionMode.TRUNCATED);
  }
  
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
  @Override
  public Number[] divideAndRemainder(Number n, DivisionMode divisionMode)
  {
    return (Number[])(divideAndRemainderImpl(this, n, divisionMode, RemainderMode.BOTH));
  }
  
  /**
   * Private method to do all the work of integer division with fractional remainder.
   * Code is optimized for speed moreso than readability, but I've tried to comment.
   * Returns either just the quotient, just the remainder, or an array of both,
   * depending on the remainder mode.
   */
  private static Object divideAndRemainderImpl(Number na, Number nb, 
          DivisionMode divisionMode, RemainderMode remainderMode)
  {
    if(divisionMode == null)
      throw new IllegalArgumentException("Null argument");
    else if(isZero(nb))
      throw arithmeticException("Divide by zero.");
    else if(isZero(na))
      return divideAndRemainderReturner(0L, LongFraction.ZERO, remainderMode);
    
    LongFraction a = valueOf(na);
    LongFraction b = valueOf(nb);
    
    //TODO: Compute gcd and reduce before multiplying, to reduce chance of overflow?
    
    //First calculate true a/b value. We don't care about reducing to lowest terms
    //yet, so calculate numerator and denominator separately:
    //  a/b = (a.n/a.d)/(b.n/b.d) = (a.n/a.d)*(b.d/b.n) = (a.n*b.d)/(a.d*b.n)
    //also worth noting: sign(a)==sign(num), sign(b)==sign(den)
    long num = mulAndCheck(a.numerator, b.denominator);
    long den = mulAndCheck(a.denominator, b.numerator);
    
    //BigInteger.divideAndRemainder() uses TRUNCATED division to give us values 
    //q,r such that:  num/den = q + r/den
    //For other division modes, we may need to adjust q,r to new values q',r'. 
    //If we adjust q by adjustment x, i.e. q'=q+x, then:
    //  q + r/den = q' + r'/den
    //  q + r/den = q + x + r'/den
    //      r/den =     x + r'/den
    //     r'/den = r/den - x
    //         r' = r - x*den
    //In actuality, x will either be -1, 0, or 1.
    long adjustment = 0L;
    if(divisionMode == DivisionMode.FLOORED && 
       ((num < 0L || den < 0L) && signum(num) != signum(den)))
    {
      //floor is equivalent to truncation for positive quotient, 
      //but for negative quotient we have to subtract one
      adjustment = -1L;
    }
    else if(divisionMode == DivisionMode.EUCLIDEAN && num < 0L)
    {
      //Euclidean division picks a quotient to ensure the remainder is always positive.
      // *  b > 0: q = floor(a/b)
      // *  b < 0: q = ciel(a/b)
      
      //For the four different combinations of signs of the operators, 
      //two are the same as truncation,
      //and two require additional modification:
      //   + / +: +  =>  floor(q) == trunc(q)
      //   + / -: -  =>  ciel(q)  == trunc(q)
      //   - / +: -  =>  floor(q) == trunc(q) - 1  **modification required
      //   - / -: +  =>  ciel(q)  == trunc(q) + 1  **modification required
      if(den > 0L)
        adjustment = -1L;
      else
        adjustment = 1L;
    }
    
    
    //we may only need one or the other of q,r. Only compute the ones that we need.
    Long q=null, r=null;
    if(remainderMode == RemainderMode.REMAINDER)
    {
      //if we are in remainder mode, we never care about the quotient
      r = num % den;
    }
    else if(adjustment == 0L && remainderMode == RemainderMode.QUOTIENT)
    {
      //in quotient mode, if we have an adjustment we have to get both quotient 
      //and remainder, because we cancel the adjustment if the
      //remainder is 0. But if adjustment is already 0, we can get only the quotient.
      q = num / den;
    }
    else
    {
      q = num / den;
      r = num % den;
    }
    
    //if the remainder is 0, we don't do any adjustments, and we already know the remainder will
    //be zero, so go ahead and return this.
    if(isZero(r))
      return divideAndRemainderReturner(q, LongFraction.ZERO, remainderMode); //or could do: adjustment=0
    
    //avoid doing unnecessary math... at this point if we got both q and r, 
    //but only need q, we can drop r.
    if(r != null && remainderMode == RemainderMode.QUOTIENT)
      r = null;
    
    if(adjustment == -1L)
    {
      q = (q == null ? null : addAndCheck(q, -1L));  //q' = q + (-1)
      r = (r == null ? null : addAndCheck(r, den));  //r' = r - (-1)*den
    }
    else if(adjustment == 1L)
    {
      q = (q == null ? null : addAndCheck(q, 1L));   //q' = q + (1)
      r = (r == null ? null : subAndCheck(r, den));  //r' = r - (1)*den
    }
    
    //At this point we have:
    //  num/den = q + r/den
    //We want to compute q", r", such that:  a/b = q" + r"/b
    //We know that a/b = num/den, and q = q". So we are left with:
    //  (r"/b)=(r/den)
    //  r" = r * b / den = (r * b.n)/(b.d * den)
    
    LongFraction rFract = 
            r == null ? null : 
            new LongFraction(
                    mulAndCheck(r, b.numerator), 
                    mulAndCheck(b.denominator, den), Reduced.NO
            );
    
    return divideAndRemainderReturner(q, rFract, remainderMode);
  }
  
  /**
   * Helper method to handle return value for divideAndRemainderImpl.
   */
  private static Object divideAndRemainderReturner(Long q, LongFraction r, 
          RemainderMode remainderMode)
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
   * Equivalent to {@code LongFraction.valueOf(a).divideToIntegralValue(b)}.
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
  public static long integralQuotient(Number a, Number b)
  {
    return valueOf(a).divideToIntegralValue(b);
  }
  
  /**
   * Returns result of integer division a / b, using specified division mode.
   * Equivalent to {@code LongFraction.valueOf(a).divideToIntegralValue(b, divisionMode)}.
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
  public static long integralQuotient(Number a, Number b, DivisionMode divisionMode)
  {
    return valueOf(a).divideToIntegralValue(b, divisionMode);
  }
  
  
  /**
   * Returns fractional remainder of integer division a / b, using truncated division mode.
   * Equivalent to {@code LongFraction.valueOf(a).remainder(b)}.
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
  public static LongFraction remainder(Number a, Number b)
  {
    return valueOf(a).remainder(b);
  }
  
  /**
   * Returns fractional remainder of integer division a / b, using specified division mode.
   * Equivalent to {@code LongFraction.valueOf(a).remainder(b, divisionMode)}.
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
  public static LongFraction remainder(Number a, Number b, DivisionMode divisionMode)
  {
    return valueOf(a).remainder(b, divisionMode);
  }
  
  /**
   * Returns integral quotient and fractional remainder of integer division a / b, 
   * using truncated division mode.
   * Equivalent to {@code LongFraction.valueOf(a).divideAndRemainder(b)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. 
   *         First is a {@code BigInteger}, second is a {@code LongFraction}.
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
   * Returns integral quotient and fractional remainder of integer division a / b, 
   * using specified division mode.
   * Equivalent to {@code LongFraction.valueOf(a).divideAndRemainder(b, divisionMode)}.
   * Provided as static method to make code easier to write in some instances.
   * 
   * @param a number be divided (dividend)
   * @param b number to divide by (divisor)
   * @param divisionMode division mode to use when dividend or divisor is negative.
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. 
   *         First is a {@code BigInteger}, second is a {@code LongFraction}.
   * 
   * @throws IllegalArgumentException if a or b is null.
   * @throws ArithmeticException if b == 0.
   * 
   * @see #divideAndRemainder(Number n, DivisionMode divisionMode)
   * @see DivisionMode
   */
  public static Number[] quotientAndRemainder(Number a, Number b, 
          DivisionMode divisionMode)
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
  public LongFraction gcd(Number n) {
    LongFraction f = valueOf(n);
    
    if(isZero(this))
      return f.abs();
    if(isZero(f))
      return this.abs();
    
    //gcd((a/b),(c/d)) = gcd(a,c) / lcm(b,d)
    //Note: this result is guaranteed to be a reduced fraction.
    return new LongFraction(
            gcd(this.numerator, f.numerator), 
            lcm(this.denominator, f.denominator), Reduced.YES
    );
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
  public LongFraction lcm(Number n) {
    LongFraction f = valueOf(n);
    
    if(this.numerator == 0L || f.numerator == 0L)
      return LongFraction.ZERO;
    
    //lcm((a/b),(c/d)) = lcm(a,c) / gcd(b,d)
    //Note: this result is guaranteed to be a reduced fraction.
    return new LongFraction(
            lcm(this.numerator, f.numerator), 
            gcd(this.denominator, f.denominator), Reduced.YES
    );
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
  @Override
  public LongFraction pow(int exponent)
  {
    if(exponent < 0 && isZero(this))
      throw arithmeticException("Divide by zero: raising zero to negative exponent.");
    
    if(exponent == 0)
      return LongFraction.ONE;
    else if (exponent == 1)
      return this;
    else if (exponent > 0)
      return new LongFraction(
              powAndCheck(numerator, exponent), 
              powAndCheck(denominator, exponent), Reduced.YES
      );
    else
      return new LongFraction(
              powAndCheck(denominator, -exponent), 
              powAndCheck(numerator, -exponent), Reduced.YES
      );
  }
  
  /**
   * Returns 1/this.
   * 
   * @return 1/this
   * 
   * @throws ArithmeticException if this == 0.
   */
  public LongFraction reciprocal()
  {
    if(isZero(this))
      throw arithmeticException("Divide by zero: reciprocal of zero.");
    
    return new LongFraction(denominator, numerator, Reduced.YES);
  }
  
  /**
   * Returns the complement of this fraction, which is equal to 1 - this.
   * Useful for probabilities/statistics.
   * 
   * @return 1-this
   */
  public LongFraction complement()
  {
    //1 - n/d == d/d - n/d == (d-n)/d
    return new LongFraction(
            subAndCheck(denominator, numerator), 
            denominator, Reduced.YES
    );
  }
  
  /**
   * Returns -this. If this is zero, returns zero.
   * @return equivalent of {@code this.multiply(-1)}
   */
  public LongFraction negate()
  {
    return withSign(-signum(numerator));
  }
  
  /**
   * Returns the absolute value of this.
   * @return absolute value of this.
   */
  public LongFraction abs()
  {
    return withSign(1);
  }
  
  /**
   * Returns this, with sign set to the sign of {@code sgn} parameter.
   * Another way of saying it: returns the equivalent of {@code this.abs().multiply(Math.signum(sgn))}.<br>
   * <br>
   * Important Note: If this is zero, always returns zero. No exception thrown, 
   * even if we are trying to set the sign of 0 to positive or negative.
   * 
   * @param sgn an integer less than, equal to, or greater than 0, whose sign 
   *            will be assigned to the returned fraction.
   * @return equivalent of {@code this.abs().multiply(Math.signum(sgn))}.
   */
  public LongFraction withSign(int sgn)
  {
    if(sgn == 0 || isZero(this))
      return LongFraction.ZERO;
    
    int thisSignum = signum();
    if((thisSignum < 0 && sgn > 0) || (thisSignum > 0 && sgn < 0))
      return new LongFraction(negateAndCheck(numerator), denominator, Reduced.YES);
    
    return this;
  }
  
  /**
   * Returns -1, 0, or 1, representing the sign of this fraction.
   * @return -1, 0, or 1, representing the sign of this fraction.
   */
  public int signum()
  {
    return signum(numerator);
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
  @Override
  public Long getIntegerPart()
  {
    return getIntegerPart(DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns the integer part of this fraction; that is, the part that
   * would come before the decimal point if this were written as a decimal
   * number. Carries the same sign as this fraction.
   * 
   * @param divisionMode Division mode to use when computing quotient. 
   *                     Only relevant if this is negative.
   * @return integer part of this fraction (numerator/denominator), 
   *         using specified division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  @Override
  public Long getIntegerPart(DivisionMode divisionMode)
  {
    if(divisionMode == null)
      throw new IllegalArgumentException("Null argument");
    if(denominator == 1L)
      return numerator;
    
    long iPart = numerator / denominator;
    
    if(numerator < 0L && divisionMode != DivisionMode.TRUNCATED)
      iPart = addAndCheck(iPart, -1L);
    
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
   * @return fractional part of this fraction (i.e. {@code (numerator%denominator)/denominator)}), 
   *         using TRUNCATED division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public LongFraction getFractionPart()
  {
    return getFractionPart(DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns the fraction part of this fraction; that is, the remainder when
   * the numerator is divided by the denominator when using the specified
   * division mode.
   * 
   * @param divisionMode Division mode to use when computing remainder. 
   *                     Only relevant if this is negative.
   * @return fractional part of this fraction (i.e. {@code (numerator%denominator)/denominator)}), 
   *         using specified division mode.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  public LongFraction getFractionPart(DivisionMode divisionMode)
  {
    if(divisionMode == null)
      throw new IllegalArgumentException("Null argument");
    if(denominator == 1L)
      return LongFraction.ZERO;
    
    long fPart = numerator % denominator;
    
    if(numerator < 0L && divisionMode != DivisionMode.TRUNCATED)
      fPart = addAndCheck(fPart, denominator);
    
    return new LongFraction(fPart, denominator, Reduced.YES);
  }
  
  /**
   * Returns the integer and fraction parts of this fraction. The return
   * array is guaranteed to have exactly two elements. The first is guaranteed
   * to be a BigInteger, equivalent to the result of getIntegerPart().
   * The second element is guaranteed to be a LongFraction, equivalent to
   * the result of getFractionPart().<br>
   * <br>
   * Equivalent to {@code getParts(DivisionMode.TRUNCATED)}
   * 
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. 
   *         First is a {@code BigInteger}, second is a {@code LongFraction}.
   *         These represent the part that would be written before the decimal, 
   *         and the part that would be after the decimal, if this fraction
   *         were written in decimal format.
   * 
   * @see #getParts(DivisionMode divisionMode)
   */
  @Override
  public Number[] getParts()
  {
    return getParts(DivisionMode.TRUNCATED);
  }
  
  /**
   * Returns the integer and fraction parts of this fraction. The return
   * array is guaranteed to have exactly two elements. The first is guaranteed
   * to be a BigInteger, and the second is guaranteed to be a LongFraction.<br>
   * <br>
   * The first element is the result of integer division of numerator by
   * denominator, using the supplied division mode. The second element is
   * the fraction given by numerator mod denominator, using the given
   * division mode.<br>
   * <br>
   * Note that the division mode only matters if this fraction is negative.
   * Because the sign of a LongFraction is always carried by the numerator,
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
   * @return Two {@code Number} objects. Guaranteed to be two non-null elements. 
   *         First is a {@code BigInteger}, second is a {@code LongFraction}.
   *         These represent the part that would be written before the decimal, 
   *         and the part that would be after the decimal, if this fraction
   *         were written in decimal format.
   * 
   * @see DivisionMode
   */
  @Override
  public Number[] getParts(DivisionMode divisionMode) {
    if(divisionMode == null)
      throw new IllegalArgumentException("Null argument");
    if(denominator == 1L)
      return new Number[]{numerator, LongFraction.ZERO};
    
    long iPart = numerator / denominator;
    long fPart = numerator % denominator;
    
    if(numerator < 0L && divisionMode != DivisionMode.TRUNCATED) {
      iPart = addAndCheck(iPart, -1L);
      fPart = addAndCheck(fPart, denominator);
    }
    
    return new Number[]{iPart, new LongFraction(fPart, denominator, Reduced.YES)};
  }
  
  /**
   * Returns this rounded to the nearest whole number, using
   * RoundingMode.HALF_UP as the default rounding mode.
   * 
   * @return this fraction rounded to nearest whole number, using RoundingMode.HALF_UP.
   */
  @Override
  public Long round()
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
  @Override
  public Long round(RoundingMode roundingMode)
  {
    if(roundingMode == null)
      throw new IllegalArgumentException("Null argument");
    
    //Since fraction is always in lowest terms, this is an exact integer
    //iff the denominator is 1.
    if(denominator == 1L)
      return numerator;
    
    //If the denominator was not 1, rounding will be required.
    if(roundingMode == RoundingMode.UNNECESSARY)
      throw arithmeticException("Rounding necessary");
    
    final Set<RoundingMode> ROUND_HALF_MODES = 
            EnumSet.of(RoundingMode.HALF_UP, 
                       RoundingMode.HALF_DOWN, 
                       RoundingMode.HALF_EVEN);
    
    long intVal = numerator / denominator;
    long remainder = numerator % denominator;
    
    //For HALF_X rounding modes, convert to either UP or DOWN.
    if(ROUND_HALF_MODES.contains(roundingMode))
    {
      //Since fraction is always in lowest terms, the remainder is exactly
      //one-half iff the denominator is 2.
      if(denominator == 2L)
      {
        if(roundingMode == RoundingMode.HALF_UP || 
           (roundingMode == RoundingMode.HALF_EVEN && ((intVal & 1L) == 1L)))
        {
          roundingMode = RoundingMode.UP;
        }
        else
        {
          roundingMode = RoundingMode.DOWN;
        }
      }
      else if (absAndCheck(remainder) <= (denominator/2))
      {
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
      if(numerator > 0)
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
      throw new IllegalArgumentException("Unsupported rounding mode: " + roundingMode.toString());
    
    if(roundingMode == RoundingMode.UP)
    {
      if (numerator > 0)
        intVal = addAndCheck(intVal, 1L);
      else
        intVal = addAndCheck(intVal, -1L);
    }
    
    return intVal;
  }
  
  
  /**
   * Rounds this fraction to the nearest multiple of the given number, using HALF_UP
   * rounding method.
   * 
   * @param n number to which we will round to the nearest multiple
   * 
   * @return this value, rounded to the nearest multiple of n
   * 
   * @throws IllegalArgumentException If n is null.
   * @throws ArithmeticException If n is zero or negative.
   */
  public LongFraction roundToNumber(Number n) {
    return roundToNumber(n, RoundingMode.HALF_UP);
  }
  
  /**
   * Rounds this fraction to the nearest multiple of the given number, using the
   * specified rounding method.<br>
   * <br>
   * Note for HALF_EVEN rounding method: this rounds to the nearest even multiple of
   * n, which may or may not be even. For example, if rounding to the nearest 2, every
   * result will be even. So 9 rounded to nearest 2 with HALF_EVEN will round to 8, since
   * 8=2*4 (4 being an even number), whereas 10=2*5 (5 being odd).
   * 
   * @param n number to which we will round to the nearest multiple
   * @param roundingMode rounding mode to use if the answer must be rounded
   * 
   * @return this value, rounded to the nearest multiple of n
   * 
   * @throws IllegalArgumentException If n is null.
   * @throws ArithmeticException If n is zero or negative.
   * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but
   *         this fraction is not an exact multiple of the given value.
   */
  @Override
  public LongFraction roundToNumber(Number n, RoundingMode roundingMode) {
    if(n == null || roundingMode == null)
      throw new IllegalArgumentException("Null argument");
    
    LongFraction f = valueOf(n);
    
    if(f.signum() <= 0)
      throw arithmeticException("newDenominator must be positive");
    
    return product(this.divide(f).round(roundingMode), f);
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
   * @see #roundToDenominator(Long, RoundingMode)
   */
  @Override
  public Long roundToDenominator(Long newDenominator)
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
   * @throws ArithmeticException If newDenominator is zero or negative.
   * @throws ArithmeticException if RoundingMode.UNNECESSARY is used but
   *         this fraction cannot be represented exactly as a fraction with the
   *         given denominator.
   */
  @Override
  public Long roundToDenominator(Long newDenominator, RoundingMode roundingMode)
  {
    if(roundingMode == null)
      throw new IllegalArgumentException("Null argument");
    
    if(newDenominator <= 0L)
      throw arithmeticException("newDenominator must be positive");
    
    //n1/d1 = x/d2  =>   x = (n1/d1)*d2
    return this.multiply(newDenominator).round(roundingMode);
  }
  
  /**
   * Returns a string representation of this, in the form
   * numerator/denominator. The denominator will
   * always be included, even if it is 1.
   * 
   * @return This fraction, represented as a string in the format {@code numerator/denominator}.
   */
  @Override
  public String toString()
  {
    return toString(10, false);
  }
  
  /**
   * Returns string representation of this, in the form of numerator/denominator, with numerator
   * and denominator represented in the given radix. The digit-to-character mapping provided by
   * {@link Character#forDigit} is used.
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @return This fraction, represented as a string in the format {@code numerator/denominator}.
   */
  @Override
  public String toString(int radix)
  {
    return toString(radix, false);
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
    return toString(10, denominatorOptional);
  }
  
  /**
   * Returns string representation of this, in the form of numerator/denominator, with numerator
   * and denominator represented in the given radix. The digit-to-character mapping provided by
   * {@link Character#forDigit} is used.<br>
   * <br>
   * Optionally, "/denominator" part can be ommitted for whole numbers.
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @param denominatorOptional If true, the denominator will be ommitted
   *        when it is unnecessary. For example, "7" instead of "7/1".
   * @return This fraction, represented as a string in the format {@code numerator/denominator}.
   */
  public String toString(int radix, boolean denominatorOptional)
  {
    if(denominatorOptional && denominator == 1L)
      return Long.toString(numerator, radix);
    return Long.toString(numerator, radix) + "/" + Long.toString(denominator, radix);
  }
  
  /**
   * Returns string representation of this object as a mixed fraction.
   * For example, 4/3 would be "1 1/3". For negative fractions, the
   * sign is carried only by the whole number and assumed to be distributed
   * across the whole value. For example, -4/3 would be "-1 1/3". For
   * fractions that are equal to whole numbers, only the whole number will
   * be displayed. For fractions which have absolute value less than 1,
   * this will be equivalent to {@link #toString}.
   * 
   * @return String representation of this fraction as a mixed fraction.
   */
  @Override
  public String toMixedString()
  {
    return toMixedString(10);
  }
  
  
  /**
   * Returns string representation of this object as a mixed fraction.
   * For example, 4/3 would be "1 1/3". For negative fractions, the
   * sign is carried only by the whole number and assumed to be distributed
   * across the whole value. For example, -4/3 would be "-1 1/3". For
   * fractions that are equal to whole numbers, only the whole number will
   * be displayed. For fractions which have absolute value less than 1,
   * this will be equivalent to {@link #toString(int radix)}.<br>
   * <br>
   * The numbers are represented in the given radix. The digit-to-character mapping provided by
   * {@link Character#forDigit} is used.
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @return String representation of this fraction as a mixed fraction.
   */
  @Override
  public String toMixedString(int radix)
  {
    if(denominator == 1L)
      return Long.toString(numerator);
    
    if(absAndCheck(numerator) < denominator)
      return toString(radix);
    
    long iPart = numerator / denominator;
    long fPart = numerator % denominator;
    
    return Long.toString(iPart, radix) + " " + 
           Long.toString(absAndCheck(fPart), radix) + "/" + 
           Long.toString(denominator, radix);
  }
  
  
  /**
   * Returns decimal string representation of the fraction with the given number
   * of decimal digits using roundingMode ROUND_HALF_UP.
   * 
   * @param numDecimalDigits number of digits to be displayed after the decimal
   * @return decimal string representation of this fraction.
   * 
   * @throws ArithmeticException if roundingMode is UNNECESSARY but rounding is required.
   */
  @Override
  public String toDecimalString(int numDecimalDigits)
  {
    return toRadixedString(10, numDecimalDigits, RoundingMode.HALF_UP);
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
   * @throws ArithmeticException if roundingMode is UNNECESSARY but rounding is required.
   */
  public String toDecimalString(int numDecimalDigits, RoundingMode roundingMode)
  {
    return toRadixedString(10, numDecimalDigits, roundingMode);
  }
  
  /**
   * Converts the fraction to a radixed string with the given number of fraction digits
   * after the radix point. Rounds using HALF_UP rounding mode. The digit-to-character mapping provided by
   * {@link Character#forDigit} is used.
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @param numFractionalDigits number of digits to be displayed after the radix point.
   * @return radixed string representation of this fraction.
   */
  @Override
  public String toRadixedString(int radix, int numFractionalDigits)
  {
    return toRadixedString(radix, numFractionalDigits, RoundingMode.HALF_UP);
  }
  
  
  /**
   * Converts the fraction to a radixed string with the given number of fraction digits
   * after the radix point.<br>
   * <br>
   * For example, 1/8 in base 10 is 0.125. In base 2 it is 0.001.<br>
   * <br>
   * Will append trailing 0s as needed: (1/2).toRadixedString(3,2) is 0.100.<br>
   * <br>
   * If passed negative numFractionalDigits, rounds to nearest radix^(-numFractionalDigits). For example,
   * -1 means round to nearest 10, -2 means round to nearest 100, etc. No extra zeros are prepended in this
   * case, since the only time it would be necessary is if a value were rounded to zero.<br>
   * <br>
   * The digit-to-character mapping provided by {@link Character#forDigit} is used.
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @param numFractionalDigits number of digits to be displayed after the decimal
   * @param roundingMode how to round the number if necessary
   * @return radixed string representation of this fraction.
   * 
   * @throws ArithmeticException if roundingMode is UNNECESSARY but rounding is required.
   */
  public String toRadixedString(int radix, int numFractionalDigits, RoundingMode roundingMode)
  {
    if(roundingMode == null)
      throw new IllegalArgumentException("Null argument");
    
    if(radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      radix = 10;
    
    //shortcut - if we don't want any fractional digits, this is equivalent to round()
    if(numFractionalDigits == 0)
      return Long.toString(this.round(roundingMode), radix);
    
    if(numFractionalDigits > 0)
    {
      //multiply by (radix)^(digits), then round to integer
      long rounded = this.multiply(BigInteger.valueOf(radix)
              .pow(numFractionalDigits)).round(roundingMode);
      
      //get the actual digits (ignoring the sign bit)
      String digits = Long.toString(Math.abs(rounded), radix);
      
      String beforeRadixPoint = "0";
      String afterRadixPoint = digits;
      int padLen = 0; //number of zeros we need to pad afterDecimal with with
      
      if(digits.length() > numFractionalDigits)
      {
        //we got too many digits... need to split into before/after decimal parts
        beforeRadixPoint = digits.substring(0, digits.length() - numFractionalDigits);
        afterRadixPoint = digits.substring(digits.length() - numFractionalDigits);
      }
      else if (digits.length() < numFractionalDigits)
      {
        //we don't have enough digits. We will have to pad with zeros
        padLen = numFractionalDigits - digits.length();
      }
      //else: we got exactly the right number of digits. nothing to do!
      
      //create string builder to hold result. init buffer to max possible size: 
      //length of parts plus length of padding plus space for . and -
      StringBuilder sb = new StringBuilder(
              beforeRadixPoint.length() + afterRadixPoint.length() + padLen + 2);
      
      //Note: need to use sign of rounded, not sign of this, 
      //because if we round a small negative number to
      //zero the sign will be lost.
      if(rounded < 0)
        sb.append('-');
      
      sb.append(beforeRadixPoint).append('.');
      
      for(int i = 0; i < padLen; i++)
        sb.append('0');
      
      sb.append(afterRadixPoint);
      
      return sb.toString();
    }
    else
    {
      //numFractionalDigits is negative. divide out the number of digits then round to integer
      int absFractionalDigits = -numFractionalDigits;
      
      String rounded = Long.toString(this.divide(BigInteger.valueOf(radix)
              .pow(absFractionalDigits)).round(roundingMode), radix);
      
      //at this point, if we got 0, just return 0. No need to return something like "00000". if we have anything
      //other than 0, then we need to append as many 0s as abs(numFractionalDigits)
      if(rounded.equals("0"))
        return "0";
      
      StringBuilder sb = new StringBuilder(rounded.length() + absFractionalDigits);
      sb.append(rounded);
      for(int i = 0; i < absFractionalDigits; i++)
        sb.append('0');
      
      return sb.toString();
    }
  }
  
  /**
   * Converts the fraction to a radixed string with repeating digits. The
   * repeating digits are indicated by parenthesis: 1/9 becomes 0.(1)<br>
   * <br>
   * Equivalent to {@code toRepeatingString(10, false)}
   * 
   * @return radixed string representation of this fraction with repeating digits denoted in parenthesis.
   * 
   * @see #toRepeatingDigitString(int, boolean)
   */
  @Override
  public String toRepeatingDigitString() {
    return toRepeatingDigitString(10, false);
  }
  
  /**
   * Converts the fraction to a radixed string with repeating digits. The
   * repeating digits are indicated by parenthesis: 1/9 becomes 0.(1)<br>
   * <br>
   * Equivalent to {@code toRepeatingString(10, forceRepeating)}
   * 
   * @param forceRepeating whether or not to force this function to always use a repeating fraction,
   *                       even if the radixed string terminates
   * @return radixed string representation of this fraction with repeating digits denoted in parenthesis.
   * 
   * @see #toRepeatingDigitString(int, boolean)
   */
  public String toRepeatingDigitString(boolean forceRepeating) {
    return toRepeatingDigitString(10, forceRepeating);
  }
  
  /**
   * Converts the fraction to a radixed string with repeating digits, in the given radix. The
   * repeating digits are indicated by parenthesis: 1/9 becomes 0.(1)<br>
   * <br>
   * Equivalent to {@code toRepeatingString(radix, false)}
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @return radixed string representation of this fraction with repeating digits denoted in parenthesis.
   * 
   * @see #toRepeatingDigitString(int, boolean)
   */
  public String toRepeatingDigitString(int radix) {
    return toRepeatingDigitString(radix, false);
  }
  
  /**
   * Converts the fraction to a radixed string with repeating digits, in the given radix. The
   * repeating digits are indicated by parenthesis: 1/9 becomes 0.(1)<br>
   * <br>
   * All rational fractions can be represented as a radixed string with repeating digits, but
   * some fractions can also be represented as a radixed string that terminates. In these cases,
   * the {@code forceRepeating} parameter can be used to force this function to return the
   * repeating fraction. For example, the fraction {@code 1/10} could be represented as terminating
   * string {@code "0.1"} or as repeating string {@code "0.0(9)"}.<br>
   * <br>
   * There is one special case for the value of 0. If {@code forceRepeating==true}, the return
   * value will be {@code "0.(0)"}. For all other fractions, the repeating digits will never
   * be all zeros.<br>
   * <br>
   * The repeating digits will always follow the radix point. For example, {@code 500/11} is
   * represented as {@code "45.(45)"}.<br>
   * <br>
   * <strong>Warning</strong>: This method is quite slow, as it essentially implements long division.<br>
   * <br>
   * The digit-to-character mapping provided by {@link Character#forDigit} is used.<br>
   * <br>
   * Examples:<br>
   * {@code LongFraction.valueOf(1,9).toRepeatingDigitString(10, false): 0.(1)}<br>
   * {@code LongFraction.valueOf(1).toRepeatingDigitString(10, false): 1.0}<br>
   * {@code LongFraction.valueOf(1).toRepeatingDigitString(10, true): 0.(9)}<br>
   * {@code LongFraction.valueOf(1,100).toRepeatingDigitString(10, false): 0.01}<br>
   * {@code LongFraction.valueOf(1,100).toRepeatingDigitString(10, true): 0.00(9)}<br>
   * {@code LongFraction.valueOf(45,22).toRepeatingDigitString(10, false): 2.0(45)}<br>
   * {@code LongFraction.valueOf(500,11).toRepeatingDigitString(10, false): 45.(45)}<br>
   * 
   * @param radix radix of the String representation. If the radix is outside the range from
   *              {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will default to 10
   *              (as is the case for Integer.toString)
   * @param forceRepeating whether or not to force this function to always use a repeating fraction,
   *                       even if the radixed string terminates
   * @return radixed string representation of this fraction with repeating digits denoted in parenthesis.
   */
  public String toRepeatingDigitString(int radix, boolean forceRepeating) {
    if(radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
      radix = 10;
    
    //special case for 0
    if(isZero(this))
      return (forceRepeating ? "0.(0)" : "0.0");
    
    long absNum = absAndCheck(numerator);
    String sign = (numerator < 0 ? "-" : "");
    char maxDigit = Character.forDigit(radix-1, radix);
    
    //whole numbers are also easy
    if(denominator == 1L)
    {
      if(forceRepeating)
        return sign + Long.toString(absNum - 1L, radix) + ".(" + maxDigit + ")";
      else
        return Long.toString(numerator, radix) + ".0";
    }
    
    //not a whole number or zero... we're going to have to do long division
    //first start by dividing to a remainder
    String iPart = "0";
    long dividend = absNum;
    if(dividend > denominator)
    {
      iPart = Long.toString(absNum / denominator, radix);
      dividend = absNum % denominator;
    }
    
    StringBuilder quotient = new StringBuilder(); //stores the digits we get in long division algorithm
    
    //Next loop does the actual long division. Take dividend, add a zero on the end, divide by denominator,
    //append the quotient digit, then update dividend to the remainder. Keep track of dividends that we
    //have seen before--when we get a dividend that we have seen before, then the digits are repeating.
    //The value in the hash map is the index where we saw that dividend--we'll need it to split the
    //quotient string between static and repeating digits.
    Map<Long, Integer> prevDividends = new HashMap<Long, Integer>();
    while(dividend != 0L && !prevDividends.containsKey(dividend))
    {
      prevDividends.put(dividend, quotient.length());
      dividend = mulAndCheck(dividend, radix); //same as appending a "0" in this base
      
      quotient.append(Character.forDigit((int)(dividend / denominator), radix));
      dividend = dividend % denominator;
    }
    
    StringBuilder result = new StringBuilder().append(sign).append(iPart).append('.');
    
    //if dividend is 0, the digits terminated
    if(dividend == 0L)
    {
      //if we are not forcing a repeating string, this is simple
      if(!forceRepeating)
        return result.append(quotient).toString();
      
      //to force terminating, convert the qoutient into a big integer, and subtract one from it, then use
      //the largest value in this radix as the repeating digit. i.e. 0.11 becomes 0.10(9)
      String adjustedQuotient = Long.toString(Long.parseLong(quotient.toString(), radix) - 1L, radix);
      
      //we may need to pad with leading zeros - the adjusted quotient won't have them
      int padLen = quotient.length() - adjustedQuotient.length();
      for(int i = 0; i < padLen; i++)
        result.append('0');
      
      return result.append(adjustedQuotient).append('(').append(maxDigit).append(')').toString();
    }
    
    //insert parens around the repeating part
    int numStaticDigits = prevDividends.get(dividend);
    quotient.insert(numStaticDigits, '(').append(')');
    
    return result.append(quotient).toString();
  }
  
  /**
   * Returns if this object is equal to another object. In order to maintain symmetry,
   * this will *only* return true if the other object is a LongFraction. For looser
   * comparison to other Number objects, use the equalsNumber(Number) method.
   * 
   * @param o Object to compare to this
   * @return true if {@code o instanceof LongFraction} and is equal to this.
   * 
   * @see #equalsNumber(Number)
   */
  @Override
  public boolean equals(Object o)
  {
    if(this == o)
      return true;
    
    if(!(o instanceof LongFraction))
      return false;
    
    LongFraction f = (LongFraction)o;
    return numerator == f.numerator && denominator == f.denominator;
  }
  
  /**
   * Returns if this object is equal to another Number object. Equivalent
   * to: {@code this.equals(LongFraction.valueOf(n))}
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
    return (31 + Long.hashCode(numerator))*31 + Long.hashCode(denominator);
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
    LongFraction f = valueOf(n);
    
    //easy case: this and f have different signs
    if(signum() != f.signum())
      return signum() - f.signum();
    
    //next easy case: this and f have the same denominator
    if(denominator == f.denominator)
      return Long.compare(numerator, f.numerator);
    
    //to reduce possibility of overflow, first try comparing double values. This should work most
    //of the time.
    double q1 = this.doubleValue();
    double q2 = f.doubleValue();
    
    if(q1 != q2 && Double.isFinite(q1) && Double.isFinite(q2))
      return Double.compare(q1, q2);
    
    //we know this and f are not equal, but their double values were equal. That could possibly
    //happen if the two fractions were close enough that they had the same double value--double
    //only stores 53 bits of integer part
    //TODO: try to find an example of this...
    return this.subtract(f).signum();
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
  public LongFraction fareyNext(int maxDenominator)
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
  public LongFraction fareyPrev(int maxDenominator)
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
  public LongFraction fareyClosest(int maxDenominator)
  {
    return fareyImpl(maxDenominator, FareyMode.CLOSEST);
  }
  
  /**
   * Common private function for handling the Farey Sequence methods.
   * 
   * @throws IllegalArgumentException if maxDenominator is non-positive.
   */
  private LongFraction fareyImpl(int maxDenominator, FareyMode fareyMode)
  {
    if(maxDenominator <= 0)
      throw new IllegalArgumentException("maxDenominator must be positive");
    
    //shortcut - if we are finding closest, but we are actually already in the sequence, just return this
    if(fareyMode == FareyMode.CLOSEST && denominator <= maxDenominator)
      return this;
    
    //shortcut - if this is a whole number, and we want next/prev, we just add or subtract 1/maxDenominator
    if(denominator == 1L)
    {
      // a/1 + 1/b = ab/b + 1/b = (ab+1)/b
      if(fareyMode == FareyMode.NEXT)
        return new LongFraction(addAndCheck(mulAndCheck(numerator, maxDenominator), 1L), maxDenominator, Reduced.YES);
      else if(fareyMode == FareyMode.PREV)
        return new LongFraction(addAndCheck(mulAndCheck(numerator, maxDenominator), -1L), maxDenominator, Reduced.YES);
    }
    
    //For negatives, we call negate this then call the sequence on the opposite mode, then negate the result
    if(numerator < 0L)
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
    if(numerator > denominator)
    {
      LongFraction fPartSeq = new LongFraction(numerator % denominator, denominator, Reduced.YES)
              .fareyImpl(maxDenominator, fareyMode);
      
      // n + a/b = nb/b + a/b = (nb + a)/b
      //return new LongFraction(addAndCheck(mulAndCheck(numerator/denominator, 
      //fPartSeq.denominator), fPartSeq.numerator), fPartSeq.denominator, Reduced.YES);
      return fPartSeq.add(numerator/denominator);
    }
    
    //Now... do the actual algorithm. We have lower bound a/b (initally 0/1), and upper bound c/d (initially 1/0).
    //We repeatedly take the mediant of a/b and c/d -- that is, (a+c)/(b+d). This is guaranteed to be a fraction
    //between a/b and c/d. Then we see which side of the mediant this is on, and set either upper bound or lower
    //bound to the mediant, and repeat until denominator is greater than maxDenominator
    long a=0, b=1, c=1, d=1;
    while(b+d <= maxDenominator)
    {
      long med_n = a+c, med_d = b+d;
      int cmp = Long.compare(mulAndCheck(med_n, this.denominator), 
              mulAndCheck(med_d, this.numerator));
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
      return new LongFraction(c, d, Reduced.YES);
    if(fareyMode == FareyMode.PREV)
      return new LongFraction(a, b, Reduced.YES);
    
    //else: we need to determine whether lowerbound or upper bound is closer to this
    LongFraction lower = new LongFraction(c, d, Reduced.YES);
    LongFraction upper = new LongFraction(a, b, Reduced.YES);
    
    if(this.subtract(lower).compareTo(upper.subtract(this)) > 0)
      return lower;
    else
      return upper;
  }
  
  /**
   * Returns the smaller of this and n. If they have equal value, this is returned.
   * Worth noting: if n is smaller, the returned Number is n, <i>not</i> a LongFraction
   * representing n.
   * 
   * @param n number to compare to this
   * @return smaller of this and n
   * @throws IllegalArgumentException if n is null
   */
  @Override
  public Number min(Number n)
  {
    return (this.compareTo(n) <= 0 ? this : n);
  }
  
  /**
   * Returns the smaller of a and b. If they have equal value, a is returned.
   * Worth noting: the returned Number is always one of the two arguments, not
   * necessarily a LongFraction.
   * 
   * @param a one number to compare
   * @param b another number to compare
   * @return smaller of a and b
   * @throws IllegalArgumentException if a or b is null
   */
  public static Number min(Number a, Number b)
  {
    return (valueOf(a).compareTo(b) <= 0 ? a : b);
  }
  
  
  /**
   * Returns the larger of this and n. If they have equal value, this is returned.
   * Worth noting: if n is larger, the returned Number is n, <i>not</i> a LongFraction
   * representing n.
   * 
   * @param n number to compare to this
   * @return larger of this and n
   * @throws IllegalArgumentException if n is null
   */
  @Override
  public Number max(Number n)
  {
    return (this.compareTo(n) >= 0 ? this : n);
  }
  
  /**
   * Returns the larger of a and b. If they have equal value, a is returned.
   * Worth noting: the returned Number is always one of the two arguments, not
   * necessarily a LongFraction.
   * 
   * @param a one number to compare
   * @param b another number to compare
   * @return larger of a and b
   * @throws IllegalArgumentException if a or b is null
   */
  public static Number max(Number a, Number b)
  {
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
  @Override
  public LongFraction mediant(Number n)
  {
    LongFraction f = valueOf(n);
    
    //if the two fractions are equal, we can avoid the math
    if(this.equals(f))
      return this;
    
    return new LongFraction(
            addAndCheck(numerator, f.numerator), 
            addAndCheck(this.denominator, f.denominator), Reduced.NO
    );
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
  public static LongFraction mediant(Number a, Number b)
  {
    return valueOf(a).mediant(b);
  }
  
  /**
   * Returns a BigDecimal representation of this fraction.<br>
   * <br>
   * If possible, the returned value will be exactly equal to the fraction. If not,
   * this is equivalent to {@code toBigDecimal(18)}, approximately the same precision
   * as a double-precision number.
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
    
    long tmpDen = denominator;
    int twos = 0;
    int fives = 0;
    //these loops will always terminate because denominator cannot be zero
    while(tmpDen % 2L == 0L) { tmpDen /= 2L; twos++; }
    while(tmpDen % 5L == 0L) { tmpDen /= 5L; fives++; }
    
    if(tmpDen == 1L)
    {
      //This fraction will terminate in base 10, so it can be represented exactly as
      //a BigDecimal. We would now like to make the fraction of the form
      //unscaled / 10^scale. We know that 2^x * 5^x = 10^x, and our denominator is
      //in the form 2^twos * 5^fives. So use max(twos, fives) as the scale, and
      //multiply the numerator and deminator by the appropriate number of 2's or 5's
      //such that the denominator is of the form 2^scale * 5^scale. (Of course, we
      //only have to actually multiply the numerator, since all we need for the
      //BigDecimal constructor is the scale.)
      BigInteger unscaled = BigInteger.valueOf(numerator);
      int scale = Math.max(twos, fives);
      
      if(twos < fives)
        unscaled = unscaled.shiftLeft(fives - twos); //x * 2^n === x << n
      else if (fives < twos)
        unscaled = unscaled.multiply(BIGINT_FIVE.pow(twos - fives));
      
      return new BigDecimal(unscaled, scale);
    }
    
    //else: this number will repeat infinitely in base-10. I used to try to figure out an
    //appropriate precision based the bit length of the numerator and denominator, but that
    //was just an approximation and not very useful in most circumstances. Instead, it now
    //uses 18 digits of precision (comparable to a double-precision number).
    return toBigDecimal(18);
  }
  
  /**
   * Returns a BigDecimal representation of this fraction, with a given precision.
   * @param precision  the number of significant figures to be used in the result.
   * @return BigDecimal representation of this fraction, to the given precision.
   */
  public BigDecimal toBigDecimal(int precision)
  {
    return new BigDecimal(numerator).divide(new BigDecimal(denominator), 
            new MathContext(precision, RoundingMode.HALF_EVEN));
  }
  
  //--------------------------------------------------------------------------
  //  IMPLEMENTATION OF NUMBER INTERFACE
  //--------------------------------------------------------------------------
  /**
   * Returns a long representation of this fraction. This value is
   * obtained by integer division of numerator by denominator.
   * 
   * @return long representation of this fraction
   */
  @Override
  public long longValue()
  {
    return this.round(RoundingMode.DOWN);
  }
  
  /**
   * Returns an exact long representation of this fraction.
   * 
   * 
   * @return exact long representation of this fraction
   * @throws ArithmeticException if this has a nonzero fractional
   *                             part, or will not fit in a long.
   */
  @Override
  public long longValueExact()
  {
    if(denominator != 1L)
      throw noExactValueException("long");
    
    return numerator;
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
  @Override
  public int intValueExact()
  {
    if(denominator != 1L || numerator < Integer.MIN_VALUE || numerator > Integer.MAX_VALUE)
      throw noExactValueException("int");
    
    return (int)numerator;
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
  @Override
  public short shortValueExact()
  {
    if(denominator != 1L || numerator < Short.MIN_VALUE || numerator > Short.MAX_VALUE)
      throw noExactValueException("short");
    
    return (short)numerator;
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
  @Override
  public byte byteValueExact()
  {
    if(denominator != 1L || numerator < Byte.MIN_VALUE || numerator > Byte.MAX_VALUE)
      throw noExactValueException("byte");
    
    return (byte)numerator;
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
    
    //note: must use precision+2 so that  new LongFraction(d).doubleValue() == d,
    //      for all possible double values.
    return toBigDecimal(MathContext.DECIMAL64.getPrecision() + 2).doubleValue();
  }
  
  /**
   * Returns an exact double representation of this fraction.<br>
   * <br>
   * <b>Warning</b>: Current algorithm is simply to convert to double, then convert back to
   * BigFraction, then make sure the copy of copy is identical to the original. This is not
   * optimized, and fails on some edge cases. This will be addressed in future updates.
   * 
   * @return exact double representation of this fraction
   * @throws ArithmeticException if this cannot be represented exactly as a double.
   */
  @Override
  public double doubleValueExact()
  {
    //TODO: UNOPTIMIZED! Algorithm is simply to convert to double, then convert back to LongFraction,
    //then make sure the copy of copy is identical to the original.
    //One test: if denominator.bitLength() != 1, then this cannot be represented exactly
    
    double doubleVal = this.doubleValue();
    if(Double.isFinite(doubleVal)) {
      LongFraction copy = valueOfHelper(doubleVal);
      if(this.equals(copy))
        return doubleVal;
    }
    
    throw noExactValueException("double");
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
    
    //note: must use precision+2 so that  new LongFraction(f).floatValue() == f,
    //      for all possible float values.
    return toBigDecimal(MathContext.DECIMAL32.getPrecision() + 2).floatValue(); 
  }
  
  /**
   * Returns an exact float representation of this fraction.<br>
   * <br>
   * <b>Warning</b>: Current algorithm is simply to convert to float, then convert back to
   * BigFraction, then make sure the copy of copy is identical to the original. This is not
   * optimized, and fails on some edge cases. This will be addressed in future updates.
   * 
   * @return exact float representation of this fraction
   * @throws ArithmeticException if this cannot be represented exactly as a float.
   */
  @Override
  public float floatValueExact()
  {
    //TODO: UNOPTIMIZED! Algorithm is simply to convert to float, then convert back to LongFraction,
    //then make sure the copy of copy is identical to the original.
    
    float floatVal = this.floatValue();
    if(Float.isFinite(floatVal)) {
      LongFraction copy = valueOfHelper(floatVal);
      if(this.equals(copy))
        return floatVal;
    }
    
    throw noExactValueException("float");
  }
  
  
  //--------------------------------------------------------------------------
  //  PRIVATE FUNCTIONS
  //--------------------------------------------------------------------------
  
  
  /**
   * Constructs a LongFraction from a floating-point number.
   */
  private static LongFraction valueOfHelper(double d)
  {
    //TODO: Refactor so as to not use BigInteger
    
    if(Double.isInfinite(d))
      throw new IllegalArgumentException("double val is infinite");
    if(Double.isNaN(d))
      throw new IllegalArgumentException("double val is NaN");
    
    //special case - math below won't work right for 0.0 or -0.0
    if(d == 0.0)
      return LongFraction.ZERO;
    
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
    
    BigInteger tmpNumerator = 
        BigInteger.valueOf((isSubnormal ? 0L : 0x10000000000000L) + mantissa);
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
    return new LongFraction(
            tmpNumerator.longValueExact(), 
            tmpDenominator.longValueExact(), Reduced.YES
    );
  }
  
  /**
   * Constructs a LongFraction from two floating-point numbers.<br>
   * <br>
   * Warning: round-off error in IEEE floating point numbers can result
   * in answers that are unexpected. See {@link #valueOf(Number)} for more
   * information.<br>
   * <br>
   * NOTE: In many cases, LongFraction(Double.toString(numerator) + "/" + Double.toString(denominator))
   * may give a result closer to what the user expects.
   * 
   * @throws ArithmeticException if denominator == 0.
   */
  private static LongFraction valueOfHelper(double numerator, double denominator)
  {
    if(denominator == 0.0)
      throw arithmeticException("Divide by zero: fraction denominator is zero.");
    
    if(numerator == 0.0)
      return LongFraction.ZERO;
    
    if(denominator < 0.0)
    {
      numerator = -numerator;
      denominator = -denominator;
    }
    
    LongFraction numFract = valueOfHelper(numerator);
    LongFraction denFract = valueOfHelper(denominator);
    
    //We can avoid the check for gcd here because we know that a fraction created from
    //a double will be of the form n/2^x, where x >= 0. So we have:
    //     (n1/2^x1)/(n2/2^x2)
    //   = (n1/n2) * (2^x2 / 2^x1).
    //
    //Now, we only have to check for gcd(n1,n2), and we know gcd(2^x2, 2^x1) = 2^(abs(x2 - x1)), or max(d2, d1)/min(d2, d1).
    //This gives us the following:
    // For x1 < x2 :  (n1 * 2^(x2 - x1)) / n2  =  (n1 << (x2 - x1)) / n2
    // For x1 = x2 :  n1 / n2
    // For x1 > x2 :  n1 / (n2 * 2^(x1 - x2))  =  n1 / (n2 << (x1 - x2))
    //
    //Further, we know that if x1 > 0, n1 is not divisible by 2 (likewise for x2 > 0 and n2).
    //This guarantees that the GCD for any of the above three cases is equal to gcd(n1,n2).c
    //Since it is easier to compute GCD of smaller numbers, this can speed us up a bit.
    
    long gcd = gcd(numFract.numerator, denFract.numerator);
    long tmpNumerator = numFract.numerator / gcd;
    long tmpDenominator = denFract.numerator / gcd;
    
    if(numFract.denominator < denFract.denominator)
      tmpNumerator = mulAndCheck(tmpNumerator, denFract.denominator/numFract.denominator);
    else if (numFract.denominator > denFract.denominator)
      tmpDenominator = mulAndCheck(tmpDenominator, numFract.denominator/denFract.denominator);
    //else: they are equal: do nothing
    
    return new LongFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Constructs a new LongFraction from the given BigDecimal object.
   */
  private static LongFraction valueOfHelper(BigDecimal d)
  {
    //BigDecimal format: unscaled / 10^scale.
    long tmpNumerator = d.unscaledValue().longValueExact();
    long tmpDenominator = 1L;
    
    //Special case for d == 0 (math below won't work right)
    //Note:  Cannot use d.equals(BigDecimal.ZERO), because BigDecimal.equals()
    //       does not consider numbers equal if they have different scales. So,
    //       0.00 is not equal to BigDecimal.ZERO.
    if(tmpNumerator == 0L)
      return LongFraction.ZERO;
    
    if(d.scale() < 0)
    {
      tmpNumerator = mulAndCheck(tmpNumerator, powAndCheck(10L, -d.scale()));
    }
    else if (d.scale() > 0)
    {
      //Now we have the form:  unscaled / 10^scale = unscaled / (2^scale * 5^scale)
      //We know then that gcd(unscaled, 2^scale * 5^scale) = 2^commonTwos * 5^commonFives
      
      int commonTwos = 0;
      int commonFives = 0;
      
      while(commonTwos < d.scale()  && tmpNumerator % 2L == 0L) { tmpNumerator /= 2L; commonTwos++; }
      while(commonFives < d.scale() && tmpNumerator % 5L == 0L) { tmpNumerator /= 5L; commonFives++; }
      
      if(commonTwos < d.scale())
        tmpDenominator = mulAndCheck(tmpDenominator, powAndCheck(2L, d.scale() - commonTwos));
      if(commonFives < d.scale())
        tmpDenominator = mulAndCheck(tmpDenominator, powAndCheck(5L, d.scale() - commonFives));
    }
    //else: d.scale() == 0: do nothing
    
    //Guaranteed there is no gcd, so fraction is in lowest terms
    return new LongFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Constructs a new LongFraction from two BigDecimals.
   * 
   * @throws ArithmeticException if denominator == 0.
   */
  private static LongFraction valueOfHelper(BigDecimal numerator, BigDecimal denominator)
  {
    //Note:  Cannot use .equals(BigDecimal.ZERO), because "0.00" != "0.0".
    if(denominator.unscaledValue().equals(BigInteger.ZERO))
      throw arithmeticException("Divide by zero: fraction denominator is zero.");
    
    //Format of BigDecimal: unscaled / 10^scale
    long tmpNumerator = numerator.unscaledValue().longValueExact();
    long tmpDenominator = denominator.unscaledValue().longValueExact();
    
    if(tmpNumerator == 0L)
      return LongFraction.ZERO;
    
    // (u1/10^s1) / (u2/10^s2) = u1 / (u2 * 10^(s1-s2)) = (u1 * 10^(s2-s1)) / u2
    if(numerator.scale() > denominator.scale())
      tmpDenominator = mulAndCheck(tmpDenominator, powAndCheck(10L, numerator.scale() - denominator.scale()));
    else if(numerator.scale() < denominator.scale())
      tmpNumerator = mulAndCheck(tmpNumerator, powAndCheck(10L, denominator.scale() - numerator.scale()));
    //else: scales are equal, do nothing.
    
    long gcd = gcd(tmpNumerator, tmpDenominator);
    tmpNumerator /= gcd;
    tmpDenominator /= gcd;
    
    if(tmpDenominator < 0)
    {
      tmpNumerator = negateAndCheck(tmpNumerator);
      tmpDenominator = negateAndCheck(tmpDenominator);
    }
    
    return new LongFraction(tmpNumerator, tmpDenominator, Reduced.YES);
  }
  
  /**
   * Converts a radixed string to a LongFraction.
   */
  private static LongFraction valueOfHelper(String s, int radix)
  {
    int parenPos = s.indexOf('(');
    if(parenPos >= 0)
      return valueOfHelper_repeating(s, radix);
    
    //in base 10, if we don't have repeating fractions, piggy-back off of BigDecimal
    if(radix == 10)
      return valueOfHelper(new BigDecimal(s));
    
    int radixPos = s.indexOf('.');
    
    //if no radix point (decimal), this is just a BigInteger
    if(radixPos < 0)
      return new LongFraction(Long.parseLong(s, radix), 1L, Reduced.YES);
    
    //otherwise, we have a radix to deal with. Just take the radix point out of the string, and parse it as
    //a BigInteger. Then the denominator is radix^(numFractionDigits).
    //Example in base 10:  123.45 = 12345 / 10^2
    //Same holds true for other bases.
    
    String iPart = s.substring(0, radixPos);
    String fPart = s.substring(radixPos+1, s.length());
    
    long num = Long.parseLong(iPart + fPart, radix);
    long den = powAndCheck(radix, fPart.length());
    
    return new LongFraction(num, den, Reduced.NO);
  }
  
  /**
   * Converts a radixed string with repeating digits to a LongFraction.
   */
  private static LongFraction valueOfHelper_repeating(String s, int radix)
  {
    //largest digit in this radix
    char maxDigit = Character.forDigit(radix-1, radix);
    
    //digits group: regex pattern to match any digits in the given radix
    String digitsGroup = null;
    if(radix < 11)
      digitsGroup = "[0-" + (radix-1) + "]";
    else if(radix == 11)
      digitsGroup = "[0-9" + maxDigit + "]";
    else
      digitsGroup = "[0-9" + Character.forDigit(10, radix) + "-" + maxDigit + "]";
    
    //optional sign, optional iPart digits before radix point, optional fPart 
    //digits after radix point, and at least one repeating digit in parens
    Pattern pattern = Pattern.compile("^([\\+\\-]?)(" + digitsGroup + "*)\\.(" 
        + digitsGroup + "*)\\((" + digitsGroup + "+)\\)$", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(s);
    
    if(!matcher.find())
      throw new NumberFormatException();
    
    String sign = matcher.group(1);
    String ipart = matcher.group(2);
    String fpart = matcher.group(3);
    String repeating = matcher.group(4);
    
    // A.B(C) = A.B + 0.0(C). First create a LongFraction for terminating part A.B (iPart.fPart)
    LongFraction terminating = LongFraction.ZERO;
    if(ipart.length() + fpart.length() > 0)
      terminating = valueOfHelper(ipart + '.' + fpart, radix);
    
    //No create a fraction just for the repeating part. We already have the numerator (repeating), we just need
    //the denominator. The denominator is always a series of the largest digit in the base, with the same length
    //as the numerator, followed by a series of 0s the same length as the fPart. For examples, in base 10:
    // 0.(3) = 3/9
    // 0.(36) = 36/99
    // 0.000(4) = 4/9000
    // 0.000(45) = 45/99000
    //same holds true in other base. For example, in hex:
    // 0.00(1f) = 1f/ff00
    StringBuilder den = new StringBuilder(fpart.length() + repeating.length());
    for(int i = 0; i < repeating.length(); i++)
      den.append(maxDigit);
    for(int i = 0; i < fpart.length(); i++)
      den.append('0');
    
    //TODO: Instead of using a string builder, we could also compute denominator as:
    //    mulAndCheck(subAndCheck(powAndCheck(radix, repeating.length()), 1L), 
    //    powAndCheck(radix, fPart.length()))
    //Need to do performance analysis to see which method is more efficient
    
    //add the terminating part and the repeating part together to get the true fraction
    LongFraction ret = terminating.add(
        new LongFraction(
            Long.parseLong(repeating, radix), 
            Long.parseLong(den.toString(), radix), Reduced.NO
        )
    );
    
    //don't forget the sign!
    if(sign.equals("-"))
      ret = ret.negate();
    
    return ret;
  }
  
  /**
   * Private constructor, used when you can be certain that the fraction is already in
   * lowest terms. No check is done to reduce numerator/denominator. A check is still
   * done to maintain a positive denominator.
   * 
   * @param numerator numerator of fraction
   * @param denominator denominator of fraction
   * @param reduced  Indicates whether or not the fraction is already known to be
   *                 reduced to lowest terms.
   */
  private LongFraction(long numerator, long denominator, Reduced reduced)
  {
    if(denominator == 0L)
      throw arithmeticException("Divide by zero: fraction denominator is zero.");
    
    //if numerator is zero, we don't care about the denominator. force it to 1.
    if(reduced == Reduced.NO && numerator == 0)
    {
      denominator = 1L;
      reduced = Reduced.YES;
    }
    
    //only numerator should be negative.
    if(denominator < 0)
    {
      numerator = negateAndCheck(numerator);
      denominator = negateAndCheck(denominator);
    }
    
    if(reduced == Reduced.NO && denominator == 1L)
      reduced = Reduced.YES;

    //common special case - denominator is one. No need to do GCD check.
    if(reduced == Reduced.NO)
    {
      //create a reduced fraction
      long gcd = gcd(numerator, denominator);
      if(gcd != 1L)
      {
        numerator /= gcd;
        denominator /= gcd;
      }
    }
    
    this.numerator = numerator;
    this.denominator = denominator;
  }
  
  /**
   * Converts a Number to a BigInteger. Assumes that a check on the type of n
   * has already been performed.
   */
  private static long toLong(Number n)
  {
    if(n instanceof BigInteger)
      return ((BigInteger)n).longValueExact();
    
    if(isJavaInteger(n))
      return n.longValue();
    
    if(n instanceof BigFraction)
      return ((BigFraction)n).getNumerator().longValueExact();
    
    if(n instanceof LongFraction)
      return ((LongFraction)n).numerator;
    
    if(n instanceof BigDecimal)
    {
      final BigDecimal bd = (BigDecimal)n;
      return bd.unscaledValue().multiply(BigInteger.TEN.pow(-bd.scale())).longValueExact();
    }
    
    //unknown implementation... fall back to double value
    final double d = n.doubleValue();
    
    if(d == 0.0)
      return 0L;
    
    //This is similar to valueOfHelper(double), except we know that the exponent is greater than 52. See the comments
    //in valueOfHelper(double) for much more detailed information
    final int sign = DoubleUtil.getSign(d);
    final int exponent = DoubleUtil.getExponent(d);
    final long mantissa = DoubleUtil.getMantissa(d);
    
    BigInteger ret = BigInteger.valueOf(0x10000000000000L + mantissa).shiftLeft(exponent - 52);
    return (sign == 0 ? ret : ret.negate()).longValueExact();
  }
  
  /**
   * Returns true if the given BigFraction represents zero. Overloaded as this is a common case.
   */
  private final static boolean isZero(Number n)
  {
    //micro-optimization- most common type first...
    if(n instanceof LongFraction)
      return ((LongFraction)n).getNumerator() == 0L;
    
    if(n instanceof BigInteger)
      return ((BigInteger)n).equals(BigInteger.ZERO);
    
    if(n == null)
      return false;
    
    if(isJavaInteger(n))
      return n.longValue() == 0L;
    
    if(n instanceof BigFraction)
      return ((BigFraction)n).getNumerator().equals(BigInteger.ZERO);
    
    if(n instanceof BigDecimal)
      return ((BigDecimal)n).unscaledValue().equals(BigInteger.ZERO);
    
    //double or unknown type - use doubleValue()
    return n.doubleValue() == 0.0;
  }
  
  /**
   * Returns true if the given Number represents zero. For unknown numbers, utilizes Number.doubleValue().
   */
  private final static boolean isZero(LongFraction f)
  {
    return f.numerator == 0L;
  }
  
  /**
   * Returns true if the given Number represents one. For unknown numbers, utilizes Number.doubleValue().
   */
  private final static boolean isOne(Number n)
  {
    //micro-optimization- most common type first...
    if(n instanceof LongFraction)
      return ((LongFraction)n).equals(LongFraction.ONE);
    
    if(n == null)
      return false;
    
    if(n instanceof BigInteger)
      return ((BigInteger)n).equals(BigInteger.ONE);
    
    if(isJavaInteger(n))
      return n.longValue() == 1L;
    
    if(n instanceof BigFraction)
      return ((BigFraction)n).equals(BigFraction.ONE);
    
    if(n instanceof BigDecimal)
      return ((BigDecimal)n).compareTo(BigDecimal.ONE) == 0;
    
    //double or unknown type - use doubleValue()
    return n.doubleValue() == 1.0;
  }
  
  /**
   * Returns -1, 0, or 1, matching the sign of n.
   */
  private static int signum(long n)
  {
    if(n > 0L)
      return 1;
    if(n == 0L)
      return 0;
    return -1;
  }
  
  /**
   * Computes gcd using Euclid's algorithm: https://en.wikipedia.org/wiki/Euclidean_algorithm
   */
  private static long gcd(long a, long b) {
    long tmp = 0L;
    while(b != 0L)
    {
       tmp = b; 
       b = a % b; 
       a = tmp;
    }
    
    return absAndCheck(a);
  }
  
  private static long lcm(long a, long b) {
    //lcm(a, b) = abs(a*b)/gcd(a,b)
    //to reduce chance of overflow, simply this as:
    //abs((max(a,b)/gcd(a,b))*min(a,b)
    if(a == 0L && b == 0L)
      return 0L;
    
    long gcd = gcd(a,b);
    long maxAbs = maxAbs(a, b);
    if(maxAbs == a)
      return absAndCheck(mulAndCheck(a/gcd, b));
    return absAndCheck(mulAndCheck(b/gcd, a));
  }
  
  /**
   * Computes absolute value with check on overflow. Throws ArithmeticException if
   * value cannot be negated (only for Long.MIN_VALUE).
   */
  private static long absAndCheck(long n) {
    if(n == Long.MIN_VALUE)
      throw arithmeticException("Integer Overflow: abs(", n, "L)");
    return (n < 0 ? -n : n);
  }
  
  /**
   * Returns the paramter with the largest absolute value. Does not actually change the sign of the input.
   */
  private static long maxAbs(long a, long b) {
    if(a == Long.MIN_VALUE || b == Long.MIN_VALUE)
      return Long.MIN_VALUE;
    long absA = (a < 0 ? -a : a);
    long absB = (b < 0 ? -b : b);
    return (absA >= absB ? a : b);
  }
  
  /**
   * Negate value with check on overflow. Throws ArithmeticException if
   * value cannot be negated (only for Long.MIN_VALUE).
   */
  private static long negateAndCheck(long n) {
    if(n == Long.MIN_VALUE)
      throw arithmeticException("Integer Overflow: -(" , n, "L)");
    return -n;
  }
  
  /**
   * Adds two values with check on overflow. Throws AritmeticException on overflow.
   * Modified from ArithmeticUtils in Apache Commons.
   */
  private static long addAndCheck(long a, long b)
  {
    // use symmetry to reduce boundary cases
    if (a > b)
      return addAndCheck(b, a);
    
    if(a < 0 && b < 0 && a < Long.MIN_VALUE - b)
      throw arithmeticException("Integer Overflow: ", a, "L + ", b, "L");
    if(a > 0 && b > 0 && a > Long.MAX_VALUE - b)
      throw arithmeticException("Integer Overflow: ", a, "L + ", b, "L");
    
    return a + b;
  }
  
  /**
   * Subtracts two values with check on overflow. Throws AritmeticException on overflow.
   * Modified from ArithmeticUtils in Apache Commons.
   */
  private static long subAndCheck(long a, long b)
  {
    if(b != Long.MIN_VALUE)
      return addAndCheck(a, -b);
    
    if (a < 0)
      return a - b;
    
    throw arithmeticException("Integer Overflow: ", a , "L - ", b, "L");
  }
  
  /**
   * Multiplies two values with check on overflow. Throws AritmeticException on overflow.
   * Modified from ArithmeticUtils in Apache Commons.
   */
  private static long mulAndCheck(long a, long b)
  {
    if(a == 0L || b == 0L)
      return 0L;
    
    // use symmetry to reduce boundary cases
    if (a > b)
      return mulAndCheck(b, a);
    
    boolean aPos = (a > 0L);
    boolean bPos = (b > 0L);
    
    if(aPos && a > Long.MAX_VALUE / b) //both positive (a < b, so aPos implies bPos)
      throw arithmeticException("Integer Overflow: ", a, "L * ", b, "L");
    else if (!aPos && bPos && a < Long.MIN_VALUE / b) //positive a, negative b
      throw arithmeticException("Integer Overflow: ", a, "L * ", b, "L");
    else if (!aPos && !bPos && a < Long.MAX_VALUE / b) //both negative
      throw arithmeticException("Integer Overflow: ", a, "L * ", b, "L");
    
    return a*b;
  }
  
  /**
   * Raises n to the given exponent. Assumes x is nonnegative.
   */
  private static long powAndCheck(long n, int x)
  {
    //TODO: research more efficient way of doing this.
    long ret = 1L;
    try
    {
      for(int i = 0; i < x; i++)
        ret = mulAndCheck(ret, n);
    }
    catch(ArithmeticException e)
    {
      throw arithmeticException("Integer Overflow: (", n, "L)^(", x, ")");
    }
    
    return ret;
  }
}
