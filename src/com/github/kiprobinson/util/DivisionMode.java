/**
 * 
 */
package com.github.kiprobinson.util;

/**
 * Representation of methods of performing Euclidean division--dividing to an integer and obtaining
 * a remainder. The division methods are all the same when dividend and divisor are positive, but they
 * differ when one or both values are negative.
 * 
 * Given the division problem a/b, all methods produce integer quotient q and remainder r, with {@code 0 <= r < b},
 * fulfilling the equations:
 * 
 *   {@code a/b = q + r/b  =>  a = b*q + r  =>  r = a - b*q}
 * 
 * More information available at https://en.wikipedia.org/wiki/Modulo_operation
 * 
 * @author Kip Robinson, https://github.com/kiprobinson
 */
public enum DivisionMode {
  
  /**
   * Truncated division is the method used by built-in % operator. The quotient
   * is truncated (rounded toward zero).
   * With this method, the remainder will have the same sign as the dividend (a).
   * 
   * {@code q = trunc(a/b)}   (round toward zero)
   * {@code r = a - b*q}
   */
  TRUNCATED,
  
  /**
   * In floored division, the quotient is floored (rounded toward negative infinity).
   * With this method, the remainder will have the same sign as the divisor (b)
   * 
   * {@code q = floor(a/b)}   (round toward negative infinity)
   * {@code r = a - b*q}
   */
  FLOORED,
  
  /**
   * Euclidean division results in a remainder that is always positive.
   * 
   * {@code q = sign(b) * floor(a/abs(b))}
   * Or:
   *   {@code b > 0: q = floor(a/b)}
   *   {@code b < 0: q = ciel(a/b)}
   * 
   * {@code r = a - b*q}
   */
  EUCLIDEAN
}
