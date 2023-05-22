package com.github.kiprobinson.bigfraction.util;

/**
 * Additional utilities for working with floay values. Useful to prevent hard-coding IEEE 754 constants in code.
 * Consider this a complement to the methods provided in {@link Float}.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Float-precision_floating-point_format">
 * Wikipedia overview of IEEE 754 float-precision floating point specifications</a>
 */
public final class FloatUtil
{
  /** Mask to get sign bit in a raw float value. */
  private final static int SIGN_MASK = 0x80000000;
  /** Bit position of the sign bit in IEEE 754 float-precision binary representation. */
  private final static int SIGN_POS = 31;
  
  /** Mask to get exponent bits in a raw float value. */
  private final static int EXPONENT_BITS_MASK = 0x7f800000;
  /** Bit position of the exponent bits in IEEE 754 float-precision binary representation. */
  private final static int EXPONENT_POS = 23;
  /** Exponent offset, per IEEE 754 spec. */
  private final static int EXPONENT_OFFSET = 0x7f;
  
  /** Mask to get mantissa bits in a raw float value. */
  private final static int MANTISSA_MASK = 0x7fffff;
  
  /** The largest permitted mantissa value. */
  public final static int MAX_MANTISSA = MANTISSA_MASK;
  
  /** The largest permitted exponent value, when using raw exponent bits. */
  public final static int MAX_EXPONENT_BITS = (EXPONENT_BITS_MASK >> EXPONENT_POS);
  
  /** The smallest permitted exponent value, when using an exponent with offset. */
  public final static int MIN_EXPONENT = -EXPONENT_OFFSET;
  /** The largest permitted exponent value, when using an exponent with offset. */
  public final static int MAX_EXPONENT = MAX_EXPONENT_BITS - EXPONENT_OFFSET;
  
  
  /** Hide default constructor to prevent instantiation. */
  private FloatUtil() {}
  
  /**
   * Returns true if f is finite--not infinite and not NaN. (Equivalent to
   * Float.isFinite() available from Java 8.)
   * 
   * @param f a float value
   * @return whether this value is finite.
   * 
   * @deprecated use {@link Float#isFinite(float)} instead. This method was provided
   *             for use in Java7, where {@link Float#isFinite(float)} did not exist yet.
   */
  @Deprecated
  public static boolean isFinite(float f) {
    return Float.isFinite(f);
  }
  
  /**
   * Returns the sign bit (bit 63). 0=positive, 1=negative. This is returned even for NaN values.
   * 
   * @param f a float value
   * @return the sign bit
   */
  public static int getSign(float f)
  {
    return (int)((Float.floatToRawIntBits(f) & SIGN_MASK) >>> SIGN_POS);
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
   * @param f any float value
   * @return adjusted exponent
   */
  public static int getExponent(float f)
  {
    return getExponentBits(f) - EXPONENT_OFFSET;
  }
  
  /**
   * Returns the raw exponent bits, without adjusting for the offset. 
   * In other words, the value in bits 62-52.<br>
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
   * @param f a float value
   * @return raw exponent bits
   */
  public static int getExponentBits(float f)
  {
    return (int)
        ((Float.floatToRawIntBits(f) & EXPONENT_BITS_MASK) >>> EXPONENT_POS);
  }
  
  /**
   * Returns the mantissa bits (bits 51-0). Returned even for NaN values.
   * @param f a float value
   * @return mantissa bits
   */
  public static int getMantissa(float f)
  {
    return Float.floatToRawIntBits(f) & MANTISSA_MASK;
  }
  
  /**
   * Returns whether or not this float is a subnormal value.
   * @param f a float value
   * @return whether or not this float is a subnormal value
   */
  public static boolean isSubnormal(float f)
  {
    int bits = Float.floatToRawIntBits(f);
    return ((bits & EXPONENT_BITS_MASK) == 0) && ((bits & MANTISSA_MASK) != 0);
  }
  
  
  /**
   * Returns an array containing the parts of the float. Avoids the overhead of 
   * four separate function calls.
   * 
   * @param f a float value
   * @return array with four elements:<ul>
   *     <li>return[0]: Equivalent to getSign(f)</li>
   *     <li>return[1]: Equivalent to getExponent(f)</li>
   *     <li>return[2]: Equivalent to getMantissa(f)</li>
   *     <li>return[3]: Equivalent to isSubnormal(f) - Uses zero for false, non-zero for true.</li>
   * </ul>
   */
  public static int[] getAllParts(float f)
  {
    return getAllParts(f, false);
  }
  
  /**
   * Returns an array containing the parts of the float. 
   * Avoids the overhead of four separate function calls.
   * 
   * @param f a float value
   * @param exponentAsBits whether to return exponent as raw bits rather than adjusted value
   * @return array with four elements:<ul>
   *     <li>return[0]: Equivalent to getSign(f)</li>
   *     <li>return[1]: Equivalent to (exponentAsBits ? getExponentBits(f) : getExponent(f))</li>
   *     <li>return[2]: Equivalent to getMantissa(f)</li>
   *     <li>return[3]: Equivalent to isSubnormal(f) - Uses zero for false, non-zero for true.</li>
   * </ul>
   */
  public static int[] getAllParts(float f, boolean exponentAsBits)
  {
    int[] segments = new int[4];
    int bits = Float.floatToRawIntBits(f);
    
    segments[0] = (bits & SIGN_MASK) >>> SIGN_POS;
    segments[1] = (bits & EXPONENT_BITS_MASK) >>> EXPONENT_POS;
    segments[2] = bits & MANTISSA_MASK;
    segments[3] = (segments[1] == 0 && segments[2] != 0 ? 1 : 0);
    
    if(!exponentAsBits)
      segments[1] -= EXPONENT_OFFSET;
    
    return segments;
  }
  
  /**
   * Creates a new float primitive using the provided component bits. 
   * Assumes the exponent parameter is signed.
   * 
   * @param sign sign bit
   * @param exponent adjusted exponent
   * @param mantissa mantissa bits
   * 
   * @return The float value represented by the provided binary parts.
   * 
   * @throws IllegalArgumentException if any of the parts contain invalid bits.
   */
  public static float getFloat(int sign, int exponent, int mantissa)
  {
    return getFloat(sign, exponent, mantissa, false);
  }
  
  /**
   * Creates a new float primitive using the provided component bits.
   * 
   * @param sign            sign bit
   * @param exponent        exponent (either raw bits or adjusted value)
   * @param mantissa        mantissa bits
   * @param exponentAsBits  If true, assumes that exponent parameter represents the actual exponent bits. If false,
   *                        IEEE exponent offset value will be added to the offset to get the bits.
   * 
   * @return The float value represented by the provided binary parts.
   * 
   * @throws IllegalArgumentException if any of the parts contain invalid bits.
   */
  public static float getFloat(int sign, int exponent, int mantissa, 
          boolean exponentAsBits)
  {
    if(sign < 0 || sign > 1)
      throw new IllegalArgumentException("Illegal sign bit: " + sign);
    
    if(0 != (mantissa & ~MANTISSA_MASK))
      throw new IllegalArgumentException("Illegal mantissa: " + mantissa);
    
    int offsetExponent = (exponentAsBits ? exponent : exponent + EXPONENT_OFFSET);
    
    if(0 != (offsetExponent & ~MAX_EXPONENT_BITS))
      throw new IllegalArgumentException("Illegal exponent: " + exponent);
    
    return Float.intBitsToFloat((((int)sign) << SIGN_POS) | 
           (((int)offsetExponent) << EXPONENT_POS) | mantissa);
  }
}
