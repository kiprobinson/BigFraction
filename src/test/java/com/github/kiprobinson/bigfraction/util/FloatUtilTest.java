package com.github.kiprobinson.bigfraction.util;

import static org.junit.Assert.*;

import com.github.kiprobinson.bigfraction.util.FloatUtil;

import org.junit.Test;


/**
 * JUnit tests for BigFraction class.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public class FloatUtilTest {
  
  
  @Test
  public void testValues() {
    runTest( 1.0f, 0, 0x7f, 0, 0x000000, false, true);
    runTest(-1.0f, 1, 0x7f, 0, 0x000000, false, true);
    
    runTest( 2.0f, 0, 0x80, 1, 0x000000, false, true);
    runTest(-2.0f, 1, 0x80, 1, 0x000000, false, true);
    
    runTest( 8.0f, 0, 0x82, 3, 0x000000, false, true);
    runTest(-8.0f, 1, 0x82, 3, 0x000000, false, true);
    
    runTest( 0.5f, 0, 0x7e, -1, 0x000000, false, true);
    runTest(-0.5f, 1, 0x7e, -1, 0x000000, false, true);
    
    runTest( 0.125f, 0, 0x7c, -3, 0x000000, false, true);
    runTest(-0.125f, 1, 0x7c, -3, 0x000000, false, true);
    
    runTest( 1.625f, 0, 0x7f, 0, 0x500000, false, true);
    runTest(-1.625f, 1, 0x7f, 0, 0x500000, false, true);
    
    runTest( 0.0f, 0, 0x000, -127, 0x000000, false, true);
    runTest(-0.0f, 1, 0x000, -127, 0x000000, false, true);
    
    runTest( Float.MAX_VALUE, 0, 0xfe, 127, 0x7fffff, false, true);
    runTest(-Float.MAX_VALUE, 1, 0xfe, 127, 0x7fffff, false, true);
    
    runTest( Float.MIN_NORMAL, 0, 0x001, -126, 0x000000, false, true);
    runTest(-Float.MIN_NORMAL, 1, 0x001, -126, 0x000000, false, true);
    
    //smallest subnormals
    runTest( Float.MIN_VALUE, 0, 0x000, -127, 0x000001, true, true);
    runTest(-Float.MIN_VALUE, 1, 0x000, -127, 0x000001, true, true);
    
    //largest subnormals
    runTest(Float.intBitsToFloat(0x007fffff), 0, 0x000, -127, 0x7fffff, true, true);
    runTest(Float.intBitsToFloat(0x807fffff), 1, 0x000, -127, 0x7fffff, true, true);
    
    runTest( Float.POSITIVE_INFINITY, 0, 0xff, 128, 0x000000, false, false);
    runTest(-Float.POSITIVE_INFINITY, 1, 0xff, 128, 0x000000, false, false);
    
    runTest(-Float.NEGATIVE_INFINITY, 0, 0xff, 128, 0x000000, false, false);
    runTest( Float.NEGATIVE_INFINITY, 1, 0xff, 128, 0x000000, false, false);
    
    //test NaN values. You can't negate a NaN, so the only way to test it is to construct it directly
    runTest(Float.NaN, 0, 0xff, 128, 0x400000, false, false);
    runTest(Float.intBitsToFloat(0x7fc00000), 0, 0xff, 128, 0x400000, false, false);
    runTest(Float.intBitsToFloat(0xffc00000), 1, 0xff, 128, 0x400000, false, false);
    runTest(Float.intBitsToFloat(0x7f800001), 0, 0xff, 128, 0x000001, false, false);
    runTest(Float.intBitsToFloat(0xff800001), 1, 0xff, 128, 0x000001, false, false);
    runTest(Float.intBitsToFloat(0x7fffffff), 0, 0xff, 128, 0x7fffff, false, false);
    runTest(Float.intBitsToFloat(0xffffffff), 1, 0xff, 128, 0x7fffff, false, false);
    
    runTest(Float.intBitsToFloat(0xdeadbeef), 1, 0xbd, 0x3e, 0x2dbeef, false, true);
    runTest(Float.intBitsToFloat(0xcafebabe), 1, 0x95, 0x16, 0x7ebabe, false, true);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallSign() {
    FloatUtil.getFloat(-1, 0, 0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeSign() {
    FloatUtil.getFloat(2, 0, 0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallExponent_Bits() {
    FloatUtil.getFloat(0, -1, 0, true);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeExponent_Bits() {
    FloatUtil.getFloat(0, 0x800, 0, true);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallExponent_Signed() {
    FloatUtil.getFloat(0, -1024, 0, false);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeExponent_Signed() {
    FloatUtil.getFloat(0, 0x401, 0, false);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallMantissa() {
    FloatUtil.getFloat(0, 0, -1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeMantissa() {
    FloatUtil.getFloat(0, 0, 0x800000);
  }
  
  
  /**
   * Takes a float value and the expected components, and makes sure that all functions return the expected value.
   */
  @SuppressWarnings("deprecation")
  private void runTest(float val, int sign, int exponentBits, int exponent, int mantissa, boolean isSubnormal, boolean isFinite) {
    assertEquals("getSign(" + val + ")", sign, FloatUtil.getSign(val));
    assertEquals("getExponentBits(" + val + ")", exponentBits, FloatUtil.getExponentBits(val));
    assertEquals("getExponent(" + val + ")", exponent, FloatUtil.getExponent(val));
    assertEquals("getMantissa(" + val + ")", mantissa, FloatUtil.getMantissa(val));
    assertEquals("isSubnormal(" + val + ")", isSubnormal, FloatUtil.isSubnormal(val));
    assertEquals("isFinite(" + val + ")", isFinite, FloatUtil.isFinite(val));
    
    float tmp = FloatUtil.getFloat(sign, exponent, mantissa);
    assertEquals("getFloat(" + val + ") - with signed exponent", Float.floatToRawIntBits(val), Float.floatToRawIntBits(tmp));
    
    tmp = FloatUtil.getFloat(sign, exponent, mantissa, false);
    assertEquals("getFloat(" + val + ", false) - with signed exponent", Float.floatToRawIntBits(val), Float.floatToRawIntBits(tmp));
    
    tmp = FloatUtil.getFloat(sign, exponentBits, mantissa, true);
    assertEquals("getFloat(" + val + ", true) - with exponent as bits", Float.floatToRawIntBits(val), Float.floatToRawIntBits(tmp));
    
    int[] parts = FloatUtil.getAllParts(val);
    assertEquals("getAllParts(" + val + ") - signed exponent - sign", sign, parts[0]);
    assertEquals("getAllParts(" + val + ") - signed exponent - exponent", exponent, parts[1]);
    assertEquals("getAllParts(" + val + ") - signed exponent - mantissa", mantissa, parts[2]);
    assertEquals("getAllParts(" + val + ") - signed exponent - isSubnormal - " + parts[3], isSubnormal, (parts[3] != 0L));
    
    parts = FloatUtil.getAllParts(val, false);
    assertEquals("getAllParts(" + val + ", false) - signed exponent - sign", sign, parts[0]);
    assertEquals("getAllParts(" + val + ", false) - signed exponent - exponent", exponent, parts[1]);
    assertEquals("getAllParts(" + val + ", false) - signed exponent - mantissa", mantissa, parts[2]);
    assertEquals("getAllParts(" + val + ", false) - signed exponent - isSubnormal - " + parts[3], isSubnormal, (parts[3] != 0L));
    
    parts = FloatUtil.getAllParts(val, true);
    assertEquals("getAllParts(" + val + ", true) - unsigned exponent - sign", sign, parts[0]);
    assertEquals("getAllParts(" + val + ", true) - unsigned exponent - exponent", exponentBits, parts[1]);
    assertEquals("getAllParts(" + val + ", true) - unsigned exponent - mantissa", mantissa, parts[2]);
    assertEquals("getAllParts(" + val + ", true) - unsigned exponent - isSubnormal", isSubnormal, (parts[3] != 0L));
    
  }
}
