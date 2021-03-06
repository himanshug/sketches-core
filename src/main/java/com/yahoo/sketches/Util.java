/*
 * Copyright 2015, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */
package com.yahoo.sketches;

/**
 * Common utility functions.
 * 
 * @author Lee Rhodes
 */
public final class Util {
  
  private Util() {}
  
  /**
   * The default nominal entries is provided as a convenience for those cases where the 
   * nominal sketch size in number of entries is not provided.  
   * A sketch of 4096 entries has a Relative Standard Error (RSE) of +/- 1.56% at a confidence of 
   * 68%; or equivalently, a Relative Error of +/- 3.1% at a confidence of 95.4%. 
   * <a href="{@docRoot}/resources/dictionary.html#defaultNomEntries">See Default Nominal Entries</a>
   */
  public static final int DEFAULT_NOMINAL_ENTRIES = 4096;
  
  /**
   * <p>The seed 9001 used in the sketch update methods is a prime number that 
   * was chosen very early on in experimental testing. Choosing a seed is somewhat arbitrary, and 
   * the author cannot prove that this particular seed is somehow superior to other seeds.  There 
   * was some early internet disussion that a seed of 0 did not produce as clean avalanche diagrams 
   * as non-zero seeds, but this may have been more related to the MurmurHash2 release, which did 
   * have some issues. As far as the author can determine, MurmurHash3 does not have these problems.
   * 
   * <p>In order to perform set operations on two sketches it is critical that the same hash
   * function and seed are identical for both sketches, otherwise the assumed 1:1 relationship 
   * between the original source key value and the hashed bit string would be violated. Once 
   * you have developed a history of stored sketches you are stuck with it.
   * <a href="{@docRoot}/resources/dictionary.html#defaultUpdateSeed">See Default Update Seed</a>
   */
  public static final long DEFAULT_UPDATE_SEED = 9001L;
  
  /**
   * The java line separator character.
   */
  public static final String LS = System.getProperty("line.separator");
  
  /**
   * Checks if parameter v is a multiple of 8 and greater than zero.
   * @param v The parameter to check
   * @param argName This name will be part of the error message if the check fails.
   */
  public static void checkIfMultipleOf8AndGT0(long v, String argName) {
    if (((v & 0X7L) == 0L) && (v > 0L)) {
      return;
    }
    throw new IllegalArgumentException("The value of the parameter \"" + argName
      + "\" must be a positive multiple of 8 and greater than zero: " + v);
  }
  
  /**
   * Returns true if v is a multiple of 8 and greater than zero
   * @param v The parameter to check
   * @return true if v is a multiple of 8 and greater than zero
   */
  public static boolean isMultipleOf8AndGT0(long v) {
    return (((v & 0X7L) == 0L) && (v > 0L));
  }
  
  /**
   * Returns true if argument is exactly a positive power of 2 and greater than zero.
   * 
   * @param v The input argument.
   * @return true if argument is exactly a positive power of 2 and greater than zero.
   */
  public static boolean isPowerOf2(int v) {
    return (v > 0) && ((v & (v - 1)) == 0); //or (v > 0) && ((v & -v) == v)
  }

  /**
   * Checks the given parameter to make sure it is positive, an integer-power of 2 and greater than
   * zero.
   * 
   * @param v The input argument.
   * @param argName Used in the thrown exception.
   */
  public static void checkIfPowerOf2(int v, String argName) {
    if ((v > 0) && ((v & (v - 1)) == 0)) {
      return;
    }
    throw new IllegalArgumentException("The value of the parameter \"" + argName
        + "\" must be a positive integer-power of 2" + " and greater than 0: " + v);
  }
  
  /**
   * Checks the given parameter to make sure it is positive and between 0.0 inclusive and 1.0
   * inclusive.
   * 
   * @param p <a href="{@docRoot}/resources/dictionary.html#p">See Sampling Probability, <i>p</i></a>
   * @param argName Used in the thrown exception.
   */
  public static void checkProbability(double p, String argName) {
    if ((p >= 0.0) && (p <= 1.0)) {
      return;
    }
    throw new IllegalArgumentException("The value of the parameter \"" + argName
        + "\" must be between 0.0 inclusive and 1.0 inclusive: " + p);
  }
  
  private static final int IntTopPwrOf2 = 1 << 30; //= #Bits -2
  private static final int MaxIntShifts = 16; //= #Bits / 2

  /**
   * Computes the ceiling power of 2 within the range [1, 2^30]. This is the smallest positive power
   * of 2 that equal to or greater than the given n. This algorithm finds the ceiling in a maximum
   * of lg(32)=5 iterations. <br>
   * For:
   * <ul>
   * <li>n &le; 1: returns 1</li>
   * <li>2^30 &le; n &le; 2^31 -1 : returns 2^30</li>
   * <li>n == a power of 2 : returns n</li>
   * <li>otherwise returns the smallest power of 2 greater than n</li>
   * </ul>
   * 
   * @param n The input argument.
   * @return the ceiling power of 2.
   */
  public static int ceilingPowerOf2(int n) {
    if (n <= 1) {
      return 1;
    }
    if (n >= IntTopPwrOf2) {
      return IntTopPwrOf2;
    }
    n-- ;
    for (int i = 1; i <= MaxIntShifts; i <<= 1) {
      int m = (n | (n >> i));
      if (n == m) {
        break;
      }
      n = m;
    }
    return ++n;
  }

  /**
   * Computes the floor power of 2 within the range [1, 2^30]. This is the largest positive power of
   * 2 that equal to or less than the given n. This algorithm finds the floor in a maximum of
   * lg(32)=5 iterations. <br>
   * For:
   * <ul>
   * <li>n &le; 1: returns 1</li>
   * <li>2^30 &le; n &le; 2^31 -1 : returns 2^30</li>
   * <li>n == a power of 2 : returns n</li>
   * <li>otherwise returns the largest power of 2 less than n</li>
   * </ul>
   * 
   * @param n The given argument.
   * @return the floor power of 2.
   */
  public static int floorPowerOf2(int n) {
    if (n <= 1) {
      return 1;
    }
    int f = ceilingPowerOf2(n);
    if (f <= n) {
      return f;
    }
    return f >> 1;
  }
  
  /**
   * Unsigned compare with longs.
   * @param n1 A long to be treated as if unsigned.
   * @param n2 A long to be treated as if unsigned.
   * @return true if n1 &gt; n2.
   */
  public static boolean isLessThanUnsigned(long n1, long n2) {
    return (n1 < n2) ^ ((n1 < 0) != (n2 < 0));
  }
  
  /**
   * Prepend the given string with zeros.
   * @param s the given string
   * @param fieldLength desired total field length including the string
   * @return a string front padded with zeros.
   */
  public static final String zeroPad(String s, int fieldLength) {
    char[] chArr = s.toCharArray();
    int sLen = chArr.length;
    if (sLen < fieldLength) {
      char[] out = new char[fieldLength]; 
      int zeros = fieldLength - sLen;
      for (int i=0; i<zeros; i++) {
        out[i] = '0';
      }
      for (int i=zeros; i<fieldLength; i++) {
        out[i] = chArr[i-zeros];
      }
      return String.valueOf(out);
    }
    return s;
  }
  
}