/**
 * 
 */
package com.github.kiprobinson.util;

/**
 * Representation of methods of performing Euclidean division--dividing to an integer and obtaining
 * a remainder. The division methods are all the same when dividend and divisor are positive, but they
 * differ when one or both values are negative.<br>
 * <br>
 * Given the division problem a/b, all methods produce integer quotient q and remainder r, with {@code 0 <= r < b},
 * fulfilling the equations:<br>
 * <br>
 * {@code a/b = q + r/b}<br>
 * {@code a = b*q + r}<br>
 * {@code r = a - b*q}<br>
 * <br>
 * More information available at <a href="https://en.wikipedia.org/wiki/Modulo_operation">https://en.wikipedia.org/wiki/Modulo_operation</a>.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public enum DivisionMode {
  
  /**
   * In truncated division, the quotient is truncated (rounded toward zero). This
   * is the method used by built-in % operator.<br>
   * <br>
   * With this method, the remainder will have the same sign as the dividend (a).<br>
   * <br>
   * {@code   q = trunc(a/b)}   (round toward zero)<br>
   * {@code   r = a - b*q}
   */
  TRUNCATED,
  
  /**
   * In floored division, the quotient is floored (rounded toward negative infinity). <br>
   * <br>
   * With this method, the remainder will have the same sign as the divisor (b)<br>
   * <br>
   * {@code   q = floor(a/b)}   (round toward negative infinity)<br>
   * {@code   r = a - b*q}
   */
  FLOORED,
  
  /**
   * Euclidean division chooses a quotient which will ensure that the remainder is always positive. <br>
   * <br>
   * {@code   q = sign(b) * floor(a/abs(b))}<br>
   * Or:<br>
   * {@code   b > 0: q = floor(a/b)}<br>
   * {@code   b < 0: q = ciel(a/b)}<br>
   * <br>
   * {@code   r = a - b*q}
   */
  EUCLIDEAN
}
