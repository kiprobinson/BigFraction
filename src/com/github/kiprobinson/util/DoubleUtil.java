package com.github.kiprobinson.util;

/**
 * Additional utilities for working with double values. Useful to prevent hard-coding IEEE 754 constants in code.
 * Consider this a complement to the methods provided in java.lang.Double.
 * 
 * @author Kip Robinson, https://github.com/kiprobinson
 */
public final class DoubleUtil
{
  /** Mask to get sign bit. */
  private final static long SIGN_MASK = 0x8000000000000000L;
  /** Bit position of the sign bit in IEEE 754 double-precision binary representation. */
  private final static int SIGN_POS = 63;
  
  /** Mask to get exponent bits. */
  private final static long EXPONENT_MASK = 0x7ff0000000000000L;
  /** Bit position of the exponent bits in IEEE 754 double-precision binary representation. */
  private final static int EXPONENT_POS = 52;
  /** Exponent offset, per IEEE 754 spec. */
  private final static int EXPONENT_OFFSET = 0x3ff;
  /** Maximum allowed exponent bits */
  private final static int MAX_EXPONENT = 0x7ff;
  
  /** Mask to get mantissa bits. */
  private final static long MANTISSA_MASK = 0xfffffffffffffL;
  
  /** Hide default constructor to prevent instantiation. */
  private DoubleUtil() {}
  
  /**
   * Returns true if d is finite--not infinite and not NaN. (Equivalent to
   * Double.isFinite() available from Java 8.)
   */
  public static boolean isFinite(double d) {
    return !Double.isInfinite(d) && !Double.isNaN(d);
  }
  
  /**
   * Returns the sign bit. 0=positive, 1=negative. This is returned even for NaN values.
   */
  public static int getSign(double d)
  {
    return (int)((Double.doubleToRawLongBits(d) & SIGN_MASK) >>> SIGN_POS);
  }
  
  /**
   * Returns the exponent, after adding the exponent offset to the exponent bits.
   * 
   * Examples:
   *   getExponent(0.5) == -1    because 1.0 == 1.0 * 2^-1
   *   getExponent(1.0) ==  0    because 1.0 == 1.0 * 2^0
   *   getExponent(2.0) ==  1    because 2.0 == 1.0 * 2^1
   */
  public static int getExponent(double d)
  {
    return getExponentBits(d) - EXPONENT_OFFSET;
  }
  
  /**
   * Returns the raw exponent bits, without adjusting for the offset.
   */
  public static int getExponentBits(double d)
  {
    return (int)((Double.doubleToRawLongBits(d) & EXPONENT_MASK) >>> EXPONENT_POS);
  }
  
  /**
   * Returns the mantissa bits.
   */
  public static long getMantissa(double d)
  {
    return Double.doubleToRawLongBits(d) & MANTISSA_MASK;
  }
  
  /**
   * Returns whether or not this double is a subnormal value.
   */
  public static boolean isSubnormal(double d)
  {
    long bits = Double.doubleToRawLongBits(d);
    return ((bits & EXPONENT_MASK) == 0) && ((bits & MANTISSA_MASK) != 0);
  }
  
  
  /**
   * Returns an array containing the parts of the double. Avoids the overhead of four separate function calls.
   * 
   * @return array with four elements:
   *     return[0]: Equivalent to getSign(d)
   *     return[1]: Equivalent to getExponent(d)
   *     return[2]: Equivalent to getMantissa(d)
   *     return[3]: Equivalent to isSubnormal(d) - Uses zero for false, non-zero for true.
   */
  public static long[] getAllParts(double d)
  {
    return getAllParts(d, false);
  }
  
  /**
   * Returns an array containing the parts of the double. Avoids the overhead of four separate function calls.
   * 
   * @return array with four elements:
   *     return[0]: Equivalent to getSign(d)
   *     return[1]: Equivalent to (exponentAsBits ? getExponentBits(d) : getExponent(d))
   *     return[2]: Equivalent to getMantissa(d)
   *     return[3]: Equivalent to isSubnormal(d) - Uses zero for false, non-zero for true.
   */
  public static long[] getAllParts(double d, boolean exponentAsBits)
  {
    long[] segments = new long[4];
    long bits = Double.doubleToRawLongBits(d);
    
    segments[0] = (bits & SIGN_MASK) >>> SIGN_POS;
    segments[1] = (bits & EXPONENT_MASK) >>> EXPONENT_POS;
    segments[2] = bits & MANTISSA_MASK;
    segments[3] = (segments[1] == 0L && segments[2] != 0L ? 1L : 0L);
    
    
    if(!exponentAsBits)
      segments[1] -= EXPONENT_OFFSET;
    
    return segments;
  }
  
  /**
   * Creates a new double primitive using the provided component bits. Assumes the exponent parameter is signed.
   */
  public static double getDouble(int sign, int exponent, long mantissa)
  {
    return getDouble(sign, exponent, mantissa, false);
  }
  
  /**
   * Creates a new double primitive using the provided component bits.
   * 
   * @param sign
   * @param exponent
   * @param mantissa
   * @param exponentAsBits  If true, assumes that exponent parameter represents the actual exponent bits. If false,
   *                        IEEE exponent offset value will be added to the offset to get the bits.
   * 
   * @return The double value represented by the provided binary parts.
   */
  public static double getDouble(int sign, int exponent, long mantissa, boolean exponentAsBits)
  {
    if(sign < 0 || sign > 1)
      throw new IllegalArgumentException("Illegal sign bit: " + sign);
    
    if(0 != (mantissa & ~MANTISSA_MASK))
      throw new IllegalArgumentException("Illegal mantissa: " + mantissa);
    
    int offsetExponent = (exponentAsBits ? exponent : exponent + EXPONENT_OFFSET);
    
    if(offsetExponent < 0 || offsetExponent > MAX_EXPONENT)
      throw new IllegalArgumentException("Illegal exponent: " + exponent);
    
    return Double.longBitsToDouble((((long)sign) << SIGN_POS) | (((long)offsetExponent) << EXPONENT_POS) | mantissa);
  }

}
