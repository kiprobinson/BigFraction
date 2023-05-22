package com.github.kiprobinson.bigfraction.util;

/**
 * Additional utilities for working with double values. Useful to prevent hard-coding IEEE 754 constants in code.
 * Consider this a complement to the methods provided in {@link Double}.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Double-precision_floating-point_format">
 * Wikipedia overview of IEEE 754 double-precision floating point specifications</a>
 */
public final class DoubleUtil
{
  /** Mask to get sign bit in a raw double value. */
  private final static long SIGN_MASK = 0x8000000000000000L;
  /** Bit position of the sign bit in IEEE 754 double-precision binary representation. */
  private final static int SIGN_POS = 63;
  
  /** Mask to get exponent bits in a raw double value. */
  private final static long EXPONENT_BITS_MASK = 0x7ff0000000000000L;
  /** Bit position of the exponent bits in IEEE 754 double-precision binary representation. */
  private final static int EXPONENT_POS = 52;
  /** Exponent offset, per IEEE 754 spec. */
  private final static int EXPONENT_OFFSET = 0x3ff;
  
  /** Mask to get mantissa bits in a raw double value. */
  private final static long MANTISSA_MASK = 0xfffffffffffffL;
  
  /** The largest permitted mantissa value. */
  public final static long MAX_MANTISSA = MANTISSA_MASK;
  
  /** The largest permitted exponent value, when using raw exponent bits. */
  public final static int MAX_EXPONENT_BITS = (int)(EXPONENT_BITS_MASK >> EXPONENT_POS);
  
  /** The smallest permitted exponent value, when using an exponent with offset. */
  public final static int MIN_EXPONENT = -EXPONENT_OFFSET;
  /** The largest permitted exponent value, when using an exponent with offset. */
  public final static int MAX_EXPONENT = MAX_EXPONENT_BITS - EXPONENT_OFFSET;
  
  
  /** Hide default constructor to prevent instantiation. */
  private DoubleUtil() {}
  
  /**
   * Returns true if d is finite--not infinite and not NaN. (Equivalent to
   * Double.isFinite() available from Java 8.)
   * 
   * @param d a double value
   * @return whether this value is finite.
   * 
   * @deprecated use {@link Double#isFinite(double)} instead. This method was provided
   *             for use in Java7, where {@link Double#isFinite(double)} did not exist yet.
   */
  @Deprecated
  public static boolean isFinite(double d) {
    return Double.isFinite(d);
  }
  
  /**
   * Returns the sign bit (bit 63). 0=positive, 1=negative. 
   * This is returned even for NaN values.
   * 
   * @param d a double value
   * @return the sign bit
   */
  public static int getSign(double d)
  {
    return (int)((Double.doubleToRawLongBits(d) & SIGN_MASK) >>> SIGN_POS);
  }
  
  /**
   * Returns the exponent, after adding the exponent offset to the exponent bits. 
   * In other words, the value in bits 62-52, plus 0x3ff.<br>
   * <br>
   * Examples:<ul>
   *   <li>{@code getExponent(0.5) == -1} because {@code 1.0 == 1.0 * 2^-1}</li>
   *   <li>{@code getExponent(1.0) ==  0} because {@code 1.0 == 1.0 * 2^0}</li>
   *   <li>{@code getExponent(2.0) ==  1} because {@code 2.0 == 1.0 * 2^1}</li>
   *   <li>Subnormal values: Always returns -1023.</li>
   *   <li>Zero: Always returns -1023.</li>
   *   <li>Infinity: Always returns 1024.</li>
   *   <li>NaN: Always returns 1024.</li>
   *   </ul>
   * 
   * @param d any double value
   * @return adjusted exponent
   */
  public static int getExponent(double d)
  {
    return getExponentBits(d) - EXPONENT_OFFSET;
  }
  
  /**
   * Returns the raw exponent bits, without adjusting for the offset. In other words, the value
   * in bits 62-52.<br>
   * <br>
   * Examples:<ul>
   *   <li>{@code getExponentBits(0.5) == 0x3fe}</li>
   *   <li>{@code getExponentBits(1.0) == 0x3ff}</li>
   *   <li>{@code getExponentBits(2.0) == 0x400}</li>
   *   <li>Subnormal values: Always returns 0x000.</li>
   *   <li>Zero: Always returns 0x000.</li>
   *   <li>Infinity: Always returns 0x7ff.</li>
   *   <li>NaN: Always returns 0x7ff.</li>
   * </ul>
   * 
   * @param d a double value
   * @return raw exponent bits
   */
  public static int getExponentBits(double d)
  {
    return (int)
        ((Double.doubleToRawLongBits(d) & EXPONENT_BITS_MASK) >>> EXPONENT_POS);
  }
  
  /**
   * Returns the mantissa bits (bits 51-0). Returned even for NaN values.
   * @param d a double value
   * @return mantissa bits
   */
  public static long getMantissa(double d)
  {
    return Double.doubleToRawLongBits(d) & MANTISSA_MASK;
  }
  
  /**
   * Returns whether or not this double is a subnormal value.
   * @param d a double value
   * @return whether or not this double is a subnormal value
   */
  public static boolean isSubnormal(double d)
  {
    long bits = Double.doubleToRawLongBits(d);
    return ((bits & EXPONENT_BITS_MASK) == 0) && ((bits & MANTISSA_MASK) != 0);
  }
  
  
  /**
   * Returns an array containing the parts of the double. Avoids the overhead of four separate function calls.
   * 
   * @param d a double value
   * @return array with four elements:<ul>
   *     <li>return[0]: Equivalent to getSign(d)</li>
   *     <li>return[1]: Equivalent to getExponent(d)</li>
   *     <li>return[2]: Equivalent to getMantissa(d)</li>
   *     <li>return[3]: Equivalent to isSubnormal(d) - Uses zero for false, non-zero for true.</li>
   * </ul>
   */
  public static long[] getAllParts(double d)
  {
    return getAllParts(d, false);
  }
  
  /**
   * Returns an array containing the parts of the double. Avoids the overhead of four separate function calls.
   * 
   * @param d a double value
   * @param exponentAsBits whether to return exponent as raw bits rather than adjusted value
   * @return array with four elements:<ul>
   *     <li>return[0]: Equivalent to getSign(d)</li>
   *     <li>return[1]: Equivalent to (exponentAsBits ? getExponentBits(d) : getExponent(d))</li>
   *     <li>return[2]: Equivalent to getMantissa(d)</li>
   *     <li>return[3]: Equivalent to isSubnormal(d) - Uses zero for false, non-zero for true.</li>
   * </ul>
   */
  public static long[] getAllParts(double d, boolean exponentAsBits)
  {
    long[] segments = new long[4];
    long bits = Double.doubleToRawLongBits(d);
    
    segments[0] = (bits & SIGN_MASK) >>> SIGN_POS;
    segments[1] = (bits & EXPONENT_BITS_MASK) >>> EXPONENT_POS;
    segments[2] = bits & MANTISSA_MASK;
    segments[3] = (segments[1] == 0L && segments[2] != 0L ? 1L : 0L);
    
    if(!exponentAsBits)
      segments[1] -= EXPONENT_OFFSET;
    
    return segments;
  }
  
  /**
   * Creates a new double primitive using the provided component bits. 
   * Assumes the exponent parameter is signed.
   * 
   * @param sign sign bit
   * @param exponent adjusted exponent
   * @param mantissa mantissa bits
   * 
   * @return The double value represented by the provided binary parts.
   * 
   * @throws IllegalArgumentException if any of the parts contain invalid bits.
   */
  public static double getDouble(int sign, int exponent, long mantissa)
  {
    return getDouble(sign, exponent, mantissa, false);
  }
  
  /**
   * Creates a new double primitive using the provided component bits.
   * 
   * @param sign            sign bit
   * @param exponent        exponent (either raw bits or adjusted value)
   * @param mantissa        mantissa bits
   * @param exponentAsBits  If true, assumes that exponent parameter represents the actual exponent bits. 
   *                        If false, IEEE exponent offset value will be added to the offset to get the bits.
   * 
   * @return The double value represented by the provided binary parts.
   * 
   * @throws IllegalArgumentException if any of the parts contain invalid bits.
   */
  public static double getDouble(int sign, int exponent, long mantissa, 
          boolean exponentAsBits)
  {
    if(sign < 0 || sign > 1)
      throw new IllegalArgumentException("Illegal sign bit: " + sign);
    
    if(0 != (mantissa & ~MANTISSA_MASK))
      throw new IllegalArgumentException("Illegal mantissa: " + mantissa);
    
    int offsetExponent = (exponentAsBits ? exponent : exponent + EXPONENT_OFFSET);
    
    if(0 != (offsetExponent & ~MAX_EXPONENT_BITS))
      throw new IllegalArgumentException("Illegal exponent: " + exponent);
    
    return Double.longBitsToDouble((((long)sign) << SIGN_POS) | 
           (((long)offsetExponent) << EXPONENT_POS) | mantissa);
  }

}
