BigFraction
===========

Java class that represents a fraction as a ratio of two BigIntegers, reduced to lowest terms. Originally created for use Project Euler problems.

## Support for Older Java Versions

Switch to the java7 branch for a version of this library that compiles in Java7. I think it will also compile in Java 5-6. Master branch will keep up to date with latest Java version.

## Creating BigFractions

Constructors are protected. Create new BigFractions using `valueOf(Number numerator)`, `valueOf(Number numerator, Number denominator)`, or `valueOf(String)`:

    BigFraction.valueOf(11);      // 11/1
    BigFraction.valueOf(11, 17);  // 11/17
    BigFraction.valueOf("19/81"); // 19/81

Fractions are always reduced to lowest terms:

    BigFraction.valueOf(17, 34);  // 1/2
    BigFraction.valueOf(0, 999);  // 0/1

The sign is always carried by the numerator:

    BigFraction.valueOf(-9, 4);   // -9/4
    BigFraction.valueOf(9, -4);   // -9/4
    BigFraction.valueOf(-9, -4);  // 9/4  (negatives cancel out)

You can use floating point numbers to create the fraction:

    BigFraction.valueOf(0.625);  // 5/8
    BigFraction.valueOf(-8.5, 6.25); //-34/25

But be careful, what you get is *exactly* equal to the value you provide:

    BigFraction.valueOf(1.1);  // 2476979795053773/2251799813685248
    BigFraction.valueOf(1.1f); // 9227469/8388608

The version that takes a String may be more like what you expect:

    BigFraction.valueOf("1.1");                // 11/10
    BigFraction.valueOf(Double.toString(1.1)); // 11/10
    BigFraction.valueOf(Float.toString(1.1f)); // 11/10

You can also use BigInteger and BigDecimal:

    BigFraction.valueOf(new BigInteger("9999999999999999999"), BigInteger.valueOf(1));
    // ->  9999999999999999999/1   (note that this is larger than Long.MAX_VALUE)
    
    BigFraction.valueOf(new BigDecimal("1.23456789012345678901E-50"));
    // ->  123456789012345678901/10000000000000000000000000000000000000000000000000000000000000000000000

You can even mix different Number types for numerator and denominator:

    BigFraction.valueOf(1.5, BigInteger.valueOf(17)); // 3/34

A few exceptions:

    BigFraction.valueOf(1,0); // ArithmeticException - divide by zero
    BigFraction.valueOf(0,0); // ArithmeticException - divide by zero
    BigFraction.valueOf(Double.POSITIVE_INFINITY); //IllegalArgumentException
    BigFraction.valueOf(Double.NaN); //IllegalArgumentException

## Mathematical Operations

    BigFraction a = BigFraction.valueOf(1,2);
    BigFraction b = BigFraction.valueOf(3,4);
    BigFraction z = BigFraction.ZERO;
    
    a.add(a); // 1/2 + 1/2 = 1/1
    a.add(b); // 1/2 + 3/4 = 5/4
    b.add(z); // 3/4 + 0/1 = 3/4
    z.add(a); // 0/1 + 1/2 = 1/2
    
    a.subtract(a); // 1/2 - 1/2 = 0/1
    a.subtract(b); // 1/2 - 3/4 = -1/4
    b.subtract(z); // 3/4 - 0/1 = 3/4
    z.subtract(a); // 0/1 - 1/2 = -1/2
    
    a.multiply(a); // (1/2) * (1/2) = 1/4
    a.multiply(b); // (1/2) * (3/4) = 3/8
    b.multiply(z); // (3/4) * (0/1) = 0/1
    z.multiply(a); // (0/1) * (1/2) = 0/1
    
    a.divide(a); // (1/2) / (1/2) = 1/1
    a.divide(b); // (1/2) / (3/4) = 2/3
    b.divide(z); // => ArithmeticException - divide by zero
    z.divide(a); // (0/1) / (1/2) = 0/1
    
    a.pow(3);  // (1/2)^3 = 1/8
    b.pow(4);  // (3/4)^4 = 81/256
    a.pow(0);  // (1/2)^0 = 1/1
    z.pow(0);  // (0/1)^0 = 1/1  => Mathematicians may not like it, but this is consistent with Math.pow()
    
    a.pow(-3); // (1/2)^(-3) = 8/1
    b.pow(-4); // (3/4)^(-4) = 256/81
    z.pow(-1); // => ArithmeticException (divide by zero)
    
    a.reciprocal(); // (1/2)^(-1) = 2/1
    b.reciprocal(); // (3/4)^(-1) = 4/3
    z.reciprocal(); // => ArithmeticException (divide by zero)

Complement is `1 - n`. Useful in statistics a lot:

    a.complement(); // 1 - 1/2 = 1/2
    b.complement(); // 1 - 3/4 = 1/4
    z.complement(); // 1 - 0/1 = 1/1

## Building

This project is built using Gradle. The simplest way to build is to use the enclosed Gradle wrapper:

    $ gradlew build

You can also download Gradle and install it locally, then build with:

    $ gradle build

I also have project files set up for Eclipse. If you check out as an Eclipse project, and you have the
Gradle plugin for Eclipse installed, you can right-click on project and select "Run As > Gradle Build".

If you get an error like the following:

    > Could not find tools.jar. Please check that SOME_PATH contains a valid JDK installation.

This means your system default Java is a JRE rather than a JDK. In Eclipse, to resolve it, right-click on
project and select Run As > Gradle Build..., then select arguments, and select the JDK that is configured
in Eclipse.

If you are running from command line and get this error, either install a JDK, or set JAVA_HOME variable
to point to a JDK.
