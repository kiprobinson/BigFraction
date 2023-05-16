package com.github.kiprobinson.bigfraction;

import static org.junit.Assert.*;

import java.math.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kiprobinson.bigfraction.util.DoubleUtil;
import com.github.kiprobinson.bigfraction.util.FloatUtil;

import org.junit.Test;


/**
 * JUnit tests for LongFraction class.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
 */
public class LongFractionTest {
  
  @Test
  public void testValueOf() {
    assertEquals("valueOf(1.1)", "2476979795053773/2251799813685248", LongFraction.valueOf(1.1).toString());
    assertEquals("valueOf(-0.0)", "0/1", LongFraction.valueOf(-0.0).toString());
    assertEquals("valueOf(1.1f)", "9227469/8388608", LongFraction.valueOf(1.1f).toString());
    assertEquals("valueOf(\"1.1\")", "11/10", LongFraction.valueOf("1.1").toString());
    assertEquals("valueOf(11,10)", "11/10", LongFraction.valueOf(11,10).toString());
    assertEquals("valueOf(2*5*7, 3*5*11)", "14/33", LongFraction.valueOf(2*5*7,3*5*11).toString());
    assertEquals("valueOf(100,-7)", "-100/7", LongFraction.valueOf(100,-7).toString());
    assertEquals("valueOf(\"-1.0E2/-0.007E3\")", "100/7", LongFraction.valueOf("-1.0E2/-0.007E3").toString());
    assertEquals("valueOf(\"+9.02E-10\")", "451/500000000000", LongFraction.valueOf("+9.02E-10").toString());
    assertEquals("valueOf(\"-0.000000E+500\")", "0/1", LongFraction.valueOf("-0.000000E+500").toString());
    assertEquals("valueOf(0,19)", "0/1", LongFraction.valueOf(0,19).toString());
    assertEquals("valueOf(0.0,19.0)", "0/1", LongFraction.valueOf(0.0,19.0).toString());
    assertEquals("valueOf(\"dead/BEEF\", 16)", "57005/48879", LongFraction.valueOf("dead/BEEF", 16).toString());
    assertEquals("valueOf(\"lAzY.fOx\", 36)", "15459161339/15552", LongFraction.valueOf("lAzY.fOx", 36).toString());
    
    //invalid radix must convert convert to 10
    assertEquals("valueOf(\"13/5\", -1)", "13/5", LongFraction.valueOf("13/5", -1).toString());
    assertEquals("valueOf(\"13/5\", 0)", "13/5", LongFraction.valueOf("13/5", 0).toString());
    assertEquals("valueOf(\"13/5\", 1)", "13/5", LongFraction.valueOf("13/5", 1).toString());
    assertEquals("valueOf(\"13/5\", 37)", "13/5", LongFraction.valueOf("13/5", 37).toString());
    
    assertEquals("10/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(10), 0)).toString());
    assertEquals("10/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(1), -1)).toString());
    assertEquals("10/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(100), 1)).toString());
    
    assertEquals("123000/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), -3)).toString());
    assertEquals("12300/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), -2)).toString());
    assertEquals("1230/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), -1)).toString());
    assertEquals("123/1", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 0)).toString());
    assertEquals("123/10", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 1)).toString());
    assertEquals("123/100", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 2)).toString());
    assertEquals("123/1000", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(123), 3)).toString());
    
    assertEquals("123/1", LongFraction.valueOf(1.23e2).toString());
    assertEquals("1230/1", LongFraction.valueOf(1.23e3).toString());
    assertEquals("12300/1", LongFraction.valueOf(1.23e4).toString());
    
    assertEquals("58/25", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("25/58", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("-58/25", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(-348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("-25/58", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(-15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("4/1", LongFraction.valueOf(14, new BigDecimal(BigInteger.valueOf(3500), 3)).toString());
    assertEquals("-1/4", LongFraction.valueOf(new BigDecimal(BigInteger.valueOf(3500), 3), -14).toString());
    
    //some doubles which should have simple, exact representations
    assertEquals("1/2", LongFraction.valueOf(0.5).toString());
    assertEquals("1/4", LongFraction.valueOf(0.25).toString());
    assertEquals("5/8", LongFraction.valueOf(0.625).toString());
    assertEquals("-3/2", LongFraction.valueOf(-1.5).toString());
    assertEquals("-9/4", LongFraction.valueOf(-2.25).toString());
    assertEquals("-29/8", LongFraction.valueOf(-3.625).toString());
    
    assertEquals("-2/9", LongFraction.valueOf(0.5, -2.25).toString());
    assertEquals("-2/9", LongFraction.valueOf(-2.0, 9.0).toString());
    
    assertEquals("valueOf(4.5, 0.625)", "36/5", LongFraction.valueOf(4.5, 0.625).toString());
    assertEquals("valueOf(0.625, 4.5)", "5/36", LongFraction.valueOf(0.625, 4.5).toString());
    assertEquals("valueOf(4.5, -0.625)", "-36/5", LongFraction.valueOf(4.5, -0.625).toString());
    assertEquals("valueOf(0.625, -4.5)", "-5/36", LongFraction.valueOf(0.625, -4.5).toString());
    
    //BigFraction -> LongFraction
    assertEquals("0/1", LongFraction.valueOf(BigFraction.ZERO).toString());
    assertEquals("1/1", LongFraction.valueOf(BigFraction.ONE).toString());
    assertEquals("-9223372036854775808/1", LongFraction.valueOf(BigFraction.valueOf(Long.MIN_VALUE)).toString());
    assertEquals("1/9223372036854775807", LongFraction.valueOf(1, BigFraction.valueOf(Long.MAX_VALUE)).toString());
  }
  
  @Test
  public void testValueOf_Repeating() {
    assertEquals("valueOf( \"0.(4)\")",  "4/9", LongFraction.valueOf( "0.(4)").toString());
    assertEquals("valueOf(\"+0.(4)\")",  "4/9", LongFraction.valueOf("+0.(4)").toString());
    assertEquals("valueOf(\"-0.(4)\")", "-4/9", LongFraction.valueOf("-0.(4)").toString());
    assertEquals("valueOf(  \".(4)\")",  "4/9", LongFraction.valueOf(  ".(4)").toString());
    assertEquals("valueOf( \"+.(4)\")",  "4/9", LongFraction.valueOf( "+.(4)").toString());
    assertEquals("valueOf( \"-.(4)\")", "-4/9", LongFraction.valueOf( "-.(4)").toString());
    
    assertEquals("valueOf( \"0.0(4)\")",  "2/45", LongFraction.valueOf( "0.0(4)").toString());
    assertEquals("valueOf(\"+0.0(4)\")",  "2/45", LongFraction.valueOf("+0.0(4)").toString());
    assertEquals("valueOf(\"-0.0(4)\")", "-2/45", LongFraction.valueOf("-0.0(4)").toString());
    assertEquals("valueOf(  \".0(4)\")",  "2/45", LongFraction.valueOf(  ".0(4)").toString());
    assertEquals("valueOf( \"+.0(4)\")",  "2/45", LongFraction.valueOf( "+.0(4)").toString());
    assertEquals("valueOf( \"-.0(4)\")", "-2/45", LongFraction.valueOf( "-.0(4)").toString());
    
    assertEquals("valueOf(\"0.444(4)\")", "4/9", LongFraction.valueOf("0.444(4)").toString());
    assertEquals("valueOf(\"0.4(444)\")", "4/9", LongFraction.valueOf("0.4(444)").toString());
    assertEquals("valueOf(\"0.044(4)\")", "2/45", LongFraction.valueOf("0.044(4)").toString());
    assertEquals("valueOf(\"0.0(444)\")", "2/45", LongFraction.valueOf("0.0(444)").toString());
    
    assertEquals("valueOf(\"0.(56)\")", "56/99", LongFraction.valueOf("0.(56)").toString());
    assertEquals("valueOf(\"0.5(65)\")", "56/99", LongFraction.valueOf("0.5(65)").toString());
    assertEquals("valueOf(\"0.56(56)\")", "56/99", LongFraction.valueOf("0.56(56)").toString());
    assertEquals("valueOf(\"0.565(6565)\")", "56/99", LongFraction.valueOf("0.565(6565)").toString());
    
    assertEquals("valueOf(\"0.(012)\")", "4/333", LongFraction.valueOf("0.(012)").toString());
    assertEquals("valueOf(\"0.(9)\")", "1/1", LongFraction.valueOf("0.(9)").toString());
    assertEquals("valueOf(\"0.000(4)\")", "1/2250", LongFraction.valueOf("0.000(4)").toString());
    assertEquals("valueOf(\"0.000(9)\")", "1/1000", LongFraction.valueOf("0.000(9)").toString());
    assertEquals("valueOf(\"0.000(120)\")", "1/8325", LongFraction.valueOf("0.000(120)").toString());
    assertEquals("valueOf(\"1.23(4)\")", "1111/900", LongFraction.valueOf("1.23(4)").toString());
    assertEquals("valueOf(\"0.3(789)\")", "631/1665", LongFraction.valueOf("0.3(789)").toString());
    
    assertEquals("valueOf(\"0.(012)/1.23(4)\")", "400/41107", LongFraction.valueOf("0.(012)/1.23(4)").toString());
    assertEquals("valueOf(\"0.(012)/1.6e3\")", "1/133200", LongFraction.valueOf("0.(012)/1.6e3").toString());
    
    assertEquals("valueOf(\"7.000(00)\")", "7/1", LongFraction.valueOf("7.000(00)").toString());
    assertEquals("valueOf(\"000.00(00)\")", "0/1", LongFraction.valueOf("000.00(00)").toString());
    
    //different bases
    assertEquals("valueOf(\"0.0(0011)\", 2)", "1/10", LongFraction.valueOf("0.0(0011)", 2).toString());
    assertEquals("valueOf(\"0.1(9))\", 16)", "1/10", LongFraction.valueOf("0.1(9)", 16).toString());
    assertEquals("valueOf(\"12.(a9))\", 11)", "1679/120", LongFraction.valueOf("12.(a9)", 11).toString());
    assertEquals("valueOf(\"-a.(i))\", 19)", "-11/1", LongFraction.valueOf("-a.(i)", 19).toString());
    assertEquals("valueOf(\"the.lazy(fox)\", 36)", "2994276908470787/78362484480", LongFraction.valueOf("the.lazy(fox)", 36).toString());
  }
  
  @Test
  public void testValueOf_CustomNumberInterface() {
    assertEquals("Custom Number representing an integer", "123456/1", lf(new CustomNumber(123456.0)).toString());
    assertEquals("Custom Number representing a floating-point number", "987653/8", lf(new CustomNumber(123456.625)).toString());
    assertEquals("Custom Number representing a negative number", "-1/8", lf(new CustomNumber(-0.125)).toString());
    assertEquals("Custom Number representing zero", "0/1", lf(new CustomNumber(0)).toString());
  }
  
  
  @Test
  public void testConstructor() {
    assertEquals("constructor(1.1)", "2476979795053773/2251799813685248", new LongFraction(1.1).toString());
    assertEquals("constructor(-0.0)", "0/1", new LongFraction(-0.0).toString());
    assertEquals("constructor(1.1f)", "9227469/8388608", new LongFraction(1.1f).toString());
    assertEquals("constructor(\"1.1\")", "11/10", new LongFraction("1.1").toString());
    assertEquals("constructor(11,10)", "11/10", new LongFraction(11,10).toString());
    assertEquals("constructor(2*5*7, 3*5*11)", "14/33", new LongFraction(2*5*7,3*5*11).toString());
    assertEquals("constructor(100,-7)", "-100/7", new LongFraction(100,-7).toString());
    assertEquals("constructor(\"-1.0E2/-0.007E3\")", "100/7", new LongFraction("-1.0E2/-0.007E3").toString());
    assertEquals("constructor(\"+9.02E-10\")", "451/500000000000", new LongFraction("+9.02E-10").toString());
    assertEquals("constructor(\"-0.000000E+500\")", "0/1", new LongFraction("-0.000000E+500").toString());
    assertEquals("constructor(0,19)", "0/1", new LongFraction(0,19).toString());
    assertEquals("valueOf(0.0,19.0)", "0/1", new LongFraction(0.0,19.0).toString());
    assertEquals("constructor(\"dead/BEEF\", 16)", "57005/48879", new LongFraction("dead/BEEF", 16).toString());
    assertEquals("constructor(\"lAzY.fOx\", 36)", "15459161339/15552", new LongFraction("lAzY.fOx", 36).toString());
    
    //invalid radix must convert convert to 10
    assertEquals("constructor(\"13/5\", -1)", "13/5", new LongFraction("13/5", -1).toString());
    assertEquals("constructor(\"13/5\", 0)", "13/5", new LongFraction("13/5", 0).toString());
    assertEquals("constructor(\"13/5\", 1)", "13/5", new LongFraction("13/5", 1).toString());
    assertEquals("constructor(\"13/5\", 37)", "13/5", new LongFraction("13/5", 37).toString());
    
    assertEquals("10/1", new LongFraction(new BigDecimal(BigInteger.valueOf(10), 0)).toString());
    assertEquals("10/1", new LongFraction(new BigDecimal(BigInteger.valueOf(1), -1)).toString());
    assertEquals("10/1", new LongFraction(new BigDecimal(BigInteger.valueOf(100), 1)).toString());
    
    assertEquals("123000/1", new LongFraction(new BigDecimal(BigInteger.valueOf(123), -3)).toString());
    assertEquals("12300/1", new LongFraction(new BigDecimal(BigInteger.valueOf(123), -2)).toString());
    assertEquals("1230/1", new LongFraction(new BigDecimal(BigInteger.valueOf(123), -1)).toString());
    assertEquals("123/1", new LongFraction(new BigDecimal(BigInteger.valueOf(123), 0)).toString());
    assertEquals("123/10", new LongFraction(new BigDecimal(BigInteger.valueOf(123), 1)).toString());
    assertEquals("123/100", new LongFraction(new BigDecimal(BigInteger.valueOf(123), 2)).toString());
    assertEquals("123/1000", new LongFraction(new BigDecimal(BigInteger.valueOf(123), 3)).toString());
    
    assertEquals("123/1", new LongFraction(1.23e2).toString());
    assertEquals("1230/1", new LongFraction(1.23e3).toString());
    assertEquals("12300/1", new LongFraction(1.23e4).toString());
    
    assertEquals("58/25", new LongFraction(new BigDecimal(BigInteger.valueOf(348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("25/58", new LongFraction(new BigDecimal(BigInteger.valueOf(15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("-58/25", new LongFraction(new BigDecimal(BigInteger.valueOf(-348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("-25/58", new LongFraction(new BigDecimal(BigInteger.valueOf(-15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("4/1", new LongFraction(14, new BigDecimal(BigInteger.valueOf(3500), 3)).toString());
    assertEquals("-1/4", new LongFraction(new BigDecimal(BigInteger.valueOf(3500), 3), -14).toString());
    
    //some doubles which should have simple, exact representations
    assertEquals("1/2", new LongFraction(0.5).toString());
    assertEquals("1/4", new LongFraction(0.25).toString());
    assertEquals("5/8", new LongFraction(0.625).toString());
    assertEquals("-3/2", new LongFraction(-1.5).toString());
    assertEquals("-9/4", new LongFraction(-2.25).toString());
    assertEquals("-29/8", new LongFraction(-3.625).toString());
    
    assertEquals("-2/9", new LongFraction(0.5, -2.25).toString());
    assertEquals("-2/9", new LongFraction(-2.0, 9.0).toString());
    
    assertEquals("valueOf(4.5, 0.625)", "36/5", new LongFraction(4.5, 0.625).toString());
    assertEquals("valueOf(0.625, 4.5)", "5/36", new LongFraction(0.625, 4.5).toString());
    assertEquals("valueOf(4.5, -0.625)", "-36/5", new LongFraction(4.5, -0.625).toString());
    assertEquals("valueOf(0.625, -4.5)", "-5/36", new LongFraction(0.625, -4.5).toString());
  }
  
  @Test
  public void testConstructor_Repeating() {
    assertEquals("constructor( \"0.(4)\")",  "4/9", new LongFraction( "0.(4)").toString());
    assertEquals("constructor(\"+0.(4)\")",  "4/9", new LongFraction("+0.(4)").toString());
    assertEquals("constructor(\"-0.(4)\")", "-4/9", new LongFraction("-0.(4)").toString());
    assertEquals("constructor(  \".(4)\")",  "4/9", new LongFraction(  ".(4)").toString());
    assertEquals("constructor( \"+.(4)\")",  "4/9", new LongFraction( "+.(4)").toString());
    assertEquals("constructor( \"-.(4)\")", "-4/9", new LongFraction( "-.(4)").toString());
    
    assertEquals("constructor( \"0.0(4)\")",  "2/45", new LongFraction( "0.0(4)").toString());
    assertEquals("constructor(\"+0.0(4)\")",  "2/45", new LongFraction("+0.0(4)").toString());
    assertEquals("constructor(\"-0.0(4)\")", "-2/45", new LongFraction("-0.0(4)").toString());
    assertEquals("constructor(  \".0(4)\")",  "2/45", new LongFraction(  ".0(4)").toString());
    assertEquals("constructor( \"+.0(4)\")",  "2/45", new LongFraction( "+.0(4)").toString());
    assertEquals("constructor( \"-.0(4)\")", "-2/45", new LongFraction( "-.0(4)").toString());
    
    assertEquals("constructor(\"0.444(4)\")", "4/9", new LongFraction("0.444(4)").toString());
    assertEquals("constructor(\"0.4(444)\")", "4/9", new LongFraction("0.4(444)").toString());
    assertEquals("constructor(\"0.044(4)\")", "2/45", new LongFraction("0.044(4)").toString());
    assertEquals("constructor(\"0.0(444)\")", "2/45", new LongFraction("0.0(444)").toString());
    
    assertEquals("constructor(\"0.(56)\")", "56/99", new LongFraction("0.(56)").toString());
    assertEquals("constructor(\"0.5(65)\")", "56/99", new LongFraction("0.5(65)").toString());
    assertEquals("constructor(\"0.56(56)\")", "56/99", new LongFraction("0.56(56)").toString());
    assertEquals("constructor(\"0.565(6565)\")", "56/99", new LongFraction("0.565(6565)").toString());
    
    assertEquals("constructor(\"0.(012)\")", "4/333", new LongFraction("0.(012)").toString());
    assertEquals("constructor(\"0.(9)\")", "1/1", new LongFraction("0.(9)").toString());
    assertEquals("constructor(\"0.000(4)\")", "1/2250", new LongFraction("0.000(4)").toString());
    assertEquals("constructor(\"0.000(9)\")", "1/1000", new LongFraction("0.000(9)").toString());
    assertEquals("constructor(\"0.000(120)\")", "1/8325", new LongFraction("0.000(120)").toString());
    assertEquals("constructor(\"1.23(4)\")", "1111/900", new LongFraction("1.23(4)").toString());
    assertEquals("constructor(\"0.3(789)\")", "631/1665", new LongFraction("0.3(789)").toString());
    
    assertEquals("constructor(\"0.(012)/1.23(4)\")", "400/41107", new LongFraction("0.(012)/1.23(4)").toString());
    assertEquals("constructor(\"0.(012)/1.6e3\")", "1/133200", new LongFraction("0.(012)/1.6e3").toString());
    
    assertEquals("constructor(\"7.000(00)\")", "7/1", new LongFraction("7.000(00)").toString());
    assertEquals("constructor(\"000.00(00)\")", "0/1", new LongFraction("000.00(00)").toString());
    
    //different bases
    assertEquals("constructor(\"0.0(0011)\", 2)", "1/10", new LongFraction("0.0(0011)", 2).toString());
    assertEquals("constructor(\"0.1(9))\", 16)", "1/10", new LongFraction("0.1(9)", 16).toString());
    assertEquals("constructor(\"12.(a9))\", 11)", "1679/120", new LongFraction("12.(a9)", 11).toString());
    assertEquals("constructor(\"-a.(i))\", 19)", "-11/1", new LongFraction("-a.(i)", 19).toString());
    assertEquals("constructor(\"the.lazy(fox)\", 36)", "2994276908470787/78362484480", new LongFraction("the.lazy(fox)", 36).toString());
  }
  
  @Test
  public void testConstructor_CustomNumberInterface() {
    assertEquals("Custom Number representing an integer", "123456/1", new LongFraction(new CustomNumber(123456.0)).toString());
    assertEquals("Custom Number representing a floating-point number", "987653/8", new LongFraction(new CustomNumber(123456.625)).toString());
    assertEquals("Custom Number representing a negative number", "-1/8", new LongFraction(new CustomNumber(-0.125)).toString());
    assertEquals("Custom Number representing zero", "0/1", new LongFraction(new CustomNumber(0)).toString());
  }
  
  
  @Test
  public void testAdd() {
    assertEquals("5/1 + -3", "2/1", lf(5).add(-3).toString());
    assertEquals("11/17 + 2/3", "67/51", lf("11/17").add(lf("2/3")).toString());
    assertEquals("1/6 + 1/15", "7/30", lf("1/6").add(lf("1/15")).toString());
    assertEquals("1/6 + 1/6", "1/3", lf("1/6").add(lf("1/6")).toString());
    assertEquals("-1/6 + 1/6", "0/1", lf("-1/6").add(lf("1/6")).toString());
    assertEquals("-1/6 + -1/6", "-1/3", lf("-1/6").add(lf("-1/6")).toString());
    assertEquals("-1/6 + 1/15", "-1/10", lf("-1/6").add(lf("1/15")).toString());
    assertEquals("1/7 + 0", "1/7", lf("1/7").add(0).toString());
    assertEquals("-1/7 + 0", "-1/7", lf("-1/7").add(0.0).toString());
  }
  
  @Test
  public void testSum() {
    assertEquals("2/1", LongFraction.sum(5, -3).toString());
    assertEquals("13/4", LongFraction.sum(6.5, -3.25f).toString());
    assertEquals("9937/100", LongFraction.sum(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testSubtract() {
    assertEquals("5/1 - -3/1", "8/1", lf(5).subtract(-3).toString());
    assertEquals("-5/1 - 2/1", "-7/1", lf(-5).subtract(2).toString());
    assertEquals("-5/1 - -2/1", "-3/1", lf(-5).subtract(-2).toString());
    assertEquals("11/17 - 2/3", "-1/51", lf("11/17").subtract(lf("2/3")).toString());
    assertEquals("1/6 - 1/6", "0/1", lf("1/6").subtract(lf("1/6")).toString());
    assertEquals("-1/6 - 1/6", "-1/3", lf("-1/6").subtract(lf("1/6")).toString());
    assertEquals("1/7 + 0", "1/7", lf("1/7").subtract(0).toString());
    assertEquals("-1/7 + 0", "-1/7", lf("-1/7").subtract(0.0).toString());
  }
  
  @Test
  public void testSubtractFrom() {
    assertEquals("-3/1 - 5/1", "-8/1", lf(5).subtractFrom(-3).toString());
    assertEquals("2/1 - -5/1", "7/1", lf(-5).subtractFrom(2).toString());
    assertEquals("-2/1 - -5/1", "3/1", lf(-5).subtractFrom(-2).toString());
    assertEquals("2/3 - 11/17", "1/51", lf("11/17").subtractFrom(lf("2/3")).toString());
    assertEquals("1/6 - 1/6", "0/1", lf("1/6").subtractFrom(lf("1/6")).toString());
    assertEquals("1/6 - -1/6", "1/3", lf("-1/6").subtractFrom(lf("1/6")).toString());
    assertEquals("1/7 + 0", "1/7", lf("-1/7").subtractFrom(0).toString());
    assertEquals("-1/7 + 0", "-1/7", lf("1/7").subtractFrom(0.0).toString());
  }
  
  @Test
  public void testDifference() {
    assertEquals("8/1", LongFraction.difference(5, -3).toString());
    assertEquals("39/4", LongFraction.difference(6.5, -3.25f).toString());
    assertEquals("3263/100", LongFraction.difference(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testMultiply() {
    assertEquals("(11/17)(0/1)", "0/1", lf("11/17").multiply(-0.0).toString());
    assertEquals("(1/3)(3/4)", "1/4", lf("1/3").multiply(lf("3/4")).toString());
    assertEquals("(-1/12)(16/5)", "-4/15", lf("-1/12").multiply(lf("16/5")).toString());
    assertEquals("(-7/6)(-5/9)", "35/54", lf("-7/6").multiply(lf("-5/9")).toString());
    assertEquals("(4/5)(-7/2)", "-14/5", lf("4/5").multiply(lf("7/-2")).toString());
    assertEquals("(1/7)(0)", "0/1", lf("1/7").multiply(0).toString());
    assertEquals("(-1/7)(0)", "0/1", lf("-1/7").multiply(0.0).toString());
    assertEquals("(1/7)(1)", "1/7", lf("1/7").multiply(1).toString());
    assertEquals("(-1/7)(1)", "-1/7", lf("-1/7").multiply(1.0).toString());
  }
  
  @Test
  public void testProduct() {
    assertEquals("-15/1", LongFraction.product(5, -3).toString());
    assertEquals("-169/8", LongFraction.product(6.5, -3.25f).toString());
    assertEquals("110121/50", LongFraction.product(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testDivide() {
    assertEquals("(1/3)/(4/3)", "1/4", lf("1/3").divide(lf("4/3")).toString());
    assertEquals("(-1/12)/(5/16)", "-4/15", lf("-1/12").divide(lf("5/16")).toString());
    assertEquals("(-7/6)/(-9/5)", "35/54", lf("-7/6").divide(lf("9/-5")).toString());
    assertEquals("(4/5)/(-2/7)", "-14/5", lf("4/5").divide(lf("-2/7")).toString());
    assertEquals("(1/7)/(1)", "1/7", lf("1/7").divide(1).toString());
    assertEquals("(-1/7)/(1)", "-1/7", lf("-1/7").divide(1.0).toString());
  }
  
  @Test
  public void testDivideInto() {
    assertEquals("(4/3)/(1/3)", "4/1", lf("1/3").divideInto(lf("4/3")).toString());
    assertEquals("(5/16)/(-1/12)", "-15/4", lf("-1/12").divideInto(lf("5/16")).toString());
    assertEquals("(-9/5)/(-7/6)", "54/35", lf("-7/6").divideInto(lf("9/-5")).toString());
    assertEquals("(-2/7)/(4/5)", "-5/14", lf("4/5").divideInto(lf("-2/7")).toString());
    assertEquals("(0)/(1/7)", "0/1", lf("1/7").divideInto(0).toString());
    assertEquals("(0)/(-1/7)", "0/1", lf("-1/7").divideInto(0.0).toString());
    assertEquals("(1)/(1/7)", "1/7", lf("7/1").divideInto(1).toString());
    assertEquals("(1)/(-1/7)", "-1/7", lf("-7/1").divideInto(1.0).toString());
  }
  
  @Test
  public void testQuotient() {
    assertEquals("-5/3", LongFraction.quotient(5, -3).toString());
    assertEquals("-2/1", LongFraction.quotient(6.5, -3.25f).toString());
    assertEquals("6600/3337", LongFraction.quotient(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testDivideAndRemainder() {
    new DivideAndRemainderTest("0",  "999", "0", "0/1", "0", "0/1", "0", "0/1").test();
    new DivideAndRemainderTest("0", "-999", "0", "0/1", "0", "0/1", "0", "0/1").test();
    
    new DivideAndRemainderTest( "4/3",  "1",  "1",  "1/3",  "1",  "1/3",  "1", "1/3").test();
    new DivideAndRemainderTest("-4/3",  "1", "-1", "-1/3", "-2",  "2/3", "-2", "2/3").test();
    new DivideAndRemainderTest( "4/3", "-1", "-1",  "1/3", "-2", "-2/3", "-1", "1/3").test();
    new DivideAndRemainderTest("-4/3", "-1",  "1", "-1/3",  "1", "-1/3",  "2", "2/3").test();
    
    new DivideAndRemainderTest( "8/3",  "1",  "2",  "2/3",  "2",  "2/3",  "2", "2/3").test();
    new DivideAndRemainderTest("-8/3",  "1", "-2", "-2/3", "-3",  "1/3", "-3", "1/3").test();
    new DivideAndRemainderTest( "8/3", "-1", "-2",  "2/3", "-3", "-1/3", "-2", "2/3").test();
    new DivideAndRemainderTest("-8/3", "-1",  "2", "-2/3",  "2", "-2/3",  "3", "1/3").test();
    
    new DivideAndRemainderTest( "4/3",  "4/3",  "1", "0/1",  "1", "0/1",  "1", "0/1").test();
    new DivideAndRemainderTest("-4/3",  "4/3", "-1", "0/1", "-1", "0/1", "-1", "0/1").test();
    new DivideAndRemainderTest( "4/3", "-4/3", "-1", "0/1", "-1", "0/1", "-1", "0/1").test();
    new DivideAndRemainderTest("-4/3", "-4/3",  "1", "0/1",  "1", "0/1",  "1", "0/1").test();
    
    new DivideAndRemainderTest( "4/3",  "1/3",  "4", "0/1",  "4", "0/1",  "4", "0/1").test();
    new DivideAndRemainderTest("-4/3",  "1/3", "-4", "0/1", "-4", "0/1", "-4", "0/1").test();
    new DivideAndRemainderTest( "4/3", "-1/3", "-4", "0/1", "-4", "0/1", "-4", "0/1").test();
    new DivideAndRemainderTest("-4/3", "-1/3",  "4", "0/1",  "4", "0/1",  "4", "0/1").test();
    
    new DivideAndRemainderTest( "5/4",  "1/2",  "2",  "1/4",  "2",  "1/4",  "2", "1/4").test();
    new DivideAndRemainderTest("-5/4",  "1/2", "-2", "-1/4", "-3",  "1/4", "-3", "1/4").test();
    new DivideAndRemainderTest( "5/4", "-1/2", "-2",  "1/4", "-3", "-1/4", "-2", "1/4").test();
    new DivideAndRemainderTest("-5/4", "-1/2",  "2", "-1/4",  "2", "-1/4",  "3", "1/4").test();
    
    new DivideAndRemainderTest( "5/3",  "7/11",  "2",  "13/33",  "2",  "13/33",  "2", "13/33").test();
    new DivideAndRemainderTest("-5/3",  "7/11", "-2", "-13/33", "-3",   "8/33", "-3",  "8/33").test();
    new DivideAndRemainderTest( "5/3", "-7/11", "-2",  "13/33", "-3",  "-8/33", "-2", "13/33").test();
    new DivideAndRemainderTest("-5/3", "-7/11",  "2", "-13/33",  "2", "-13/33",  "3",  "8/33").test();
    
    new DivideAndRemainderTest( "7/11",  "13/5", "0",  "7/11",  "0",    "7/11",  "0",   "7/11").test();
    new DivideAndRemainderTest("-7/11",  "13/5", "0", "-7/11", "-1",  "108/55", "-1", "108/55").test();
    new DivideAndRemainderTest( "7/11", "-13/5", "0",  "7/11", "-1", "-108/55",  "0",   "7/11").test();
    new DivideAndRemainderTest("-7/11", "-13/5", "0", "-7/11",  "0",   "-7/11",  "1", "108/55").test();
    
    new DivideAndRemainderTest( "16/9",  "4/3",  "1",  "4/9",  "1",  "4/9",  "1", "4/9").test();
    new DivideAndRemainderTest("-16/9",  "4/3", "-1", "-4/9", "-2",  "8/9", "-2", "8/9").test();
    new DivideAndRemainderTest( "16/9", "-4/3", "-1",  "4/9", "-2", "-8/9", "-1", "4/9").test();
    new DivideAndRemainderTest("-16/9", "-4/3",  "1", "-4/9",  "1", "-4/9",  "2", "8/9").test();
    
    new DivideAndRemainderTest( "190/9",  "14/7",  "10",  "10/9",  "10",  "10/9",  "10", "10/9").test();
    new DivideAndRemainderTest("-190/9",  "14/7", "-10", "-10/9", "-11",   "8/9", "-11",  "8/9").test();
    new DivideAndRemainderTest( "190/9", "-14/7", "-10",  "10/9", "-11",  "-8/9", "-10", "10/9").test();
    new DivideAndRemainderTest("-190/9", "-14/7",  "10", "-10/9",  "10", "-10/9",  "11",  "8/9").test();
    
    new DivideAndRemainderTest( "13/5",  "5/13",  "6",  "19/65",  "6",  "19/65",  "6", "19/65").test();
    new DivideAndRemainderTest("-13/5",  "5/13", "-6", "-19/65", "-7",   "6/65", "-7",  "6/65").test();
    new DivideAndRemainderTest( "13/5", "-5/13", "-6",  "19/65", "-7",  "-6/65", "-6", "19/65").test();
    new DivideAndRemainderTest("-13/5", "-5/13",  "6", "-19/65",  "6", "-19/65",  "7",  "6/65").test();
  }
  
  
  @Test
  public void testReciprocal() {
    assertEquals("(1/1).reciprocal", "1/1", lf(1.0).reciprocal().toString());
    assertEquals("(1/2).reciprocal", "2/1", lf(2,4).reciprocal().toString());
    assertEquals("(-10/-14).reciprocal", "7/5", lf(-10,-14).reciprocal().toString());
    assertEquals("(-6/1).reciprocal", "-1/6", lf(-6).reciprocal().toString());
  }
  
  @Test
  public void testComplement() {
    assertEquals("(10/14).complement", "2/7", lf(10,14).complement().toString());
    assertEquals("(1/1).complement", "0/1", lf(1).complement().toString());
    assertEquals("(-1/1).complement", "2/1", lf(-1).complement().toString());
    assertEquals("(0/1).complement", "1/1", lf(0).complement().toString());
    assertEquals("(17/11).complement", "-6/11", lf(17,11).complement().toString());
  }
  
  @Test
  public void testNegate() {
    assertEquals("(2/7).negate()", "-2/7", lf(2,7).negate().toString());
    assertEquals("(0/1).negate()", "0/1", lf(0).negate().toString());
    assertEquals("(-2/7).negate()", "2/7", lf(-2,7).negate().toString());
  }
  
  @Test
  public void testAbs() {
    assertEquals("(2/7).abs()", "2/7", lf(2,7).abs().toString());
    assertEquals("(0/1).abs()", "0/1", lf(0).abs().toString());
    assertEquals("(-2/7).abs()", "2/7", lf(-2,7).abs().toString());
  }
  
  @Test
  public void testWithSign() {
    assertEquals("(2/7).withSign(-1)", "-2/7", lf(2,7).withSign(-1).toString());
    assertEquals("(2/7).withSign(0)", "0/1", lf(2,7).withSign(0).toString());
    assertEquals("(2/7).withSign(1)", "2/7", lf(2,7).withSign(1).toString());
    
    assertEquals("(-2/7).withSign(-1)", "-2/7", lf(-2,7).withSign(-1).toString());
    assertEquals("(-2/7).withSign(0)", "0/1", lf(-2,7).withSign(0).toString());
    assertEquals("(-2/7).withSign(1)", "2/7", lf(-2,7).withSign(1).toString());
    
    //make sure we don't get exception when we try to set 0 to some sign.
    assertEquals("(0/7).withSign(-1)", "0/1", lf(0,7).withSign(-1).toString());
    assertEquals("(0/7).withSign(0)", "0/1", lf(0,7).withSign(0).toString());
    assertEquals("(0/7).withSign(1)", "0/1", lf(0,7).withSign(1).toString());
    
    //a signum value other than -1, 0, or 1 is ok too.
    assertEquals("(2/7).withSign(-999)", "-2/7", lf(2,7).withSign(-999).toString());
    assertEquals("(2/7).withSign(1048)", "2/7", lf(2,7).withSign(1048).toString());
    assertEquals("(-2/7).withSign(-999)", "-2/7", lf(-2,7).withSign(-999).toString());
    assertEquals("(-2/7).withSign(1048)", "2/7", lf(-2,7).withSign(1048).toString());
    assertEquals("(0).withSign(-999)", "0/1", lf(0.0).withSign(-999).toString());
    assertEquals("(0).withSign(1048)", "0/1", lf(0.0).withSign(1048).toString());
    
    //test with extreme values just to be safe
    assertEquals("(2/7).withSign(Integer.MIN_VALUE)", "-2/7", lf(2,7).withSign(Integer.MIN_VALUE).toString());
    assertEquals("(2/7).withSign(Integer.MAX_VALUE)", "2/7", lf(2,7).withSign(Integer.MAX_VALUE).toString());
    assertEquals("(-2/7).withSign(Integer.MIN_VALUE)", "-2/7", lf(-2,7).withSign(Integer.MIN_VALUE).toString());
    assertEquals("(-2/7).withSign(Integer.MAX_VALUE)", "2/7", lf(-2,7).withSign(Integer.MAX_VALUE).toString());
    assertEquals("(0).withSign(Integer.MIN_VALUE)", "0/1", lf(0.0).withSign(Integer.MIN_VALUE).toString());
    assertEquals("(0).withSign(Integer.MAX_VALUE)", "0/1", lf(0.0).withSign(Integer.MAX_VALUE).toString());
  }
  
  @Test
  public void testSignum() {
    assertEquals("(2/7).signum()", 1, lf(2,7).signum());
    assertEquals("(0/1).signum()", 0, lf(0).signum());
    assertEquals("(-2/7).signum()", -1, lf(-2,7).signum());
  }
  
  
  
  @Test
  public void testPow() {
    //Note: 0^0 returns 1 (just like Math.pow())
    assertEquals("(0/1)^(0)", "1/1", lf(0,1).pow(0).toString());
    assertEquals("(0/1)^(10)", "0/1", lf(0,1).pow(10).toString());
    assertEquals("(11/17)^(5)", "161051/1419857", lf(11,17).pow(5).toString());
    assertEquals("(11/17)^(-5)", "1419857/161051", lf(11,17).pow(-5).toString());
    assertEquals("(5/8)^(0)", "1/1", lf(5,8).pow(0).toString());
    assertEquals("(9/16)^(-1)", "16/9", lf(9,16).pow(-1).toString());
    assertEquals("(9/16)^(1)", "9/16", lf(9,16).pow(1).toString());
  }
  
  
  @Test
  public void testGcdAndLcm() {
    //first let's test the edge cases around zero
    assertEquals("11/17", lf(0).gcd(lf(11,17)).toString());
    assertEquals("19/81", lf(19,81).gcd(lf(0)).toString());
    assertEquals("0/1", lf(0).gcd(lf(0)).toString());
    assertEquals("0/1", lf(0).lcm(lf(11,17)).toString());
    assertEquals("0/1", lf(19,81).lcm(lf(0)).toString());
    assertEquals("0/1", lf(0).lcm(lf(0)).toString());
    
    final int MAX_VAL = 20;
    
    //Create a set of all positive fractions with numerator and denominator <= MAX_VAL.
    //There will be a lot of repetition but reducing to lowest terms should make HashSet
    //see them as equivalent
    Set<LongFraction> fracSet = new HashSet<LongFraction>();
    for(long d = 1; d <= MAX_VAL; d++) {
      for(long n = 1; n <= MAX_VAL; n++) {
        fracSet.add(lf(n, d));
      }
    }
    
    List<LongFraction> fracList = new ArrayList<LongFraction>(fracSet.size());
    fracList.addAll(fracSet);
    fracSet = null;
    
    for(int i = 0; i < fracList.size(); i++) {
      LongFraction a = fracList.get(i);
      for(int j = i; j < fracList.size(); j++) {
        LongFraction b = fracList.get(j);
        
        LongFraction exp_gcd = naiveGCD(a,b);
        LongFraction act_gcd = a.gcd(b);
        assertEquals("gcd(" + a + "," + b + ")", exp_gcd, act_gcd);
        
        LongFraction exp_lcm = naiveLCM(a,b);
        LongFraction act_lcm = a.lcm(b);
        assertEquals("lcm(" + a + "," + b + ")", exp_lcm, act_lcm);
        
        //make sure basic properties of gcd/lcm hold up
        assertEquals("(" + a + ")/(" + exp_gcd + ") is not an integer!", (Long)1L, a.divide(act_gcd).getDenominator());
        assertEquals("(" + b + ")/(" + exp_gcd + ") is not an integer!", (Long)1L, a.divide(act_gcd).getDenominator());
        assertEquals("(" + exp_lcm + ")/(" + a + ") is not an integer!", (Long)1L, exp_lcm.divide(a).getDenominator());
        assertEquals("(" + exp_lcm + ")/(" + b + ") is not an integer!", (Long)1L, exp_lcm.divide(b).getDenominator());
        assertEquals("|(" + a + ")*(" + b + ")| != (" + exp_lcm + ")*(" + exp_gcd + ")", a.multiply(b), exp_gcd.multiply(exp_lcm));
        
        assertEquals("gcd(" + a + "," + b + ") != gcd(" + b + "," + a + ")", act_gcd, b.gcd(a));
        assertEquals("gcd(" + a + "," + b + ") != gcd(-" + a + "," + b + ")", act_gcd, a.negate().gcd(b));
        assertEquals("gcd(" + a + "," + b + ") != gcd(" + a + ",-" + b + ")", act_gcd, a.gcd(b.negate()));
        assertEquals("gcd(" + a + "," + b + ") != gcd(-" + a + ",-" + b + ")", act_gcd, a.negate().gcd(b.negate()));
        
        assertEquals("lcm(" + a + "," + b + ") != lcm(" + b + "," + a + ")", act_lcm, b.lcm(a));
        assertEquals("lcm(" + a + "," + b + ") != lcm(-" + a + "," + b + ")", act_lcm, a.negate().lcm(b));
        assertEquals("lcm(" + a + "," + b + ") != lcm(" + a + ",-" + b + ")", act_lcm, a.lcm(b.negate()));
        assertEquals("lcm(" + a + "," + b + ") != lcm(-" + a + ",-" + b + ")", act_lcm, a.negate().lcm(b.negate()));
        
        //if we have integers, compare to known algorithm
        if(a.getDenominator() == 1L && b.getDenominator() == 1L) {
          assertEquals("gcd of two integers is not an integer!", lf(BigInteger.valueOf(a.getNumerator()).gcd(BigInteger.valueOf(b.getNumerator()))), act_gcd);
          //no lcm() in BigInteger, so using lcm(a,b)=|a*b|/gcd(a,b)
          assertEquals("lcm of two integers is not an integer!", lf(BigInteger.valueOf(a.getNumerator()).multiply(BigInteger.valueOf(b.getNumerator()).divide(BigInteger.valueOf(a.getNumerator()).gcd(BigInteger.valueOf(b.getNumerator()))))), act_lcm);
        }
      }
    }
  }
  
  @Test
  public void testGetParts() {
    new GetPartsTest(  "4/3",  "1",  "1/3",  "1", "1/3",  "1", "1/3").test();
    new GetPartsTest( "-4/3", "-1", "-1/3", "-2", "2/3", "-2", "2/3").test();
    new GetPartsTest(  "2/1",  "2",  "0/1",  "2", "0/1",  "2", "0/1").test();
    new GetPartsTest( "-2/1", "-2",  "0/1", "-2", "0/1", "-2", "0/1").test();
    new GetPartsTest(  "2/3",  "0",  "2/3",  "0", "2/3",  "0", "2/3").test();
    new GetPartsTest( "-2/3",  "0", "-2/3", "-1", "1/3", "-1", "1/3").test();
    new GetPartsTest(  "0/1",  "0",  "0/1",  "0", "0/1",  "0", "0/1").test();
    new GetPartsTest( "17/7",  "2",  "3/7",  "2", "3/7",  "2", "3/7").test();
    new GetPartsTest("-17/7", "-2", "-3/7", "-3", "4/7", "-3", "4/7").test();
  }
  
  
  @Test
  public void testMediant() {
    assertEquals("mediant(1/1,1/2)", lf(1,2), lf(1,1).mediant(lf(1,3)));
    
    //ensure that we are always reducing to lowest terms
    assertEquals("mediant(2/2,1/2)", lf(1,2), lf(2,2).mediant(lf(1,3)));
    
    //things to test: zero, negative, self
    assertEquals("mediant(3/29,7/15)", lf(5,22), lf(3,29).mediant(lf(7,15)));
    assertEquals("mediant(29/3,15/7)", lf(22,5), lf(29,3).mediant(lf(15,7)));
    assertEquals("mediant(-3/29,7/15)", lf(1,11), lf(-3,29).mediant(lf(7,15)));
    assertEquals("mediant(-29/3,15/7)", lf(-7,5), lf(-29,3).mediant(lf(15,7)));
    assertEquals("mediant(3/29,-7/15)", lf(-1,11), lf(3,29).mediant(lf(-7,15)));
    assertEquals("mediant(29/3,-15/7)", lf(7,5), lf(29,3).mediant(lf(-15,7)));
    assertEquals("mediant(-3/29,-7/15)", lf(-5,22), lf(-3,29).mediant(lf(-7,15)));
    assertEquals("mediant(-29/3,-15/7)", lf(-22,5), lf(-29,3).mediant(lf(-15,7)));
    
    assertEquals("mediant(19/81,19/81)", lf(19,81), lf(19,81).mediant(lf(19,81)));
    assertEquals("mediant(-19/81,19/81)", lf(0), lf(-19,81).mediant(lf(19,81)));
    assertEquals("mediant(19/81,-19/81)", lf(0), lf(19,81).mediant(lf(-19,81)));
    assertEquals("mediant(-19/81,-19/81)", lf(-19,81), lf(-19,81).mediant(lf(-19,81)));
    
    assertEquals("mediant(0,81/19)", lf(81,20), lf(0).mediant(lf(81,19)));
    assertEquals("mediant(0,-81/19)", lf(-81,20), lf(0).mediant(lf(-81,19)));
    assertEquals("mediant(0,0)", lf(0), lf(0).mediant(lf(0)));
    
    //test static method
    assertEquals("mediant(1/1,1/2)", lf(1,2), LongFraction.mediant(lf(1,1), lf(1,3)));
    assertEquals("mediant(2/2,1/2)", lf(1,2), LongFraction.mediant(lf(2,2), lf(1,3)));
    assertEquals("mediant(3/29,7/15)", lf(5,22), LongFraction.mediant(lf(3,29), lf(7,15)));
  }
  
  
  @Test
  public void testFareyPrevNext() {
    final int MAX_VAL = 3;
    final int MAX_DEN = 20;
    
    //Create a set of all fractions from -MAX_VAL to +MAX_VAL, with denominator <= MAX_DEN.
    //There will be a lot of repetition but reducing to lowest terms should make HashSet
    //see them as equivalent
    Set<LongFraction> fareySet = new HashSet<LongFraction>();
    for(long d = 1; d <= MAX_DEN; d++) {
      for(long n = 0; n <= MAX_VAL*d; n++) {
        fareySet.add(lf(n, d));
        fareySet.add(lf(-n, d));
      }
    }
    
    //sort the sequence
    List<LongFraction> fareySeq = new ArrayList<LongFraction>(fareySet);
    Collections.sort(fareySeq);
    
    LongFraction last = lf(-MAX_VAL*MAX_DEN-1, MAX_DEN);
    for(LongFraction expected : fareySeq){
      LongFraction actual = last.fareyNext(MAX_DEN);
      assertEquals("(" + last.toString() + ").fareyNext(" + MAX_DEN + ")", expected.toString(), actual.toString());
      last = actual;
    }
    
    //now try in reverse to test fareyPrev
    Collections.reverse(fareySeq);
    last = lf(MAX_VAL*MAX_DEN+1, MAX_DEN);
    for(LongFraction expected : fareySeq){
      LongFraction actual = last.fareyPrev(MAX_DEN);
      assertEquals("(" + last.toString() + ").fareyPrev(" + MAX_DEN + ")", expected.toString(), actual.toString());
      last = actual;
    }
    
  }
  
  
  @Test
  public void testFareyClosest() {
    //a few simple cases- if we are in the sequence already, it should just return what was passed in
    assertEquals("4/3", lf(4,3).fareyClosest(3).toString());
    assertEquals("4/3", lf(4,3).fareyClosest(4).toString());
    assertEquals("4/3", lf(4,3).fareyClosest(99).toString());
    
    assertEquals("-4/3", lf(-4,3).fareyClosest(3).toString());
    assertEquals("-4/3", lf(-4,3).fareyClosest(4).toString());
    assertEquals("-4/3", lf(-4,3).fareyClosest(99).toString());
    
    LongFraction lfPi = lf(Math.PI);
    LongFraction lfNegPi = lfPi.negate();
    
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
      assertEquals("PI.FareyClosest(" + i + ")", expected, lfPi.fareyClosest(i).toString());
      assertEquals("(-PI).FareyClosest(" + i + ")", "-" + expected, lfNegPi.fareyClosest(i).toString());
    }
    
    //also test with approximations of e
    LongFraction lfE = lf(Math.E);
    LongFraction lfNegE = lfE.negate();
    
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
      try {
        assertEquals("e.FareyClosest(" + i + ")", expected, lfE.fareyClosest(i).toString());
        assertEquals("(-e).FareyClosest(" + i + ")", "-" + expected, lfNegE.fareyClosest(i).toString());
      }
      catch(ArithmeticException e) {
        assertTrue("e.FareyClosest(" + i + ") - expected: " + expected, false);
      }
    }
    
  }
  
  
  @Test
  public void testToString() {
    assertEquals("1/1", lf("1").toString());
    assertEquals("1/1", lf("1").toString(false));
    assertEquals("1", lf("1").toString(true));
    
    assertEquals("0/1", lf("0").toString());
    assertEquals("0/1", lf("0").toString(false));
    assertEquals("0", lf("0").toString(true));
    
    assertEquals("-1/1", lf("-1").toString());
    assertEquals("-1/1", lf("-1").toString(false));
    assertEquals("-1", lf("-1").toString(true));
    
    assertEquals("1/10", lf(".1").toString());
    assertEquals("1/10", lf(".1").toString(false));
    assertEquals("1/10", lf(".1").toString(true));
    
    assertEquals("-10/3", lf("10/-3").toString());
    assertEquals("-10/3", lf("10/-3").toString(false));
    assertEquals("-10/3", lf("10/-3").toString(true));
  }
  
  @Test
  public void testToString_radix() {
    assertEquals("10/1", lf("16").toString(16));
    assertEquals("-ff/ac", lf("255/-172").toString(16));
    assertEquals("101/101010101", lf("5/341").toString(2));
    assertEquals("zyx/d", lf("46617/13").toString(36));
    
    assertEquals("zyx/1", lf("46617").toString(36, false));
    assertEquals("zyx", lf("46617").toString(36, true));
    
    //radix must be in range 2-36
    assertEquals("46617/13", lf("46617/13").toString(37));
    assertEquals("46617/13", lf("46617/13").toString(0));
    assertEquals("46617/13", lf("46617/13").toString(1));
    assertEquals("46617/13", lf("46617/13").toString(-7));
  }
  
  @Test
  public void testToMixedString() {
    assertEquals("4/3 == 1 1/3", "1 1/3", lf("4/3").toMixedString());
    assertEquals("-4/3 == -1 1/3", "-1 1/3", lf("-4/3").toMixedString());
    assertEquals("6/3 == 2", lf("6/3").toMixedString(), "2");
    assertEquals("6/-3 == -2", lf("6/-3").toMixedString(), "-2");
    assertEquals("2/3 == 2/3", lf("2/3").toMixedString(), "2/3");
    assertEquals("2/-3 == -2/3", lf("2/-3").toMixedString(), "-2/3");
    assertEquals("0/3 == 0", lf("0/3").toMixedString(), "0");
    assertEquals("0/-3 == 0", lf("0/-3").toMixedString(), "0");
  }
  
  @Test
  public void testRound() {
    new RoundingTest("9.5", "10", "9", "10", "9", "10", "9", "10", "ArithmeticException").test();
    new RoundingTest("5.5", "6", "5", "6", "5", "6", "5", "6", "ArithmeticException").test();
    new RoundingTest("2.5", "3", "2", "3", "2", "3", "2", "2", "ArithmeticException").test();
    new RoundingTest("1.6", "2", "1", "2", "1", "2", "2", "2", "ArithmeticException").test();
    new RoundingTest("1.1", "2", "1", "2", "1", "1", "1", "1", "ArithmeticException").test();
    new RoundingTest("1", "1", "1", "1", "1", "1", "1", "1", "1").test();
    new RoundingTest("0.999999999999999999", "1", "0", "1", "0", "1", "1", "1", "ArithmeticException").test();
    new RoundingTest("0.500000000000000001", "1", "0", "1", "0", "1", "1", "1", "ArithmeticException").test();
    new RoundingTest("0.5", "1", "0", "1", "0", "1", "0", "0", "ArithmeticException").test();
    new RoundingTest("0.499999999999999999", "1", "0", "1", "0", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("0.000000000000000001", "1", "0", "1", "0", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("0", "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingTest("0.000000000000000000", "0", "0", "0", "0", "0", "0", "0", "0").test();
    new RoundingTest("-0.000000000000000001", "-1", "0", "0", "-1", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("-0.499999999999999999", "-1", "0", "0", "-1", "0", "0", "0", "ArithmeticException").test();
    new RoundingTest("-0.5", "-1", "0", "0", "-1", "-1", "0", "0", "ArithmeticException").test();
    new RoundingTest("-0.500000000000000001", "-1", "0", "0", "-1", "-1", "-1", "-1", "ArithmeticException").test();
    new RoundingTest("-0.999999999999999999", "-1", "0", "0", "-1", "-1", "-1", "-1", "ArithmeticException").test();
    new RoundingTest("-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1").test();
    new RoundingTest("-1.1", "-2", "-1", "-1", "-2", "-1", "-1", "-1", "ArithmeticException").test();
    new RoundingTest("-1.6", "-2", "-1", "-1", "-2", "-2", "-2", "-2", "ArithmeticException").test();
    new RoundingTest("-2.5", "-3", "-2", "-2", "-3", "-3", "-2", "-2", "ArithmeticException").test();
    new RoundingTest("-5.5", "-6", "-5", "-5", "-6", "-6", "-5", "-6", "ArithmeticException").test();
    new RoundingTest("-9.5", "-10", "-9", "-9", "-10", "-10", "-9", "-10", "ArithmeticException").test();
  }
  
  @Test
  public void testRoundToNumber() {
    new RoundToNumberTest("13", 2, "14/1", "12/1", "14/1", "12/1", "14/1", "12/1", "12/1", "ArithmeticException").test();
    new RoundToNumberTest("14", 2, "14/1", "14/1", "14/1", "14/1", "14/1", "14/1", "14/1", "14/1").test();
    new RoundToNumberTest("15", 2, "16/1", "14/1", "16/1", "14/1", "16/1", "14/1", "16/1", "ArithmeticException").test();
    new RoundToNumberTest("15", 5, "15/1", "15/1", "15/1", "15/1", "15/1", "15/1", "15/1", "15/1").test();
    new RoundToNumberTest("2.49", 5, "5/1", "0/1", "5/1", "0/1", "0/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("2.50", 5, "5/1", "0/1", "5/1", "0/1", "5/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("2.51", 5, "5/1", "0/1", "5/1", "0/1", "5/1", "5/1", "5/1", "ArithmeticException").test();
    new RoundToNumberTest("12.49", 5, "15/1", "10/1", "15/1", "10/1", "10/1", "10/1", "10/1", "ArithmeticException").test();
    new RoundToNumberTest("12.50", 5, "15/1", "10/1", "15/1", "10/1", "15/1", "10/1", "10/1", "ArithmeticException").test();
    new RoundToNumberTest("12.51", 5, "15/1", "10/1", "15/1", "10/1", "15/1", "15/1", "15/1", "ArithmeticException").test();
    new RoundToNumberTest("17.49", 5, "20/1", "15/1", "20/1", "15/1", "15/1", "15/1", "15/1", "ArithmeticException").test();
    new RoundToNumberTest("17.50", 5, "20/1", "15/1", "20/1", "15/1", "20/1", "15/1", "20/1", "ArithmeticException").test();
    new RoundToNumberTest("17.51", 5, "20/1", "15/1", "20/1", "15/1", "20/1", "20/1", "20/1", "ArithmeticException").test();
    new RoundToNumberTest("4/7", lf(1,7), "4/7", "4/7", "4/7", "4/7", "4/7", "4/7", "4/7", "4/7").test();
    new RoundToNumberTest("2/3", lf(1,7), "5/7", "4/7", "5/7", "4/7", "5/7", "5/7", "5/7", "ArithmeticException").test();
    new RoundToNumberTest("31/14", lf(1,7), "16/7", "15/7", "16/7", "15/7", "16/7", "15/7", "16/7", "ArithmeticException").test();
    new RoundToNumberTest("33/14", lf(1,7), "17/7", "16/7", "17/7", "16/7", "17/7", "16/7", "16/7", "ArithmeticException").test();
    new RoundToNumberTest("1649/11", lf(17,11), "1649/11", "1649/11", "1649/11", "1649/11", "1649/11", "1649/11", "1649/11", "1649/11").test();
    new RoundToNumberTest("1650/11", lf(17,11), "1666/11", "1649/11", "1666/11", "1649/11", "1649/11", "1649/11", "1649/11", "ArithmeticException").test();
    new RoundToNumberTest("1648/11", lf(17,11), "1649/11", "1632/11", "1649/11", "1632/11", "1649/11", "1649/11", "1649/11", "ArithmeticException").test();
    
    new RoundToNumberTest("0.49/16", lf(1,16), "1/16", "0/1", "1/16", "0/1", "0/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("0.50/16", lf(1,16), "1/16", "0/1", "1/16", "0/1", "1/16", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("0.51/16", lf(1,16), "1/16", "0/1", "1/16", "0/1", "1/16", "1/16", "1/16", "ArithmeticException").test();
    
    new RoundToNumberTest("0", 4, "0/1", "0/1", "0/1", "0/1", "0/1", "0/1", "0/1", "0/1").test();
    
    new RoundToNumberTest("-13", 2, "-14/1", "-12/1", "-12/1", "-14/1", "-14/1", "-12/1", "-12/1", "ArithmeticException").test();
    new RoundToNumberTest("-14", 2, "-14/1", "-14/1", "-14/1", "-14/1", "-14/1", "-14/1", "-14/1", "-14/1").test();
    new RoundToNumberTest("-15", 2, "-16/1", "-14/1", "-14/1", "-16/1", "-16/1", "-14/1", "-16/1", "ArithmeticException").test();
    new RoundToNumberTest("-15", 5, "-15/1", "-15/1", "-15/1", "-15/1", "-15/1", "-15/1", "-15/1", "-15/1").test();
    new RoundToNumberTest("-2.49", 5, "-5/1", "0/1", "0/1", "-5/1", "0/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("-2.50", 5, "-5/1", "0/1", "0/1", "-5/1", "-5/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("-2.51", 5, "-5/1", "0/1", "0/1", "-5/1", "-5/1", "-5/1", "-5/1", "ArithmeticException").test();
    new RoundToNumberTest("-12.49", 5, "-15/1", "-10/1", "-10/1", "-15/1", "-10/1", "-10/1", "-10/1", "ArithmeticException").test();
    new RoundToNumberTest("-12.50", 5, "-15/1", "-10/1", "-10/1", "-15/1", "-15/1", "-10/1", "-10/1", "ArithmeticException").test();
    new RoundToNumberTest("-12.51", 5, "-15/1", "-10/1", "-10/1", "-15/1", "-15/1", "-15/1", "-15/1", "ArithmeticException").test();
    new RoundToNumberTest("-17.49", 5, "-20/1", "-15/1", "-15/1", "-20/1", "-15/1", "-15/1", "-15/1", "ArithmeticException").test();
    new RoundToNumberTest("-17.50", 5, "-20/1", "-15/1", "-15/1", "-20/1", "-20/1", "-15/1", "-20/1", "ArithmeticException").test();
    new RoundToNumberTest("-17.51", 5, "-20/1", "-15/1", "-15/1", "-20/1", "-20/1", "-20/1", "-20/1", "ArithmeticException").test();
    new RoundToNumberTest("-4/7", lf(1,7), "-4/7", "-4/7", "-4/7", "-4/7", "-4/7", "-4/7", "-4/7", "-4/7").test();
    new RoundToNumberTest("-2/3", lf(1,7), "-5/7", "-4/7", "-4/7", "-5/7", "-5/7", "-5/7", "-5/7", "ArithmeticException").test();
    new RoundToNumberTest("-31/14", lf(1,7), "-16/7", "-15/7", "-15/7", "-16/7", "-16/7", "-15/7", "-16/7", "ArithmeticException").test();
    new RoundToNumberTest("-33/14", lf(1,7), "-17/7", "-16/7", "-16/7", "-17/7", "-17/7", "-16/7", "-16/7", "ArithmeticException").test();
    new RoundToNumberTest("-1649/11", lf(17,11), "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11").test();
    new RoundToNumberTest("-1650/11", lf(17,11), "-1666/11", "-1649/11", "-1649/11", "-1666/11", "-1649/11", "-1649/11", "-1649/11", "ArithmeticException").test();
    new RoundToNumberTest("-1648/11", lf(17,11), "-1649/11", "-1632/11", "-1632/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "ArithmeticException").test();
    
    new RoundToNumberTest("-0.49/16", lf(1,16), "-1/16", "0/1", "0/1", "-1/16", "0/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("-0.50/16", lf(1,16), "-1/16", "0/1", "0/1", "-1/16", "-1/16", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("-0.51/16", lf(1,16), "-1/16", "0/1", "0/1", "-1/16", "-1/16", "-1/16", "-1/16", "ArithmeticException").test();
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
  public void testToBigDecimal() {
    //default scale is 18 significant digits
    assertEquals("333.333333333333333", lf("1000/3").toBigDecimal().toString());
    assertEquals("-3.33333333333333333", lf("-10/3").toBigDecimal().toString());
    assertEquals("0.333333333333333333", lf("1/3").toBigDecimal().toString());
    assertEquals("-0.00333333333333333333", lf("-1/300").toBigDecimal().toString());
    
    assertEquals("-300", lf("-300").toBigDecimal().toString());
    assertEquals("3", lf("3").toBigDecimal().toString());
    assertEquals("-0.3", lf("-3/10").toBigDecimal().toString());
    assertEquals("0.003", lf("3/1000").toBigDecimal().toString());
    
    //714285 714285 714285
    assertEquals("71.4286", lf("500/7").toBigDecimal(6).toString());
    assertEquals("-0.714286", lf("-5/7").toBigDecimal(6).toString());
    assertEquals("0.0714286", lf("5/70").toBigDecimal(6).toString());
    assertEquals("-0.000714286", lf("-5/7000").toBigDecimal(6).toString());
    
    //if numerator or denominator has more than 18 significant digits, we should use as many as the larger
    assertEquals("3.07445734561825860E+18", lf(Long.MAX_VALUE, 3).toBigDecimal().toString());
  }
  
  
  @Test
  public void testToDecimalString() {
    new ToRadixedStringTest("55/10", 1, "5.5", "5.5", "5.5", "5.5", "5.5", "5.5", "5.5", "5.5").test();
    new ToRadixedStringTest("555/100", 1, "5.6", "5.5", "5.6", "5.5", "5.6", "5.5", "5.6", "ArithmeticException").test();
    new ToRadixedStringTest("545/100", 1, "5.5", "5.4", "5.5", "5.4", "5.5", "5.4", "5.4", "ArithmeticException").test();
    new ToRadixedStringTest("55/10", 4, "5.5000", "5.5000", "5.5000", "5.5000", "5.5000", "5.5000", "5.5000", "5.5000").test();
    
    new ToRadixedStringTest("-55/10", 1, "-5.5", "-5.5", "-5.5", "-5.5", "-5.5", "-5.5", "-5.5", "-5.5").test();
    new ToRadixedStringTest("-555/100", 1, "-5.6", "-5.5", "-5.5", "-5.6", "-5.6", "-5.5", "-5.6", "ArithmeticException").test();
    new ToRadixedStringTest("-545/100", 1, "-5.5", "-5.4", "-5.4", "-5.5", "-5.5", "-5.4", "-5.4", "ArithmeticException").test();
    new ToRadixedStringTest("-55/10", 4, "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000", "-5.5000").test();
    
    //3/7 = 0.428571 428571 428571 ...
    new ToRadixedStringTest("3/7", 3, "0.429", "0.428", "0.429", "0.428", "0.429", "0.429", "0.429", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", 5, "0.42858", "0.42857", "0.42858", "0.42857", "0.42857", "0.42857", "0.42857", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", 9, "0.428571429", "0.428571428", "0.428571429", "0.428571428", "0.428571429", "0.428571429", "0.428571429", "ArithmeticException").test();
    
    new ToRadixedStringTest("-3/7", 3, "-0.429", "-0.428", "-0.428", "-0.429", "-0.429", "-0.429", "-0.429", "ArithmeticException").test();
    new ToRadixedStringTest("-3/7", 5, "-0.42858", "-0.42857", "-0.42857", "-0.42858", "-0.42857", "-0.42857", "-0.42857", "ArithmeticException").test();
    new ToRadixedStringTest("-3/7", 9, "-0.428571429", "-0.428571428", "-0.428571428", "-0.428571429", "-0.428571429", "-0.428571429", "-0.428571429", "ArithmeticException").test();
    
    //test scenarios where the rounding causes an additional digit before the decimal
    new ToRadixedStringTest("99/10", 1, "9.9", "9.9", "9.9", "9.9", "9.9", "9.9", "9.9", "9.9").test();
    new ToRadixedStringTest("995/100", 1, "10.0", "9.9", "10.0", "9.9", "10.0", "9.9", "10.0", "ArithmeticException").test();
    
    new ToRadixedStringTest("-99/10", 1, "-9.9", "-9.9", "-9.9", "-9.9", "-9.9", "-9.9", "-9.9", "-9.9").test();
    new ToRadixedStringTest("-995/100", 1, "-10.0", "-9.9", "-9.9", "-10.0", "-10.0", "-9.9", "-10.0", "ArithmeticException").test();
    
    //test leading zeros scenario
    new ToRadixedStringTest("5/10000", 3, "0.001", "0.000", "0.001", "0.000", "0.001", "0.000", "0.000", "ArithmeticException").test();
    new ToRadixedStringTest("0/10000", 3, "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000").test();
    new ToRadixedStringTest("-5/10000", 3, "-0.001", "0.000", "0.000", "-0.001", "-0.001", "0.000", "0.000", "ArithmeticException").test();
    
    //test half_X rounding modes - 0.5 rounded to 0 decimal digits
    new ToRadixedStringTest( "1/2", 0,  "1",  "0",  "1",  "0",  "1",  "0",  "0", "ArithmeticException").test();
    new ToRadixedStringTest("-1/2", 0, "-1",  "0",  "0", "-1", "-1",  "0",  "0", "ArithmeticException").test();
    new ToRadixedStringTest( "3/2", 0,  "2",  "1",  "2",  "1",  "2",  "1",  "2", "ArithmeticException").test();
    new ToRadixedStringTest("-3/2", 0, "-2", "-1", "-1", "-2", "-2", "-1", "-2", "ArithmeticException").test();
    new ToRadixedStringTest( "5/2", 0,  "3",  "2",  "3",  "2",  "3",  "2",  "2", "ArithmeticException").test();
    new ToRadixedStringTest("-5/2", 0, "-3", "-2", "-2", "-3", "-3", "-2", "-2", "ArithmeticException").test();
    new ToRadixedStringTest( "7/2", 0,  "4",  "3",  "4",  "3",  "4",  "3",  "4", "ArithmeticException").test();
    new ToRadixedStringTest("-7/2", 0, "-4", "-3", "-3", "-4", "-4", "-3", "-4", "ArithmeticException").test();
    
    //test half_X rounding modes - 0.005 rounded to 2 digits
    new ToRadixedStringTest( "1/200", 2,  "0.01",  "0.00",  "0.01",  "0.00",  "0.01",  "0.00",  "0.00", "ArithmeticException").test();
    new ToRadixedStringTest("-1/200", 2, "-0.01",  "0.00",  "0.00", "-0.01", "-0.01",  "0.00",  "0.00", "ArithmeticException").test();
    new ToRadixedStringTest( "3/200", 2,  "0.02",  "0.01",  "0.02",  "0.01",  "0.02",  "0.01",  "0.02", "ArithmeticException").test();
    new ToRadixedStringTest("-3/200", 2, "-0.02", "-0.01", "-0.01", "-0.02", "-0.02", "-0.01", "-0.02", "ArithmeticException").test();
    new ToRadixedStringTest( "5/200", 2,  "0.03",  "0.02",  "0.03",  "0.02",  "0.03",  "0.02",  "0.02", "ArithmeticException").test();
    new ToRadixedStringTest("-5/200", 2, "-0.03", "-0.02", "-0.02", "-0.03", "-0.03", "-0.02", "-0.02", "ArithmeticException").test();
    new ToRadixedStringTest( "7/200", 2,  "0.04",  "0.03",  "0.04",  "0.03",  "0.04",  "0.03",  "0.04", "ArithmeticException").test();
    new ToRadixedStringTest("-7/200", 2, "-0.04", "-0.03", "-0.03", "-0.04", "-0.04", "-0.03", "-0.04", "ArithmeticException").test();
  }
  
  @Test
  public void testToDecimalString_NegativeFractionalDigits() {
    new ToRadixedStringTest("1230", -1, "1230", "1230", "1230", "1230", "1230", "1230", "1230", "1230").test();
    new ToRadixedStringTest("-1230", -1, "-1230", "-1230", "-1230", "-1230", "-1230", "-1230", "-1230", "-1230").test();
    
    new ToRadixedStringTest("1234", -1, "1240", "1230", "1240", "1230", "1230", "1230", "1230", "ArithmeticException").test();
    new ToRadixedStringTest("1234", -2, "1300", "1200", "1300", "1200", "1200", "1200", "1200", "ArithmeticException").test();
    new ToRadixedStringTest("1234", -3, "2000", "1000", "2000", "1000", "1000", "1000", "1000", "ArithmeticException").test();
    new ToRadixedStringTest("1234", -4, "10000", "0", "10000", "0", "0", "0", "0", "ArithmeticException").test();
    new ToRadixedStringTest("1234", -5, "100000", "0", "100000", "0", "0", "0", "0", "ArithmeticException").test();
    new ToRadixedStringTest("1234", -6, "1000000", "0", "1000000", "0", "0", "0", "0", "ArithmeticException").test();
    
    new ToRadixedStringTest("-1234", -1, "-1240", "-1230", "-1230", "-1240", "-1230", "-1230", "-1230", "ArithmeticException").test();
    new ToRadixedStringTest("-1234", -2, "-1300", "-1200", "-1200", "-1300", "-1200", "-1200", "-1200", "ArithmeticException").test();
    new ToRadixedStringTest("-1234", -3, "-2000", "-1000", "-1000", "-2000", "-1000", "-1000", "-1000", "ArithmeticException").test();
    new ToRadixedStringTest("-1234", -4, "-10000", "0", "0", "-10000", "0", "0", "0", "ArithmeticException").test();
    new ToRadixedStringTest("-1234", -5, "-100000", "0", "0", "-100000", "0", "0", "0", "ArithmeticException").test();
    new ToRadixedStringTest("-1234", -6, "-1000000", "0", "0", "-1000000", "0", "0", "0", "ArithmeticException").test();
    
    new ToRadixedStringTest("12345000", -4, "12350000", "12340000", "12350000", "12340000", "12350000", "12340000", "12340000", "ArithmeticException").test();
    new ToRadixedStringTest("12355000", -4, "12360000", "12350000", "12360000", "12350000", "12360000", "12350000", "12360000", "ArithmeticException").test();
    new ToRadixedStringTest("12345001", -4, "12350000", "12340000", "12350000", "12340000", "12350000", "12350000", "12350000", "ArithmeticException").test();
    new ToRadixedStringTest("12355001", -4, "12360000", "12350000", "12360000", "12350000", "12360000", "12360000", "12360000", "ArithmeticException").test();
    
    new ToRadixedStringTest("-12345000", -4, "-12350000", "-12340000", "-12340000", "-12350000", "-12350000", "-12340000", "-12340000", "ArithmeticException").test();
    new ToRadixedStringTest("-12355000", -4, "-12360000", "-12350000", "-12350000", "-12360000", "-12360000", "-12350000", "-12360000", "ArithmeticException").test();
    new ToRadixedStringTest("-12345001", -4, "-12350000", "-12340000", "-12340000", "-12350000", "-12350000", "-12350000", "-12350000", "ArithmeticException").test();
    new ToRadixedStringTest("-12355001", -4, "-12360000", "-12350000", "-12350000", "-12360000", "-12360000", "-12360000", "-12360000", "ArithmeticException").test();
    
    new ToRadixedStringTest("999", -1, "1000", "990", "1000", "990", "1000", "1000", "1000", "ArithmeticException").test();
    new ToRadixedStringTest("-999", -1, "-1000", "-990", "-990", "-1000", "-1000", "-1000", "-1000", "ArithmeticException").test();
    
    //1E6/7 = 142857.142857...
    new ToRadixedStringTest("1E6/7", -3, "143000", "142000", "143000", "142000", "143000", "143000", "143000", "ArithmeticException").test();
    new ToRadixedStringTest("1E6/-7", -3, "-143000", "-142000", "-142000", "-143000", "-143000", "-143000", "-143000", "ArithmeticException").test();
  }
  
  @Test
  public void testToRadixedString() {
    new ToRadixedStringTest("5/2", 2, 1, "10.1", "10.1", "10.1", "10.1", "10.1", "10.1", "10.1", "10.1").test();
    new ToRadixedStringTest("-5/2", 2, 1, "-10.1", "-10.1", "-10.1", "-10.1", "-10.1", "-10.1", "-10.1", "-10.1").test();
    
    // 1/3 has an exact representation in base 3
    new ToRadixedStringTest("1/3", 3, 1, "0.1", "0.1", "0.1", "0.1", "0.1", "0.1", "0.1", "0.1").test();
    new ToRadixedStringTest("-1/3", 3, 1, "-0.1", "-0.1", "-0.1", "-0.1", "-0.1", "-0.1", "-0.1", "-0.1").test();
    
    new ToRadixedStringTest("16", 16, 3, "10.000", "10.000", "10.000", "10.000", "10.000", "10.000", "10.000", "10.000").test();
    new ToRadixedStringTest("-16", 16, 3, "-10.000", "-10.000", "-10.000", "-10.000", "-10.000", "-10.000", "-10.000", "-10.000").test();
    
    //dead.beef in base 16
    new ToRadixedStringTest("3735928559/65536",  16, 4,  "dead.beef",  "dead.beef",  "dead.beef",  "dead.beef",  "dead.beef",  "dead.beef",  "dead.beef",  "dead.beef").test();
    new ToRadixedStringTest("-3735928559/65536", 16, 4, "-dead.beef", "-dead.beef", "-dead.beef", "-dead.beef", "-dead.beef", "-dead.beef", "-dead.beef", "-dead.beef").test();
    new ToRadixedStringTest("3735928559/65536",  16, 2,  "dead.bf",    "dead.be",    "dead.bf",    "dead.be",    "dead.bf",    "dead.bf",    "dead.bf",    "ArithmeticException").test();
    new ToRadixedStringTest("-3735928559/65536", 16, 2, "-dead.bf",   "-dead.be",   "-dead.be",   "-dead.bf",   "-dead.bf",   "-dead.bf",   "-dead.bf",    "ArithmeticException").test();
    
    //lazy.fox in base 36
    new ToRadixedStringTest( "46377484017/46656", 36, 3,  "lazy.fox",  "lazy.fox",  "lazy.fox",  "lazy.fox",  "lazy.fox",  "lazy.fox",  "lazy.fox",  "lazy.fox").test();
    new ToRadixedStringTest("-46377484017/46656", 36, 3, "-lazy.fox", "-lazy.fox", "-lazy.fox", "-lazy.fox", "-lazy.fox", "-lazy.fox", "-lazy.fox", "-lazy.fox").test();
    new ToRadixedStringTest( "46377484017/46656", 36, 0,  "lazz",      "lazy",      "lazz",      "lazy",      "lazy",      "lazy",      "lazy",      "ArithmeticException").test();
    new ToRadixedStringTest("-46377484017/46656", 36, 0, "-lazz",     "-lazy",     "-lazy",     "-lazz",     "-lazy",     "-lazy",     "-lazy",      "ArithmeticException").test();
    
    //20102.10201 in base 3
    new ToRadixedStringTest("42139/243",  3, 5,  "20102.10201",  "20102.10201",  "20102.10201",  "20102.10201",  "20102.10201",  "20102.10201",  "20102.10201",  "20102.10201").test();
    new ToRadixedStringTest("-42139/243", 3, 5, "-20102.10201", "-20102.10201", "-20102.10201", "-20102.10201", "-20102.10201", "-20102.10201", "-20102.10201", "-20102.10201").test();
    new ToRadixedStringTest("42139/243",  3, 4,  "20102.1021",   "20102.1020",   "20102.1021",   "20102.1020",   "20102.1020",    "20102.1020",   "20102.1020",  "ArithmeticException").test();
    new ToRadixedStringTest("-42139/243", 3, 4, "-20102.1021",  "-20102.1020",  "-20102.1020",  "-20102.1021",  "-20102.1020",   "-20102.1020",  "-20102.1020",  "ArithmeticException").test();
    new ToRadixedStringTest("42139/243",  3, 3,  "20102.110",    "20102.102",    "20102.110",    "20102.102",    "20102.102",     "20102.102",    "20102.102",   "ArithmeticException").test();
    new ToRadixedStringTest("-42139/243", 3, 3, "-20102.110",   "-20102.102",   "-20102.102",   "-20102.110",   "-20102.102",    "-20102.102",   "-20102.102",   "ArithmeticException").test();
    new ToRadixedStringTest("42139/243",  3, 2,  "20102.11",     "20102.10",     "20102.11",     "20102.10",     "20102.11",      "20102.11",     "20102.11",    "ArithmeticException").test();
    new ToRadixedStringTest("-42139/243", 3, 2, "-20102.11",    "-20102.10",    "-20102.10",    "-20102.11",    "-20102.11",     "-20102.11",    "-20102.11",    "ArithmeticException").test();
    new ToRadixedStringTest("42139/243",  3, 1,  "20102.2",      "20102.1",      "20102.2",      "20102.1",      "20102.1",       "20102.1",      "20102.1",     "ArithmeticException").test();
    new ToRadixedStringTest("-42139/243", 3, 1, "-20102.2",     "-20102.1",     "-20102.1",     "-20102.2",     "-20102.1",      "-20102.1",     "-20102.1",     "ArithmeticException").test();
    new ToRadixedStringTest("42139/243",  3, 0,  "20110",        "20102",        "20110",        "20102",        "20102",         "20102",        "20102",       "ArithmeticException").test();
    new ToRadixedStringTest("-42139/243", 3, 0, "-20110",       "-20102",       "-20102",       "-20110",       "-20102",        "-20102",       "-20102",       "ArithmeticException").test();
    
    //yy.yyy in base 35
    new ToRadixedStringTest( "52521874/42875", 35, 4,   "yy.yyy0",  "yy.yyy0",  "yy.yyy0",  "yy.yyy0",  "yy.yyy0",  "yy.yyy0",  "yy.yyy0",  "yy.yyy0").test();
    new ToRadixedStringTest("-52521874/42875", 35, 4,  "-yy.yyy0", "-yy.yyy0", "-yy.yyy0", "-yy.yyy0", "-yy.yyy0", "-yy.yyy0", "-yy.yyy0", "-yy.yyy0").test();
    new ToRadixedStringTest( "52521874/42875", 35, 3,   "yy.yyy",   "yy.yyy",   "yy.yyy",   "yy.yyy",   "yy.yyy",   "yy.yyy",   "yy.yyy",   "yy.yyy").test();
    new ToRadixedStringTest("-52521874/42875", 35, 3,  "-yy.yyy",  "-yy.yyy",  "-yy.yyy",  "-yy.yyy",  "-yy.yyy",  "-yy.yyy",  "-yy.yyy",  "-yy.yyy").test();
    new ToRadixedStringTest( "52521874/42875", 35, 2,  "100.00",    "yy.yy",   "100.00",    "yy.yy",   "100.00",   "100.00",   "100.00",    "ArithmeticException").test();
    new ToRadixedStringTest("-52521874/42875", 35, 2, "-100.00",   "-yy.yy",   "-yy.yy",  "-100.00",  "-100.00",  "-100.00",  "-100.00",    "ArithmeticException").test();
    new ToRadixedStringTest( "52521874/42875", 35, 1,  "100.0",     "yy.y",    "100.0",     "yy.y",    "100.0",    "100.0",    "100.0",     "ArithmeticException").test();
    new ToRadixedStringTest("-52521874/42875", 35, 1, "-100.0",    "-yy.y",    "-yy.y",   "-100.0",   "-100.0",   "-100.0",   "-100.0",     "ArithmeticException").test();
    new ToRadixedStringTest( "52521874/42875", 35, 0,  "100",       "yy",      "100",       "yy",      "100",      "100",      "100",       "ArithmeticException").test();
    new ToRadixedStringTest("-52521874/42875", 35, 0, "-100",      "-yy",      "-yy",     "-100",     "-100",     "-100",     "-100",       "ArithmeticException").test();
    
    //test half_X rounding modes - 0.2 in base-4 (equal to decimal 1/2) rounded to 0 base-4 digits
    new ToRadixedStringTest(  "2/4", 4, 0,   "1",  "0",   "1",   "0",   "1",  "0",   "0", "ArithmeticException").test();
    new ToRadixedStringTest( "-2/4", 4, 0,  "-1",  "0",   "0",  "-1",  "-1",  "0",   "0", "ArithmeticException").test();
    new ToRadixedStringTest(  "6/4", 4, 0,   "2",  "1",   "2",   "1",   "2",  "1",   "2", "ArithmeticException").test();
    new ToRadixedStringTest( "-6/4", 4, 0,  "-2", "-1",  "-1",  "-2",  "-2", "-1",  "-2", "ArithmeticException").test();
    new ToRadixedStringTest( "10/4", 4, 0,   "3",  "2",   "3",   "2",   "3",  "2",   "2", "ArithmeticException").test();
    new ToRadixedStringTest("-10/4", 4, 0,  "-3", "-2",  "-2",  "-3",  "-3", "-2",  "-2", "ArithmeticException").test();
    new ToRadixedStringTest( "14/4", 4, 0,  "10",  "3",  "10",   "3",  "10",  "3",  "10", "ArithmeticException").test();
    new ToRadixedStringTest("-14/4", 4, 0, "-10", "-3",  "-3", "-10", "-10", "-3", "-10", "ArithmeticException").test();
    
    //test half_X rounding modes - 0.002 in base-4 rounded to 2 octal digits
    new ToRadixedStringTest(  "2/64", 4, 2,  "0.01",  "0.00",  "0.01",  "0.00",  "0.01",  "0.00",  "0.00", "ArithmeticException").test();
    new ToRadixedStringTest( "-2/64", 4, 2, "-0.01",  "0.00",  "0.00", "-0.01", "-0.01",  "0.00",  "0.00", "ArithmeticException").test();
    new ToRadixedStringTest(  "6/64", 4, 2,  "0.02",  "0.01",  "0.02",  "0.01",  "0.02",  "0.01",  "0.02", "ArithmeticException").test();
    new ToRadixedStringTest( "-6/64", 4, 2, "-0.02", "-0.01", "-0.01", "-0.02", "-0.02", "-0.01", "-0.02", "ArithmeticException").test();
    new ToRadixedStringTest( "10/64", 4, 2,  "0.03",  "0.02",  "0.03",  "0.02",  "0.03",  "0.02",  "0.02", "ArithmeticException").test();
    new ToRadixedStringTest("-10/64", 4, 2, "-0.03", "-0.02", "-0.02", "-0.03", "-0.03", "-0.02", "-0.02", "ArithmeticException").test();
    new ToRadixedStringTest( "14/64", 4, 2,  "0.10",  "0.03",  "0.10",  "0.03",  "0.10",  "0.03",  "0.10", "ArithmeticException").test();
    new ToRadixedStringTest("-14/64", 4, 2, "-0.10", "-0.03", "-0.03", "-0.10", "-0.10", "-0.03", "-0.10", "ArithmeticException").test();
    
    //invalid radix should be same as radix 10 (no exception thrown)
    new ToRadixedStringTest("3/7", -100, 3, "0.429", "0.428", "0.429", "0.428", "0.429", "0.429", "0.429", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", -1, 5, "0.42858", "0.42857", "0.42858", "0.42857", "0.42857", "0.42857", "0.42857", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", 0, 9, "0.428571429", "0.428571428", "0.428571429", "0.428571428", "0.428571429", "0.428571429", "0.428571429", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", 1, 3, "0.429", "0.428", "0.429", "0.428", "0.429", "0.429", "0.429", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", 37, 5, "0.42858", "0.42857", "0.42858", "0.42857", "0.42857", "0.42857", "0.42857", "ArithmeticException").test();
    new ToRadixedStringTest("3/7", 100, 9, "0.428571429", "0.428571428", "0.428571429", "0.428571428", "0.428571429", "0.428571429", "0.428571429", "ArithmeticException").test();
  }
  
  @Test
  public void testToRadixedString_NegativeFractionalDigits() {
    new ToRadixedStringTest("5/2", 2, -1, "100", "10", "100", "10", "10", "10", "10", "ArithmeticException").test();
    new ToRadixedStringTest("-5/2", 2, -1, "-100", "-10", "-10", "-100", "-10", "-10", "-10", "ArithmeticException").test();
    
    //b83160 base 12
    new ToRadixedStringTest("2908440", 12, -1, "b83160", "b83160", "b83160", "b83160", "b83160", "b83160", "b83160",              "b83160").test();
    new ToRadixedStringTest("2908440", 12, -2, "b83200", "b83100", "b83200", "b83100", "b83200", "b83100", "b83200", "ArithmeticException").test();
    new ToRadixedStringTest("2908440", 12, -3, "b84000", "b83000", "b84000", "b83000", "b83000", "b83000", "b83000", "ArithmeticException").test();
    
    //b83060 base 12
    new ToRadixedStringTest("2908296", 12, -2, "b83100", "b83000", "b83100", "b83000", "b83100", "b83000", "b83000", "ArithmeticException").test();
    //b83b60 base 12
    new ToRadixedStringTest("2909880", 12, -2, "b84000", "b83b00", "b84000", "b83b00", "b84000", "b83b00", "b84000", "ArithmeticException").test();
    
    new ToRadixedStringTest("-2908440", 12, -1, "-b83160", "-b83160", "-b83160", "-b83160", "-b83160", "-b83160", "-b83160",             "-b83160").test();
    new ToRadixedStringTest("-2908440", 12, -2, "-b83200", "-b83100", "-b83100", "-b83200", "-b83200", "-b83100", "-b83200", "ArithmeticException").test();
    new ToRadixedStringTest("-2908440", 12, -3, "-b84000", "-b83000", "-b83000", "-b84000", "-b83000", "-b83000", "-b83000", "ArithmeticException").test();
    new ToRadixedStringTest("-2908296", 12, -2, "-b83100", "-b83000", "-b83000", "-b83100", "-b83100", "-b83000", "-b83000", "ArithmeticException").test();
    new ToRadixedStringTest("-2909880", 12, -2, "-b84000", "-b83b00", "-b83b00", "-b84000", "-b84000", "-b83b00", "-b84000", "ArithmeticException").test();
    
    //lazy.fox in base 36
    new ToRadixedStringTest("46377484017/46656", 36,  0,   "lazz", "lazy",   "lazz", "lazy",  "lazy",  "lazy",  "lazy", "ArithmeticException").test();
    new ToRadixedStringTest("46377484017/46656", 36, -1,   "lb00", "laz0",   "lb00", "laz0",  "lb00",  "lb00",  "lb00", "ArithmeticException").test();
    new ToRadixedStringTest("46377484017/46656", 36, -2,   "lb00", "la00",   "lb00", "la00",  "lb00",  "lb00",  "lb00", "ArithmeticException").test();
    new ToRadixedStringTest("46377484017/46656", 36, -3,   "m000", "l000",   "m000", "l000",  "l000",  "l000",  "l000", "ArithmeticException").test();
    new ToRadixedStringTest("46377484017/46656", 36, -4,  "10000",    "0",  "10000",    "0", "10000", "10000", "10000", "ArithmeticException").test();
    new ToRadixedStringTest("46377484017/46656", 36, -5, "100000",    "0", "100000",    "0",     "0",     "0",     "0", "ArithmeticException").test();
    
    new ToRadixedStringTest("-46377484017/46656", 36,  0,   "-lazz", "-lazy", "-lazy",   "-lazz",  "-lazy",  "-lazy",  "-lazy", "ArithmeticException").test();
    new ToRadixedStringTest("-46377484017/46656", 36, -1,   "-lb00", "-laz0", "-laz0",   "-lb00",  "-lb00",  "-lb00",  "-lb00", "ArithmeticException").test();
    new ToRadixedStringTest("-46377484017/46656", 36, -2,   "-lb00", "-la00", "-la00",   "-lb00",  "-lb00",  "-lb00",  "-lb00", "ArithmeticException").test();
    new ToRadixedStringTest("-46377484017/46656", 36, -3,   "-m000", "-l000", "-l000",   "-m000",  "-l000",  "-l000",  "-l000", "ArithmeticException").test();
    new ToRadixedStringTest("-46377484017/46656", 36, -4,  "-10000",     "0",     "0",  "-10000", "-10000", "-10000", "-10000", "ArithmeticException").test();
    new ToRadixedStringTest("-46377484017/46656", 36, -5, "-100000",     "0",     "0", "-100000",      "0",      "0",      "0", "ArithmeticException").test();
    
    //428571.428571
    //invalid radix should be same as radix 10 (no exception thrown)
    new ToRadixedStringTest("3000000/7", -100, -2, "428600", "428500", "428600", "428500", "428600", "428600", "428600", "ArithmeticException").test();
    new ToRadixedStringTest("3000000/7",   -1, -4, "430000", "420000", "430000", "420000", "430000", "430000", "430000", "ArithmeticException").test();
    new ToRadixedStringTest("3000000/7",    0, -6, "1000000", "0", "1000000", "0", "0", "0", "0", "ArithmeticException").test();
    new ToRadixedStringTest("3000000/7",    1, -2, "428600", "428500", "428600", "428500", "428600", "428600", "428600", "ArithmeticException").test();
    new ToRadixedStringTest("3000000/7",   37, -4, "430000", "420000", "430000", "420000", "430000", "430000", "430000", "ArithmeticException").test();
    new ToRadixedStringTest("3000000/7",  100, -6, "1000000", "0", "1000000", "0", "0", "0", "0", "ArithmeticException").test();
  }
  
  @Test
  public void testToRepeatingDigitString() {
    assertEquals("\"1.0\".toRepeatingDigitString(10, true)", "0.(9)", lf("1.0").toRepeatingDigitString(10, true));
    assertEquals("\"10.0\".toRepeatingDigitString(10, true)", "9.(9)", lf("10.0").toRepeatingDigitString(10, true));
    assertEquals("\"0.1\".toRepeatingDigitString(10, true)", "0.0(9)", lf("0.1").toRepeatingDigitString(10, true));
    assertEquals("\"0.01\".toRepeatingDigitString(10, true)", "0.00(9)", lf("0.01").toRepeatingDigitString(10, true));
    assertEquals("\"0.02\".toRepeatingDigitString(10, true)", "0.01(9)", lf("0.02").toRepeatingDigitString(10, true));
    assertEquals("\"5.1\".toRepeatingDigitString(10, true)", "5.0(9)", lf("5.1").toRepeatingDigitString(10, true));
    assertEquals("\"5.101\".toRepeatingDigitString(10, true)", "5.100(9)", lf("5.101").toRepeatingDigitString(10, true));
    
    assertEquals("\"1.0\".toRepeatingDigitString(10, false)", "1.0", lf("1.0").toRepeatingDigitString(10, false));
    assertEquals("\"10.0\".toRepeatingDigitString(10, false)", "10.0", lf("10.0").toRepeatingDigitString(10, false));
    assertEquals("\"0.1\".toRepeatingDigitString(10, false)", "0.1", lf("0.1").toRepeatingDigitString(10, false));
    assertEquals("\"0.01\".toRepeatingDigitString(10, false)", "0.01", lf("0.01").toRepeatingDigitString(10, false));
    assertEquals("\"0.02\".toRepeatingDigitString(10, false)", "0.02", lf("0.02").toRepeatingDigitString(10, false));
    assertEquals("\"5.1\".toRepeatingDigitString(10, false)", "5.1", lf("5.1").toRepeatingDigitString(10, false));
    assertEquals("\"5.101\".toRepeatingDigitString(10, false)", "5.101", lf("5.101").toRepeatingDigitString(10, false));
    
    
    assertEquals("\"4/9\".toRepeatingDigitString(10, false)", "0.(4)", lf("4/9").toRepeatingDigitString(10, false));
    assertEquals("\"4/9\".toRepeatingDigitString(10, true)", "0.(4)", lf("4/9").toRepeatingDigitString(10, true));
    assertEquals("\"-4/9\".toRepeatingDigitString(10, false)", "-0.(4)", lf("-4/9").toRepeatingDigitString(10, false));
    assertEquals("\"-4/9\".toRepeatingDigitString(10, true)", "-0.(4)", lf("-4/9").toRepeatingDigitString(10, true));
    
    assertEquals("\"2/45\".toRepeatingDigitString(10, false)", "0.0(4)", lf("2/45").toRepeatingDigitString(10, false));
    assertEquals("\"2/45\".toRepeatingDigitString(10, true)", "0.0(4)", lf("2/45").toRepeatingDigitString(10, true));
    assertEquals("\"-2/45\".toRepeatingDigitString(10, false)", "-0.0(4)", lf("-2/45").toRepeatingDigitString(10, false));
    assertEquals("\"-2/45\".toRepeatingDigitString(10, true)", "-0.0(4)", lf("-2/45").toRepeatingDigitString(10, true));
    
    assertEquals("\"56/99\".toRepeatingDigitString(10, false)", "0.(56)", lf("56/99").toRepeatingDigitString(10, false));
    
    assertEquals("\"4/333\".toRepeatingDigitString(10, false)", "0.(012)", lf("4/333").toRepeatingDigitString(10, false));
    assertEquals("\"1/1\".toRepeatingDigitString(10, false)", "1.0", lf("1/1").toRepeatingDigitString(10, false));
    assertEquals("\"1/1\".toRepeatingDigitString(10, true)", "0.(9)", lf("1/1").toRepeatingDigitString(10, true));
    assertEquals("\"1/2250\".toRepeatingDigitString(10, false)", "0.000(4)", lf("1/2250").toRepeatingDigitString(10, false));
    assertEquals("\"1/1000\".toRepeatingDigitString(10, false)", "0.001", lf("1/1000").toRepeatingDigitString(10, false));
    assertEquals("\"1/1000\".toRepeatingDigitString(10, true)", "0.000(9)", lf("1/1000").toRepeatingDigitString(10, true));
    assertEquals("\"1/8325\".toRepeatingDigitString(10, false)", "0.00(012)", lf("1/8325").toRepeatingDigitString(10, false));
    assertEquals("\"1111/900\".toRepeatingDigitString(10, false)", "1.23(4)", lf("1111/900").toRepeatingDigitString(10, false));
    assertEquals("\"631/1665\".toRepeatingDigitString(10, false)", "0.3(789)", lf("631/1665").toRepeatingDigitString(10, false));
    
    assertEquals("\"7/1\".toRepeatingDigitString(10, false)", "7.0", lf("7/1").toRepeatingDigitString(10, false));
    assertEquals("\"7/1\".toRepeatingDigitString(10, true)", "6.(9)", lf("7/1").toRepeatingDigitString(10, true));
    assertEquals("\"0/1\".toRepeatingDigitString(10, false)", "0.0", lf("0/1").toRepeatingDigitString(10, false));
    assertEquals("\"0/1\".toRepeatingDigitString(10, true)", "0.(0)", lf("0/1").toRepeatingDigitString(10, true));
    
    //different bases
    assertEquals("\"1/10\".toRepeatingDigitString(2, false)", "0.0(0011)", lf("1/10").toRepeatingDigitString(2, false));
    assertEquals("\"1/10\".toRepeatingDigitString(2, true)", "0.0(0011)", lf("1/10").toRepeatingDigitString(2, true));
    assertEquals("\"1/10\".toRepeatingDigitString(16, false)", "0.1(9)", lf("1/10").toRepeatingDigitString(16, false));
    assertEquals("\"1/10\".toRepeatingDigitString(16, true)", "0.1(9)", lf("1/10").toRepeatingDigitString(16, true));
    assertEquals("\"-11/1\".toRepeatingDigitString(19, false)", "-b.0", lf("-11/1").toRepeatingDigitString(19, false));
    assertEquals("\"-11/1\".toRepeatingDigitString(19, true)", "-a.(i)", lf("-11/1").toRepeatingDigitString(19, true));
    assertEquals("\"2994276908470787/78362484480\".toRepeatingDigitString(36, false)", "the.lazy(fox)", lf("2994276908470787/78362484480").toRepeatingDigitString(36, false));
    assertEquals("\"2994276908470787/78362484480\".toRepeatingDigitString(36, true)", "the.lazy(fox)", lf("2994276908470787/78362484480").toRepeatingDigitString(36, true));
    
    //optional args should be same as (10, false)
    assertEquals("\"12.0\".toRepeatingDigitString()", "12.0", lf("12.0").toRepeatingDigitString());
    assertEquals("\"12.0\".toRepeatingDigitString(16)", "c.0", lf("12.0").toRepeatingDigitString(16));
    assertEquals("\"12.0\".toRepeatingDigitString(true)", "11.(9)", lf("12.0").toRepeatingDigitString(true));
    assertEquals("\"12.0\".toRepeatingDigitString(16, true)", "b.(f)", lf("12.0").toRepeatingDigitString(16, true));
    
    //invalid radix equivalent to 10
    assertEquals("\"12.0\".toRepeatingDigitString(-100)", "12.0", lf("12.0").toRepeatingDigitString(-100));
    assertEquals("\"12.0\".toRepeatingDigitString(-1)", "12.0", lf("12.0").toRepeatingDigitString(-1));
    assertEquals("\"12.0\".toRepeatingDigitString(0)", "12.0", lf("12.0").toRepeatingDigitString(0));
    assertEquals("\"12.0\".toRepeatingDigitString(1)", "12.0", lf("12.0").toRepeatingDigitString(1));
    assertEquals("\"12.0\".toRepeatingDigitString(37)", "12.0", lf("12.0").toRepeatingDigitString(37));
    assertEquals("\"12.0\".toRepeatingDigitString(100)", "12.0", lf("12.0").toRepeatingDigitString(100));
  }
  
  @Test
  public void testEquals() {
    assertTrue(lf(5,7).equals(lf(15,21)));
    assertTrue(lf(5,10).equals(LongFraction.ONE_HALF));
    assertTrue(LongFraction.TEN.equals(lf(BigInteger.valueOf(10))));
    assertTrue(lf(1,2).equals(lf(0.5)));
    assertTrue(lf(1,2).equals(lf("1/2")));
    assertTrue(lf(1,2).equals(lf("0.5")));
    assertTrue(lf(1,2).equals(lf("5e-1")));
    assertTrue(lf(5,1).equals(lf(5)));
    assertFalse(lf(5,1).equals(5));
    assertFalse(lf(5,1).equals(BigInteger.valueOf(5)));
    assertFalse(lf(7,10).equals(new BigDecimal("0.70")));
    assertFalse(lf(3,4).equals(null));
    assertFalse(lf(3,4).equals("3/4"));
    assertFalse(lf(3,4).equals(0.75));
    
    assertTrue(lf(-11,17).equals(lf("-11/17")));
    assertTrue(LongFraction.ZERO.equals(lf(0)));
    assertFalse(LongFraction.ZERO.equals(0));
    assertFalse(LongFraction.ZERO.equals(0.0));
    assertFalse(LongFraction.ZERO.equals(-0.0));
    assertFalse(LongFraction.ZERO.equals(BigInteger.ZERO));
    
    assertFalse(lf(1,10).equals(lf(0.1)));
    assertFalse(lf(1,10).equals(0.1));
  }
  
  @Test
  public void testEqualsNumber() {
    assertTrue(lf(5,7).equalsNumber(lf(15,21)));
    assertTrue(lf(5,10).equalsNumber(LongFraction.ONE_HALF));
    assertTrue(LongFraction.TEN.equalsNumber(lf(BigInteger.valueOf(10))));
    assertTrue(lf(1,2).equalsNumber(lf(0.5)));
    assertTrue(lf(1,2).equalsNumber(lf("1/2")));
    assertTrue(lf(1,2).equalsNumber(lf("0.5")));
    assertTrue(lf(1,2).equalsNumber(lf("5e-1")));
    assertTrue(lf(5,1).equalsNumber(lf(5)));
    assertTrue(lf(5,1).equalsNumber(5));
    assertTrue(lf(5,1).equalsNumber(BigInteger.valueOf(5)));
    assertTrue(lf(7,10).equalsNumber(new BigDecimal("0.70")));
    assertFalse(lf(3,4).equalsNumber(null));
    assertTrue(lf(3,4).equalsNumber(0.75));
    
    assertTrue(lf(-11,17).equalsNumber(lf("-11/17")));
    assertTrue(LongFraction.ZERO.equalsNumber(lf(0)));
    assertTrue(LongFraction.ZERO.equalsNumber(0));
    assertTrue(LongFraction.ZERO.equalsNumber(0.0));
    assertTrue(LongFraction.ZERO.equalsNumber(-0.0));
    assertTrue(LongFraction.ZERO.equalsNumber(BigInteger.ZERO));
    
    assertFalse(lf(1,10).equalsNumber(lf(0.1)));
    assertFalse(lf(1,10).equalsNumber(0.1));
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
    Set<LongFraction> set = new HashSet<LongFraction>();
    
    for(int num = 0; num <= 20; num++)
    {
      for(int den = 1; den <= 10; den++)
      {
        LongFraction lf = lf(num, den);
        set.add(lf);
        set.add(lf.negate());
      }
    }
    
    List<LongFraction> lst = new ArrayList<LongFraction>(set);
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
    
    //throw in min/max test here too
    for(int i = 0; i < lst.size(); i++) {
      for(int j = 0; j < lst.size(); j++) {
        LongFraction lhs = lst.get(i);
        LongFraction rhs = lst.get(j);
        if(i <= j) {
          assertEquals(lhs, lhs.min(rhs));
          assertEquals(lhs, LongFraction.min(lhs, rhs));
          assertEquals(rhs, lhs.max(rhs));
          assertEquals(rhs, LongFraction.max(lhs, rhs));
        }
        else {
          assertEquals(rhs, lhs.min(rhs));
          assertEquals(rhs, LongFraction.min(lhs, rhs));
          assertEquals(lhs, lhs.max(rhs));
          assertEquals(lhs, LongFraction.max(lhs, rhs));
        }
      }
    }
  }
  
  @Test
  public void testNumberInterface() {
    //Same setup as testCompareTo, but after sorting test each of
    //the methods from Number interface.
    Set<LongFraction> set = new HashSet<LongFraction>();
    
    for(int num = 0; num <= 20; num++)
    {
      for(int den = 1; den <= 10; den++)
      {
        LongFraction lf = lf(num, den);
        set.add(lf);
        set.add(lf.negate());
      }
    }
    
    List<LongFraction> lst = new ArrayList<LongFraction>(set);
    Collections.sort(lst);
    
    LongFraction lastBF = lst.get(0);
    byte lastByte = lastBF.byteValue();
    short lastShort = lastBF.shortValue();
    int lastInt = lastBF.intValue();
    long lastLong = lastBF.longValue();
    float lastFloat = lastBF.floatValue();
    double lastDouble = lastBF.doubleValue();
    BigDecimal lastBD = lastBF.toBigDecimal();
    
    for(int i = 1; i < lst.size(); i++)
    {
      LongFraction currBF = lst.get(i);
      byte currByte = currBF.byteValue();
      short currShort = currBF.shortValue();
      int currInt = currBF.intValue();
      long currLong = currBF.longValue();
      float currFloat = currBF.floatValue();
      double currDouble = currBF.doubleValue();
      BigDecimal currBD = currBF.toBigDecimal();
      
      assertTrue("(byte)(" + lastBF + ") <= (byte)(" + currBF + ")", lastByte <= currByte);
      assertTrue("(short)(" + lastBF + ") <= (short)(" + currBF + ")", lastShort <= currShort);
      assertTrue("(int)(" + lastBF + ") <= (int)(" + currBF + ")", lastInt <= currInt);
      assertTrue("(long)(" + lastBF + ") <= (long)(" + currBF + ")", lastLong <= currLong);
      assertTrue("(float)(" + lastBF + ") < (float)(" + currBF + ")", lastFloat < currFloat);
      assertTrue("(double)(" + lastBF + ") < (double)(" + currBF + ")", lastDouble < currDouble);
      assertTrue("(BigDecimal)(" + lastBF + ") < (BigDecimal)(" + currBF + ")", lastBD.compareTo(currBD) < 0);
      
      lastBF = currBF;
      lastByte = currByte;
      lastShort = currShort;
      lastInt = currInt;
      lastLong = currLong;
      lastFloat = currFloat;
      lastDouble = currDouble;
      lastBD = currBD;
    }
  }
  
  @Test
  public void testDoubleValue() {
    //test strategy here: split up all double values into NUM_TESTS tests, roughly evenly
    //distributed. Then check that converting from double to LongFraction and back to double returns
    //exactly equivalent result.
    
    //the float, when converted to fraction, will be 2^(exponent) * (0x800000+mantissa)*2^(-23).
    //if exponent is negative, and less than -62, then we may get underflow on the denominator.
    //while some smaller doubles can be expressed as LongFraction (see testDoubleValue_EdgeCases()), to
    //ensure we don't underflow, start with 1*2^(-10)
    final long MIN = Double.doubleToRawLongBits(DoubleUtil.getDouble(0, -10, 0, false));
    
    //largest positive double that can fit in a LongFraction.
    //double only carries 53 bits (52-bit mantissa with 1 implied bit), and Long.MAX_VALUE has 63 significant
    //bits, so we can't store Long.MAX_VALUE as a double. We can store the first 53 bits of it, however:
    //   0 bit (sign), 53 1 bits, 10 0 bits = 0x7ffffffffffffc00.
    //Denominator is just 1; anything larger would make the value of the fraction smaller.
    final long MAX = Double.doubleToRawLongBits(DoubleUtil.getDouble(0, 62, DoubleUtil.MAX_MANTISSA, false));
    
    //Selected 60k tests because that is about how many took 1 second for me.
    //Then I picked the closest prime to that number to use as the divisor
    final long NUM_TESTS = 59999L;
    final long DELTA = (MAX-MIN)/NUM_TESTS;
    
    //note i>0 check is to prevent overflow into negatives
    for(long i = MIN; i <= MAX && i > 0; i+= DELTA)
    {
      double in = Double.longBitsToDouble(i);
      LongFraction f = lf(in);
      double out = f.doubleValue();
      assertEquals("failed to get back the same double we put in for raw bits: " + i, in, out, 0.0);
      
      //this should not throw exception, because we started with an exact double.
      double exactOut = f.doubleValueExact();
      assertEquals("failed to get back the same double we put in, using doubleValueExact(), for raw bits: " + i, in, exactOut, 0.0);
      
      //make sure we get same behavior with negative value
      LongFraction negF = lf(-in);
      double negOut = negF.doubleValue();
      assertEquals("failed to get back the same double we put in with negative value for raw bits: " + i, -in, negOut, 0.0);
      
      //this should not throw exception, because we started with an exact double.
      double negExactOut = f.doubleValueExact();
      assertEquals("failed to get back the same double we put in with negative value, using doubleValueExact(), for raw bits: " + i, in, negExactOut, 0.0);
      
      //make sure we also test with the max value on the last iteration
      if(i < MAX && (i + DELTA > MAX || i + DELTA < 0))
        i = MAX - DELTA;
    }
  }
  
  @Test
  public void testDoubleValue_EdgeCases() {
    //test behavior with +0.0 and -0.0. Since LongFraction does not have concept of negative zero, both should be equal to +0.0.
    final double POSITIVE_ZERO = +0.0;
    final double NEGATIVE_ZERO = -0.0;
    
    //confirm our test setup here, just to be sure...
    assertEquals("Test setup issue: +0.0 doesn't have expected raw bits.", 0x0000000000000000L, Double.doubleToRawLongBits(POSITIVE_ZERO));
    assertEquals("Test setup issue: -0.0 doesn't have expected raw bits.", 0x8000000000000000L, Double.doubleToRawLongBits(NEGATIVE_ZERO));
    
    LongFraction f = lf(POSITIVE_ZERO);
    assertEquals("doubleValue for positive zero", 0L, Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for positive zero", 0L, Double.doubleToRawLongBits(f.doubleValueExact()));
    
    f = lf(NEGATIVE_ZERO);
    assertEquals("doubleValue for negative zero", 0L, Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for negative zero", 0L, Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //smallest positive double that can fit in a LongFraction: 1 / 2^62
    final double MIN_DBL_LF = DoubleUtil.getDouble(0, -62, 0, false);
    f = lf(MIN_DBL_LF);
    assertEquals(new Long(1), f.getNumerator());
    assertEquals(0x4000000000000000L, (Object)f.getDenominator());
    assertEquals(Double.doubleToRawLongBits(MIN_DBL_LF), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //since sign is carried by numerator, negation won't change anything
    f = lf(-MIN_DBL_LF);
    assertEquals(new Long(-1), f.getNumerator());
    assertEquals(0x4000000000000000L, (Object)f.getDenominator());
    assertEquals(Double.doubleToRawLongBits(-MIN_DBL_LF), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //largest positive double that can fit in a LongFraction.
    //double only carries 53 bits (52-bit mantissa with 1 implied bit), and Long.MAX_VALUE has 63 significant
    //bits, so we can't store Long.MAX_VALUE as a double. We can store the first 53 bits of it, however:
    //   0 bit (sign), 53 1 bits, 10 0 bits = 0x7ffffffffffffc00.
    //Denominator is just 1; anything larger would make the value of the fraction smaller.
    final double MAX_DBL_LF = DoubleUtil.getDouble(0, 62, DoubleUtil.MAX_MANTISSA, false);
    f = lf(MAX_DBL_LF);
    assertEquals(0x7ffffffffffffc00L, (Object)f.getNumerator());
    assertEquals(Long.MAX_VALUE & ~0x3ffL, (Object)f.getNumerator()); //validate my assumptions...
    assertEquals(new Long(1), f.getDenominator());
    assertEquals(Double.doubleToRawLongBits(MAX_DBL_LF), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    f = lf(-MAX_DBL_LF);
    assertEquals(0x8000000000000400L, (Object)f.getNumerator());
    assertEquals(new Long(1), f.getDenominator());
    assertEquals(Double.doubleToRawLongBits(-MAX_DBL_LF), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //largest (greatest absolute value) negative double that can fit in a LongFraction.
    //Long.MIN_VALUE only has 1 significant bit (it is equal to -(2^63), so that can be stored exactly as a double
    final double MAX_NEG_DBL_LF = DoubleUtil.getDouble(1, 63, 0, false);
    f = lf(MAX_NEG_DBL_LF);
    assertEquals(Long.MIN_VALUE, (Object)f.getNumerator());
    assertEquals(new Long(1), f.getDenominator());
    assertEquals(Double.doubleToRawLongBits(MAX_NEG_DBL_LF), Double.doubleToRawLongBits(f.doubleValueExact()));
  }
  
  @Test
  public void testFloatValue() {
    //test strategy here: split up all possible float values into NUM_TESTS tests, roughly evenly
    //distributed. Then check that converting from float to LongFraction and back to float returns
    //exactly equivalent result.
    
    //the float, when converted to fraction, will be 2^(exponent) * (0x800000+mantissa)*2^(-23).
    //if exponent is negative, and less than -62, then we may get underflow on the denominator.
    //while some smaller floats can be expressed as LongFraction (see testFloatValue_EdgeCases()), to
    //ensure we don't underflow, start with 1*2^(-39)
    final long MIN = Float.floatToRawIntBits(FloatUtil.getFloat(0, -39, 0, false));
    
    //largest positive float that can fit in a LongFraction.
    //float only carries 24 bits (23-bit mantissa with 1 implied bit), and Long.MAX_VALUE has 63 significant
    //bits, so we can't store Long.MAX_VALUE as a float. We can store the first 23 bits of it, however:
    //   0 bit (sign), 24 1 bits, 39 0 bits = 0x7fffff8000000000.
    //Denominator is just 1; anything larger would make the value of the fraction smaller.
    final long MAX = Float.floatToRawIntBits(FloatUtil.getFloat(0, 62, FloatUtil.MAX_MANTISSA, false));
    
    //Selected 60k tests because that is about how many took 1 second for me.
    //Then I picked the closest prime to that number to use as the divisor
    final long NUM_TESTS = 59999L;
    final long DELTA = (MAX-MIN)/NUM_TESTS;
    
    for(long i = MIN; i <= MAX; i+= DELTA)
    {
      float in = Float.intBitsToFloat((int)i);
      LongFraction f = lf(in);
      float out = f.floatValue();
      assertEquals("failed to get back the same float we put in for raw bits: " + i, in, out, 0.0f);
      
      //this should not throw exception, because we started with an exact float.
      float exactOut = f.floatValueExact();
      assertEquals("failed to get back the same float we put in, using floatValueExact(), for raw bits: " + i, in, exactOut, 0.0);
      
      //make sure we get same behavior with negative value
      LongFraction negF = lf(-in);
      float negOut = negF.floatValue();
      assertEquals("failed to get back the same float we put in with negative value for raw bits: " + i, -in, negOut, 0.0f);
      
      //this should not throw exception, because we started with an exact float.
      float negExactOut = f.floatValueExact();
      assertEquals("failed to get back the same float we put in with negative value, using floatValueExact(), for raw bits: " + i, in, negExactOut, 0.0);
      
      //make sure we also test with the max value on the last iteration
      if(i < MAX && i + DELTA > MAX)
        i = MAX - DELTA;
    }
  }
  
  @Test
  public void testFloatValue_EdgeCases() {
    //test behavior with +0.0 and -0.0. Since LongFraction does not have concept of negative zero, both should be equal to +0.0.
    final float POSITIVE_ZERO = +0.0f;
    final float NEGATIVE_ZERO = -0.0f;
    
    //confirm our test setup here, just to be sure...
    assertEquals("Test setup issue: +0.0f doesn't have expected raw bits.", 0x00000000, Float.floatToRawIntBits(POSITIVE_ZERO));
    assertEquals("Test setup issue: -0.0f doesn't have expected raw bits.", 0x80000000, Float.floatToRawIntBits(NEGATIVE_ZERO));
    
    LongFraction f = lf(POSITIVE_ZERO);
    assertEquals("floatValue for positive zero", 0, Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for positive zero", 0, Float.floatToRawIntBits(f.floatValueExact()));
    
    f = lf(NEGATIVE_ZERO);
    assertEquals("floatValue for negative zero", 0, Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for negative zero", 0, Float.floatToRawIntBits(f.floatValueExact()));
    
    //smallest positive float that can fit in a LongFraction: 1 / 2^62
    final float MIN_FLT_LF = (float)DoubleUtil.getDouble(0, -62, 0, false);
    f = lf(MIN_FLT_LF);
    assertEquals(new Long(1), f.getNumerator());
    assertEquals(0x4000000000000000L, (Object)f.getDenominator());
    assertEquals(Float.floatToRawIntBits(MIN_FLT_LF), Float.floatToRawIntBits(f.floatValueExact()));
    
    //since sign is carried by numerator, negation won't change anything
    f = lf(-MIN_FLT_LF);
    assertEquals(new Long(-1), f.getNumerator());
    assertEquals(0x4000000000000000L, (Object)f.getDenominator());
    assertEquals(Float.floatToRawIntBits(-MIN_FLT_LF), Float.floatToRawIntBits(f.floatValueExact()));
    
    //largest positive float that can fit in a LongFraction.
    //float only carries 24 bits (23-bit mantissa with 1 implied bit), and Long.MAX_VALUE has 63 significant
    //bits, so we can't store Long.MAX_VALUE as a float. We can store the first 23 bits of it, however:
    //   0 bit (sign), 24 1 bits, 39 0 bits = 0x7fffff8000000000.
    //Denominator is just 1; anything larger would make the value of the fraction smaller.
    final float MAX_FLT_LF = FloatUtil.getFloat(0, 62, FloatUtil.MAX_MANTISSA, false);
    f = lf(MAX_FLT_LF);
    assertEquals(0x7fffff8000000000L, (Object)f.getNumerator());
    assertEquals(Long.MAX_VALUE & ~0x7fffffffffL, (Object)f.getNumerator()); //validate my assumptions...
    assertEquals(new Long(1), f.getDenominator());
    assertEquals(Float.floatToRawIntBits(MAX_FLT_LF), Float.floatToRawIntBits(f.floatValueExact()));
    
    f = lf(-MAX_FLT_LF);
    assertEquals(0x8000008000000000L, (Object)f.getNumerator());
    assertEquals(new Long(1), f.getDenominator());
    assertEquals(Float.floatToRawIntBits(-MAX_FLT_LF), Float.floatToRawIntBits(f.floatValueExact()));
    
    //largest (greatest absolute value) negative float that can fit in a LongFraction.
    //Long.MIN_VALUE only has 1 significant bit (it is equal to -(2^63), so that can be stored exactly as a float
    final float MAX_NEG_FLT_LF = FloatUtil.getFloat(1, 63, 0, false);
    f = lf(MAX_NEG_FLT_LF);
    assertEquals(Long.MIN_VALUE, (Object)f.getNumerator());
    assertEquals(new Long(1), f.getDenominator());
    assertEquals(Float.floatToRawIntBits(MAX_NEG_FLT_LF), Float.floatToRawIntBits(f.floatValueExact()));
  }
  
  @Test
  public void testLongValue() {
    assertEquals(Long.MAX_VALUE, lf(Long.MAX_VALUE).longValue());
    assertEquals(Long.MAX_VALUE-1, lf(Long.MAX_VALUE-1).longValue());
    
    assertEquals(Long.MIN_VALUE, lf(Long.MIN_VALUE).longValue());
    assertEquals(Long.MIN_VALUE+1, lf(Long.MIN_VALUE+1).longValue());
    
    assertEquals(Long.MAX_VALUE, lf(Long.MAX_VALUE).longValueExact());
    assertEquals(Long.MAX_VALUE-1, lf(Long.MAX_VALUE-1).longValueExact());
    
    assertEquals(Long.MIN_VALUE, lf(Long.MIN_VALUE).longValueExact());
    assertEquals(Long.MIN_VALUE+1, lf(Long.MIN_VALUE+1).longValueExact());
  }
  
  @Test
  public void testIntValue() {
    assertEquals(Integer.MAX_VALUE, lf(Integer.MAX_VALUE).intValue());
    assertEquals(Integer.MAX_VALUE-1, lf(Integer.MAX_VALUE-1L).intValue());
    assertEquals(Integer.MAX_VALUE, lf(Integer.MAX_VALUE+1L).intValue());
    
    assertEquals(Integer.MIN_VALUE, lf(Integer.MIN_VALUE).intValue());
    assertEquals(Integer.MIN_VALUE+1, lf(Integer.MIN_VALUE+1L).intValue());
    assertEquals(Integer.MIN_VALUE, lf(Integer.MIN_VALUE-1L).intValue());
    
    assertEquals(Integer.MAX_VALUE, lf(Integer.MAX_VALUE).intValueExact());
    assertEquals(Integer.MAX_VALUE-1, lf(Integer.MAX_VALUE-1L).intValueExact());
    
    assertEquals(Integer.MIN_VALUE, lf(Integer.MIN_VALUE).intValueExact());
    assertEquals(Integer.MIN_VALUE+1, lf(Integer.MIN_VALUE+1L).intValueExact());
  }
  
  @Test
  public void testShortValue() {
    assertEquals(Short.MAX_VALUE, lf(Short.MAX_VALUE).shortValue());
    assertEquals(Short.MAX_VALUE-1, lf(Short.MAX_VALUE-1L).shortValue());
    assertEquals(Short.MAX_VALUE, lf(Short.MAX_VALUE+1L).shortValue());
    
    assertEquals(Short.MIN_VALUE, lf(Short.MIN_VALUE).shortValue());
    assertEquals(Short.MIN_VALUE+1, lf(Short.MIN_VALUE+1L).shortValue());
    assertEquals(Short.MIN_VALUE, lf(Short.MIN_VALUE-1L).shortValue());
    
    assertEquals(Short.MAX_VALUE, lf(Short.MAX_VALUE).shortValueExact());
    assertEquals(Short.MAX_VALUE-1, lf(Short.MAX_VALUE-1L).shortValueExact());
    
    assertEquals(Short.MIN_VALUE, lf(Short.MIN_VALUE).shortValueExact());
    assertEquals(Short.MIN_VALUE+1, lf(Short.MIN_VALUE+1L).shortValueExact());
  }
  
  @Test
  public void testByteValue() {
    assertEquals(Byte.MAX_VALUE, lf(Byte.MAX_VALUE).byteValue());
    assertEquals(Byte.MAX_VALUE-1, lf(Byte.MAX_VALUE-1L).byteValue());
    assertEquals(Byte.MAX_VALUE, lf(Byte.MAX_VALUE+1L).byteValue());
    
    assertEquals(Byte.MIN_VALUE, lf(Byte.MIN_VALUE).byteValue());
    assertEquals(Byte.MIN_VALUE+1, lf(Byte.MIN_VALUE+1L).byteValue());
    assertEquals(Byte.MIN_VALUE, lf(Byte.MIN_VALUE-1L).byteValue());
    
    assertEquals(Byte.MAX_VALUE, lf(Byte.MAX_VALUE).byteValueExact());
    assertEquals(Byte.MAX_VALUE-1, lf(Byte.MAX_VALUE-1L).byteValueExact());
    
    assertEquals(Byte.MIN_VALUE, lf(Byte.MIN_VALUE).byteValueExact());
    assertEquals(Byte.MIN_VALUE+1, lf(Byte.MIN_VALUE+1L).byteValueExact());
  }
  
  //exception testing
  //---------------------------------------------------------------------------

  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull1() {
    LongFraction.valueOf((Number) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull2() {
    LongFraction.valueOf((Number) null, (Number) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull3() {
    LongFraction.valueOf(1, (Number) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull4() {
    LongFraction.valueOf((Number) null, 1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull5() {
    LongFraction.valueOf((String) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull6() {
    LongFraction.valueOf((String) null, 1);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow1() {
    lf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow2() {
    lf(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow3() {
    lf(Long.MAX_VALUE, Long.MIN_VALUE);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow4() {
    lf(Long.MIN_VALUE, -1);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow5() {
    lf("0.999999999999999999999999999999999999999999999999999999999999999999999999");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow6() {
    lf("-0.999999999999999999999999999999999999999999999999999999999999999999999999");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow7() {
    lf("0.500000000000000000000000000000000000000000000000000000000000000000000001");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow8() {
    lf("-0.500000000000000000000000000000000000000000000000000000000000000000000001");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow9() {
    lf("0.499999999999999999999999999999999999999999999999999999999999999999999999");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow10() {
    lf("-0.499999999999999999999999999999999999999999999999999999999999999999999999");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow11() {
    lf("0.000000000000000000000000000000000000000000000000000000000000000000000001");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow12() {
    lf("-0.000000000000000000000000000000000000000000000000000000000000000000000001");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow13() {
    lf(BigFraction.valueOf(1, BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow14() {
    lf(BigFraction.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)), 1);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow15() {
    lf(BigFraction.valueOf(1, BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testValueOfOverflow16() {
    lf(BigFraction.valueOf(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)), 1);
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString1() {
    LongFraction.valueOf("uh oh");
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString2() {
    LongFraction.valueOf("12345678", 8);
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString3() {
    LongFraction.valueOf("12.34(5)", 5);
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString4() {
    LongFraction.valueOf("5.4(321)", 5);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNaN() {
    lf(Double.NaN);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testPositiveInfinity() {
    lf(Double.POSITIVE_INFINITY);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNegativeInfinity() {
    lf(Double.NEGATIVE_INFINITY);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNaN_CustomNumber() {
    lf(new CustomNumber(Double.NaN));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testPositiveInfinity_CustomNumber() {
    lf(new CustomNumber(Double.POSITIVE_INFINITY));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNegativeInfinity_CustomNumber() {
    lf(new CustomNumber(Double.NEGATIVE_INFINITY));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testAddNull() {
    lf(4,3).add(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSumNull1() {
    LongFraction.sum(lf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSumNull2() {
    LongFraction.sum(null, lf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSumNull3() {
    LongFraction.sum(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSubtractNull() {
    lf(4,3).subtract(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSubtractFromNull() {
    lf(4,3).subtractFrom(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDifferenceNull1() {
    LongFraction.difference(lf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDifferenceNull2() {
    LongFraction.difference(null, lf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDifferenceNull3() {
    LongFraction.difference(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMultiplyNull() {
    lf(4,3).multiply(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testProductNull1() {
    LongFraction.product(lf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testProductNull2() {
    LongFraction.product(null, lf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testProductNull3() {
    LongFraction.product(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideNull() {
    lf(4,3).divide(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideIntoNull() {
    lf(4,3).divideInto(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testQuotientNull1() {
    LongFraction.quotient(lf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testQuotientNull2() {
    LongFraction.quotient(null, lf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testQuotientNull3() {
    LongFraction.quotient(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull1() {
    lf(4,3).divideToIntegralValue(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull2() {
    lf(4,3).divideToIntegralValue(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull3() {
    lf(4,3).divideToIntegralValue(null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull4() {
    lf(4,3).divideToIntegralValue(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull1() {
    lf(4,3).remainder(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull2() {
    lf(4,3).remainder(7, (DivisionMode)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull3() {
    lf(4,3).remainder(null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull4() {
    lf(4,3).remainder(null, (DivisionMode)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull1() {
    LongFraction.integralQuotient(7, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull2() {
    LongFraction.integralQuotient(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull3() {
    LongFraction.integralQuotient(null, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull4() {
    LongFraction.integralQuotient(7, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull5() {
    LongFraction.integralQuotient(null, 8, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull6() {
    LongFraction.integralQuotient(null, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull7() {
    LongFraction.integralQuotient(7, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull8() {
    LongFraction.integralQuotient(7, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull9() {
    LongFraction.integralQuotient(null, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull10() {
    LongFraction.integralQuotient(null, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull1() {
    LongFraction.remainder(7, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull2() {
    LongFraction.remainder(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull3() {
    LongFraction.remainder(null, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull4() {
    LongFraction.remainder(7, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull5() {
    LongFraction.remainder(null, 8, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull6() {
    LongFraction.remainder(null, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull7() {
    LongFraction.remainder(7, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull8() {
    LongFraction.remainder(7, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull9() {
    LongFraction.remainder(null, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull10() {
    LongFraction.remainder(null, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGcdNull() {
    lf(4,3).gcd(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLcmNull() {
    lf(4,3).lcm(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGetIntegerPartNull() {
    lf(4,3).getIntegerPart(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGetFractionPartNull() {
    lf(4,3).getFractionPart(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGetPartsNull() {
    lf(4,3).getParts(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundNull() {
    lf(4,3).round(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull1() {
    lf(4,3).roundToNumber(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull2() {
    lf(4,3).roundToNumber(lf(1,7), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull3() {
    lf(4,3).roundToNumber(null, RoundingMode.HALF_UP);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull4() {
    lf(4,3).roundToNumber(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToDenominatorNull() {
    lf(4,3).roundToDenominator(7l, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testToDecimalStringNull() {
    lf(4,3).toDecimalString(2, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testToRadixedStringNull() {
    lf(4,3).toRadixedString(10, 2, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testCompareToNull() {
    lf(4,3).compareTo(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyNextZero() {
    lf(4,3).fareyNext(0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyNextNegative() {
    lf(4,3).fareyNext(-1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyPrevZero() {
    lf(4,3).fareyPrev(0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyPrevNegative() {
    lf(4,3).fareyPrev(-1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyClosestZero() {
    lf(4,3).fareyClosest(0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyClosestNegative() {
    lf(4,3).fareyClosest(-1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMinNull() {
    lf(4,3).min(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMinNull1() {
    LongFraction.min(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMinNull2() {
    LongFraction.min(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMinNull3() {
    LongFraction.min(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMaxNull() {
    lf(4,3).max(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMaxNull1() {
    LongFraction.max(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMaxNull2() {
    LongFraction.max(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMaxNull3() {
    LongFraction.max(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMediantNull() {
    lf(4,3).mediant(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMediantNull1() {
    LongFraction.mediant(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMediantNull2() {
    LongFraction.mediant(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMediantNull3() {
    LongFraction.mediant(null, null);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator1() {
    lf(0,0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator2() {
    lf(100.9,0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator3() {
    lf("900/0");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator4() {
    lf(lf(900),lf(0,488));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero1() {
    lf(0).divide(lf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero2() {
    lf(10).divide(0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero3() {
    lf(90).divide(0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero4() {
    lf(16).divide(new BigDecimal("0"));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero5() {
    LongFraction.quotient(1, new BigDecimal("0"));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero6() {
    LongFraction.quotient(1, -0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero7() {
    LongFraction.quotient(1, lf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero8() {
    lf(0).divideInto(lf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero9() {
    lf(0).divideInto(7);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testNegateOverflow() {
    lf(Long.MIN_VALUE).negate();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideToIntegralValueZero1() {
    lf(1).divideToIntegralValue(lf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideToIntegralValueZero2() {
    lf(0).divideToIntegralValue(-0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRemainderZero1() {
    lf(1).remainder(lf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRemainderZero2() {
    lf(0).remainder(-0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideAndRemainderZero1() {
    lf(1).divideAndRemainder(lf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideAndRemainderZero2() {
    lf(0).divideAndRemainder(-0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroReciprocal() {
    LongFraction.ZERO.reciprocal();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroNegativePow() {
    LongFraction.ZERO.pow(-3);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToNumberZero() {
    lf(11,17).roundToNumber(BigInteger.ZERO);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToNumberNegative() {
    lf(11,17).roundToNumber(lf(-1,4));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToDenominatorZero() {
    lf(11,17).roundToDenominator(0L);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToDenominatorNegative() {
    lf(11,17).roundToDenominator(-4L);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactFraction() {
    lf(1,2).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactOverflowPositive() {
    lf(Integer.MAX_VALUE + 1L).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactOverflowNegative() {
    lf(Integer.MIN_VALUE - 1L).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactFraction() {
    lf(1,2).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactOverflowPositive() {
    lf(Short.MAX_VALUE + 1L).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactOverflowNegative() {
    lf(Short.MIN_VALUE - 1L).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactFraction() {
    lf(1,2).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactOverflowPositive() {
    lf(Integer.MAX_VALUE + 1L).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactOverflowNegative() {
    lf(Integer.MIN_VALUE - 1L).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactFraction() {
    lf(1,2).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactOverflowPositive() {
    lf(Double.MAX_VALUE).add(1).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactOverflowNegative() {
    lf(-Double.MAX_VALUE).subtract(1).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactUnderflowPositive() {
    lf(Double.MIN_VALUE, 2).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactUnderflowNegative() {
    lf(-Double.MIN_VALUE, 2).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid1() {
    lf(1, 10).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid2() {
    lf(1, 3).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid3() {
    lf(10, 3).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid4() {
    lf(Long.MAX_VALUE).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactOverflowPositive() {
    lf(Float.MAX_VALUE).add(1).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactOverflowNegative() {
    lf(-Float.MAX_VALUE).subtract(1).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactUnderflowPositive() {
    lf(Float.MIN_VALUE, 2).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactUnderflowNegative() {
    lf(-Float.MIN_VALUE, 2).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid1() {
    lf(1, 10).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid2() {
    lf(1, 3).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid3() {
    lf(10, 3).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid4() {
    lf(Integer.MAX_VALUE).floatValueExact();
  }
  
  /**
   * Helper class to reduce repetitive typing of tests for rounding. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for that rounding method.
   */
  private static class RoundingTest
  {
    private final String input;
    private final LongFraction lf;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public RoundingTest(String input, String up, String down, String ceiling, String floor,
            String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      lf = lf(input);
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
      for(RoundingMode mode : RoundingMode.values())
      {
        String actual;
        try {
          actual = Long.toString(lf.round(mode));
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("round(" + input + ", " + mode + ")", expected.get(mode), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ")", expected.get(RoundingMode.HALF_UP), Long.toString(lf.round()));
    }
  }
  
  
  /**
   * Helper class to reduce repetitive typing of tests for roundToNumber. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for that rounding method.
   */
  private static class RoundToNumberTest
  {
    private final String input;
    private final Number n;
    private final LongFraction lf;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public RoundToNumberTest(String input, Number n, String up, String down, String ceiling, String floor,
        String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      this.n = n;
      lf = lf(input);
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
      for(RoundingMode mode : RoundingMode.values())
      {
        String actual;
        try {
          actual = lf.roundToNumber(n, mode).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("(" + input + ").roundToNumber(" + n + ", " + mode + ")", expected.get(mode), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("(" + input + ").roundToNumber(" + n + ")", expected.get(RoundingMode.HALF_UP), lf.roundToNumber(n).toString());
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
    private final LongFraction lf;
    private final long denominator;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public RoundingToDenominatorTest(String input, long denominator, String up, String down, String ceiling, String floor,
            String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      lf = lf(input);
      this.denominator = denominator;
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
      for(RoundingMode mode : RoundingMode.values())
      {
        String actual;
        try {
          actual = Long.toString(lf.roundToDenominator(denominator, mode));
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("roundToDenominator(" + input + ", " + denominator + ", " + mode + ")", expected.get(mode), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ", " + denominator + ")", expected.get(RoundingMode.HALF_UP), Long.toString(lf.roundToDenominator(denominator)));
    }
  }
  
  /**
   * Helper class to reduce repetitive typing of tests for toRadixedString. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for that rounding method.
   */
  private static class ToRadixedStringTest
  {
    private final String input;
    private final LongFraction lf;
    private final int radix;
    private final int digits;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public ToRadixedStringTest(String input, int digits, String up, String down, String ceiling, String floor,
        String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this(input, 10, digits, up, down, ceiling, floor, halfUp, halfDown, halfEven, unnecessary);
    }
    
    public ToRadixedStringTest(String input, int radix, int digits, String up, String down, String ceiling, String floor,
            String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      lf = lf(input);
      this.radix = radix;
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
      for(RoundingMode mode : RoundingMode.values())
      {
        String actual;
        try {
          actual = lf.toRadixedString(radix, digits, mode).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("(" + input + ").toRadixedString(" + digits + ", " + mode + ")", expected.get(mode), actual);
        
        //toDecimalString() should be same as toRadixedString(10)
        if(radix == 10)
        {
          try {
            actual = lf.toDecimalString(digits, mode).toString();
          }
          catch(Exception e) {
            actual = e.getClass().getSimpleName();
          }
          assertEquals("(" + input + ").toDecimalString(" + radix + ", " + digits + ", " + mode + ")", expected.get(mode), actual);
        }
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("(" + input + ").toRadixedString(" + radix + ", " + digits + ")", expected.get(RoundingMode.HALF_UP), lf.toRadixedString(radix, digits));
      
      if(radix == 10)
        assertEquals("(" + input + ").toDecimalString(" + digits + ")", expected.get(RoundingMode.HALF_UP), lf.toDecimalString(digits));
    }
  }
  
  /**
   * Helper class to reduce repetitive typing of tests for getParts. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for each division mode.
   */
  private static class GetPartsTest
  {
    private final String input;
    private final LongFraction lf;
    private final Map<DivisionMode, String> expectedIPart = new HashMap<DivisionMode, String>();
    private final Map<DivisionMode, String> expectedFPart = new HashMap<DivisionMode, String>();
    
    public GetPartsTest(String input, String truncatedIPart, String truncatedFPart,
            String flooredIPart, String flooredFPart, String euclideanIPart, String euclideanFPart)
    {
      this.input = input;
      lf = lf(input);
      expectedIPart.put(DivisionMode.TRUNCATED, truncatedIPart);
      expectedFPart.put(DivisionMode.TRUNCATED, truncatedFPart);
      expectedIPart.put(DivisionMode.FLOORED, flooredIPart);
      expectedFPart.put(DivisionMode.FLOORED, flooredFPart);
      expectedIPart.put(DivisionMode.EUCLIDEAN, euclideanIPart);
      expectedFPart.put(DivisionMode.EUCLIDEAN, euclideanFPart);
    }
    
    public void test()
    {
      String actual;
      for(Map.Entry<DivisionMode, String> entry : expectedIPart.entrySet())
      {
        actual = Long.toString(lf.getIntegerPart(entry.getKey()));
        assertEquals("(" + input + ").getIntegerPart(" + entry.getKey() + ")", entry.getValue(), actual);
        
        actual = lf.getParts(entry.getKey())[0].toString();
        assertEquals("(" + input + ").getParts(" + entry.getKey() + ")[0]", entry.getValue(), actual);
      }
      
      for(Map.Entry<DivisionMode, String> entry : expectedFPart.entrySet())
      {
        actual = lf.getFractionPart(entry.getKey()).toString();
        assertEquals("(" + input + ").getFractionPart(" + entry.getKey() + ")", entry.getValue(), actual);
        
        actual = lf.getParts(entry.getKey())[1].toString();
        assertEquals("(" + input + ").getParts(" + entry.getKey() + ")[1]", entry.getValue(), actual);
      }
      
      //make sure that calling with no arguments is equivalent to TRUNCATED mode
      assertEquals("(" + input + ").getIntegerPart()",  expectedIPart.get(DivisionMode.TRUNCATED), Long.toString(lf.getIntegerPart()));
      assertEquals("(" + input + ").getFractionPart()", expectedFPart.get(DivisionMode.TRUNCATED), lf.getFractionPart().toString());
      assertEquals("(" + input + ").getParts()[0]", expectedIPart.get(DivisionMode.TRUNCATED), lf.getParts()[0].toString());
      assertEquals("(" + input + ").getParts()[1]", expectedFPart.get(DivisionMode.TRUNCATED), lf.getParts()[1].toString());
    }
  }
  
  /**
   * Helper class to reduce repetitive typing of tests for divideAndRemainder. Basically, you
   * call the constructor with the input value (as a String), and the expected output
   * for each division mode.
   */
  private static class DivideAndRemainderTest
  {
    private final String a;
    private final String b;
    private final LongFraction lfA;
    private final LongFraction lfB;
    private final Map<DivisionMode, String> expectedQ = new HashMap<DivisionMode, String>();
    private final Map<DivisionMode, String> expectedR = new HashMap<DivisionMode, String>();
    
    public DivideAndRemainderTest(String a, String b, String truncatedQ, String truncatedR,
        String flooredQ, String flooredR, String euclideanQ, String euclideanR)
    {
      this.a = a;
      lfA = lf(a);
      this.b = b;
      lfB = lf(b);
      expectedQ.put(DivisionMode.TRUNCATED, truncatedQ);
      expectedR.put(DivisionMode.TRUNCATED, truncatedR);
      expectedQ.put(DivisionMode.FLOORED, flooredQ);
      expectedR.put(DivisionMode.FLOORED, flooredR);
      expectedQ.put(DivisionMode.EUCLIDEAN, euclideanQ);
      expectedR.put(DivisionMode.EUCLIDEAN, euclideanR);
    }
    
    public void test()
    {
      for(DivisionMode mode : DivisionMode.values())
      {
        assertEquals("(" + a + ").divideToIntegralValue((" + b + "), " + mode + ")", expectedQ.get(mode), Long.toString(lfA.divideToIntegralValue(lfB, mode)));
        assertEquals("(" + a + ").remainder((" + b + "), " + mode + ")", expectedR.get(mode), lfA.remainder(lfB, mode).toString());
        assertEquals("(" + a + ").divideAndRemainder((" + b + "), " + mode + ")[0]", expectedQ.get(mode), lfA.divideAndRemainder(lfB, mode)[0].toString());
        assertEquals("(" + a + ").divideAndRemainder((" + b + "), " + mode + ")[1]", expectedR.get(mode), lfA.divideAndRemainder(lfB, mode)[1].toString());
        
        //check the static methods too
        assertEquals("LongFraction.integralQuotient((" + a + "), (" + b + "), " + mode + ")", expectedQ.get(mode), Long.toString(LongFraction.integralQuotient(lfA, lfB, mode)));
        assertEquals("LongFraction.remainder((" + a + "), (" + b + "), " + mode + ")", expectedR.get(mode), LongFraction.remainder(lfA, lfB, mode).toString());
        assertEquals("LongFraction.quotientAndRemainder((" + a + "), (" + b + "), " + mode + ")[0]", expectedQ.get(mode), LongFraction.quotientAndRemainder(lfA, lfB, mode)[0].toString());
        assertEquals("LongFraction.quotientAndRemainder((" + a + "), (" + b + "), " + mode + ")[1]", expectedR.get(mode), LongFraction.quotientAndRemainder(lfA, lfB, mode)[1].toString());
        
        //make sure that the math actually works out:  a/b = q + r/b, and also a = bq + r
        Number[] actuals = lfA.divideAndRemainder(lfB, mode);
        assertEquals("(" + a + ")/(" + b + ") = (" + actuals[0] + ") + (" + actuals[1] + ")/(" + b + ")", lf(lfA, lfB).toString(), lf(actuals[0]).add(lf(actuals[1], lfB)).toString());
        assertEquals("(" + a + ") = (" + b + ")(" + actuals[0] + ") + (" + actuals[1] + ")", lfA.toString(), lfB.multiply(actuals[0]).add(actuals[1]).toString());
      }
      
      //make sure that calling with no division mode is equivalent to TRUNCATED mode
      assertEquals("(" + a + ").divideToIntegralValue((" + b + "))",  expectedQ.get(DivisionMode.TRUNCATED), Long.toString(lfA.divideToIntegralValue(lfB)));
      assertEquals("(" + a + ").remainder((" + b + "))", expectedR.get(DivisionMode.TRUNCATED), lfA.remainder(lfB).toString());
      assertEquals("(" + a + ").divideAndRemainder((" + b + "))[0]", expectedQ.get(DivisionMode.TRUNCATED), lfA.divideAndRemainder(lfB)[0].toString());
      assertEquals("(" + a + ").divideAndRemainder((" + b + "))[1]", expectedR.get(DivisionMode.TRUNCATED), lfA.divideAndRemainder(lfB)[1].toString());
      
      //check the static methods too
      assertEquals("LongFraction.integralQuotient((" + a + "), (" + b + "))",  expectedQ.get(DivisionMode.TRUNCATED), Long.toString(LongFraction.integralQuotient(lfA, lfB)));
      assertEquals("LongFraction.remainder((" + a + "), (" + b + "))", expectedR.get(DivisionMode.TRUNCATED), LongFraction.remainder(lfA, lfB).toString());
      assertEquals("LongFraction.quotientAndRemainder((" + a + "), (" + b + "))[0]", expectedQ.get(DivisionMode.TRUNCATED), LongFraction.quotientAndRemainder(lfA, lfB)[0].toString());
      assertEquals("LongFraction.quotientAndRemainder((" + a + "), (" + b + "))[1]", expectedR.get(DivisionMode.TRUNCATED), LongFraction.quotientAndRemainder(lfA, lfB)[1].toString());
    }
  }
  
  /**
   * Custom implementation of Number class. Used in test to ensure that LongFraction.valueOf() falls back to doubleValue() if it
   * doesn't recognize the type. Returns hard-coded values for every getter except doubleValue().
   */
  private static class CustomNumber extends Number {
    
    private static final long serialVersionUID = 1L;
    private final double doubleVal;
    public CustomNumber(double doubleVal) { this.doubleVal = doubleVal; }
    
    @Override
    public int intValue() { return 98; }
    
    @Override
    public long longValue() { return 865; }
    
    @Override
    public float floatValue() { return 1437.625f; }
    
    @Override
    public double doubleValue() { return doubleVal; }
    
  }
  
  /**
   * Implements gcd() in a naive fashion, computing it as you might in grade school. This is to check against the optimized version.
   */
  private final static LongFraction naiveGCD(LongFraction f1, LongFraction f2) {
    Set<LongFraction> divisors = new HashSet<LongFraction>();
    
    f1 = f1.abs();
    f2 = f2.abs();
    
    if(f1.equals(f2))
      return f1;
    
    long d1 = 1;
    long d2 = 1;
    
    LongFraction lastDivisor1 = f1;
    LongFraction lastDivisor2 = f2;
    divisors.add(lastDivisor1);
    divisors.add(lastDivisor2);
    for(int i = 0; i < 10000; i++) {
      if(lastDivisor1.compareTo(lastDivisor2) > 0) {
        lastDivisor1 = f1.divide(++d1);
        if(divisors.contains(lastDivisor1))
          return lastDivisor1;
        else
          divisors.add(lastDivisor1);
      }
      else {
        lastDivisor2 = f2.divide(++d2);
        if(divisors.contains(lastDivisor2))
          return lastDivisor2;
        else
          divisors.add(lastDivisor2);
      }
    }
    
    //surely we will have a result after 10000 divisors...
    return null;
  }
  
  /**
   * Implements lcm() in a naive fashion, computing it as you might in grade school. This is to check against the optimized version.
   */
  private final static LongFraction naiveLCM(LongFraction f1, LongFraction f2) {
    Set<LongFraction> multiples = new HashSet<LongFraction>();
    
    f1 = f1.abs();
    f2 = f2.abs();
    
    if(f1.equals(f2))
      return f1;
    
    long m1 = 1;
    long m2 = 1;
    
    LongFraction lastMultiple1 = f1;
    LongFraction lastMultiple2 = f2;
    multiples.add(lastMultiple1);
    multiples.add(lastMultiple2);
    for(int i = 0; i < 10000; i++) {
      if(lastMultiple1.compareTo(lastMultiple2) < 0) {
        lastMultiple1 = f1.multiply(++m1);
        if(multiples.contains(lastMultiple1))
          return lastMultiple1;
        else
          multiples.add(lastMultiple1);
      }
      else {
        lastMultiple2 = f2.multiply(++m2);
        if(multiples.contains(lastMultiple2))
          return lastMultiple2;
        else
          multiples.add(lastMultiple2);
      }
    }
    
    //surely we will have a result after 10000 divisors...
    return null;
  }
  
  //helper functions to save typing...
  private final static LongFraction lf(Number n) { return LongFraction.valueOf(n); }
  private final static LongFraction lf(Number n, Number d) { return LongFraction.valueOf(n, d); }
  private final static LongFraction lf(String s) { return LongFraction.valueOf(s); }
}
