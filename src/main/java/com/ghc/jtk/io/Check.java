/****************************************************************************
Copyright (c) 2004, Colorado School of Mines and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is 
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package com.ghc.jtk.io;

/**
 * Facilitates checks for common conditions. Methods in this class throw
 * appropriate exceptions when specified conditions are not satisfied.
 * @author Dave Hale, Colorado School of Mines
 * @version 2004.11.24
 */
public class Check {

  /**
   * Ensures that the specified condition for an argument is true.
   * @param condition the condition.
   * @param message a description of the condition.
   * @exception IllegalArgumentException if the condition is false.
   */
  public static void argument(boolean condition, String message) {
    if (!condition)
      throw new IllegalArgumentException("required condition: "+message);
  }

  /**
   * Ensures that the specified condition of state is true.
   * @param condition the condition.
   * @param message a description of the condition.
   * @exception IllegalStateException if the condition is false.
   */
  public static void state(boolean condition, String message) {
    if (!condition)
      throw new IllegalStateException("required condition: "+message);
  }

  /**
   * Ensures that the specified zero-based index is in bounds.
   * @param n the smallest positive number that is not in bounds.
   * @param i the index.
   * @exception IndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(int n, int i) {
    if (i<0)
      throw new IndexOutOfBoundsException("index i="+i+" < 0");
    if (n<=i)
      throw new IndexOutOfBoundsException("index i="+i+" >= n="+n);
  }

  /**
   * Ensures that the specified array index is in bounds.
   * @param a the array.
   * @param i the index.
   * @exception ArrayIndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(byte[] a, int i) {
    _b = a[i];
  }

  /**
   * Ensures that the specified array index is in bounds.
   * @param a the array.
   * @param i the index.
   * @exception ArrayIndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(short[] a, int i) {
    _s = a[i];
  }

  /**
   * Ensures that the specified array index is in bounds.
   * @param a the array.
   * @param i the index.
   * @exception ArrayIndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(int[] a, int i) {
    _i = a[i];
  }

  /**
   * Ensures that the specified array index is in bounds.
   * @param a the array.
   * @param i the index.
   * @exception ArrayIndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(long[] a, int i) {
    _l = a[i];
  }

  /**
   * Ensures that the specified array index is in bounds.
   * @param a the array.
   * @param i the index.
   * @exception ArrayIndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(float[] a, int i) {
    _f = a[i];
  }

  /**
   * Ensures that the specified array index is in bounds.
   * @param a the array.
   * @param i the index.
   * @exception ArrayIndexOutOfBoundsException if index is out of bounds.
   */
  public static void index(double[] a, int i) {
    _d = a[i];
  }

  private static byte _b;
  private static short _s;
  private static int _i;
  private static long _l;
  private static float _f;
  private static double _d;

  // Static methods only.
  private Check() {
	System.out.println(_b);
	System.out.println(_s);
	System.out.println(_i);
	System.out.println(_l);
	System.out.println(_f);
	System.out.println(_d);
  }
}
