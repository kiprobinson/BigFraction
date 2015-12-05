package com.github.kiprobinson.main;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.kiprobinson.util.BigFraction;

/**
 * Class with public static void main(). Allows easy demo..
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public final class BigFractionMain
{
  
  public static void main(String[] args)
  {
    
    // Creating BigFractions
    //=========================================
    
    //Constructors are protected. Create new BigFractions using `valueOf(Number numerator)`, `valueOf(Number numerator, Number denominator)`, or `valueOf(String)`:
    
    System.out.println(BigFraction.valueOf(11));      // 11/1
    System.out.println(BigFraction.valueOf(11, 17));  // 11/17
    System.out.println(BigFraction.valueOf("19/81")); // 19/81
    
    //Fractions are always reduced to lowest terms:
    
    System.out.println(BigFraction.valueOf(17, 34));  // 1/2
    System.out.println(BigFraction.valueOf(0, 999));  // 0/1
    
    //The sign is always carried by the numerator:
    
    System.out.println(BigFraction.valueOf(-9, 4));   // -9/4
    System.out.println(BigFraction.valueOf(9, -4));   // -9/4
    System.out.println(BigFraction.valueOf(-9, -4));  // 9/4  (negatives cancel out)
    
    //You can use floating point numbers to create the fraction:
    
    System.out.println(BigFraction.valueOf(0.625));  // 5/8
    System.out.println(BigFraction.valueOf(-8.5, 6.25)); //-34/25
    
    //But be careful, what you get is *exactly* equal to the value you provide:
    
    System.out.println(BigFraction.valueOf(1.1));  // 2476979795053773/2251799813685248
    System.out.println(BigFraction.valueOf(1.1f)); // 9227469/8388608
    
    //The version that takes a String may be more like what you expect:
    
    System.out.println(BigFraction.valueOf("1.1"));                // 11/10
    System.out.println(BigFraction.valueOf(Double.toString(1.1))); // 11/10
    System.out.println(BigFraction.valueOf(Float.toString(1.1f))); // 11/10
    
    //You can also use BigInteger and BigDecimal:
    
    System.out.println(BigFraction.valueOf(new BigInteger("9999999999999999999"), BigInteger.valueOf(1)));
    // ->  9999999999999999999/1   (note that this is larger than Long.MAX_VALUE)
    
    System.out.println(BigFraction.valueOf(new BigDecimal("1.23456789012345678901E-50")));
    // ->  123456789012345678901/10000000000000000000000000000000000000000000000000000000000000000000000
    
    //You can even mix different Number types for numerator and denominator:
    
    System.out.println(BigFraction.valueOf(1.5, BigInteger.valueOf(17))); // 3/34
    
    //A few exceptions:
    
    //System.out.println(BigFraction.valueOf(1,0)); // ArithmeticException - divide by zero
    //System.out.println(BigFraction.valueOf(0,0)); // ArithmeticException - divide by zero
    //System.out.println(BigFraction.valueOf(Double.POSITIVE_INFINITY)); //IllegalArgumentException
    //System.out.println(BigFraction.valueOf(Double.NaN)); //IllegalArgumentException
    
    //  Mathematical operations
    //=========================================
    
    BigFraction a = BigFraction.valueOf(1,2);
    BigFraction b = BigFraction.valueOf(3,4);
    BigFraction z = BigFraction.ZERO;
    
    System.out.println(a.add(a)); // 1/2 + 1/2 = 1/1
    System.out.println(a.add(b)); // 1/2 + 3/4 = 5/4
    System.out.println(b.add(z)); // 3/4 + 0/1 = 3/4
    System.out.println(z.add(a)); // 0/1 + 1/2 = 1/2
    
    System.out.println(a.subtract(a)); // 1/2 - 1/2 = 0/1
    System.out.println(a.subtract(b)); // 1/2 - 3/4 = -1/4
    System.out.println(b.subtract(z)); // 3/4 - 0/1 = 3/4
    System.out.println(z.subtract(a)); // 0/1 - 1/2 = -1/2
    
    System.out.println(a.multiply(a)); // (1/2) * (1/2) = 1/4
    System.out.println(a.multiply(b)); // (1/2) * (3/4) = 3/8
    System.out.println(b.multiply(z)); // (3/4) * (0/1) = 0/1
    System.out.println(z.multiply(a)); // (0/1) * (1/2) = 0/1
    
    System.out.println(a.divide(a)); // (1/2) / (1/2) = 1/1
    System.out.println(a.divide(b)); // (1/2) / (3/4) = 2/3
    //System.out.println(b.divide(z)); // => ArithmeticException - divide by zero
    System.out.println(z.divide(a)); // (0/1) / (1/2) = 0/1
    
    System.out.println(a.pow(3));  // (1/2)^3 = 1/8
    System.out.println(b.pow(4));  // (3/4)^4 = 81/256
    System.out.println(a.pow(0));  // (1/2)^0 = 1/1
    System.out.println(z.pow(0));  // (0/1)^0 = 1/1  => Mathematicians may not like it, but this is consistent with Math.pow()
    
    System.out.println(a.pow(-3)); // (1/2)^(-3) = 8/1
    System.out.println(b.pow(-4)); // (3/4)^(-4) = 256/81
    //System.out.println(z.pow(-1)); // => ArithmeticException (divide by zero)
    
    System.out.println(a.reciprocal()); // (1/2)^(-1) = 2/1
    System.out.println(b.reciprocal()); // (3/4)^(-1) = 4/3
    //System.out.println(z.reciprocal()); // => ArithmeticException (divide by zero)
    
    //Complement is `1 - n`. Useful in statistics a lot:
    
    System.out.println(a.complement()); // 1 - 1/2 = 1/2
    System.out.println(b.complement()); // 1 - 3/4 = 1/4
    System.out.println(z.complement()); // 1 - 0/1 = 1/1
  }
  
}
