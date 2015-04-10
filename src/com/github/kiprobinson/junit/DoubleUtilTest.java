package com.github.kiprobinson.junit;

import static org.junit.Assert.*;

import com.github.kiprobinson.util.DoubleUtil;

import org.junit.Test;


/**
 * JUnit tests for BigFraction class.
 * 
 * @author Kip Robinson, https://github.com/kiprobinson
 */
public class DoubleUtilTest {
  
  
  @Test
  public void testValues() {
    runTest( 1.0, 0, 0x3ff, 0, 0x0000000000000L, false);
    runTest(-1.0, 1, 0x3ff, 0, 0x0000000000000L, false);
    
    runTest( 2.0, 0, 0x400, 1, 0x0000000000000L, false);
    runTest(-2.0, 1, 0x400, 1, 0x0000000000000L, false);
    
    runTest( 8.0, 0, 0x402, 3, 0x0000000000000L, false);
    runTest(-8.0, 1, 0x402, 3, 0x0000000000000L, false);
    
    runTest( 0.5, 0, 0x3fe, -1, 0x0000000000000L, false);
    runTest(-0.5, 1, 0x3fe, -1, 0x0000000000000L, false);
    
    runTest( 0.125, 0, 0x3fc, -3, 0x0000000000000L, false);
    runTest(-0.125, 1, 0x3fc, -3, 0x0000000000000L, false);
    
    runTest( 1.625, 0, 0x3ff, 0, 0xa000000000000L, false);
    runTest(-1.625, 1, 0x3ff, 0, 0xa000000000000L, false);
    
    runTest( 0.0, 0, 0x000, -1023, 0x0000000000000L, false);
    runTest(-0.0, 1, 0x000, -1023, 0x0000000000000L, false);
    
    runTest( Double.MAX_VALUE, 0, 0x7fe, 1023, 0xfffffffffffffL, false);
    runTest(-Double.MAX_VALUE, 1, 0x7fe, 1023, 0xfffffffffffffL, false);
    
    runTest( Double.MIN_NORMAL, 0, 0x001, -1022, 0x0000000000000L, false);
    runTest(-Double.MIN_NORMAL, 1, 0x001, -1022, 0x0000000000000L, false);
    
    //smallest subnormals
    runTest( Double.MIN_VALUE, 0, 0x000, -1023, 0x0000000000001L, true);
    runTest(-Double.MIN_VALUE, 1, 0x000, -1023, 0x0000000000001L, true);
    
    //largest subnormals
    runTest(Double.longBitsToDouble(0x000fffffffffffffL), 0, 0x000, -1023, 0xfffffffffffffL, true);
    runTest(Double.longBitsToDouble(0x800fffffffffffffL), 1, 0x000, -1023, 0xfffffffffffffL, true);
    
    runTest( Double.POSITIVE_INFINITY, 0, 0x7ff, 1024, 0x0000000000000L, false);
    runTest(-Double.POSITIVE_INFINITY, 1, 0x7ff, 1024, 0x0000000000000L, false);
    
    runTest(-Double.NEGATIVE_INFINITY, 0, 0x7ff, 1024, 0x0000000000000L, false);
    runTest( Double.NEGATIVE_INFINITY, 1, 0x7ff, 1024, 0x0000000000000L, false);
    
    //test NaN values. You can't negate a NaN, so the only way to test it is to construct it directly
    runTest(Double.NaN, 0, 0x7ff, 1024, 0x8000000000000L, false);
    runTest(Double.longBitsToDouble(0x7ff8000000000000L), 0, 0x7ff, 1024, 0x8000000000000L, false);
    runTest(Double.longBitsToDouble(0xfff8000000000000L), 1, 0x7ff, 1024, 0x8000000000000L, false);
    runTest(Double.longBitsToDouble(0x7ff0000000000001L), 0, 0x7ff, 1024, 0x0000000000001L, false);
    runTest(Double.longBitsToDouble(0xfff0000000000001L), 1, 0x7ff, 1024, 0x0000000000001L, false);
    runTest(Double.longBitsToDouble(0x7fffffffffffffffL), 0, 0x7ff, 1024, 0xfffffffffffffL, false);
    runTest(Double.longBitsToDouble(0xffffffffffffffffL), 1, 0x7ff, 1024, 0xfffffffffffffL, false);
    
    runTest(Double.longBitsToDouble(0xdeadbeefdeadbeefL), 1, 0x5ea, 0x1eb, 0xdbeefdeadbeefL, false);
    runTest(Double.longBitsToDouble(0xcafebabecafebabeL), 1, 0x4af, 0x0b0, 0xebabecafebabeL, false);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallSign() {
    DoubleUtil.getDouble(-1, 0, 0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeSign() {
    DoubleUtil.getDouble(2, 0, 0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallExponent_Bits() {
    DoubleUtil.getDouble(0, -1, 0, true);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeExponent_Bits() {
    DoubleUtil.getDouble(0, 0x800, 0, true);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallExponent_Signed() {
    DoubleUtil.getDouble(0, -1024, 0, false);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeExponent_Signed() {
    DoubleUtil.getDouble(0, 0x401, 0, false);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSmallMantissa() {
    DoubleUtil.getDouble(0, 0, -1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLargeMantissa() {
    DoubleUtil.getDouble(0, 0, 0x10000000000000L);
  }
  
  
  /**
   * Takes a double value and the expected components, and makes sure that all functions return the expected value.
   */
  private void runTest(double val, int sign, int exponentBits, int exponent, long mantissa, boolean isSubnormal) {
    assertEquals("getSign(" + val + ")", sign, DoubleUtil.getSign(val));
    assertEquals("getExponentBits(" + val + ")", exponentBits, DoubleUtil.getExponentBits(val));
    assertEquals("getExponent(" + val + ")", exponent, DoubleUtil.getExponent(val));
    assertEquals("getMantissa(" + val + ")", mantissa, DoubleUtil.getMantissa(val));
    assertEquals("isSubnormal(" + val + ")", isSubnormal, DoubleUtil.isSubnormal(val));
    
    double tmp1 = DoubleUtil.getDouble(sign, exponent, mantissa, false);
    assertEquals("getDouble(" + val + ") - with signed exponent", Double.doubleToRawLongBits(val), Double.doubleToRawLongBits(tmp1));
    
    double tmp2 = DoubleUtil.getDouble(sign, exponentBits, mantissa, true);
    assertEquals("getDouble(" + val + ") - with exponent as bits", Double.doubleToRawLongBits(val), Double.doubleToRawLongBits(tmp2));
  }
}
