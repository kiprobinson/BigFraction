package com.github.kiprobinson.junit;

import static org.junit.Assert.*;

import java.math.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kiprobinson.util.BigFraction;

import org.junit.Test;


/**
 * JUnit tests for BigFraction class.
 * 
 * @author Kip Robinson, https://github.com/kiprobinson
 */
public class BigFractionTest {
  
  @Test
  public void testValueOf() {
    assertEquals("valueOf(1.1)", "2476979795053773/2251799813685248", BigFraction.valueOf(1.1).toString());
    assertEquals("valueOf(-0.0)", "0/1", BigFraction.valueOf(-0.0).toString());
    assertEquals("valueOf(1.1f)", "9227469/8388608", BigFraction.valueOf(1.1f).toString());
    assertEquals("valueOf(\"1.1\")", "11/10", BigFraction.valueOf("1.1").toString());
    assertEquals("valueOf(11,10)", "11/10", BigFraction.valueOf(11,10).toString());
    assertEquals("valueOf(2*5*7, 3*5*11)", "14/33", BigFraction.valueOf(2*5*7,3*5*11).toString());
    assertEquals("valueOf(100,-7)", "-100/7", BigFraction.valueOf(100,-7).toString());
    assertEquals("valueOf(\"-1.0E2/-0.007E3\")", "100/7", BigFraction.valueOf("-1.0E2/-0.007E3").toString());
    assertEquals("valueOf(\"+9.02E-10\")", "451/500000000000", BigFraction.valueOf("+9.02E-10").toString());
    assertEquals("valueOf(\"-0.000000E+500\")", "0/1", BigFraction.valueOf("-0.000000E+500").toString());
    
    assertEquals("10/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(10), 0)).toString());
    assertEquals("10/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(1), -1)).toString());
    assertEquals("10/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(100), 1)).toString());

    assertEquals("123000/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), -3)).toString());
    assertEquals("12300/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), -2)).toString());
    assertEquals("1230/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), -1)).toString());
    assertEquals("123/1", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 0)).toString());
    assertEquals("123/10", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 1)).toString());
    assertEquals("123/100", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 2)).toString());
    assertEquals("123/1000", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 3)).toString());
    
    assertEquals("123/1", BigFraction.valueOf(1.23e2).toString());
    assertEquals("1230/1", BigFraction.valueOf(1.23e3).toString());
    assertEquals("12300/1", BigFraction.valueOf(1.23e4).toString());
    
    //some doubles which should have simple, exact representations
    assertEquals("1/2", BigFraction.valueOf(0.5).toString());
    assertEquals("1/4", BigFraction.valueOf(0.25).toString());
    assertEquals("5/8", BigFraction.valueOf(0.625).toString());
    assertEquals("-3/2", BigFraction.valueOf(-1.5).toString());
    assertEquals("-9/4", BigFraction.valueOf(-2.25).toString());
    assertEquals("-29/8", BigFraction.valueOf(-3.625).toString());
    
    assertEquals("-2/9", BigFraction.valueOf(0.5, -2.25).toString());
    assertEquals("-2/9", BigFraction.valueOf(-2.0, 9.0).toString());
  }
  
  
  @Test
  public void testAdd() {
    assertEquals("5/1 + -3", "2/1", BigFraction.valueOf(5).add(-3).toString());
    assertEquals("11/17 + 2/3", "67/51", BigFraction.valueOf("11/17").add(BigFraction.valueOf("2/3")).toString());
    assertEquals("1/6 + 1/15", "7/30", BigFraction.valueOf("1/6").add(BigFraction.valueOf("1/15")).toString());
    assertEquals("1/6 + 1/6", "1/3", BigFraction.valueOf("1/6").add(BigFraction.valueOf("1/6")).toString());
    assertEquals("-1/6 + 1/6", "0/1", BigFraction.valueOf("-1/6").add(BigFraction.valueOf("1/6")).toString());
    assertEquals("-1/6 + -1/6", "-1/3", BigFraction.valueOf("-1/6").add(BigFraction.valueOf("-1/6")).toString());
    assertEquals("-1/6 + 1/15", "-1/10", BigFraction.valueOf("-1/6").add(BigFraction.valueOf("1/15")).toString());
  }
  
  @Test
  public void testSubtract() {
    assertEquals("5/1 - -3/1", "8/1", BigFraction.valueOf(5).subtract(-3).toString());
    assertEquals("-5/1 - 2/1", "-7/1", BigFraction.valueOf(-5).subtract(2).toString());
    assertEquals("-5/1 - -2/1", "-3/1", BigFraction.valueOf(-5).subtract(-2).toString());
    assertEquals("11/17 - 2/3", "-1/51", BigFraction.valueOf("11/17").subtract(BigFraction.valueOf("2/3")).toString());
    assertEquals("1/6 - 1/6", "0/1", BigFraction.valueOf("1/6").subtract(BigFraction.valueOf("1/6")).toString());
    assertEquals("-1/6 - 1/6", "-1/3", BigFraction.valueOf("-1/6").subtract(BigFraction.valueOf("1/6")).toString());
  }
  
  @Test
  public void testMultiply() {
    assertEquals("(11/17)(0/1)", "0/1", BigFraction.valueOf("11/17").multiply(-0.0).toString());
    assertEquals("(1/3)(3/4)", "1/4", BigFraction.valueOf("1/3").multiply(BigFraction.valueOf("3/4")).toString());
    assertEquals("(-1/12)(16/5)", "-4/15", BigFraction.valueOf("-1/12").multiply(BigFraction.valueOf("16/5")).toString());
    assertEquals("(-7/6)(-5/9)", "35/54", BigFraction.valueOf("-7/6").multiply(BigFraction.valueOf("-5/9")).toString());
    assertEquals("(4/5)(-7/2)", "-14/5", BigFraction.valueOf("4/5").multiply(BigFraction.valueOf("7/-2")).toString());
  }
  
  @Test
  public void testDivide() {
    assertEquals("(1/3)/(4/3)", "1/4", BigFraction.valueOf("1/3").divide(BigFraction.valueOf("4/3")).toString());
    assertEquals("(-1/12)/(5/16)", "-4/15", BigFraction.valueOf("-1/12").divide(BigFraction.valueOf("5/16")).toString());
    assertEquals("(-7/6)/(-9/5)", "35/54", BigFraction.valueOf("-7/6").divide(BigFraction.valueOf("9/-5")).toString());
    assertEquals("(4/5)/(-2/7)", "-14/5", BigFraction.valueOf("4/5").divide(BigFraction.valueOf("-2/7")).toString());
  }
  
  @Test
  public void testReciprocal() {
    assertEquals("(1/1).reciprocal", "1/1", BigFraction.valueOf(1.0).reciprocal().toString());
    assertEquals("(1/2).reciprocal", "2/1", BigFraction.valueOf(2,4).reciprocal().toString());
    assertEquals("(-10/-14).reciprocal", "7/5", BigFraction.valueOf(-10,-14).reciprocal().toString());
    assertEquals("(-6/1).reciprocal", "-1/6", BigFraction.valueOf(-6).reciprocal().toString());
  }
  
  @Test
  public void testComplement() {
    assertEquals("(10/14).complement", "2/7", BigFraction.valueOf(10,14).complement().toString());
    assertEquals("(1/1).complement", "0/1", BigFraction.valueOf(1).complement().toString());
    assertEquals("(-1/1).complement", "2/1", BigFraction.valueOf(-1).complement().toString());
    assertEquals("(0/1).complement", "1/1", BigFraction.valueOf(0).complement().toString());
    assertEquals("(17/11).complement", "-6/11", BigFraction.valueOf(17,11).complement().toString());
  }
  
  @Test
  public void testSubnormals() {
    double minSubnormal = Double.longBitsToDouble(0x0000000000000001L);
    double arbSubnormal = Double.longBitsToDouble(0x000deadbeef01010L);
    double maxSubnormal = Double.longBitsToDouble(0x000fffffffffffffL);
    double negMinSubnormal = Double.longBitsToDouble(0x8000000000000001L);
    double negArbSubnormal = Double.longBitsToDouble(0x800deadbeef01010L);
    double negMaxSubnormal = Double.longBitsToDouble(0x800fffffffffffffL);
    
    //NOTE: For these first tests, I'm relying on the Java team to have correctly implemented this in the BigDecimal class...
    assertEquals("minimum subnormal value", new BigDecimal(minSubnormal).toString(), BigFraction.valueOf(minSubnormal).toBigDecimal().toString());
    assertEquals("arbitrary subnormal value", new BigDecimal(arbSubnormal).toString(), BigFraction.valueOf(arbSubnormal).toBigDecimal().toString());
    assertEquals("maximum subnormal value", new BigDecimal(maxSubnormal).toString(), BigFraction.valueOf(maxSubnormal).toBigDecimal().toString());
    
    assertEquals("negative minimum subnormal value", new BigDecimal(negMinSubnormal).toString(), BigFraction.valueOf(negMinSubnormal).toBigDecimal().toString());
    assertEquals("negative arbitrary subnormal value", new BigDecimal(negArbSubnormal).toString(), BigFraction.valueOf(negArbSubnormal).toBigDecimal().toString());
    assertEquals("negative maximum subnormal value", new BigDecimal(negMaxSubnormal).toString(), BigFraction.valueOf(negMaxSubnormal).toBigDecimal().toString());
    
    //the minimum subnormal is 1/(2^1074). make sure we get that fraction
    BigInteger minExponent = BigInteger.valueOf(2).pow(1074);
    assertEquals("minimum subnormal fraction", "1/" + minExponent.toString(), BigFraction.valueOf(minSubnormal).toString());
    assertEquals("negative minimum subnormal fraction", "-1/" + minExponent.toString(), BigFraction.valueOf(negMinSubnormal).toString());
    
    //ensure that we are reducing correctly. 96/(2^1074) should reduce to 3/(2^1069)
    assertEquals("3/" + BigInteger.valueOf(2).pow(1069).toString(), BigFraction.valueOf(Double.longBitsToDouble(96L)).toString());
  }
  
  @Test
  public void testPow() {
    //Note: 0^0 returns 1 (just like Math.pow())
    assertEquals("(0/1)^(0)", "1/1", BigFraction.valueOf(0,1).pow(0).toString());
    assertEquals("(11/17)^(5)", "161051/1419857", BigFraction.valueOf(11,17).pow(5).toString());
    assertEquals("(11/17)^(-5)", "1419857/161051", BigFraction.valueOf(11,17).pow(-5).toString());
    assertEquals("(5/8)^(0)", "1/1", BigFraction.valueOf(5,8).pow(0).toString());
    assertEquals("(9/16)^(-1)", "16/9", BigFraction.valueOf(9,16).pow(-1).toString());
    assertEquals("(9/16)^(1)", "9/16", BigFraction.valueOf(9,16).pow(1).toString());
  }
  
  @Test
  public void testGetIntegerPart() {
    assertEquals("1", BigFraction.valueOf("4/3").getIntegerPart().toString());
    assertEquals("-1", BigFraction.valueOf("-4/3").getIntegerPart().toString());
    assertEquals("2", BigFraction.valueOf("6/3").getIntegerPart().toString());
    assertEquals("-2", BigFraction.valueOf("6/-3").getIntegerPart().toString());
    assertEquals("0", BigFraction.valueOf("2/3").getIntegerPart().toString());
    assertEquals("0", BigFraction.valueOf("2/-3").getIntegerPart().toString());
    assertEquals("0", BigFraction.valueOf("0/3").getIntegerPart().toString());
    assertEquals("0", BigFraction.valueOf("0/-3").getIntegerPart().toString());
  }
  
  @Test
  public void testGetFractionPart() {
    assertEquals("1/3", BigFraction.valueOf("4/3").getFractionPart().toString());
    assertEquals("-1/3", BigFraction.valueOf("-4/3").getFractionPart().toString());
    assertEquals("0/1", BigFraction.valueOf("6/3").getFractionPart().toString());
    assertEquals("0/1", BigFraction.valueOf("6/-3").getFractionPart().toString());
    assertEquals("2/3", BigFraction.valueOf("2/3").getFractionPart().toString());
    assertEquals("-2/3", BigFraction.valueOf("2/-3").getFractionPart().toString());
    assertEquals("0/1", BigFraction.valueOf("0/3").getFractionPart().toString());
    assertEquals("0/1", BigFraction.valueOf("0/-3").getFractionPart().toString());
  }
  
  
  @Test
  public void testGetParts() {
    assertEquals("1", BigFraction.valueOf("4/3").getParts()[0].toString());
    assertEquals("-1", BigFraction.valueOf("-4/3").getParts()[0].toString());
    assertEquals("2", BigFraction.valueOf("6/3").getParts()[0].toString());
    assertEquals("-2", BigFraction.valueOf("6/-3").getParts()[0].toString());
    assertEquals("0", BigFraction.valueOf("2/3").getParts()[0].toString());
    assertEquals("0", BigFraction.valueOf("2/-3").getParts()[0].toString());
    assertEquals("0", BigFraction.valueOf("0/3").getParts()[0].toString());
    assertEquals("0", BigFraction.valueOf("0/-3").getParts()[0].toString());
    
    assertEquals("1/3", BigFraction.valueOf("4/3").getParts()[1].toString());
    assertEquals("-1/3", BigFraction.valueOf("-4/3").getParts()[1].toString());
    assertEquals("0/1", BigFraction.valueOf("6/3").getParts()[1].toString());
    assertEquals("0/1", BigFraction.valueOf("6/-3").getParts()[1].toString());
    assertEquals("2/3", BigFraction.valueOf("2/3").getParts()[1].toString());
    assertEquals("-2/3", BigFraction.valueOf("2/-3").getParts()[1].toString());
    assertEquals("0/1", BigFraction.valueOf("0/3").getParts()[1].toString());
    assertEquals("0/1", BigFraction.valueOf("0/-3").getParts()[1].toString());
  }
  
  
  @Test
  public void testMediant() {
    //assertTrue(false);
    assertEquals("mediant(1/1,1/2)", bf(1,2), bf(1,1).mediant(bf(1,3)));
    
    //ensure that we are always reducing to lowest terms
    assertEquals("mediant(2/2,1/2)", bf(1,2), bf(2,2).mediant(bf(1,3)));
    
    //things to test: zero, negative, self
    assertEquals("mediant(3/29,7/15)", bf(5,22), bf(3,29).mediant(bf(7,15)));
    assertEquals("mediant(29/3,15/7)", bf(22,5), bf(29,3).mediant(bf(15,7)));
    assertEquals("mediant(-3/29,7/15)", bf(1,11), bf(-3,29).mediant(bf(7,15)));
    assertEquals("mediant(-29/3,15/7)", bf(-7,5), bf(-29,3).mediant(bf(15,7)));
    assertEquals("mediant(3/29,-7/15)", bf(-1,11), bf(3,29).mediant(bf(-7,15)));
    assertEquals("mediant(29/3,-15/7)", bf(7,5), bf(29,3).mediant(bf(-15,7)));
    assertEquals("mediant(-3/29,-7/15)", bf(-5,22), bf(-3,29).mediant(bf(-7,15)));
    assertEquals("mediant(-29/3,-15/7)", bf(-22,5), bf(-29,3).mediant(bf(-15,7)));
    
    assertEquals("mediant(19/81,19/81)", bf(19,81), bf(19,81).mediant(bf(19,81)));
    assertEquals("mediant(-19/81,19/81)", bf(0), bf(-19,81).mediant(bf(19,81)));
    assertEquals("mediant(19/81,-19/81)", bf(0), bf(19,81).mediant(bf(-19,81)));
    assertEquals("mediant(-19/81,-19/81)", bf(-19,81), bf(-19,81).mediant(bf(-19,81)));
    
    assertEquals("mediant(0,81/19)", bf(81,20), bf(0).mediant(bf(81,19)));
    assertEquals("mediant(0,-81/19)", bf(-81,20), bf(0).mediant(bf(-81,19)));
    assertEquals("mediant(0,0)", bf(0), bf(0).mediant(bf(0)));
  }
  
  
  @Test
  public void testFareyPrevNext() {
    final int MAX_VAL = 3;
    final int MAX_DEN = 20;
    
    //Create a set of all fractions from -MAX_VAL to +MAX_VAL, with denominator <= MAX_DEN.
    //There will be a lot of repetition but reducing to lowest terms should make HashSet
    //see them as equivalent
    Set<BigFraction> fareySet = new HashSet<BigFraction>();
    for(long d = 1; d <= MAX_DEN; d++) {
      for(long n = 0; n <= MAX_VAL*d; n++) {
        fareySet.add(bf(n, d));
        fareySet.add(bf(-n, d));
      }
    }
    
    //sort the sequence
    List<BigFraction> fareySeq = new ArrayList<BigFraction>(fareySet);
    Collections.sort(fareySeq);
    
    BigFraction last = bf(-MAX_VAL*MAX_DEN-1, MAX_DEN);
    for(BigFraction expected : fareySeq){
      BigFraction actual = last.fareyNext(MAX_DEN);
      assertEquals("(" + last.toString() + ").fareyNext(" + MAX_DEN + ")", expected.toString(), actual.toString());
      last = actual;
    }
    
    //now try in reverse to test fareyPrev
    Collections.reverse(fareySeq);
    last = bf(MAX_VAL*MAX_DEN+1, MAX_DEN);
    for(BigFraction expected : fareySeq){
      BigFraction actual = last.fareyPrev(MAX_DEN);
      assertEquals("(" + last.toString() + ").fareyPrev(" + MAX_DEN + ")", expected.toString(), actual.toString());
      last = actual;
    }
    
  }
  
  
  @Test
  public void testFareyClosest() {
    BigFraction bfPi = bf(Math.PI);
    BigFraction bfNegPi = bfPi.negate();
    
    //Test fareyClosest() by using rational approximations of pi with denominator less than 300.
    //Found these here: http://www.isi.edu/~johnh/BLOG/1999/0728_RATIONAL_PI/
    Map<Integer,String> rationalPi= new HashMap<Integer,String>();
    rationalPi.put(1, "3/1");
    rationalPi.put(4, "13/4");
    rationalPi.put(5, "16/5");
    rationalPi.put(6, "19/6");
    rationalPi.put(7, "22/7");
    rationalPi.put(57, "179/57");
    rationalPi.put(64, "201/64");
    rationalPi.put(71, "223/71");
    rationalPi.put(78, "245/78");
    rationalPi.put(85, "267/85");
    rationalPi.put(92, "289/92");
    rationalPi.put(99, "311/99");
    rationalPi.put(106, "333/106");
    rationalPi.put(113, "355/113");
    
    String expected = null;
    for(int i = 1; i < 300; i++) {
      if(rationalPi.containsKey(i))
        expected = rationalPi.get(i);
      assertEquals("PI.FareyClosest(" + i + ")", expected, bfPi.fareyClosest(i).toString());
      assertEquals("(-PI).FareyClosest(" + i + ")", "-" + expected, bfNegPi.fareyClosest(i).toString());
    }
    
    //also test with approximations of e
    BigFraction bfE = bf(Math.E);
    BigFraction bfNegE = bfE.negate();
    
    Map<Integer,String> rationalE= new HashMap<Integer,String>();
    rationalE.put(1, "3/1");
    rationalE.put(2, "5/2");
    rationalE.put(3, "8/3");
    rationalE.put(4, "11/4");
    rationalE.put(7, "19/7");
    rationalE.put(18, "49/18");
    rationalE.put(25, "68/25");
    rationalE.put(32, "87/32");
    rationalE.put(39, "106/39");
    rationalE.put(71, "193/71");
    rationalE.put(252, "685/252");
    
    expected = null;
    for(int i = 1; i < 300; i++) {
      if(rationalE.containsKey(i))
        expected = rationalE.get(i);
      assertEquals("e.FareyClosest(" + i + ")", expected, bfE.fareyClosest(i).toString());
      assertEquals("(-e).FareyClosest(" + i + ")", "-" + expected, bfNegE.fareyClosest(i).toString());
    }
    
  }
  
  
  @Test
  public void testToMixedString() {
    assertEquals("4/3 == 1 1/3", "1 1/3", BigFraction.valueOf("4/3").toMixedString());
    assertEquals("-4/3 == -1 1/3", "-1 1/3", BigFraction.valueOf("-4/3").toMixedString());
    assertEquals("6/3 == 2", BigFraction.valueOf("6/3").toMixedString(), "2");
    assertEquals("6/-3 == -2", BigFraction.valueOf("6/-3").toMixedString(), "-2");
    assertEquals("2/3 == 2/3", BigFraction.valueOf("2/3").toMixedString(), "2/3");
    assertEquals("2/-3 == -2/3", BigFraction.valueOf("2/-3").toMixedString(), "-2/3");
    assertEquals("0/3 == 0", BigFraction.valueOf("0/3").toMixedString(), "0");
    assertEquals("0/-3 == 0", BigFraction.valueOf("0/-3").toMixedString(), "0");
  }
  
  @Test
  public void testRound() {
    new RoundingTest("9.5", "10", "9", "10", "9", "10", "9", "10", "ArithmeticException").test();
    new RoundingTest("5.5", "6", "5", "6", "5", "6", "5", "6", "ArithmeticException").test();
    new RoundingTest("2.5", "3", "2", "3", "2", "3", "2", "2", "ArithmeticException").test();
    new RoundingTest("1.6", "2", "1", "2", "1", "2", "2", "2", "ArithmeticException").test();
    new RoundingTest("1.1", "2", "1", "2", "1", "1", "1", "1", "ArithmeticException").test();
    new RoundingTest("1", "1", "1", "1", "1", "1", "1", "1", "1").test();
    new RoundingTest("0.999999999999999999999999999999999999999999999999999999999999999999999999", "1", "0", "1", "0", "1", "1", "1", "ArithmeticException").test();
    new RoundingTest("0.500000000000000000000000000000000000000000000000000000000000000000000001", "1", "0", "1", "0", "1", "1", "1", "ArithmeticException").test();
    new RoundingTest("0.5", "1", "0", "1", "0", "1", "0", "0", "ArithmeticException").test();
    new RoundingTest("0.499999999999999999999999999999999999999999999999999999999999999999999999", "1", "0", "1", "0", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("0.000000000000000000000000000000000000000000000000000000000000000000000001", "1", "0", "1", "0", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("0", "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingTest("0.000000000000000000000000000000000000000000000000000000000000000000000000", "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingTest("-0.000000000000000000000000000000000000000000000000000000000000000000000001", "-1", "0", "0", "-1", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("-0.499999999999999999999999999999999999999999999999999999999999999999999999", "-1", "0", "0", "-1", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("-0.5", "-1", "0", "0", "-1", "-1", "0", "0", "ArithmeticException").test();
    new RoundingTest("-0.500000000000000000000000000000000000000000000000000000000000000000000001", "-1", "0", "0", "-1", "-1", "-1", "-1", "ArithmeticException").test();
    new RoundingTest("-0.999999999999999999999999999999999999999999999999999999999999999999999999", "-1", "0", "0", "-1", "-1", "-1", "-1", "ArithmeticException").test();
    new RoundingTest("-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1").test();
    new RoundingTest("-1.1", "-2", "-1", "-1", "-2", "-1", "-1", "-1", "ArithmeticException").test();
    new RoundingTest("-1.6", "-2", "-1", "-1", "-2", "-2", "-2", "-2", "ArithmeticException").test();
    new RoundingTest("-2.5", "-3", "-2", "-2", "-3", "-3", "-2", "-2", "ArithmeticException").test();
    new RoundingTest("-5.5", "-6", "-5", "-5", "-6", "-6", "-5", "-6", "ArithmeticException").test();
    new RoundingTest("-9.5", "-10", "-9", "-9", "-10", "-10", "-9", "-10", "ArithmeticException").test();
  }
  
  @Test
  public void testRoundToDenominator() {
    new RoundingToDenominatorTest("5.5", 1, "6", "5", "6", "5", "6", "5", "6", "ArithmeticException").test();
    new RoundingToDenominatorTest("7/15", 6, "3", "2", "3", "2", "3", "3", "3", "ArithmeticException").test();
    new RoundingToDenominatorTest("7/15", 15, "7", "7", "7", "7", "7", "7", "7", "7").test();
    new RoundingToDenominatorTest("7/15", 30, "14", "14", "14", "14", "14", "14", "14", "14").test();
    new RoundingToDenominatorTest("7/15", 43, "21", "20", "21", "20", "20", "20", "20", "ArithmeticException").test();
    new RoundingToDenominatorTest("5/4", 2, "3", "2", "3", "2", "3", "2", "2", "ArithmeticException").test();
    new RoundingToDenominatorTest("3/4", 2, "2", "1", "2", "1", "2", "1", "2", "ArithmeticException").test();
    new RoundingToDenominatorTest("1/4", 2, "1", "0", "1", "0", "1", "0", "0", "ArithmeticException").test();
    new RoundingToDenominatorTest("0", 1, "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingToDenominatorTest("0", 2, "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingToDenominatorTest("0", 3, "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingToDenominatorTest("0", 9999, "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingToDenominatorTest("-1/4", 2, "-1", "0", "0", "-1", "-1", "0", "0", "ArithmeticException").test();
    new RoundingToDenominatorTest("-3/4", 2, "-2", "-1", "-1", "-2", "-2", "-1", "-2", "ArithmeticException").test();
    new RoundingToDenominatorTest("-5/4", 2, "-3", "-2", "-2", "-3", "-3", "-2", "-2", "ArithmeticException").test();
    new RoundingToDenominatorTest("-7/15", 43, "-21", "-20", "-20", "-21", "-20", "-20", "-20", "ArithmeticException").test();
    new RoundingToDenominatorTest("-7/15", 30, "-14", "-14", "-14", "-14", "-14", "-14", "-14", "-14").test();
    new RoundingToDenominatorTest("-7/15", 15, "-7", "-7", "-7", "-7", "-7", "-7", "-7", "-7").test();
    new RoundingToDenominatorTest("-7/15", 6, "-3", "-2", "-2", "-3", "-3", "-3", "-3", "ArithmeticException").test();
    new RoundingToDenominatorTest("-5.5", 1, "-6", "-5", "-5", "-6", "-6", "-5", "-6", "ArithmeticException").test();
    
  }
  
  @Test
  public void testToDecimalString() {
    new ToDecimalStringTest("55/10", 1, "5.5", "5.5", "5.5", "5.5", "5.5", "5.5", "5.5", "5.5").test();
    new ToDecimalStringTest("555/100", 1, "5.6", "5.5", "5.6", "5.5", "5.6", "5.5", "5.6", "ArithmeticException").test();
    new ToDecimalStringTest("545/100", 1, "5.5", "5.4", "5.5", "5.4", "5.5", "5.4", "5.4", "ArithmeticException").test();
    new ToDecimalStringTest("55/10", 4, "5.5000", "5.5000", "5.5000", "5.5000", "5.5000", "5.5000", "5.5000", "5.5000").test();
    
    new ToDecimalStringTest("-55/10", 1, "-5.5", "-5.5", "-5.5", "-5.5", "-5.5", "-5.5", "-5.5", "-5.5").test();
    new ToDecimalStringTest("-555/100", 1, "-5.6", "-5.5", "-5.5", "-5.6", "-5.6", "-5.5", "-5.6", "ArithmeticException").test();
    new ToDecimalStringTest("-545/100", 1, "-5.5", "-5.4", "-5.4", "-5.5", "-5.5", "-5.4", "-5.4", "ArithmeticException").test();
    new ToDecimalStringTest("-55/10", 4, "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000").test();
    
    //3/7 = 0.428571 428571 428571 ...
    new ToDecimalStringTest("3/7", 3, "0.429", "0.428", "0.429", "0.428", "0.429", "0.429", "0.429", "ArithmeticException").test();
    new ToDecimalStringTest("3/7", 5, "0.42858", "0.42857", "0.42858", "0.42857", "0.42857", "0.42857", "0.42857", "ArithmeticException").test();
    new ToDecimalStringTest("3/7", 9, "0.428571429", "0.428571428", "0.428571429", "0.428571428", "0.428571429", "0.428571429", "0.428571429", "ArithmeticException").test();
    
    new ToDecimalStringTest("-3/7", 3, "-0.429", "-0.428", "-0.428", "-0.429", "-0.429", "-0.429", "-0.429", "ArithmeticException").test();
    new ToDecimalStringTest("-3/7", 5, "-0.42858", "-0.42857", "-0.42857", "-0.42858", "-0.42857", "-0.42857", "-0.42857", "ArithmeticException").test();
    new ToDecimalStringTest("-3/7", 9, "-0.428571429", "-0.428571428", "-0.428571428", "-0.428571429", "-0.428571429", "-0.428571429", "-0.428571429", "ArithmeticException").test();
    
    //test scenarios where the rounding causes an additional digit before the decimal
    new ToDecimalStringTest("99/10", 1, "9.9", "9.9", "9.9", "9.9", "9.9", "9.9", "9.9", "9.9").test();
    new ToDecimalStringTest("995/100", 1, "10.0", "9.9", "10.0", "9.9", "10.0", "9.9", "10.0", "ArithmeticException").test();
    
    new ToDecimalStringTest("-99/10", 1, "-9.9", "-9.9", "-9.9", "-9.9", "-9.9", "-9.9", "-9.9", "-9.9").test();
    new ToDecimalStringTest("-995/100", 1, "-10.0", "-9.9", "-9.9", "-10.0", "-10.0", "-9.9", "-10.0", "ArithmeticException").test();
    
    //test leading zeros scenario
    new ToDecimalStringTest("5/10000", 3, "0.001", "0.000", "0.001", "0.000", "0.001", "0.000", "0.000", "ArithmeticException").test();
    new ToDecimalStringTest("0/10000", 3, "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000").test();
    new ToDecimalStringTest("-5/10000", 3, "-0.001", "0.000", "0.000", "-0.001", "-0.001", "0.000", "0.000", "ArithmeticException").test();
    
  }
  
  @Test
  public void testCompareTo() {
    //Adds all positive and negative fractions with denominator less than or
    //equal to 10 and numerator less than or equal to 20 to a set. Then
    //creates a list from the set and sorts the set.
    //
    //This tests several things at once:
    // * It tests .equals() and .hashCode() by making sure that fractions that
    //   are duplicates (once reduced) don't appear in the set twice (since Set
    //   does not contain duplicates).
    // * Verifies once again that reduction works.
    // * Most importantly, tests compareTo() across positive, negative, and zero
    //   values, and across proper and improper fractions, by ensuring that
    //   sorting works.
    Set<BigFraction> set = new HashSet<BigFraction>();
    
    for(int num = 0; num <= 20; num++)
    {
      for(int den = 1; den <= 10; den++)
      {
        BigFraction bf = BigFraction.valueOf(num, den);
        set.add(bf);
        set.add(bf.negate());
      }
    }
    
    List<BigFraction> lst = new ArrayList<BigFraction>(set);
    Collections.sort(lst);
    
    String expected = "[-20/1, -19/1, -18/1, -17/1, -16/1, -15/1, -14/1, -13/1, -12/1, -11/1, -10/1"
                    + ", -19/2, -9/1, -17/2, -8/1, -15/2, -7/1, -20/3, -13/2, -19/3, -6/1, -17/3"
                    + ", -11/2, -16/3, -5/1, -19/4, -14/3, -9/2, -13/3, -17/4, -4/1, -19/5, -15/4"
                    + ", -11/3, -18/5, -7/2, -17/5, -10/3, -13/4, -16/5, -19/6, -3/1, -20/7, -17/6"
                    + ", -14/5, -11/4, -19/7, -8/3, -13/5, -18/7, -5/2, -17/7, -12/5, -19/8, -7/3"
                    + ", -16/7, -9/4, -20/9, -11/5, -13/6, -15/7, -17/8, -19/9, -2/1, -19/10, -17/9"
                    + ", -15/8, -13/7, -11/6, -9/5, -16/9, -7/4, -12/7, -17/10, -5/3, -13/8, -8/5"
                    + ", -11/7, -14/9, -3/2, -13/9, -10/7, -7/5, -11/8, -4/3, -13/10, -9/7, -5/4"
                    + ", -11/9, -6/5, -7/6, -8/7, -9/8, -10/9, -11/10, -1/1, -9/10, -8/9, -7/8, -6/7"
                    + ", -5/6, -4/5, -7/9, -3/4, -5/7, -7/10, -2/3, -5/8, -3/5, -4/7, -5/9, -1/2"
                    + ", -4/9, -3/7, -2/5, -3/8, -1/3, -3/10, -2/7, -1/4, -2/9, -1/5, -1/6, -1/7"
                    + ", -1/8, -1/9, -1/10, 0/1, 1/10, 1/9, 1/8, 1/7, 1/6, 1/5, 2/9, 1/4, 2/7, 3/10"
                    + ", 1/3, 3/8, 2/5, 3/7, 4/9, 1/2, 5/9, 4/7, 3/5, 5/8, 2/3, 7/10, 5/7, 3/4, 7/9"
                    + ", 4/5, 5/6, 6/7, 7/8, 8/9, 9/10, 1/1, 11/10, 10/9, 9/8, 8/7, 7/6, 6/5, 11/9"
                    + ", 5/4, 9/7, 13/10, 4/3, 11/8, 7/5, 10/7, 13/9, 3/2, 14/9, 11/7, 8/5, 13/8"
                    + ", 5/3, 17/10, 12/7, 7/4, 16/9, 9/5, 11/6, 13/7, 15/8, 17/9, 19/10, 2/1, 19/9"
                    + ", 17/8, 15/7, 13/6, 11/5, 20/9, 9/4, 16/7, 7/3, 19/8, 12/5, 17/7, 5/2, 18/7"
                    + ", 13/5, 8/3, 19/7, 11/4, 14/5, 17/6, 20/7, 3/1, 19/6, 16/5, 13/4, 10/3, 17/5"
                    + ", 7/2, 18/5, 11/3, 15/4, 19/5, 4/1, 17/4, 13/3, 9/2, 14/3, 19/4, 5/1, 16/3"
                    + ", 11/2, 17/3, 6/1, 19/3, 13/2, 20/3, 7/1, 15/2, 8/1, 17/2, 9/1, 19/2, 10/1"
                    + ", 11/1, 12/1, 13/1, 14/1, 15/1, 16/1, 17/1, 18/1, 19/1, 20/1]";
    
    assertEquals(expected, lst.toString());
  }
  
  @Test
  public void testNumberInterface() {
    //Same setup as testCompareTo, but after sorting test each of
    //the methods from Number interface.
    Set<BigFraction> set = new HashSet<BigFraction>();
    
    for(int num = 0; num <= 20; num++)
    {
      for(int den = 1; den <= 10; den++)
      {
        BigFraction bf = BigFraction.valueOf(num, den);
        set.add(bf);
        set.add(bf.negate());
      }
    }
    
    List<BigFraction> lst = new ArrayList<BigFraction>(set);
    Collections.sort(lst);
    
    BigFraction lastBF = lst.get(0);
    byte lastByte = lastBF.byteValue();
    short lastShort = lastBF.shortValue();
    int lastInt = lastBF.intValue();
    long lastLong = lastBF.longValue();
    float lastFloat = lastBF.floatValue();
    double lastDouble = lastBF.doubleValue();
    BigInteger lastBI = lastBF.round();
    BigDecimal lastBD = lastBF.toBigDecimal();
    
    for(int i = 1; i < lst.size(); i++)
    {
      BigFraction currBF = lst.get(i);
      byte currByte = currBF.byteValue();
      short currShort = currBF.shortValue();
      int currInt = currBF.intValue();
      long currLong = currBF.longValue();
      float currFloat = currBF.floatValue();
      double currDouble = currBF.doubleValue();
      BigInteger currBI = currBF.round();
      BigDecimal currBD = currBF.toBigDecimal();
      
      assertTrue("(byte)(" + lastBF + ") <= (byte)(" + currBF + ")", lastByte <= currByte);
      assertTrue("(short)(" + lastBF + ") <= (short)(" + currBF + ")", lastShort <= currShort);
      assertTrue("(int)(" + lastBF + ") <= (int)(" + currBF + ")", lastInt <= currInt);
      assertTrue("(long)(" + lastBF + ") <= (long)(" + currBF + ")", lastLong <= currLong);
      assertTrue("(float)(" + lastBF + ") < (float)(" + currBF + ")", lastFloat < currFloat);
      assertTrue("(double)(" + lastBF + ") < (double)(" + currBF + ")", lastDouble < currDouble);
      assertTrue("(BigInteger)(" + lastBF + ") <= (BigInteger)(" + currBF + ")", lastBI.compareTo(currBI) <= 0);
      assertTrue("(BigDecimal)(" + lastBF + ") < (BigDecimal)(" + currBF + ")", lastBD.compareTo(currBD) < 0);
      
      lastBF = currBF;
      lastByte = currByte;
      lastShort = currShort;
      lastInt = currInt;
      lastLong = currLong;
      lastFloat = currFloat;
      lastDouble = currDouble;
      lastBI = currBI;
      lastBD = currBD;
    }
  }
  
  @Test
  public void testDoubleValue() {
    final long MIN = Double.doubleToRawLongBits(Double.MIN_VALUE);
    final long MAX = Double.doubleToRawLongBits(Double.MAX_VALUE);
    
    //Selected 60k tests because that is about how many took 1 second for me.
    //Then I picked the closest prime to that number to use as the divisor
    final long NUM_TESTS = 59999L;
    final long DELTA = (MAX-MIN)/NUM_TESTS;
    
    //note i>0 check is to prevent overflow into negatives
    for(long i = MIN; i <= MAX && i > 0; i+= DELTA)
    {
      double in = Double.longBitsToDouble(i);
      double out = BigFraction.valueOf(in).doubleValue();
      assertEquals("failed to get back the same double we put in for raw bits: " + i, in, out, 0.0);
      
      //make sure we get same behavior with negative value
      double negOut = BigFraction.valueOf(-in).doubleValue();
      assertEquals("failed to get back the same double we put in with negative value for raw bits: " + i, -in, negOut, 0.0);
      
      //make sure we also test with the max value on the last iteration
      if(i < MAX && (i + DELTA > MAX || i + DELTA < 0))
        i = MAX - DELTA;
    }
  }
  
  @Test
  public void testFloatValue() {
    final long MIN = Float.floatToRawIntBits(Float.MIN_VALUE);
    final long MAX = Float.floatToRawIntBits(Float.MAX_VALUE);
    
    //Selected 60k tests because that is about how many took 1 second for me.
    //Then I picked the closest prime to that number to use as the divisor
    final long NUM_TESTS = 59999L;
    final long DELTA = (MAX-MIN)/NUM_TESTS;
    
    for(long i = MIN; i <= MAX; i+= DELTA)
    {
      float in = Float.intBitsToFloat((int)i);
      float out = BigFraction.valueOf(in).floatValue();
      assertEquals("failed to get back the same float we put in for raw bits: " + i, in, out, 0.0f);
      
      //make sure we get same behavior with negative value
      float negOut = BigFraction.valueOf(-in).floatValue();
      assertEquals("failed to get back the same float we put in with negative value for raw bits: " + i, -in, negOut, 0.0f);
      
      //make sure we also test with the max value on the last iteration
      if(i < MAX && i + DELTA > MAX)
        i = MAX - DELTA;
    }
  }
  
  @Test
  public void testLongValue() {
    assertEquals(Long.MAX_VALUE, BigFraction.valueOf(Long.MAX_VALUE).longValue());
    assertEquals(Long.MAX_VALUE-1, BigFraction.valueOf(Long.MAX_VALUE-1).longValue());
    assertEquals(Long.MAX_VALUE, BigFraction.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)).longValue());
    
    assertEquals(Long.MIN_VALUE, BigFraction.valueOf(Long.MIN_VALUE).longValue());
    assertEquals(Long.MIN_VALUE+1, BigFraction.valueOf(Long.MIN_VALUE+1).longValue());
    assertEquals(Long.MIN_VALUE, BigFraction.valueOf(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)).longValue());
    
    assertEquals(Long.MAX_VALUE, BigFraction.valueOf(Long.MAX_VALUE).longValueExact());
    assertEquals(Long.MAX_VALUE-1, BigFraction.valueOf(Long.MAX_VALUE-1).longValueExact());
    
    assertEquals(Long.MIN_VALUE, BigFraction.valueOf(Long.MIN_VALUE).longValueExact());
    assertEquals(Long.MIN_VALUE+1, BigFraction.valueOf(Long.MIN_VALUE+1).longValueExact());
  }
  
  @Test
  public void testIntValue() {
    assertEquals(Integer.MAX_VALUE, BigFraction.valueOf(Integer.MAX_VALUE).intValue());
    assertEquals(Integer.MAX_VALUE-1, BigFraction.valueOf(Integer.MAX_VALUE-1L).intValue());
    assertEquals(Integer.MAX_VALUE, BigFraction.valueOf(Integer.MAX_VALUE+1L).intValue());
    
    assertEquals(Integer.MIN_VALUE, BigFraction.valueOf(Integer.MIN_VALUE).intValue());
    assertEquals(Integer.MIN_VALUE+1, BigFraction.valueOf(Integer.MIN_VALUE+1L).intValue());
    assertEquals(Integer.MIN_VALUE, BigFraction.valueOf(Integer.MIN_VALUE-1L).intValue());
    
    assertEquals(Integer.MAX_VALUE, BigFraction.valueOf(Integer.MAX_VALUE).intValueExact());
    assertEquals(Integer.MAX_VALUE-1, BigFraction.valueOf(Integer.MAX_VALUE-1L).intValueExact());
    
    assertEquals(Integer.MIN_VALUE, BigFraction.valueOf(Integer.MIN_VALUE).intValueExact());
    assertEquals(Integer.MIN_VALUE+1, BigFraction.valueOf(Integer.MIN_VALUE+1L).intValueExact());
  }
  
  @Test
  public void testShortValue() {
    assertEquals(Short.MAX_VALUE, BigFraction.valueOf(Short.MAX_VALUE).shortValue());
    assertEquals(Short.MAX_VALUE-1, BigFraction.valueOf(Short.MAX_VALUE-1L).shortValue());
    assertEquals(Short.MAX_VALUE, BigFraction.valueOf(Short.MAX_VALUE+1L).shortValue());
    
    assertEquals(Short.MIN_VALUE, BigFraction.valueOf(Short.MIN_VALUE).shortValue());
    assertEquals(Short.MIN_VALUE+1, BigFraction.valueOf(Short.MIN_VALUE+1L).shortValue());
    assertEquals(Short.MIN_VALUE, BigFraction.valueOf(Short.MIN_VALUE-1L).shortValue());
    
    assertEquals(Short.MAX_VALUE, BigFraction.valueOf(Short.MAX_VALUE).shortValueExact());
    assertEquals(Short.MAX_VALUE-1, BigFraction.valueOf(Short.MAX_VALUE-1L).shortValueExact());
    
    assertEquals(Short.MIN_VALUE, BigFraction.valueOf(Short.MIN_VALUE).shortValueExact());
    assertEquals(Short.MIN_VALUE+1, BigFraction.valueOf(Short.MIN_VALUE+1L).shortValueExact());
  }
  
  @Test
  public void testByteValue() {
    assertEquals(Byte.MAX_VALUE, BigFraction.valueOf(Byte.MAX_VALUE).byteValue());
    assertEquals(Byte.MAX_VALUE-1, BigFraction.valueOf(Byte.MAX_VALUE-1L).byteValue());
    assertEquals(Byte.MAX_VALUE, BigFraction.valueOf(Byte.MAX_VALUE+1L).byteValue());
    
    assertEquals(Byte.MIN_VALUE, BigFraction.valueOf(Byte.MIN_VALUE).byteValue());
    assertEquals(Byte.MIN_VALUE+1, BigFraction.valueOf(Byte.MIN_VALUE+1L).byteValue());
    assertEquals(Byte.MIN_VALUE, BigFraction.valueOf(Byte.MIN_VALUE-1L).byteValue());
    
    assertEquals(Byte.MAX_VALUE, BigFraction.valueOf(Byte.MAX_VALUE).byteValueExact());
    assertEquals(Byte.MAX_VALUE-1, BigFraction.valueOf(Byte.MAX_VALUE-1L).byteValueExact());
    
    assertEquals(Byte.MIN_VALUE, BigFraction.valueOf(Byte.MIN_VALUE).byteValueExact());
    assertEquals(Byte.MIN_VALUE+1, BigFraction.valueOf(Byte.MIN_VALUE+1L).byteValueExact());
  }
  
  //exception testing
  //---------------------------------------------------------------------------
  
  @Test(expected=IllegalArgumentException.class)
  public void testNaN() {
    BigFraction.valueOf(Double.NaN);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testPositiveInfinity() {
    BigFraction.valueOf(Double.POSITIVE_INFINITY);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNegativeInfinity() {
    BigFraction.valueOf(Double.NEGATIVE_INFINITY);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator1() {
    BigFraction.valueOf(0,0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator2() {
    BigFraction.valueOf(100.9,0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator3() {
    BigFraction.valueOf("900/0");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator4() {
    BigFraction.valueOf(BigFraction.valueOf(900),BigFraction.valueOf(0,488));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero1() {
    BigFraction.valueOf(0).divide(BigFraction.valueOf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero2() {
    BigFraction.valueOf(10).divide(0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero3() {
    BigFraction.valueOf(90).divide(0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero4() {
    BigFraction.valueOf(16).divide(new BigDecimal("0"));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroReciprocal() {
    BigFraction.ZERO.reciprocal();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroNegativePow() {
    BigFraction.ZERO.pow(-3);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToDenominatorZero() {
    BigFraction.valueOf(11,17).roundToDenominator(BigInteger.ZERO);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToDenominatorNegative() {
    BigFraction.valueOf(11,17).roundToDenominator(BigInteger.valueOf(-4));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactOverflowPositive() {
    BigFraction.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactOverflowNegative() {
    BigFraction.valueOf(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactFraction() {
    BigFraction.valueOf(BigFraction.valueOf(1,2)).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactOverflowPositive() {
    BigFraction.valueOf(Integer.MAX_VALUE + 1L).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactOverflowNegative() {
    BigFraction.valueOf(Integer.MIN_VALUE - 1L).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactFraction() {
    BigFraction.valueOf(BigFraction.valueOf(1,2)).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactOverflowPositive() {
    BigFraction.valueOf(Short.MAX_VALUE + 1L).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactOverflowNegative() {
    BigFraction.valueOf(Short.MIN_VALUE - 1L).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactFraction() {
    BigFraction.valueOf(BigFraction.valueOf(1,2)).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactOverflowPositive() {
    BigFraction.valueOf(Integer.MAX_VALUE + 1L).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactOverflowNegative() {
    BigFraction.valueOf(Integer.MIN_VALUE - 1L).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactFraction() {
    BigFraction.valueOf(BigFraction.valueOf(1,2)).byteValueExact();
  }
  
  /**
   * Helper class to reduce repetitive typing of tests for rounding. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for that rounding method.
   */
  private static class RoundingTest
  {
    private final String input;
    private final BigFraction bf;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public RoundingTest(String input, String up, String down, String ceiling, String floor,
            String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      bf = BigFraction.valueOf(input);
      expected.put(RoundingMode.UP, up);
      expected.put(RoundingMode.DOWN, down);
      expected.put(RoundingMode.CEILING, ceiling);
      expected.put(RoundingMode.FLOOR, floor);
      expected.put(RoundingMode.HALF_UP, halfUp);
      expected.put(RoundingMode.HALF_DOWN, halfDown);
      expected.put(RoundingMode.HALF_EVEN, halfEven);
      expected.put(RoundingMode.UNNECESSARY, unnecessary);
    }
    
    public void test()
    {
      for(Map.Entry<RoundingMode, String> entry : expected.entrySet())
      {
        String actual;
        try {
          actual = bf.round(entry.getKey()).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("round(" + input + ", " + entry.getKey() + ")", entry.getValue(), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ")", expected.get(RoundingMode.HALF_UP), bf.round().toString());
    }
  }
  
  
  /**
   * Helper class to reduce repetitive typing of tests for roundToDenominator. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for that rounding method.
   */
  private static class RoundingToDenominatorTest
  {
    private final String input;
    private final BigFraction bf;
    private final BigInteger denominator;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public RoundingToDenominatorTest(String input, long denominator, String up, String down, String ceiling, String floor,
            String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      bf = BigFraction.valueOf(input);
      this.denominator = BigInteger.valueOf(denominator);
      expected.put(RoundingMode.UP, up);
      expected.put(RoundingMode.DOWN, down);
      expected.put(RoundingMode.CEILING, ceiling);
      expected.put(RoundingMode.FLOOR, floor);
      expected.put(RoundingMode.HALF_UP, halfUp);
      expected.put(RoundingMode.HALF_DOWN, halfDown);
      expected.put(RoundingMode.HALF_EVEN, halfEven);
      expected.put(RoundingMode.UNNECESSARY, unnecessary);
    }
    
    public void test()
    {
      for(Map.Entry<RoundingMode, String> entry : expected.entrySet())
      {
        String actual;
        try {
          actual = bf.roundToDenominator(denominator, entry.getKey()).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("roundToDenominator(" + input + ", " + denominator + ", " + entry.getKey() + ")", entry.getValue(), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ", " + denominator + ")", expected.get(RoundingMode.HALF_UP), bf.roundToDenominator(denominator).toString());
    }
  }
  
  /**
   * Helper class to reduce repetitive typing of tests for toDecimalString. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for that rounding method.
   */
  private static class ToDecimalStringTest
  {
    private final String input;
    private final BigFraction bf;
    private final int digits;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public ToDecimalStringTest(String input, int digits, String up, String down, String ceiling, String floor,
            String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      bf = BigFraction.valueOf(input);
      this.digits = digits;
      expected.put(RoundingMode.UP, up);
      expected.put(RoundingMode.DOWN, down);
      expected.put(RoundingMode.CEILING, ceiling);
      expected.put(RoundingMode.FLOOR, floor);
      expected.put(RoundingMode.HALF_UP, halfUp);
      expected.put(RoundingMode.HALF_DOWN, halfDown);
      expected.put(RoundingMode.HALF_EVEN, halfEven);
      expected.put(RoundingMode.UNNECESSARY, unnecessary);
    }
    
    public void test()
    {
      for(Map.Entry<RoundingMode, String> entry : expected.entrySet())
      {
        String actual;
        try {
          actual = bf.toDecimalString(digits, entry.getKey()).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("toDecimalString(" + input + ", " + digits + ", " + entry.getKey() + ")", entry.getValue(), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ", " + digits + ")", expected.get(RoundingMode.HALF_UP), bf.toDecimalString(digits).toString());
    }
  }
  
  //helper functions to save typing...
  private BigFraction bf(Number n) { return BigFraction.valueOf(n); }
  private BigFraction bf(Number n, Number d) { return BigFraction.valueOf(n, d); }
  private BigFraction bf(String s) { return BigFraction.valueOf(s); }
}
