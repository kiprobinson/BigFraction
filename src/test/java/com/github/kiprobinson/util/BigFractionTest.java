package com.github.kiprobinson.util;

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
import com.github.kiprobinson.util.DivisionMode;

import org.junit.Test;


/**
 * JUnit tests for BigFraction class.
 * 
 * @author Kip Robinson, <a href="https://github.com/kiprobinson">https://github.com/kiprobinson</a>
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
    assertEquals("valueOf(0,19)", "0/1", BigFraction.valueOf(0,19).toString());
    assertEquals("valueOf(0.0,19.0)", "0/1", BigFraction.valueOf(0.0,19.0).toString());
    assertEquals("valueOf(\"dead/BEEF\", 16)", "57005/48879", BigFraction.valueOf("dead/BEEF", 16).toString());
    assertEquals("valueOf(\"lAzY.fOx\", 36)", "15459161339/15552", BigFraction.valueOf("lAzY.fOx", 36).toString());
    
    //invalid radix must convert convert to 10
    assertEquals("valueOf(\"13/5\", -1)", "13/5", BigFraction.valueOf("13/5", -1).toString());
    assertEquals("valueOf(\"13/5\", 0)", "13/5", BigFraction.valueOf("13/5", 0).toString());
    assertEquals("valueOf(\"13/5\", 1)", "13/5", BigFraction.valueOf("13/5", 1).toString());
    assertEquals("valueOf(\"13/5\", 37)", "13/5", BigFraction.valueOf("13/5", 37).toString());
    
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
    
    assertEquals("58/25", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("25/58", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("-58/25", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(-348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("-25/58", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(-15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("4/1", BigFraction.valueOf(14, new BigDecimal(BigInteger.valueOf(3500), 3)).toString());
    assertEquals("-1/4", BigFraction.valueOf(new BigDecimal(BigInteger.valueOf(3500), 3), -14).toString());
    
    //some doubles which should have simple, exact representations
    assertEquals("1/2", BigFraction.valueOf(0.5).toString());
    assertEquals("1/4", BigFraction.valueOf(0.25).toString());
    assertEquals("5/8", BigFraction.valueOf(0.625).toString());
    assertEquals("-3/2", BigFraction.valueOf(-1.5).toString());
    assertEquals("-9/4", BigFraction.valueOf(-2.25).toString());
    assertEquals("-29/8", BigFraction.valueOf(-3.625).toString());
    
    assertEquals("-2/9", BigFraction.valueOf(0.5, -2.25).toString());
    assertEquals("-2/9", BigFraction.valueOf(-2.0, 9.0).toString());
    
    assertEquals("valueOf(4.5, 0.625)", "36/5", BigFraction.valueOf(4.5, 0.625).toString());
    assertEquals("valueOf(0.625, 4.5)", "5/36", BigFraction.valueOf(0.625, 4.5).toString());
    assertEquals("valueOf(4.5, -0.625)", "-36/5", BigFraction.valueOf(4.5, -0.625).toString());
    assertEquals("valueOf(0.625, -4.5)", "-5/36", BigFraction.valueOf(0.625, -4.5).toString());
    
    //per spec, Double.MIN_VALUE == 2^(-1074)
    assertEquals("1/" + BigInteger.valueOf(2).pow(1074), BigFraction.valueOf(Double.MIN_VALUE).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(1074), BigFraction.valueOf(-Double.MIN_VALUE).toString());
    
    //per spec, Double.MIN_NORMAL == 2^(-1022)
    assertEquals("1/" + BigInteger.valueOf(2).pow(1022), BigFraction.valueOf(Double.MIN_NORMAL).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(1022), BigFraction.valueOf(-Double.MIN_NORMAL).toString());
    
    //per spec, Double.MAX_VALUE == (2-2^(-52))�2^(1023) == 2^(1024) - 2^(971)
    assertEquals(BigInteger.valueOf(2).pow(1024).subtract(BigInteger.valueOf(2).pow(971)).toString() + "/1", BigFraction.valueOf(Double.MAX_VALUE).toString());
    assertEquals(BigInteger.valueOf(2).pow(1024).subtract(BigInteger.valueOf(2).pow(971)).negate().toString() + "/1", BigFraction.valueOf(-Double.MAX_VALUE).toString());
    
    
    //per spec, Float.MIN_VALUE == 2^(-149)
    assertEquals("1/" + BigInteger.valueOf(2).pow(149), BigFraction.valueOf(Float.MIN_VALUE).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(149), BigFraction.valueOf(-Float.MIN_VALUE).toString());
    
    //per spec, Float.MIN_NORMAL == 2^(-126)
    assertEquals("1/" + BigInteger.valueOf(2).pow(126), BigFraction.valueOf(Float.MIN_NORMAL).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(126), BigFraction.valueOf(-Float.MIN_NORMAL).toString());
    
    //per spec, Float.MAX_VALUE == (2-2^(-23))�2^(127) == 2^(128) - 2^(104)
    assertEquals(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2).pow(104)).toString() + "/1", BigFraction.valueOf(Float.MAX_VALUE).toString());
    assertEquals(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2).pow(104)).negate().toString() + "/1", BigFraction.valueOf(-Float.MAX_VALUE).toString());
    
    //LongFraction -> BigFraction
    assertEquals("0/1", BigFraction.valueOf(LongFraction.ZERO).toString());
    assertEquals("1/1", BigFraction.valueOf(LongFraction.ONE).toString());
    assertEquals("-9223372036854775808/1", BigFraction.valueOf(LongFraction.valueOf(Long.MIN_VALUE)).toString());
    assertEquals("1/9223372036854775807", BigFraction.valueOf(1, LongFraction.valueOf(Long.MAX_VALUE)).toString());
  }
  
  @Test
  public void testValueOf_Repeating() {
    assertEquals("valueOf( \"0.(4)\")",  "4/9", BigFraction.valueOf( "0.(4)").toString());
    assertEquals("valueOf(\"+0.(4)\")",  "4/9", BigFraction.valueOf("+0.(4)").toString());
    assertEquals("valueOf(\"-0.(4)\")", "-4/9", BigFraction.valueOf("-0.(4)").toString());
    assertEquals("valueOf(  \".(4)\")",  "4/9", BigFraction.valueOf(  ".(4)").toString());
    assertEquals("valueOf( \"+.(4)\")",  "4/9", BigFraction.valueOf( "+.(4)").toString());
    assertEquals("valueOf( \"-.(4)\")", "-4/9", BigFraction.valueOf( "-.(4)").toString());
    
    assertEquals("valueOf( \"0.0(4)\")",  "2/45", BigFraction.valueOf( "0.0(4)").toString());
    assertEquals("valueOf(\"+0.0(4)\")",  "2/45", BigFraction.valueOf("+0.0(4)").toString());
    assertEquals("valueOf(\"-0.0(4)\")", "-2/45", BigFraction.valueOf("-0.0(4)").toString());
    assertEquals("valueOf(  \".0(4)\")",  "2/45", BigFraction.valueOf(  ".0(4)").toString());
    assertEquals("valueOf( \"+.0(4)\")",  "2/45", BigFraction.valueOf( "+.0(4)").toString());
    assertEquals("valueOf( \"-.0(4)\")", "-2/45", BigFraction.valueOf( "-.0(4)").toString());
    
    assertEquals("valueOf(\"0.444(4)\")", "4/9", BigFraction.valueOf("0.444(4)").toString());
    assertEquals("valueOf(\"0.4(444)\")", "4/9", BigFraction.valueOf("0.4(444)").toString());
    assertEquals("valueOf(\"0.044(4)\")", "2/45", BigFraction.valueOf("0.044(4)").toString());
    assertEquals("valueOf(\"0.0(444)\")", "2/45", BigFraction.valueOf("0.0(444)").toString());
    
    assertEquals("valueOf(\"0.(56)\")", "56/99", BigFraction.valueOf("0.(56)").toString());
    assertEquals("valueOf(\"0.5(65)\")", "56/99", BigFraction.valueOf("0.5(65)").toString());
    assertEquals("valueOf(\"0.56(56)\")", "56/99", BigFraction.valueOf("0.56(56)").toString());
    assertEquals("valueOf(\"0.565(6565)\")", "56/99", BigFraction.valueOf("0.565(6565)").toString());
    
    assertEquals("valueOf(\"0.(012)\")", "4/333", BigFraction.valueOf("0.(012)").toString());
    assertEquals("valueOf(\"0.(9)\")", "1/1", BigFraction.valueOf("0.(9)").toString());
    assertEquals("valueOf(\"0.000(4)\")", "1/2250", BigFraction.valueOf("0.000(4)").toString());
    assertEquals("valueOf(\"0.000(9)\")", "1/1000", BigFraction.valueOf("0.000(9)").toString());
    assertEquals("valueOf(\"0.000(120)\")", "1/8325", BigFraction.valueOf("0.000(120)").toString());
    assertEquals("valueOf(\"1.23(4)\")", "1111/900", BigFraction.valueOf("1.23(4)").toString());
    assertEquals("valueOf(\"0.3(789)\")", "631/1665", BigFraction.valueOf("0.3(789)").toString());
    
    assertEquals("valueOf(\"0.(012)/1.23(4)\")", "400/41107", BigFraction.valueOf("0.(012)/1.23(4)").toString());
    assertEquals("valueOf(\"0.(012)/1.6e3\")", "1/133200", BigFraction.valueOf("0.(012)/1.6e3").toString());
    
    assertEquals("valueOf(\"7.000(00)\")", "7/1", BigFraction.valueOf("7.000(00)").toString());
    assertEquals("valueOf(\"000.00(00)\")", "0/1", BigFraction.valueOf("000.00(00)").toString());
    
    //different bases
    assertEquals("valueOf(\"0.0(0011)\", 2)", "1/10", BigFraction.valueOf("0.0(0011)", 2).toString());
    assertEquals("valueOf(\"0.1(9))\", 16)", "1/10", BigFraction.valueOf("0.1(9)", 16).toString());
    assertEquals("valueOf(\"12.(a9))\", 11)", "1679/120", BigFraction.valueOf("12.(a9)", 11).toString());
    assertEquals("valueOf(\"-a.(i))\", 19)", "-11/1", BigFraction.valueOf("-a.(i)", 19).toString());
    assertEquals("valueOf(\"the.lazy(fox)\", 36)", "2994276908470787/78362484480", BigFraction.valueOf("the.lazy(fox)", 36).toString());
  }
  
  @Test
  public void testValueOf_CustomNumberInterface() {
    assertEquals("Custom Number representing an integer", "123456/1", bf(new CustomNumber(123456.0)).toString());
    assertEquals("Custom Number representing a floating-point number", "987653/8", bf(new CustomNumber(123456.625)).toString());
    assertEquals("Custom Number representing a negative number", "-1/8", bf(new CustomNumber(-0.125)).toString());
    assertEquals("Custom Number representing zero", "0/1", bf(new CustomNumber(0)).toString());
  }
  
  
  @Test
  public void testConstructor() {
    assertEquals("constructor(1.1)", "2476979795053773/2251799813685248", new BigFraction(1.1).toString());
    assertEquals("constructor(-0.0)", "0/1", new BigFraction(-0.0).toString());
    assertEquals("constructor(1.1f)", "9227469/8388608", new BigFraction(1.1f).toString());
    assertEquals("constructor(\"1.1\")", "11/10", new BigFraction("1.1").toString());
    assertEquals("constructor(11,10)", "11/10", new BigFraction(11,10).toString());
    assertEquals("constructor(2*5*7, 3*5*11)", "14/33", new BigFraction(2*5*7,3*5*11).toString());
    assertEquals("constructor(100,-7)", "-100/7", new BigFraction(100,-7).toString());
    assertEquals("constructor(\"-1.0E2/-0.007E3\")", "100/7", new BigFraction("-1.0E2/-0.007E3").toString());
    assertEquals("constructor(\"+9.02E-10\")", "451/500000000000", new BigFraction("+9.02E-10").toString());
    assertEquals("constructor(\"-0.000000E+500\")", "0/1", new BigFraction("-0.000000E+500").toString());
    assertEquals("constructor(0,19)", "0/1", new BigFraction(0,19).toString());
    assertEquals("valueOf(0.0,19.0)", "0/1", new BigFraction(0.0,19.0).toString());
    assertEquals("constructor(\"dead/BEEF\", 16)", "57005/48879", new BigFraction("dead/BEEF", 16).toString());
    assertEquals("constructor(\"lAzY.fOx\", 36)", "15459161339/15552", new BigFraction("lAzY.fOx", 36).toString());
    
    //invalid radix must convert convert to 10
    assertEquals("constructor(\"13/5\", -1)", "13/5", new BigFraction("13/5", -1).toString());
    assertEquals("constructor(\"13/5\", 0)", "13/5", new BigFraction("13/5", 0).toString());
    assertEquals("constructor(\"13/5\", 1)", "13/5", new BigFraction("13/5", 1).toString());
    assertEquals("constructor(\"13/5\", 37)", "13/5", new BigFraction("13/5", 37).toString());
    
    assertEquals("10/1", new BigFraction(new BigDecimal(BigInteger.valueOf(10), 0)).toString());
    assertEquals("10/1", new BigFraction(new BigDecimal(BigInteger.valueOf(1), -1)).toString());
    assertEquals("10/1", new BigFraction(new BigDecimal(BigInteger.valueOf(100), 1)).toString());
    
    assertEquals("123000/1", new BigFraction(new BigDecimal(BigInteger.valueOf(123), -3)).toString());
    assertEquals("12300/1", new BigFraction(new BigDecimal(BigInteger.valueOf(123), -2)).toString());
    assertEquals("1230/1", new BigFraction(new BigDecimal(BigInteger.valueOf(123), -1)).toString());
    assertEquals("123/1", new BigFraction(new BigDecimal(BigInteger.valueOf(123), 0)).toString());
    assertEquals("123/10", new BigFraction(new BigDecimal(BigInteger.valueOf(123), 1)).toString());
    assertEquals("123/100", new BigFraction(new BigDecimal(BigInteger.valueOf(123), 2)).toString());
    assertEquals("123/1000", new BigFraction(new BigDecimal(BigInteger.valueOf(123), 3)).toString());
    
    assertEquals("123/1", new BigFraction(1.23e2).toString());
    assertEquals("1230/1", new BigFraction(1.23e3).toString());
    assertEquals("12300/1", new BigFraction(1.23e4).toString());
    
    assertEquals("58/25", new BigFraction(new BigDecimal(BigInteger.valueOf(348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("25/58", new BigFraction(new BigDecimal(BigInteger.valueOf(15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("-58/25", new BigFraction(new BigDecimal(BigInteger.valueOf(-348), 2), new BigDecimal(BigInteger.valueOf(15), 1)).toString());
    assertEquals("-25/58", new BigFraction(new BigDecimal(BigInteger.valueOf(-15), 1), new BigDecimal(BigInteger.valueOf(348), 2)).toString());
    assertEquals("4/1", new BigFraction(14, new BigDecimal(BigInteger.valueOf(3500), 3)).toString());
    assertEquals("-1/4", new BigFraction(new BigDecimal(BigInteger.valueOf(3500), 3), -14).toString());
    
    //some doubles which should have simple, exact representations
    assertEquals("1/2", new BigFraction(0.5).toString());
    assertEquals("1/4", new BigFraction(0.25).toString());
    assertEquals("5/8", new BigFraction(0.625).toString());
    assertEquals("-3/2", new BigFraction(-1.5).toString());
    assertEquals("-9/4", new BigFraction(-2.25).toString());
    assertEquals("-29/8", new BigFraction(-3.625).toString());
    
    assertEquals("-2/9", new BigFraction(0.5, -2.25).toString());
    assertEquals("-2/9", new BigFraction(-2.0, 9.0).toString());
    
    assertEquals("valueOf(4.5, 0.625)", "36/5", new BigFraction(4.5, 0.625).toString());
    assertEquals("valueOf(0.625, 4.5)", "5/36", new BigFraction(0.625, 4.5).toString());
    assertEquals("valueOf(4.5, -0.625)", "-36/5", new BigFraction(4.5, -0.625).toString());
    assertEquals("valueOf(0.625, -4.5)", "-5/36", new BigFraction(0.625, -4.5).toString());
    
    //per spec, Double.MIN_VALUE == 2^(-1074)
    assertEquals("1/" + BigInteger.valueOf(2).pow(1074), new BigFraction(Double.MIN_VALUE).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(1074), new BigFraction(-Double.MIN_VALUE).toString());
    
    //per spec, Double.MIN_NORMAL == 2^(-1022)
    assertEquals("1/" + BigInteger.valueOf(2).pow(1022), new BigFraction(Double.MIN_NORMAL).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(1022), new BigFraction(-Double.MIN_NORMAL).toString());
    
    //per spec, Double.MAX_VALUE == (2-2^(-52))�2^(1023) == 2^(1024) - 2^(971)
    assertEquals(BigInteger.valueOf(2).pow(1024).subtract(BigInteger.valueOf(2).pow(971)).toString() + "/1", new BigFraction(Double.MAX_VALUE).toString());
    assertEquals(BigInteger.valueOf(2).pow(1024).subtract(BigInteger.valueOf(2).pow(971)).negate().toString() + "/1", new BigFraction(-Double.MAX_VALUE).toString());
    
    
    //per spec, Float.MIN_VALUE == 2^(-149)
    assertEquals("1/" + BigInteger.valueOf(2).pow(149), new BigFraction(Float.MIN_VALUE).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(149), new BigFraction(-Float.MIN_VALUE).toString());
    
    //per spec, Float.MIN_NORMAL == 2^(-126)
    assertEquals("1/" + BigInteger.valueOf(2).pow(126), new BigFraction(Float.MIN_NORMAL).toString());
    assertEquals("-1/" + BigInteger.valueOf(2).pow(126), new BigFraction(-Float.MIN_NORMAL).toString());
    
    //per spec, Float.MAX_VALUE == (2-2^(-23))�2^(127) == 2^(128) - 2^(104)
    assertEquals(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2).pow(104)).toString() + "/1", new BigFraction(Float.MAX_VALUE).toString());
    assertEquals(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2).pow(104)).negate().toString() + "/1", new BigFraction(-Float.MAX_VALUE).toString());
    
  }
  
  @Test
  public void testConstructor_Repeating() {
    assertEquals("constructor( \"0.(4)\")",  "4/9", new BigFraction( "0.(4)").toString());
    assertEquals("constructor(\"+0.(4)\")",  "4/9", new BigFraction("+0.(4)").toString());
    assertEquals("constructor(\"-0.(4)\")", "-4/9", new BigFraction("-0.(4)").toString());
    assertEquals("constructor(  \".(4)\")",  "4/9", new BigFraction(  ".(4)").toString());
    assertEquals("constructor( \"+.(4)\")",  "4/9", new BigFraction( "+.(4)").toString());
    assertEquals("constructor( \"-.(4)\")", "-4/9", new BigFraction( "-.(4)").toString());
    
    assertEquals("constructor( \"0.0(4)\")",  "2/45", new BigFraction( "0.0(4)").toString());
    assertEquals("constructor(\"+0.0(4)\")",  "2/45", new BigFraction("+0.0(4)").toString());
    assertEquals("constructor(\"-0.0(4)\")", "-2/45", new BigFraction("-0.0(4)").toString());
    assertEquals("constructor(  \".0(4)\")",  "2/45", new BigFraction(  ".0(4)").toString());
    assertEquals("constructor( \"+.0(4)\")",  "2/45", new BigFraction( "+.0(4)").toString());
    assertEquals("constructor( \"-.0(4)\")", "-2/45", new BigFraction( "-.0(4)").toString());
    
    assertEquals("constructor(\"0.444(4)\")", "4/9", new BigFraction("0.444(4)").toString());
    assertEquals("constructor(\"0.4(444)\")", "4/9", new BigFraction("0.4(444)").toString());
    assertEquals("constructor(\"0.044(4)\")", "2/45", new BigFraction("0.044(4)").toString());
    assertEquals("constructor(\"0.0(444)\")", "2/45", new BigFraction("0.0(444)").toString());
    
    assertEquals("constructor(\"0.(56)\")", "56/99", new BigFraction("0.(56)").toString());
    assertEquals("constructor(\"0.5(65)\")", "56/99", new BigFraction("0.5(65)").toString());
    assertEquals("constructor(\"0.56(56)\")", "56/99", new BigFraction("0.56(56)").toString());
    assertEquals("constructor(\"0.565(6565)\")", "56/99", new BigFraction("0.565(6565)").toString());
    
    assertEquals("constructor(\"0.(012)\")", "4/333", new BigFraction("0.(012)").toString());
    assertEquals("constructor(\"0.(9)\")", "1/1", new BigFraction("0.(9)").toString());
    assertEquals("constructor(\"0.000(4)\")", "1/2250", new BigFraction("0.000(4)").toString());
    assertEquals("constructor(\"0.000(9)\")", "1/1000", new BigFraction("0.000(9)").toString());
    assertEquals("constructor(\"0.000(120)\")", "1/8325", new BigFraction("0.000(120)").toString());
    assertEquals("constructor(\"1.23(4)\")", "1111/900", new BigFraction("1.23(4)").toString());
    assertEquals("constructor(\"0.3(789)\")", "631/1665", new BigFraction("0.3(789)").toString());
    
    assertEquals("constructor(\"0.(012)/1.23(4)\")", "400/41107", new BigFraction("0.(012)/1.23(4)").toString());
    assertEquals("constructor(\"0.(012)/1.6e3\")", "1/133200", new BigFraction("0.(012)/1.6e3").toString());
    
    assertEquals("constructor(\"7.000(00)\")", "7/1", new BigFraction("7.000(00)").toString());
    assertEquals("constructor(\"000.00(00)\")", "0/1", new BigFraction("000.00(00)").toString());
    
    //different bases
    assertEquals("constructor(\"0.0(0011)\", 2)", "1/10", new BigFraction("0.0(0011)", 2).toString());
    assertEquals("constructor(\"0.1(9))\", 16)", "1/10", new BigFraction("0.1(9)", 16).toString());
    assertEquals("constructor(\"12.(a9))\", 11)", "1679/120", new BigFraction("12.(a9)", 11).toString());
    assertEquals("constructor(\"-a.(i))\", 19)", "-11/1", new BigFraction("-a.(i)", 19).toString());
    assertEquals("constructor(\"the.lazy(fox)\", 36)", "2994276908470787/78362484480", new BigFraction("the.lazy(fox)", 36).toString());
  }
  
  @Test
  public void testConstructor_CustomNumberInterface() {
    assertEquals("Custom Number representing an integer", "123456/1", new BigFraction(new CustomNumber(123456.0)).toString());
    assertEquals("Custom Number representing a floating-point number", "987653/8", new BigFraction(new CustomNumber(123456.625)).toString());
    assertEquals("Custom Number representing a negative number", "-1/8", new BigFraction(new CustomNumber(-0.125)).toString());
    assertEquals("Custom Number representing zero", "0/1", new BigFraction(new CustomNumber(0)).toString());
  }
  
  
  @Test
  public void testAdd() {
    assertEquals("5/1 + -3", "2/1", bf(5).add(-3).toString());
    assertEquals("11/17 + 2/3", "67/51", bf("11/17").add(bf("2/3")).toString());
    assertEquals("1/6 + 1/15", "7/30", bf("1/6").add(bf("1/15")).toString());
    assertEquals("1/6 + 1/6", "1/3", bf("1/6").add(bf("1/6")).toString());
    assertEquals("-1/6 + 1/6", "0/1", bf("-1/6").add(bf("1/6")).toString());
    assertEquals("-1/6 + -1/6", "-1/3", bf("-1/6").add(bf("-1/6")).toString());
    assertEquals("-1/6 + 1/15", "-1/10", bf("-1/6").add(bf("1/15")).toString());
    assertEquals("1/7 + 0", "1/7", bf("1/7").add(0).toString());
    assertEquals("-1/7 + 0", "-1/7", bf("-1/7").add(0.0).toString());
  }
  
  @Test
  public void testSum() {
    assertEquals("2/1", BigFraction.sum(5, -3).toString());
    assertEquals("13/4", BigFraction.sum(6.5, -3.25f).toString());
    assertEquals("9937/100", BigFraction.sum(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testSubtract() {
    assertEquals("5/1 - -3/1", "8/1", bf(5).subtract(-3).toString());
    assertEquals("-5/1 - 2/1", "-7/1", bf(-5).subtract(2).toString());
    assertEquals("-5/1 - -2/1", "-3/1", bf(-5).subtract(-2).toString());
    assertEquals("11/17 - 2/3", "-1/51", bf("11/17").subtract(bf("2/3")).toString());
    assertEquals("1/6 - 1/6", "0/1", bf("1/6").subtract(bf("1/6")).toString());
    assertEquals("-1/6 - 1/6", "-1/3", bf("-1/6").subtract(bf("1/6")).toString());
    assertEquals("1/7 + 0", "1/7", bf("1/7").subtract(0).toString());
    assertEquals("-1/7 + 0", "-1/7", bf("-1/7").subtract(0.0).toString());
  }
  
  @Test
  public void testSubtractFrom() {
    assertEquals("-3/1 - 5/1", "-8/1", bf(5).subtractFrom(-3).toString());
    assertEquals("2/1 - -5/1", "7/1", bf(-5).subtractFrom(2).toString());
    assertEquals("-2/1 - -5/1", "3/1", bf(-5).subtractFrom(-2).toString());
    assertEquals("2/3 - 11/17", "1/51", bf("11/17").subtractFrom(bf("2/3")).toString());
    assertEquals("1/6 - 1/6", "0/1", bf("1/6").subtractFrom(bf("1/6")).toString());
    assertEquals("1/6 - -1/6", "1/3", bf("-1/6").subtractFrom(bf("1/6")).toString());
    assertEquals("1/7 + 0", "1/7", bf("-1/7").subtractFrom(0).toString());
    assertEquals("-1/7 + 0", "-1/7", bf("1/7").subtractFrom(0.0).toString());
  }
  
  @Test
  public void testDifference() {
    assertEquals("8/1", BigFraction.difference(5, -3).toString());
    assertEquals("39/4", BigFraction.difference(6.5, -3.25f).toString());
    assertEquals("3263/100", BigFraction.difference(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testMultiply() {
    assertEquals("(11/17)(0/1)", "0/1", bf("11/17").multiply(-0.0).toString());
    assertEquals("(1/3)(3/4)", "1/4", bf("1/3").multiply(bf("3/4")).toString());
    assertEquals("(-1/12)(16/5)", "-4/15", bf("-1/12").multiply(bf("16/5")).toString());
    assertEquals("(-7/6)(-5/9)", "35/54", bf("-7/6").multiply(bf("-5/9")).toString());
    assertEquals("(4/5)(-7/2)", "-14/5", bf("4/5").multiply(bf("7/-2")).toString());
    assertEquals("(1/7)(0)", "0/1", bf("1/7").multiply(0).toString());
    assertEquals("(-1/7)(0)", "0/1", bf("-1/7").multiply(0.0).toString());
    assertEquals("(1/7)(1)", "1/7", bf("1/7").multiply(1).toString());
    assertEquals("(-1/7)(1)", "-1/7", bf("-1/7").multiply(1.0).toString());
  }
  
  @Test
  public void testProduct() {
    assertEquals("-15/1", BigFraction.product(5, -3).toString());
    assertEquals("-169/8", BigFraction.product(6.5, -3.25f).toString());
    assertEquals("110121/50", BigFraction.product(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
  }
  
  @Test
  public void testDivide() {
    assertEquals("(1/3)/(4/3)", "1/4", bf("1/3").divide(bf("4/3")).toString());
    assertEquals("(-1/12)/(5/16)", "-4/15", bf("-1/12").divide(bf("5/16")).toString());
    assertEquals("(-7/6)/(-9/5)", "35/54", bf("-7/6").divide(bf("9/-5")).toString());
    assertEquals("(4/5)/(-2/7)", "-14/5", bf("4/5").divide(bf("-2/7")).toString());
    assertEquals("(1/7)/(1)", "1/7", bf("1/7").divide(1).toString());
    assertEquals("(-1/7)/(1)", "-1/7", bf("-1/7").divide(1.0).toString());
  }
  
  @Test
  public void testDivideInto() {
    assertEquals("(4/3)/(1/3)", "4/1", bf("1/3").divideInto(bf("4/3")).toString());
    assertEquals("(5/16)/(-1/12)", "-15/4", bf("-1/12").divideInto(bf("5/16")).toString());
    assertEquals("(-9/5)/(-7/6)", "54/35", bf("-7/6").divideInto(bf("9/-5")).toString());
    assertEquals("(-2/7)/(4/5)", "-5/14", bf("4/5").divideInto(bf("-2/7")).toString());
    assertEquals("(0)/(1/7)", "0/1", bf("1/7").divideInto(0).toString());
    assertEquals("(0)/(-1/7)", "0/1", bf("-1/7").divideInto(0.0).toString());
    assertEquals("(1)/(1/7)", "1/7", bf("7/1").divideInto(1).toString());
    assertEquals("(1)/(-1/7)", "-1/7", bf("-7/1").divideInto(1.0).toString());
  }
  
  @Test
  public void testQuotient() {
    assertEquals("-5/3", BigFraction.quotient(5, -3).toString());
    assertEquals("-2/1", BigFraction.quotient(6.5, -3.25f).toString());
    assertEquals("6600/3337", BigFraction.quotient(BigInteger.valueOf(66), new BigDecimal("33.37")).toString());
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
    assertEquals("(1/1).reciprocal", "1/1", bf(1.0).reciprocal().toString());
    assertEquals("(1/2).reciprocal", "2/1", bf(2,4).reciprocal().toString());
    assertEquals("(-10/-14).reciprocal", "7/5", bf(-10,-14).reciprocal().toString());
    assertEquals("(-6/1).reciprocal", "-1/6", bf(-6).reciprocal().toString());
  }
  
  @Test
  public void testComplement() {
    assertEquals("(10/14).complement", "2/7", bf(10,14).complement().toString());
    assertEquals("(1/1).complement", "0/1", bf(1).complement().toString());
    assertEquals("(-1/1).complement", "2/1", bf(-1).complement().toString());
    assertEquals("(0/1).complement", "1/1", bf(0).complement().toString());
    assertEquals("(17/11).complement", "-6/11", bf(17,11).complement().toString());
  }
  
  @Test
  public void testNegate() {
    assertEquals("(2/7).negate()", "-2/7", bf(2,7).negate().toString());
    assertEquals("(0/1).negate()", "0/1", bf(0).negate().toString());
    assertEquals("(-2/7).negate()", "2/7", bf(-2,7).negate().toString());
  }
  
  @Test
  public void testAbs() {
    assertEquals("(2/7).abs()", "2/7", bf(2,7).abs().toString());
    assertEquals("(0/1).abs()", "0/1", bf(0).abs().toString());
    assertEquals("(-2/7).abs()", "2/7", bf(-2,7).abs().toString());
  }
  
  @Test
  public void testWithSign() {
    assertEquals("(2/7).withSign(-1)", "-2/7", bf(2,7).withSign(-1).toString());
    assertEquals("(2/7).withSign(0)", "0/1", bf(2,7).withSign(0).toString());
    assertEquals("(2/7).withSign(1)", "2/7", bf(2,7).withSign(1).toString());
    
    assertEquals("(-2/7).withSign(-1)", "-2/7", bf(-2,7).withSign(-1).toString());
    assertEquals("(-2/7).withSign(0)", "0/1", bf(-2,7).withSign(0).toString());
    assertEquals("(-2/7).withSign(1)", "2/7", bf(-2,7).withSign(1).toString());
    
    //make sure we don't get exception when we try to set 0 to some sign.
    assertEquals("(0/7).withSign(-1)", "0/1", bf(0,7).withSign(-1).toString());
    assertEquals("(0/7).withSign(0)", "0/1", bf(0,7).withSign(0).toString());
    assertEquals("(0/7).withSign(1)", "0/1", bf(0,7).withSign(1).toString());
    
    //a signum value other than -1, 0, or 1 is ok too.
    assertEquals("(2/7).withSign(-999)", "-2/7", bf(2,7).withSign(-999).toString());
    assertEquals("(2/7).withSign(1048)", "2/7", bf(2,7).withSign(1048).toString());
    assertEquals("(-2/7).withSign(-999)", "-2/7", bf(-2,7).withSign(-999).toString());
    assertEquals("(-2/7).withSign(1048)", "2/7", bf(-2,7).withSign(1048).toString());
    assertEquals("(0).withSign(-999)", "0/1", bf(0.0).withSign(-999).toString());
    assertEquals("(0).withSign(1048)", "0/1", bf(0.0).withSign(1048).toString());
    
    //test with extreme values just to be safe
    assertEquals("(2/7).withSign(Integer.MIN_VALUE)", "-2/7", bf(2,7).withSign(Integer.MIN_VALUE).toString());
    assertEquals("(2/7).withSign(Integer.MAX_VALUE)", "2/7", bf(2,7).withSign(Integer.MAX_VALUE).toString());
    assertEquals("(-2/7).withSign(Integer.MIN_VALUE)", "-2/7", bf(-2,7).withSign(Integer.MIN_VALUE).toString());
    assertEquals("(-2/7).withSign(Integer.MAX_VALUE)", "2/7", bf(-2,7).withSign(Integer.MAX_VALUE).toString());
    assertEquals("(0).withSign(Integer.MIN_VALUE)", "0/1", bf(0.0).withSign(Integer.MIN_VALUE).toString());
    assertEquals("(0).withSign(Integer.MAX_VALUE)", "0/1", bf(0.0).withSign(Integer.MAX_VALUE).toString());
  }
  
  @Test
  public void testSignum() {
    assertEquals("(2/7).signum()", 1, bf(2,7).signum());
    assertEquals("(0/1).signum()", 0, bf(0).signum());
    assertEquals("(-2/7).signum()", -1, bf(-2,7).signum());
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
    assertEquals("minimum subnormal value", new BigDecimal(minSubnormal).toString(), bf(minSubnormal).toBigDecimal().toString());
    assertEquals("arbitrary subnormal value", new BigDecimal(arbSubnormal).toString(), bf(arbSubnormal).toBigDecimal().toString());
    assertEquals("maximum subnormal value", new BigDecimal(maxSubnormal).toString(), bf(maxSubnormal).toBigDecimal().toString());
    
    assertEquals("negative minimum subnormal value", new BigDecimal(negMinSubnormal).toString(), bf(negMinSubnormal).toBigDecimal().toString());
    assertEquals("negative arbitrary subnormal value", new BigDecimal(negArbSubnormal).toString(), bf(negArbSubnormal).toBigDecimal().toString());
    assertEquals("negative maximum subnormal value", new BigDecimal(negMaxSubnormal).toString(), bf(negMaxSubnormal).toBigDecimal().toString());
    
    //the minimum subnormal is 1/(2^1074). make sure we get that fraction
    BigInteger minExponent = BigInteger.valueOf(2).pow(1074);
    assertEquals("minimum subnormal fraction", "1/" + minExponent.toString(), bf(minSubnormal).toString());
    assertEquals("negative minimum subnormal fraction", "-1/" + minExponent.toString(), bf(negMinSubnormal).toString());
    
    //ensure that we are reducing correctly. 96/(2^1074) should reduce to 3/(2^1069)
    assertEquals("3/" + BigInteger.valueOf(2).pow(1069).toString(), bf(Double.longBitsToDouble(96L)).toString());
  }
  
  @Test
  public void testPow() {
    //Note: 0^0 returns 1 (just like Math.pow())
    assertEquals("(0/1)^(0)", "1/1", bf(0,1).pow(0).toString());
    assertEquals("(11/17)^(5)", "161051/1419857", bf(11,17).pow(5).toString());
    assertEquals("(11/17)^(-5)", "1419857/161051", bf(11,17).pow(-5).toString());
    assertEquals("(5/8)^(0)", "1/1", bf(5,8).pow(0).toString());
    assertEquals("(9/16)^(-1)", "16/9", bf(9,16).pow(-1).toString());
    assertEquals("(9/16)^(1)", "9/16", bf(9,16).pow(1).toString());
  }
  
  
  @Test
  public void testGcdAndLcm() {
    //first let's test the edge cases around zero
    assertEquals("11/17", bf(0).gcd(bf(11,17)).toString());
    assertEquals("19/81", bf(19,81).gcd(bf(0)).toString());
    assertEquals("0/1", bf(0).gcd(bf(0)).toString());
    assertEquals("0/1", bf(0).lcm(bf(11,17)).toString());
    assertEquals("0/1", bf(19,81).lcm(bf(0)).toString());
    assertEquals("0/1", bf(0).lcm(bf(0)).toString());
    
    final int MAX_VAL = 20;
    
    //Create a set of all positive fractions with numerator and denominator <= MAX_VAL.
    //There will be a lot of repetition but reducing to lowest terms should make HashSet
    //see them as equivalent
    Set<BigFraction> fracSet = new HashSet<BigFraction>();
    for(long d = 1; d <= MAX_VAL; d++) {
      for(long n = 1; n <= MAX_VAL; n++) {
        fracSet.add(bf(n, d));
      }
    }
    
    List<BigFraction> fracList = new ArrayList<BigFraction>(fracSet.size());
    fracList.addAll(fracSet);
    fracSet = null;
    
    for(int i = 0; i < fracList.size(); i++) {
      BigFraction a = fracList.get(i);
      for(int j = i; j < fracList.size(); j++) {
        BigFraction b = fracList.get(j);
        
        BigFraction exp_gcd = naiveGCD(a,b);
        BigFraction act_gcd = a.gcd(b);
        assertEquals("gcd(" + a + "," + b + ")", exp_gcd, act_gcd);
        
        BigFraction exp_lcm = naiveLCM(a,b);
        BigFraction act_lcm = a.lcm(b);
        assertEquals("lcm(" + a + "," + b + ")", exp_lcm, act_lcm);
        
        //make sure basic properties of gcd/lcm hold up
        assertTrue("(" + a + ")/(" + exp_gcd + ") is not an integer!", a.divide(act_gcd).getDenominator().equals(BigInteger.ONE));
        assertTrue("(" + b + ")/(" + exp_gcd + ") is not an integer!", a.divide(act_gcd).getDenominator().equals(BigInteger.ONE));
        assertTrue("(" + exp_lcm + ")/(" + a + ") is not an integer!", exp_lcm.divide(a).getDenominator().equals(BigInteger.ONE));
        assertTrue("(" + exp_lcm + ")/(" + b + ") is not an integer!", exp_lcm.divide(b).getDenominator().equals(BigInteger.ONE));
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
        if(a.getDenominator().equals(BigInteger.ONE) && b.getDenominator().equals(BigInteger.ONE)) {
          assertEquals("gcd of two integers is not an integer!", bf(a.getNumerator().gcd(b.getNumerator())), act_gcd);
          //no lcm() in BigInteger, so using lcm(a,b)=|a*b|/gcd(a,b)
          assertEquals("lcm of two integers is not an integer!", bf(a.getNumerator().multiply(b.getNumerator()).divide(a.getNumerator().gcd(b.getNumerator()))), act_lcm);
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
    
    //test static method
    assertEquals("mediant(1/1,1/2)", bf(1,2), BigFraction.mediant(bf(1,1), bf(1,3)));
    assertEquals("mediant(2/2,1/2)", bf(1,2), BigFraction.mediant(bf(2,2), bf(1,3)));
    assertEquals("mediant(3/29,7/15)", bf(5,22), BigFraction.mediant(bf(3,29), bf(7,15)));
    
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
    //a few simple cases- if we are in the sequence already, it should just return what was passed in
    assertEquals("4/3", bf(4,3).fareyClosest(3).toString());
    assertEquals("4/3", bf(4,3).fareyClosest(4).toString());
    assertEquals("4/3", bf(4,3).fareyClosest(99).toString());
    
    assertEquals("-4/3", bf(-4,3).fareyClosest(3).toString());
    assertEquals("-4/3", bf(-4,3).fareyClosest(4).toString());
    assertEquals("-4/3", bf(-4,3).fareyClosest(99).toString());
    
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
  public void testToString() {
    assertEquals("1/1", bf("1").toString());
    assertEquals("1/1", bf("1").toString(false));
    assertEquals("1", bf("1").toString(true));
    
    assertEquals("0/1", bf("0").toString());
    assertEquals("0/1", bf("0").toString(false));
    assertEquals("0", bf("0").toString(true));
    
    assertEquals("-1/1", bf("-1").toString());
    assertEquals("-1/1", bf("-1").toString(false));
    assertEquals("-1", bf("-1").toString(true));
    
    assertEquals("1/10", bf(".1").toString());
    assertEquals("1/10", bf(".1").toString(false));
    assertEquals("1/10", bf(".1").toString(true));
    
    assertEquals("-10/3", bf("10/-3").toString());
    assertEquals("-10/3", bf("10/-3").toString(false));
    assertEquals("-10/3", bf("10/-3").toString(true));
  }
  
  @Test
  public void testToString_radix() {
    assertEquals("10/1", bf("16").toString(16));
    assertEquals("-ff/ac", bf("255/-172").toString(16));
    assertEquals("101/101010101", bf("5/341").toString(2));
    assertEquals("zyx/d", bf("46617/13").toString(36));
    
    assertEquals("zyx/1", bf("46617").toString(36, false));
    assertEquals("zyx", bf("46617").toString(36, true));
    
    //radix must be in range 2-36
    assertEquals("46617/13", bf("46617/13").toString(37));
    assertEquals("46617/13", bf("46617/13").toString(0));
    assertEquals("46617/13", bf("46617/13").toString(1));
    assertEquals("46617/13", bf("46617/13").toString(-7));
  }
  
  @Test
  public void testToMixedString() {
    assertEquals("4/3 == 1 1/3", "1 1/3", bf("4/3").toMixedString());
    assertEquals("-4/3 == -1 1/3", "-1 1/3", bf("-4/3").toMixedString());
    assertEquals("6/3 == 2", bf("6/3").toMixedString(), "2");
    assertEquals("6/-3 == -2", bf("6/-3").toMixedString(), "-2");
    assertEquals("2/3 == 2/3", bf("2/3").toMixedString(), "2/3");
    assertEquals("2/-3 == -2/3", bf("2/-3").toMixedString(), "-2/3");
    assertEquals("0/3 == 0", bf("0/3").toMixedString(), "0");
    assertEquals("0/-3 == 0", bf("0/-3").toMixedString(), "0");
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
    new RoundToNumberTest("4/7", bf(1,7), "4/7", "4/7", "4/7", "4/7", "4/7", "4/7", "4/7", "4/7").test();
    new RoundToNumberTest("2/3", bf(1,7), "5/7", "4/7", "5/7", "4/7", "5/7", "5/7", "5/7", "ArithmeticException").test();
    new RoundToNumberTest("31/14", bf(1,7), "16/7", "15/7", "16/7", "15/7", "16/7", "15/7", "16/7", "ArithmeticException").test();
    new RoundToNumberTest("33/14", bf(1,7), "17/7", "16/7", "17/7", "16/7", "17/7", "16/7", "16/7", "ArithmeticException").test();
    new RoundToNumberTest("1649/11", bf(17,11), "1649/11", "1649/11", "1649/11", "1649/11", "1649/11", "1649/11", "1649/11", "1649/11").test();
    new RoundToNumberTest("1650/11", bf(17,11), "1666/11", "1649/11", "1666/11", "1649/11", "1649/11", "1649/11", "1649/11", "ArithmeticException").test();
    new RoundToNumberTest("1648/11", bf(17,11), "1649/11", "1632/11", "1649/11", "1632/11", "1649/11", "1649/11", "1649/11", "ArithmeticException").test();
    
    new RoundToNumberTest("0.49/16", bf(1,16), "1/16", "0/1", "1/16", "0/1", "0/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("0.50/16", bf(1,16), "1/16", "0/1", "1/16", "0/1", "1/16", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("0.51/16", bf(1,16), "1/16", "0/1", "1/16", "0/1", "1/16", "1/16", "1/16", "ArithmeticException").test();
    
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
    new RoundToNumberTest("-4/7", bf(1,7), "-4/7", "-4/7", "-4/7", "-4/7", "-4/7", "-4/7", "-4/7", "-4/7").test();
    new RoundToNumberTest("-2/3", bf(1,7), "-5/7", "-4/7", "-4/7", "-5/7", "-5/7", "-5/7", "-5/7", "ArithmeticException").test();
    new RoundToNumberTest("-31/14", bf(1,7), "-16/7", "-15/7", "-15/7", "-16/7", "-16/7", "-15/7", "-16/7", "ArithmeticException").test();
    new RoundToNumberTest("-33/14", bf(1,7), "-17/7", "-16/7", "-16/7", "-17/7", "-17/7", "-16/7", "-16/7", "ArithmeticException").test();
    new RoundToNumberTest("-1649/11", bf(17,11), "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11").test();
    new RoundToNumberTest("-1650/11", bf(17,11), "-1666/11", "-1649/11", "-1649/11", "-1666/11", "-1649/11", "-1649/11", "-1649/11", "ArithmeticException").test();
    new RoundToNumberTest("-1648/11", bf(17,11), "-1649/11", "-1632/11", "-1632/11", "-1649/11", "-1649/11", "-1649/11", "-1649/11", "ArithmeticException").test();
    
    new RoundToNumberTest("-0.49/16", bf(1,16), "-1/16", "0/1", "0/1", "-1/16", "0/1", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("-0.50/16", bf(1,16), "-1/16", "0/1", "0/1", "-1/16", "-1/16", "0/1", "0/1", "ArithmeticException").test();
    new RoundToNumberTest("-0.51/16", bf(1,16), "-1/16", "0/1", "0/1", "-1/16", "-1/16", "-1/16", "-1/16", "ArithmeticException").test();
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
    assertEquals("333.333333333333333", bf("1000/3").toBigDecimal().toString());
    assertEquals("-3.33333333333333333", bf("-10/3").toBigDecimal().toString());
    assertEquals("0.333333333333333333", bf("1/3").toBigDecimal().toString());
    assertEquals("-0.00333333333333333333", bf("-1/300").toBigDecimal().toString());
    
    assertEquals("-300", bf("-300").toBigDecimal().toString());
    assertEquals("3", bf("3").toBigDecimal().toString());
    assertEquals("-0.3", bf("-3/10").toBigDecimal().toString());
    assertEquals("0.003", bf("3/1000").toBigDecimal().toString());
    
    //714285 714285 714285
    assertEquals("71.4286", bf("500/7").toBigDecimal(6).toString());
    assertEquals("-0.714286", bf("-5/7").toBigDecimal(6).toString());
    assertEquals("0.0714286", bf("5/70").toBigDecimal(6).toString());
    assertEquals("-0.000714286", bf("-5/7000").toBigDecimal(6).toString());
    
    //if numerator or denominator has more than 18 significant digits, we should use as many as the larger
    assertEquals("0.124999998860937500", bf("1234567890123456789012345/9876543210987654321098765").toBigDecimal().toString());
    assertEquals("1.01249999988609375E-25", bf("1/9876543210987654321098765").toBigDecimal().toString());
    assertEquals("1.76366841446208113E+23", bf("1234567890123456789012345/7").toBigDecimal().toString());
    
    assertEquals("3.07445734561825860E+18", bf(Long.MAX_VALUE, 3).toBigDecimal().toString());
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
    assertEquals("\"1.0\".toRepeatingDigitString(10, true)", "0.(9)", bf("1.0").toRepeatingDigitString(10, true));
    assertEquals("\"10.0\".toRepeatingDigitString(10, true)", "9.(9)", bf("10.0").toRepeatingDigitString(10, true));
    assertEquals("\"0.1\".toRepeatingDigitString(10, true)", "0.0(9)", bf("0.1").toRepeatingDigitString(10, true));
    assertEquals("\"0.01\".toRepeatingDigitString(10, true)", "0.00(9)", bf("0.01").toRepeatingDigitString(10, true));
    assertEquals("\"0.02\".toRepeatingDigitString(10, true)", "0.01(9)", bf("0.02").toRepeatingDigitString(10, true));
    assertEquals("\"5.1\".toRepeatingDigitString(10, true)", "5.0(9)", bf("5.1").toRepeatingDigitString(10, true));
    assertEquals("\"5.101\".toRepeatingDigitString(10, true)", "5.100(9)", bf("5.101").toRepeatingDigitString(10, true));
    
    assertEquals("\"1.0\".toRepeatingDigitString(10, false)", "1.0", bf("1.0").toRepeatingDigitString(10, false));
    assertEquals("\"10.0\".toRepeatingDigitString(10, false)", "10.0", bf("10.0").toRepeatingDigitString(10, false));
    assertEquals("\"0.1\".toRepeatingDigitString(10, false)", "0.1", bf("0.1").toRepeatingDigitString(10, false));
    assertEquals("\"0.01\".toRepeatingDigitString(10, false)", "0.01", bf("0.01").toRepeatingDigitString(10, false));
    assertEquals("\"0.02\".toRepeatingDigitString(10, false)", "0.02", bf("0.02").toRepeatingDigitString(10, false));
    assertEquals("\"5.1\".toRepeatingDigitString(10, false)", "5.1", bf("5.1").toRepeatingDigitString(10, false));
    assertEquals("\"5.101\".toRepeatingDigitString(10, false)", "5.101", bf("5.101").toRepeatingDigitString(10, false));
    
    
    assertEquals("\"4/9\".toRepeatingDigitString(10, false)", "0.(4)", bf("4/9").toRepeatingDigitString(10, false));
    assertEquals("\"4/9\".toRepeatingDigitString(10, true)", "0.(4)", bf("4/9").toRepeatingDigitString(10, true));
    assertEquals("\"-4/9\".toRepeatingDigitString(10, false)", "-0.(4)", bf("-4/9").toRepeatingDigitString(10, false));
    assertEquals("\"-4/9\".toRepeatingDigitString(10, true)", "-0.(4)", bf("-4/9").toRepeatingDigitString(10, true));
    
    assertEquals("\"2/45\".toRepeatingDigitString(10, false)", "0.0(4)", bf("2/45").toRepeatingDigitString(10, false));
    assertEquals("\"2/45\".toRepeatingDigitString(10, true)", "0.0(4)", bf("2/45").toRepeatingDigitString(10, true));
    assertEquals("\"-2/45\".toRepeatingDigitString(10, false)", "-0.0(4)", bf("-2/45").toRepeatingDigitString(10, false));
    assertEquals("\"-2/45\".toRepeatingDigitString(10, true)", "-0.0(4)", bf("-2/45").toRepeatingDigitString(10, true));
    
    assertEquals("\"56/99\".toRepeatingDigitString(10, false)", "0.(56)", bf("56/99").toRepeatingDigitString(10, false));
    
    assertEquals("\"4/333\".toRepeatingDigitString(10, false)", "0.(012)", bf("4/333").toRepeatingDigitString(10, false));
    assertEquals("\"1/1\".toRepeatingDigitString(10, false)", "1.0", bf("1/1").toRepeatingDigitString(10, false));
    assertEquals("\"1/1\".toRepeatingDigitString(10, true)", "0.(9)", bf("1/1").toRepeatingDigitString(10, true));
    assertEquals("\"1/2250\".toRepeatingDigitString(10, false)", "0.000(4)", bf("1/2250").toRepeatingDigitString(10, false));
    assertEquals("\"1/1000\".toRepeatingDigitString(10, false)", "0.001", bf("1/1000").toRepeatingDigitString(10, false));
    assertEquals("\"1/1000\".toRepeatingDigitString(10, true)", "0.000(9)", bf("1/1000").toRepeatingDigitString(10, true));
    assertEquals("\"1/8325\".toRepeatingDigitString(10, false)", "0.00(012)", bf("1/8325").toRepeatingDigitString(10, false));
    assertEquals("\"1111/900\".toRepeatingDigitString(10, false)", "1.23(4)", bf("1111/900").toRepeatingDigitString(10, false));
    assertEquals("\"631/1665\".toRepeatingDigitString(10, false)", "0.3(789)", bf("631/1665").toRepeatingDigitString(10, false));
    
    assertEquals("\"7/1\".toRepeatingDigitString(10, false)", "7.0", bf("7/1").toRepeatingDigitString(10, false));
    assertEquals("\"7/1\".toRepeatingDigitString(10, true)", "6.(9)", bf("7/1").toRepeatingDigitString(10, true));
    assertEquals("\"0/1\".toRepeatingDigitString(10, false)", "0.0", bf("0/1").toRepeatingDigitString(10, false));
    assertEquals("\"0/1\".toRepeatingDigitString(10, true)", "0.(0)", bf("0/1").toRepeatingDigitString(10, true));
    
    //different bases
    assertEquals("\"1/10\".toRepeatingDigitString(2, false)", "0.0(0011)", bf("1/10").toRepeatingDigitString(2, false));
    assertEquals("\"1/10\".toRepeatingDigitString(2, true)", "0.0(0011)", bf("1/10").toRepeatingDigitString(2, true));
    assertEquals("\"1/10\".toRepeatingDigitString(16, false)", "0.1(9)", bf("1/10").toRepeatingDigitString(16, false));
    assertEquals("\"1/10\".toRepeatingDigitString(16, true)", "0.1(9)", bf("1/10").toRepeatingDigitString(16, true));
    assertEquals("\"-11/1\".toRepeatingDigitString(19, false)", "-b.0", bf("-11/1").toRepeatingDigitString(19, false));
    assertEquals("\"-11/1\".toRepeatingDigitString(19, true)", "-a.(i)", bf("-11/1").toRepeatingDigitString(19, true));
    assertEquals("\"2994276908470787/78362484480\".toRepeatingDigitString(36, false)", "the.lazy(fox)", bf("2994276908470787/78362484480").toRepeatingDigitString(36, false));
    assertEquals("\"2994276908470787/78362484480\".toRepeatingDigitString(36, true)", "the.lazy(fox)", bf("2994276908470787/78362484480").toRepeatingDigitString(36, true));
    
    //optional args should be same as (10, false)
    assertEquals("\"12.0\".toRepeatingDigitString()", "12.0", bf("12.0").toRepeatingDigitString());
    assertEquals("\"12.0\".toRepeatingDigitString(16)", "c.0", bf("12.0").toRepeatingDigitString(16));
    assertEquals("\"12.0\".toRepeatingDigitString(true)", "11.(9)", bf("12.0").toRepeatingDigitString(true));
    assertEquals("\"12.0\".toRepeatingDigitString(16, true)", "b.(f)", bf("12.0").toRepeatingDigitString(16, true));
    
    //invalid radix equivalent to 10
    assertEquals("\"12.0\".toRepeatingDigitString(-100)", "12.0", bf("12.0").toRepeatingDigitString(-100));
    assertEquals("\"12.0\".toRepeatingDigitString(-1)", "12.0", bf("12.0").toRepeatingDigitString(-1));
    assertEquals("\"12.0\".toRepeatingDigitString(0)", "12.0", bf("12.0").toRepeatingDigitString(0));
    assertEquals("\"12.0\".toRepeatingDigitString(1)", "12.0", bf("12.0").toRepeatingDigitString(1));
    assertEquals("\"12.0\".toRepeatingDigitString(37)", "12.0", bf("12.0").toRepeatingDigitString(37));
    assertEquals("\"12.0\".toRepeatingDigitString(100)", "12.0", bf("12.0").toRepeatingDigitString(100));
  }
  
  @Test
  public void testEquals() {
    assertTrue(bf(5,7).equals(bf(15,21)));
    assertTrue(bf(5,10).equals(BigFraction.ONE_HALF));
    assertTrue(BigFraction.TEN.equals(bf(BigInteger.valueOf(10))));
    assertTrue(bf(1,2).equals(bf(0.5)));
    assertTrue(bf(1,2).equals(bf("1/2")));
    assertTrue(bf(1,2).equals(bf("0.5")));
    assertTrue(bf(1,2).equals(bf("5e-1")));
    assertTrue(bf(5,1).equals(bf(5)));
    assertFalse(bf(5,1).equals(5));
    assertFalse(bf(5,1).equals(BigInteger.valueOf(5)));
    assertFalse(bf(7,10).equals(new BigDecimal("0.70")));
    assertFalse(bf(3,4).equals(null));
    assertFalse(bf(3,4).equals("3/4"));
    assertFalse(bf(3,4).equals(0.75));
    
    assertTrue(bf(-11,17).equals(bf("-11/17")));
    assertTrue(BigFraction.ZERO.equals(bf(0)));
    assertFalse(BigFraction.ZERO.equals(0));
    assertFalse(BigFraction.ZERO.equals(0.0));
    assertFalse(BigFraction.ZERO.equals(-0.0));
    assertFalse(BigFraction.ZERO.equals(BigInteger.ZERO));
    
    assertFalse(bf(1,10).equals(bf(0.1)));
    assertFalse(bf(1,10).equals(0.1));
  }
  
  @Test
  public void testEqualsNumber() {
    assertTrue(bf(5,7).equalsNumber(bf(15,21)));
    assertTrue(bf(5,10).equalsNumber(BigFraction.ONE_HALF));
    assertTrue(BigFraction.TEN.equalsNumber(bf(BigInteger.valueOf(10))));
    assertTrue(bf(1,2).equalsNumber(bf(0.5)));
    assertTrue(bf(1,2).equalsNumber(bf("1/2")));
    assertTrue(bf(1,2).equalsNumber(bf("0.5")));
    assertTrue(bf(1,2).equalsNumber(bf("5e-1")));
    assertTrue(bf(5,1).equalsNumber(bf(5)));
    assertTrue(bf(5,1).equalsNumber(5));
    assertTrue(bf(5,1).equalsNumber(BigInteger.valueOf(5)));
    assertTrue(bf(7,10).equalsNumber(new BigDecimal("0.70")));
    assertFalse(bf(3,4).equalsNumber(null));
    assertTrue(bf(3,4).equalsNumber(0.75));
    
    assertTrue(bf(-11,17).equalsNumber(bf("-11/17")));
    assertTrue(BigFraction.ZERO.equalsNumber(bf(0)));
    assertTrue(BigFraction.ZERO.equalsNumber(0));
    assertTrue(BigFraction.ZERO.equalsNumber(0.0));
    assertTrue(BigFraction.ZERO.equalsNumber(-0.0));
    assertTrue(BigFraction.ZERO.equalsNumber(BigInteger.ZERO));
    
    assertFalse(bf(1,10).equalsNumber(bf(0.1)));
    assertFalse(bf(1,10).equalsNumber(0.1));
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
        BigFraction bf = bf(num, den);
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
    
    //throw in min/max test here too
    for(int i = 0; i < lst.size(); i++) {
      for(int j = 0; j < lst.size(); j++) {
        BigFraction lhs = lst.get(i);
        BigFraction rhs = lst.get(j);
        if(i <= j) {
          assertEquals(lhs, lhs.min(rhs));
          assertEquals(lhs, BigFraction.min(lhs, rhs));
          assertEquals(rhs, lhs.max(rhs));
          assertEquals(rhs, BigFraction.max(lhs, rhs));
        }
        else {
          assertEquals(rhs, lhs.min(rhs));
          assertEquals(rhs, BigFraction.min(lhs, rhs));
          assertEquals(lhs, lhs.max(rhs));
          assertEquals(lhs, BigFraction.max(lhs, rhs));
        }
      }
    }
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
        BigFraction bf = bf(num, den);
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
    //test strategy here: split up all possible double values into NUM_TESTS tests, roughly evenly
    //distributed. Then check that converting from double to BigFraction and back to double returns
    //exactly equivalent result.
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
      BigFraction f = bf(in);
      double out = f.doubleValue();
      assertEquals("failed to get back the same double we put in for raw bits: " + i, in, out, 0.0);
      
      //this should not throw exception, because we started with an exact double.
      double exactOut = f.doubleValueExact();
      assertEquals("failed to get back the same double we put in, using doubleValueExact(), for raw bits: " + i, in, exactOut, 0.0);
      
      //make sure we get same behavior with negative value
      BigFraction negF = bf(-in);
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
    //test behavior with +0.0 and -0.0. Since BigFraction does not have concept of negative zero, both should be equal to +0.0.
    final double POSITIVE_ZERO = +0.0;
    final double NEGATIVE_ZERO = -0.0;
    
    //confirm our test setup here, just to be sure...
    assertEquals("Test setup issue: +0.0 doesn't have expected raw bits.", 0x0000000000000000L, Double.doubleToRawLongBits(POSITIVE_ZERO));
    assertEquals("Test setup issue: -0.0 doesn't have expected raw bits.", 0x8000000000000000L, Double.doubleToRawLongBits(NEGATIVE_ZERO));
    
    BigFraction f = bf(POSITIVE_ZERO);
    assertEquals("doubleValue for positive zero", 0L, Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for positive zero", 0L, Double.doubleToRawLongBits(f.doubleValueExact()));
    
    f = bf(NEGATIVE_ZERO);
    assertEquals("doubleValue for negative zero", 0L, Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for negative zero", 0L, Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //per spec, Double.MIN_VALUE == 2^(-1074)
    f = bf(1, BigInteger.valueOf(2).pow(1074));
    assertEquals("doubleValue for positive Double.MIN_VALUE", Double.doubleToRawLongBits(Double.MIN_VALUE), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for positive Double.MIN_VALUE", Double.doubleToRawLongBits(Double.MIN_VALUE), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    f = bf(-1, BigInteger.valueOf(2).pow(1074));
    assertEquals("doubleValue for negative Double.MIN_VALUE", Double.doubleToRawLongBits(-Double.MIN_VALUE), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for negative Double.MIN_VALUE", Double.doubleToRawLongBits(-Double.MIN_VALUE), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //per spec, Double.MIN_NORMAL == 2^(-1022)
    f = bf(1, BigInteger.valueOf(2).pow(1022));
    assertEquals("doubleValue for positive Double.MIN_NORMAL", Double.doubleToRawLongBits(Double.MIN_NORMAL), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for positive Double.MIN_NORMAL", Double.doubleToRawLongBits(Double.MIN_NORMAL), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    f = bf(-1, BigInteger.valueOf(2).pow(1022));
    assertEquals("doubleValue for negative Double.MIN_NORMAL", Double.doubleToRawLongBits(-Double.MIN_NORMAL), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for negative Double.MIN_NORMAL", Double.doubleToRawLongBits(-Double.MIN_NORMAL), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //per spec, Double.MAX_VALUE == (2-2^(-52))�2^(1023) == 2^(1024) - 2^(971)
    f = bf(BigInteger.valueOf(2).pow(1024).subtract(BigInteger.valueOf(2).pow(971)));
    assertEquals("doubleValue for positive Double.MAX_VALUE", Double.doubleToRawLongBits(Double.MAX_VALUE), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for positive Double.MAX_VALUE", Double.doubleToRawLongBits(Double.MAX_VALUE), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    f = bf(BigInteger.valueOf(2).pow(1024).subtract(BigInteger.valueOf(2).pow(971)).negate());
    assertEquals("doubleValue for negative Double.MAX_VALUE", Double.doubleToRawLongBits(-Double.MAX_VALUE), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValueExact for negative Double.MAX_VALUE", Double.doubleToRawLongBits(-Double.MAX_VALUE), Double.doubleToRawLongBits(f.doubleValueExact()));
    
    //overflow beyond max value
    f = bf(BigInteger.valueOf(2).pow(1026));
    assertEquals("doubleValue larger than Double.MAX_VALUE", Double.doubleToRawLongBits(Double.POSITIVE_INFINITY), Double.doubleToRawLongBits(f.doubleValue()));
    assertEquals("doubleValue smaller than -Double.MAX_VALUE", Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY), Double.doubleToRawLongBits(f.negate().doubleValue()));
  }
  
  @Test
  public void testFloatValue() {
    //test strategy here: split up all possible float values into NUM_TESTS tests, roughly evenly
    //distributed. Then check that converting from float to BigFraction and back to float returns
    //exactly equivalent result.
    final long MIN = Float.floatToRawIntBits(Float.MIN_VALUE);
    final long MAX = Float.floatToRawIntBits(Float.MAX_VALUE);
    
    //Selected 60k tests because that is about how many took 1 second for me.
    //Then I picked the closest prime to that number to use as the divisor
    final long NUM_TESTS = 59999L;
    final long DELTA = (MAX-MIN)/NUM_TESTS;
    
    for(long i = MIN; i <= MAX; i+= DELTA)
    {
      float in = Float.intBitsToFloat((int)i);
      BigFraction f = bf(in);
      float out = f.floatValue();
      assertEquals("failed to get back the same float we put in for raw bits: " + i, in, out, 0.0f);
      
      //this should not throw exception, because we started with an exact float.
      float exactOut = f.floatValueExact();
      assertEquals("failed to get back the same float we put in, using floatValueExact(), for raw bits: " + i, in, exactOut, 0.0);
      
      //make sure we get same behavior with negative value
      BigFraction negF = bf(-in);
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
    //test behavior with +0.0 and -0.0. Since BigFraction does not have concept of negative zero, both should be equal to +0.0.
    final float POSITIVE_ZERO = +0.0f;
    final float NEGATIVE_ZERO = -0.0f;
    
    //confirm our test setup here, just to be sure...
    assertEquals("Test setup issue: +0.0f doesn't have expected raw bits.", 0x00000000, Float.floatToRawIntBits(POSITIVE_ZERO));
    assertEquals("Test setup issue: -0.0f doesn't have expected raw bits.", 0x80000000, Float.floatToRawIntBits(NEGATIVE_ZERO));
    
    BigFraction f = bf(POSITIVE_ZERO);
    assertEquals("floatValue for positive zero", 0, Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for positive zero", 0, Float.floatToRawIntBits(f.floatValueExact()));
    
    f = bf(NEGATIVE_ZERO);
    assertEquals("floatValue for negative zero", 0, Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for negative zero", 0, Float.floatToRawIntBits(f.floatValueExact()));
    
    //per spec, Float.MIN_VALUE == 2^(-149)
    f = bf(1, BigInteger.valueOf(2).pow(149));
    assertEquals("floatValue for positive Float.MIN_VALUE", Float.floatToRawIntBits(Float.MIN_VALUE), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for positive Float.MIN_VALUE", Float.floatToRawIntBits(Float.MIN_VALUE), Float.floatToRawIntBits(f.floatValueExact()));
    
    f = bf(-1, BigInteger.valueOf(2).pow(149));
    assertEquals("floatValue for negative Float.MIN_VALUE", Float.floatToRawIntBits(-Float.MIN_VALUE), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for negative Float.MIN_VALUE", Float.floatToRawIntBits(-Float.MIN_VALUE), Float.floatToRawIntBits(f.floatValueExact()));
    
    //per spec, Float.MIN_NORMAL == 2^(-126)
    f = bf(1, BigInteger.valueOf(2).pow(126));
    assertEquals("floatValue for positive Float.MIN_NORMAL", Float.floatToRawIntBits(Float.MIN_NORMAL), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for positive Float.MIN_NORMAL", Float.floatToRawIntBits(Float.MIN_NORMAL), Float.floatToRawIntBits(f.floatValueExact()));
    
    f = bf(-1, BigInteger.valueOf(2).pow(126));
    assertEquals("floatValue for negative Float.MIN_NORMAL", Float.floatToRawIntBits(-Float.MIN_NORMAL), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for negative Float.MIN_NORMAL", Float.floatToRawIntBits(-Float.MIN_NORMAL), Float.floatToRawIntBits(f.floatValueExact()));
    
    //per spec, Float.MAX_VALUE == (2-2^(-23))�2^(127) == 2^(128) - 2^(104)
    f = bf(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2).pow(104)));
    assertEquals("floatValue for positive Float.MAX_VALUE", Float.floatToRawIntBits(Float.MAX_VALUE), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for positive Float.MAX_VALUE", Float.floatToRawIntBits(Float.MAX_VALUE), Float.floatToRawIntBits(f.floatValueExact()));
    
    f = bf(BigInteger.valueOf(2).pow(128).subtract(BigInteger.valueOf(2).pow(104)).negate());
    assertEquals("floatValue for negative Float.MAX_VALUE", Float.floatToRawIntBits(-Float.MAX_VALUE), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValueExact for negative Float.MAX_VALUE", Float.floatToRawIntBits(-Float.MAX_VALUE), Float.floatToRawIntBits(f.floatValueExact()));
    
    //overflow beyond max value
    f = bf(BigInteger.valueOf(2).pow(130));
    assertEquals("floatValue larger than Float.MAX_VALUE", Float.floatToRawIntBits(Float.POSITIVE_INFINITY), Float.floatToRawIntBits(f.floatValue()));
    assertEquals("floatValue smaller than -Float.MAX_VALUE", Float.floatToRawIntBits(Float.NEGATIVE_INFINITY), Float.floatToRawIntBits(f.negate().floatValue()));
  }
  
  @Test
  public void testLongValue() {
    assertEquals(Long.MAX_VALUE, bf(Long.MAX_VALUE).longValue());
    assertEquals(Long.MAX_VALUE-1, bf(Long.MAX_VALUE-1).longValue());
    assertEquals(Long.MAX_VALUE, bf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)).longValue());
    
    assertEquals(Long.MIN_VALUE, bf(Long.MIN_VALUE).longValue());
    assertEquals(Long.MIN_VALUE+1, bf(Long.MIN_VALUE+1).longValue());
    assertEquals(Long.MIN_VALUE, bf(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)).longValue());
    
    assertEquals(Long.MAX_VALUE, bf(Long.MAX_VALUE).longValueExact());
    assertEquals(Long.MAX_VALUE-1, bf(Long.MAX_VALUE-1).longValueExact());
    
    assertEquals(Long.MIN_VALUE, bf(Long.MIN_VALUE).longValueExact());
    assertEquals(Long.MIN_VALUE+1, bf(Long.MIN_VALUE+1).longValueExact());
  }
  
  @Test
  public void testIntValue() {
    assertEquals(Integer.MAX_VALUE, bf(Integer.MAX_VALUE).intValue());
    assertEquals(Integer.MAX_VALUE-1, bf(Integer.MAX_VALUE-1L).intValue());
    assertEquals(Integer.MAX_VALUE, bf(Integer.MAX_VALUE+1L).intValue());
    
    assertEquals(Integer.MIN_VALUE, bf(Integer.MIN_VALUE).intValue());
    assertEquals(Integer.MIN_VALUE+1, bf(Integer.MIN_VALUE+1L).intValue());
    assertEquals(Integer.MIN_VALUE, bf(Integer.MIN_VALUE-1L).intValue());
    
    assertEquals(Integer.MAX_VALUE, bf(Integer.MAX_VALUE).intValueExact());
    assertEquals(Integer.MAX_VALUE-1, bf(Integer.MAX_VALUE-1L).intValueExact());
    
    assertEquals(Integer.MIN_VALUE, bf(Integer.MIN_VALUE).intValueExact());
    assertEquals(Integer.MIN_VALUE+1, bf(Integer.MIN_VALUE+1L).intValueExact());
  }
  
  @Test
  public void testShortValue() {
    assertEquals(Short.MAX_VALUE, bf(Short.MAX_VALUE).shortValue());
    assertEquals(Short.MAX_VALUE-1, bf(Short.MAX_VALUE-1L).shortValue());
    assertEquals(Short.MAX_VALUE, bf(Short.MAX_VALUE+1L).shortValue());
    
    assertEquals(Short.MIN_VALUE, bf(Short.MIN_VALUE).shortValue());
    assertEquals(Short.MIN_VALUE+1, bf(Short.MIN_VALUE+1L).shortValue());
    assertEquals(Short.MIN_VALUE, bf(Short.MIN_VALUE-1L).shortValue());
    
    assertEquals(Short.MAX_VALUE, bf(Short.MAX_VALUE).shortValueExact());
    assertEquals(Short.MAX_VALUE-1, bf(Short.MAX_VALUE-1L).shortValueExact());
    
    assertEquals(Short.MIN_VALUE, bf(Short.MIN_VALUE).shortValueExact());
    assertEquals(Short.MIN_VALUE+1, bf(Short.MIN_VALUE+1L).shortValueExact());
  }
  
  @Test
  public void testByteValue() {
    assertEquals(Byte.MAX_VALUE, bf(Byte.MAX_VALUE).byteValue());
    assertEquals(Byte.MAX_VALUE-1, bf(Byte.MAX_VALUE-1L).byteValue());
    assertEquals(Byte.MAX_VALUE, bf(Byte.MAX_VALUE+1L).byteValue());
    
    assertEquals(Byte.MIN_VALUE, bf(Byte.MIN_VALUE).byteValue());
    assertEquals(Byte.MIN_VALUE+1, bf(Byte.MIN_VALUE+1L).byteValue());
    assertEquals(Byte.MIN_VALUE, bf(Byte.MIN_VALUE-1L).byteValue());
    
    assertEquals(Byte.MAX_VALUE, bf(Byte.MAX_VALUE).byteValueExact());
    assertEquals(Byte.MAX_VALUE-1, bf(Byte.MAX_VALUE-1L).byteValueExact());
    
    assertEquals(Byte.MIN_VALUE, bf(Byte.MIN_VALUE).byteValueExact());
    assertEquals(Byte.MIN_VALUE+1, bf(Byte.MIN_VALUE+1L).byteValueExact());
  }
  
  //exception testing
  //---------------------------------------------------------------------------
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull1() {
    BigFraction.valueOf((Number) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull2() {
    BigFraction.valueOf((Number) null, (Number) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull3() {
    BigFraction.valueOf(1, (Number) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull4() {
    BigFraction.valueOf((Number) null, 1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull5() {
    BigFraction.valueOf((String) null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testValueOfNull6() {
    BigFraction.valueOf((String) null, 1);
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString1() {
    BigFraction.valueOf("uh oh");
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString2() {
    BigFraction.valueOf("12345678", 8);
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString3() {
    BigFraction.valueOf("12.34(5)", 5);
  }
  
  @Test(expected=NumberFormatException.class)
  public void testValueOfString4() {
    BigFraction.valueOf("5.4(321)", 5);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNaN() {
    bf(Double.NaN);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testPositiveInfinity() {
    bf(Double.POSITIVE_INFINITY);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNegativeInfinity() {
    bf(Double.NEGATIVE_INFINITY);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNaN_CustomNumber() {
    bf(new CustomNumber(Double.NaN));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testPositiveInfinity_CustomNumber() {
    bf(new CustomNumber(Double.POSITIVE_INFINITY));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testNegativeInfinity_CustomNumber() {
    bf(new CustomNumber(Double.NEGATIVE_INFINITY));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testAddNull() {
    bf(4,3).add(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSumNull1() {
    BigFraction.sum(bf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSumNull2() {
    BigFraction.sum(null, bf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSumNull3() {
    BigFraction.sum(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSubtractNull() {
    bf(4,3).subtract(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testSubtractFromNull() {
    bf(4,3).subtractFrom(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDifferenceNull1() {
    BigFraction.difference(bf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDifferenceNull2() {
    BigFraction.difference(null, bf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDifferenceNull3() {
    BigFraction.difference(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMultiplyNull() {
    bf(4,3).multiply(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testProductNull1() {
    BigFraction.product(bf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testProductNull2() {
    BigFraction.product(null, bf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testProductNull3() {
    BigFraction.product(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideNull() {
    bf(4,3).divide(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideIntoNull() {
    bf(4,3).divideInto(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testQuotientNull1() {
    BigFraction.quotient(bf(4,3), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testQuotientNull2() {
    BigFraction.quotient(null, bf(4,3));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testQuotientNull3() {
    BigFraction.quotient(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull1() {
    bf(4,3).divideToIntegralValue(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull2() {
    bf(4,3).divideToIntegralValue(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull3() {
    bf(4,3).divideToIntegralValue(null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testDivideToIntegralValueNull4() {
    bf(4,3).divideToIntegralValue(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull1() {
    bf(4,3).remainder(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull2() {
    bf(4,3).remainder(7, (DivisionMode)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull3() {
    bf(4,3).remainder(null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRemainderNull4() {
    bf(4,3).remainder(null, (DivisionMode)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull1() {
    BigFraction.integralQuotient(7, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull2() {
    BigFraction.integralQuotient(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull3() {
    BigFraction.integralQuotient(null, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull4() {
    BigFraction.integralQuotient(7, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull5() {
    BigFraction.integralQuotient(null, 8, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull6() {
    BigFraction.integralQuotient(null, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull7() {
    BigFraction.integralQuotient(7, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull8() {
    BigFraction.integralQuotient(7, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull9() {
    BigFraction.integralQuotient(null, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testIntegralQuotientNull10() {
    BigFraction.integralQuotient(null, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull1() {
    BigFraction.remainder(7, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull2() {
    BigFraction.remainder(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull3() {
    BigFraction.remainder(null, (Number)null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull4() {
    BigFraction.remainder(7, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull5() {
    BigFraction.remainder(null, 8, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull6() {
    BigFraction.remainder(null, null, DivisionMode.TRUNCATED);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull7() {
    BigFraction.remainder(7, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull8() {
    BigFraction.remainder(7, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull9() {
    BigFraction.remainder(null, 8, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticRemainderNull10() {
    BigFraction.remainder(null, null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGcdNull() {
    bf(4,3).gcd(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testLcmNull() {
    bf(4,3).lcm(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGetIntegerPartNull() {
    bf(4,3).getIntegerPart(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGetFractionPartNull() {
    bf(4,3).getFractionPart(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testGetPartsNull() {
    bf(4,3).getParts(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundNull() {
    bf(4,3).round(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull1() {
    bf(4,3).roundToNumber(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull2() {
    bf(4,3).roundToNumber(bf(1,7), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull3() {
    bf(4,3).roundToNumber(null, RoundingMode.HALF_UP);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToNumberNull4() {
    bf(4,3).roundToNumber(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToDenominatorNull1() {
    bf(4,3).roundToDenominator(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToDenominatorNull2() {
    bf(4,3).roundToDenominator(BigInteger.valueOf(7), null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToDenominatorNull3() {
    bf(4,3).roundToDenominator(null, RoundingMode.HALF_UP);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRoundToDenominatorNull4() {
    bf(4,3).roundToDenominator(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testToDecimalStringNull() {
    bf(4,3).toDecimalString(2, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testToRadixedStringNull() {
    bf(4,3).toRadixedString(10, 2, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testCompareToNull() {
    bf(4,3).compareTo(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyNextZero() {
    bf(4,3).fareyNext(0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyNextNegative() {
    bf(4,3).fareyNext(-1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyPrevZero() {
    bf(4,3).fareyPrev(0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyPrevNegative() {
    bf(4,3).fareyPrev(-1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyClosestZero() {
    bf(4,3).fareyClosest(0);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testFareyClosestNegative() {
    bf(4,3).fareyClosest(-1);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMinNull() {
    bf(4,3).min(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMinNull1() {
    BigFraction.min(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMinNull2() {
    BigFraction.min(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMinNull3() {
    BigFraction.min(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMaxNull() {
    bf(4,3).max(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMaxNull1() {
    BigFraction.max(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMaxNull2() {
    BigFraction.max(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMaxNull3() {
    BigFraction.max(null, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMediantNull() {
    bf(4,3).mediant(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMediantNull1() {
    BigFraction.mediant(7, null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMediantNull2() {
    BigFraction.mediant(null, 8);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testStaticMediantNull3() {
    BigFraction.mediant(null, null);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator1() {
    bf(0,0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator2() {
    bf(100.9,0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator3() {
    bf("900/0");
  }
  
  @Test(expected=ArithmeticException.class)
  public void testZeroDenominator4() {
    bf(bf(900),bf(0,488));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero1() {
    bf(0).divide(bf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero2() {
    bf(10).divide(0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero3() {
    bf(90).divide(0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero4() {
    bf(16).divide(new BigDecimal("0"));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero5() {
    BigFraction.quotient(1, new BigDecimal("0"));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero6() {
    BigFraction.quotient(1, -0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero7() {
    BigFraction.quotient(1, bf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero8() {
    bf(0).divideInto(bf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideZero9() {
    bf(0).divideInto(7);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideToIntegralValueZero1() {
    bf(1).divideToIntegralValue(bf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideToIntegralValueZero2() {
    bf(0).divideToIntegralValue(-0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRemainderZero1() {
    bf(1).remainder(bf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRemainderZero2() {
    bf(0).remainder(-0.0);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideAndRemainderZero1() {
    bf(1).divideAndRemainder(bf(0));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDivideAndRemainderZero2() {
    bf(0).divideAndRemainder(-0.0);
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
  public void testRoundToNumberZero() {
    bf(11,17).roundToNumber(BigInteger.ZERO);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToNumberNegative() {
    bf(11,17).roundToNumber(bf(-1,4));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToDenominatorZero() {
    bf(11,17).roundToDenominator(BigInteger.ZERO);
  }
  
  @Test(expected=ArithmeticException.class)
  public void testRoundToDenominatorNegative() {
    bf(11,17).roundToDenominator(BigInteger.valueOf(-4));
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactOverflowPositive() {
    bf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactOverflowNegative() {
    bf(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testLongValueExactFraction() {
    bf(bf(1,2)).longValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactOverflowPositive() {
    bf(Integer.MAX_VALUE + 1L).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactOverflowNegative() {
    bf(Integer.MIN_VALUE - 1L).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testIntValueExactFraction() {
    bf(bf(1,2)).intValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactOverflowPositive() {
    bf(Short.MAX_VALUE + 1L).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactOverflowNegative() {
    bf(Short.MIN_VALUE - 1L).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testShortValueExactFraction() {
    bf(bf(1,2)).shortValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactOverflowPositive() {
    bf(Integer.MAX_VALUE + 1L).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactOverflowNegative() {
    bf(Integer.MIN_VALUE - 1L).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testByteValueExactFraction() {
    bf(bf(1,2)).byteValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactOverflowPositive1() {
    bf(Double.MAX_VALUE).add(1).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactOverflowNegative1() {
    bf(-Double.MAX_VALUE).subtract(1).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactOverflowPositive2() {
    bf(Double.MAX_VALUE).multiply(4).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactOverflowNegative2() {
    bf(-Double.MAX_VALUE).multiply(4).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactUnderflowPositive() {
    bf(Double.MIN_VALUE, 2).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactUnderflowNegative() {
    bf(-Double.MIN_VALUE, 2).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid1() {
    bf(1, 10).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid2() {
    bf(1, 3).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid3() {
    bf(10, 3).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testDoubleValueExactInvalid4() {
    bf(Long.MAX_VALUE).doubleValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactOverflowPositive1() {
    bf(Float.MAX_VALUE).add(1).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactOverflowNegative1() {
    bf(-Float.MAX_VALUE).subtract(1).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactOverflowPositive2() {
    bf(Float.MAX_VALUE).multiply(4).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactOverflowNegative2() {
    bf(-Float.MAX_VALUE).multiply(4).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactUnderflowPositive() {
    bf(Float.MIN_VALUE, 2).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactUnderflowNegative() {
    bf(-Float.MIN_VALUE, 2).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid1() {
    bf(1, 10).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid2() {
    bf(1, 3).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid3() {
    bf(10, 3).floatValueExact();
  }
  
  @Test(expected=ArithmeticException.class)
  public void testFloatValueExactInvalid4() {
    bf(Integer.MAX_VALUE).floatValueExact();
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
      bf = bf(input);
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
          actual = bf.round(mode).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("round(" + input + ", " + mode + ")", expected.get(mode), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ")", expected.get(RoundingMode.HALF_UP), bf.round().toString());
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
    private final BigFraction bf;
    private final Map<RoundingMode, String> expected = new HashMap<RoundingMode, String>();
    
    public RoundToNumberTest(String input, Number n, String up, String down, String ceiling, String floor,
        String halfUp, String halfDown, String halfEven, String unnecessary)
    {
      this.input = input;
      this.n = n;
      bf = bf(input);
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
          actual = bf.roundToNumber(n, mode).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("(" + input + ").roundToNumber(" + n + ", " + mode + ")", expected.get(mode), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("(" + input + ").roundToNumber(" + n + ")", expected.get(RoundingMode.HALF_UP), bf.roundToNumber(n).toString());
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
      bf = bf(input);
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
      for(RoundingMode mode : RoundingMode.values())
      {
        String actual;
        try {
          actual = bf.roundToDenominator(denominator, mode).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("roundToDenominator(" + input + ", " + denominator + ", " + mode + ")", expected.get(mode), actual);
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("round(" + input + ", " + denominator + ")", expected.get(RoundingMode.HALF_UP), bf.roundToDenominator(denominator).toString());
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
    private final BigFraction bf;
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
      bf = bf(input);
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
          actual = bf.toRadixedString(radix, digits, mode).toString();
        }
        catch(Exception e) {
          actual = e.getClass().getSimpleName();
        }
        assertEquals("(" + input + ").toRadixedString(" + radix + ", " + digits + ", " + mode + ")", expected.get(mode), actual);
        
        //toDecimalString() should be same as toRadixedString(10)
        if(radix == 10)
        {
          try {
            actual = bf.toDecimalString(digits, mode).toString();
          }
          catch(Exception e) {
            actual = e.getClass().getSimpleName();
          }
          assertEquals("(" + input + ").toDecimalString(" + digits + ", " + mode + ")", expected.get(mode), actual);
        }
      }
      
      //test that default rounding mode is the same as HALF_UP
      assertEquals("(" + input + ").toRadixedString(" + radix + ", " + digits + ")", expected.get(RoundingMode.HALF_UP), bf.toRadixedString(radix, digits));
      
      if(radix == 10)
        assertEquals("(" + input + ").toDecimalString(" + digits + ")", expected.get(RoundingMode.HALF_UP), bf.toDecimalString(digits));
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
    private final BigFraction bf;
    private final Map<DivisionMode, String> expectedIPart = new HashMap<DivisionMode, String>();
    private final Map<DivisionMode, String> expectedFPart = new HashMap<DivisionMode, String>();
    
    public GetPartsTest(String input, String truncatedIPart, String truncatedFPart,
            String flooredIPart, String flooredFPart, String euclideanIPart, String euclideanFPart)
    {
      this.input = input;
      bf = bf(input);
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
        actual = bf.getIntegerPart(entry.getKey()).toString();
        assertEquals("(" + input + ").getIntegerPart(" + entry.getKey() + ")", entry.getValue(), actual);
        
        actual = bf.getParts(entry.getKey())[0].toString();
        assertEquals("(" + input + ").getParts(" + entry.getKey() + ")[0]", entry.getValue(), actual);
      }
      
      for(Map.Entry<DivisionMode, String> entry : expectedFPart.entrySet())
      {
        actual = bf.getFractionPart(entry.getKey()).toString();
        assertEquals("(" + input + ").getFractionPart(" + entry.getKey() + ")", entry.getValue(), actual);
        
        actual = bf.getParts(entry.getKey())[1].toString();
        assertEquals("(" + input + ").getParts(" + entry.getKey() + ")[1]", entry.getValue(), actual);
      }
      
      //make sure that calling with no arguments is equivalent to TRUNCATED mode
      assertEquals("(" + input + ").getIntegerPart()",  expectedIPart.get(DivisionMode.TRUNCATED), bf.getIntegerPart().toString());
      assertEquals("(" + input + ").getFractionPart()", expectedFPart.get(DivisionMode.TRUNCATED), bf.getFractionPart().toString());
      assertEquals("(" + input + ").getParts()[0]", expectedIPart.get(DivisionMode.TRUNCATED), bf.getParts()[0].toString());
      assertEquals("(" + input + ").getParts()[1]", expectedFPart.get(DivisionMode.TRUNCATED), bf.getParts()[1].toString());
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
    private final BigFraction bfA;
    private final BigFraction bfB;
    private final Map<DivisionMode, String> expectedQ = new HashMap<DivisionMode, String>();
    private final Map<DivisionMode, String> expectedR = new HashMap<DivisionMode, String>();
    
    public DivideAndRemainderTest(String a, String b, String truncatedQ, String truncatedR,
        String flooredQ, String flooredR, String euclideanQ, String euclideanR)
    {
      this.a = a;
      bfA = bf(a);
      this.b = b;
      bfB = bf(b);
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
        assertEquals("(" + a + ").divideToIntegralValue((" + b + "), " + mode + ")", expectedQ.get(mode), bfA.divideToIntegralValue(bfB, mode).toString());
        assertEquals("(" + a + ").remainder((" + b + "), " + mode + ")", expectedR.get(mode), bfA.remainder(bfB, mode).toString());
        assertEquals("(" + a + ").divideAndRemainder((" + b + "), " + mode + ")[0]", expectedQ.get(mode), bfA.divideAndRemainder(bfB, mode)[0].toString());
        assertEquals("(" + a + ").divideAndRemainder((" + b + "), " + mode + ")[1]", expectedR.get(mode), bfA.divideAndRemainder(bfB, mode)[1].toString());
        
        //check the static methods too
        assertEquals("BigFraction.integralQuotient((" + a + "), (" + b + "), " + mode + ")", expectedQ.get(mode), BigFraction.integralQuotient(bfA, bfB, mode).toString());
        assertEquals("BigFraction.remainder((" + a + "), (" + b + "), " + mode + ")", expectedR.get(mode), BigFraction.remainder(bfA, bfB, mode).toString());
        assertEquals("BigFraction.quotientAndRemainder((" + a + "), (" + b + "), " + mode + ")[0]", expectedQ.get(mode), BigFraction.quotientAndRemainder(bfA, bfB, mode)[0].toString());
        assertEquals("BigFraction.quotientAndRemainder((" + a + "), (" + b + "), " + mode + ")[1]", expectedR.get(mode), BigFraction.quotientAndRemainder(bfA, bfB, mode)[1].toString());
        
        //make sure that the math actually works out:  a/b = q + r/b, and also a = bq + r
        Number[] actuals = bfA.divideAndRemainder(bfB, mode);
        assertEquals("(" + a + ")/(" + b + ") = (" + actuals[0] + ") + (" + actuals[1] + ")/(" + b + ")", bf(bfA, bfB).toString(), bf(actuals[0]).add(bf(actuals[1], bfB)).toString());
        assertEquals("(" + a + ") = (" + b + ")(" + actuals[0] + ") + (" + actuals[1] + ")", bfA.toString(), bfB.multiply(actuals[0]).add(actuals[1]).toString());
      }
      
      //make sure that calling with no division mode is equivalent to TRUNCATED mode
      assertEquals("(" + a + ").divideToIntegralValue((" + b + "))",  expectedQ.get(DivisionMode.TRUNCATED), bfA.divideToIntegralValue(bfB).toString());
      assertEquals("(" + a + ").remainder((" + b + "))", expectedR.get(DivisionMode.TRUNCATED), bfA.remainder(bfB).toString());
      assertEquals("(" + a + ").divideAndRemainder((" + b + "))[0]", expectedQ.get(DivisionMode.TRUNCATED), bfA.divideAndRemainder(bfB)[0].toString());
      assertEquals("(" + a + ").divideAndRemainder((" + b + "))[1]", expectedR.get(DivisionMode.TRUNCATED), bfA.divideAndRemainder(bfB)[1].toString());
      
      //check the static methods too
      assertEquals("BigFraction.integralQuotient((" + a + "), (" + b + "))",  expectedQ.get(DivisionMode.TRUNCATED), BigFraction.integralQuotient(bfA, bfB).toString());
      assertEquals("BigFraction.remainder((" + a + "), (" + b + "))", expectedR.get(DivisionMode.TRUNCATED), BigFraction.remainder(bfA, bfB).toString());
      assertEquals("BigFraction.quotientAndRemainder((" + a + "), (" + b + "))[0]", expectedQ.get(DivisionMode.TRUNCATED), BigFraction.quotientAndRemainder(bfA, bfB)[0].toString());
      assertEquals("BigFraction.quotientAndRemainder((" + a + "), (" + b + "))[1]", expectedR.get(DivisionMode.TRUNCATED), BigFraction.quotientAndRemainder(bfA, bfB)[1].toString());
    }
  }
  
  /**
   * Custom implementation of Number class. Used in test to ensure that BigFraction.valueOf() falls back to doubleValue() if it
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
  private final static BigFraction naiveGCD(BigFraction f1, BigFraction f2) {
    Set<BigFraction> divisors = new HashSet<BigFraction>();
    
    f1 = f1.abs();
    f2 = f2.abs();
    
    if(f1.equals(f2))
      return f1;
    
    long d1 = 1;
    long d2 = 1;
    
    BigFraction lastDivisor1 = f1;
    BigFraction lastDivisor2 = f2;
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
  private final static BigFraction naiveLCM(BigFraction f1, BigFraction f2) {
    Set<BigFraction> multiples = new HashSet<BigFraction>();
    
    f1 = f1.abs();
    f2 = f2.abs();
    
    if(f1.equals(f2))
      return f1;
    
    long m1 = 1;
    long m2 = 1;
    
    BigFraction lastMultiple1 = f1;
    BigFraction lastMultiple2 = f2;
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
  private final static BigFraction bf(Number n) { return BigFraction.valueOf(n); }
  private final static BigFraction bf(Number n, Number d) { return BigFraction.valueOf(n, d); }
  private final static BigFraction bf(String s) { return BigFraction.valueOf(s); }
}
