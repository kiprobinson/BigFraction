package com.github.kiprobinson.util;

import static org.junit.Assert.*;

import com.github.kiprobinson.util.FloatUtil;

import org.junit.Test;


/**
 * JUnit tests for BigFraction class.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public class FloatUtilTest {
  
  
  @Test
  public void testValues() {
    runTest( 1.0f, 0, 0x7f, 0, 0x000000, false);
    runTest(-1.0f, 1, 0x7f, 0, 0x000000, false);
    
    runTest( 2.0f, 0, 0x80, 1, 0x000000, false);
    runTest(-2.0f, 1, 0x80, 1, 0x000000, false);
    
    runTest( 8.0f, 0, 0x82, 3, 0x000000, false);
    runTest(-8.0f, 1, 0x82, 3, 0x000000, false);
    
    runTest( 0.5f, 0, 0x7e, -1, 0x000000, false);
    runTest(-0.5f, 1, 0x7e, -1, 0x000000, false);
    
    runTest( 0.125f, 0, 0x7c, -3, 0x000000, false);
    runTest(-0.125f, 1, 0x7c, -3, 0x000000, false);
    
    runTest( 1.625f, 0, 0x7f, 0, 0x500000, false);
    runTest(-1.625f, 1, 0x7f, 0, 0x500000, false);
    
    runTest( 0.0f, 0, 0x000, -127, 0x000000, false);
    runTest(-0.0f, 1, 0x000, -127, 0x000000, false);
    
    runTest( Float.MAX_VALUE, 0, 0xfe, 127, 0x7fffff, false);
    runTest(-Float.MAX_VALUE, 1, 0xfe, 127, 0x7fffff, false);
    
    runTest( Float.MIN_NORMAL, 0, 0x001, -126, 0x000000, false);
    runTest(-Float.MIN_NORMAL, 1, 0x001, -126, 0x000000, false);
    
    //smallest subnormals
    runTest( Float.MIN_VALUE, 0, 0x000, -127, 0x000001, true);
    runTest(-Float.MIN_VALUE, 1, 0x000, -127, 0x000001, true);
    
    //largest subnormals
    runTest(Float.intBitsToFloat(0x007fffff), 0, 0x000, -127, 0x7fffff, true);
    runTest(Float.intBitsToFloat(0x807fffff), 1, 0x000, -127, 0x7fffff, true);
    
    runTest( Float.POSITIVE_INFINITY, 0, 0xff, 128, 0x000000, false);
    runTest(-Float.POSITIVE_INFINITY, 1, 0xff, 128, 0x000000, false);
    
    runTest(-Float.NEGATIVE_INFINITY, 0, 0xff, 128, 0x000000, false);
    runTest( Float.NEGATIVE_INFINITY, 1, 0xff, 128, 0x000000, false);
    
    //test NaN values. You can't negate a NaN, so the only way to test it is to construct it directly
    runTest(Float.NaN, 0, 0xff, 128, 0x400000, false);
    runTest(Float.intBitsToFloat(0x7fc00000), 0, 0xff, 128, 0x400000, false);
    runTest(Float.intBitsToFloat(0xffc00000), 1, 0xff, 128, 0x400000, false);
    runTest(Float.intBitsToFloat(0x7f800001), 0, 0xff, 128, 0x000001, false);
    runTest(Float.intBitsToFloat(0xff800001), 1, 0xff, 128, 0x000001, false);
    runTest(Float.intBitsToFloat(0x7fffffff), 0, 0xff, 128, 0x7fffff, false);
    runTest(Float.intBitsToFloat(0xffffffff), 1, 0xff, 128, 0x7fffff, false);
    
    runTest(Float.intBitsToFloat(0xdeadbeef), 1, 0xbd, 0x3e, 0x2dbeef, false);
    runTest(Float.intBitsToFloat(0xcafebabe), 1, 0x95, 0x16, 0x7ebabe, false);
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
  private void runTest(float val, int sign, int exponentBits, int exponent, int mantissa, boolean isSubnormal) {
    assertEquals("getSign(" + val + ")", sign, FloatUtil.getSign(val));
    assertEquals("getExponentBits(" + val + ")", exponentBits, FloatUtil.getExponentBits(val));
    assertEquals("getExponent(" + val + ")", exponent, FloatUtil.getExponent(val));
    assertEquals("getMantissa(" + val + ")", mantissa, FloatUtil.getMantissa(val));
    assertEquals("isSubnormal(" + val + ")", isSubnormal, FloatUtil.isSubnormal(val));
    
    float tmp1 = FloatUtil.getFloat(sign, exponent, mantissa, false);
    assertEquals("getFloat(" + val + ") - with signed exponent", Float.floatToRawIntBits(val), Float.floatToRawIntBits(tmp1));
    
    float tmp2 = FloatUtil.getFloat(sign, exponentBits, mantissa, true);
    assertEquals("getFloat(" + val + ") - with exponent as bits", Float.floatToRawIntBits(val), Float.floatToRawIntBits(tmp2));
    
    int[] parts = FloatUtil.getAllParts(val, false);
    assertEquals("getAllParts(" + val + ") - signed exponent - sign", sign, parts[0]);
    assertEquals("getAllParts(" + val + ") - signed exponent - exponent", exponent, parts[1]);
    assertEquals("getAllParts(" + val + ") - signed exponent - mantissa", mantissa, parts[2]);
    assertEquals("getAllParts(" + val + ") - signed exponent - isSubnormal - " + parts[3], isSubnormal, (parts[3] != 0L));
    
    parts = FloatUtil.getAllParts(val, true);
    assertEquals("getAllParts(" + val + ") - unsigned exponent - sign", sign, parts[0]);
    assertEquals("getAllParts(" + val + ") - unsigned exponent - exponent", exponentBits, parts[1]);
    assertEquals("getAllParts(" + val + ") - unsigned exponent - mantissa", mantissa, parts[2]);
    assertEquals("getAllParts(" + val + ") - unsigned exponent - isSubnormal", isSubnormal, (parts[3] != 0L));
    
  }
}
