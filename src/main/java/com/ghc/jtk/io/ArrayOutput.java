/****************************************************************************
Copyright (c) 2006, Colorado School of Mines and others. All rights reserved.
This program and accompanying materials are made available under the terms of
the Common Public License - v1.0, which accompanies this distribution, and is
available at http://www.eclipse.org/legal/cpl-v10.html
****************************************************************************/
package com.ghc.jtk.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * An interface for writing arrays of primitive values to a binary stream.
 * This interfaces extends the standard interface {@link java.io.DataOutput}.
 * It adds methods for writing arrays of primitive values.
 * @author Dave Hale, Colorado School of Mines
 * @version 2006.08.05
 */
public interface ArrayOutput extends DataOutput {

  // From DataOutput.
  public void write(int b) throws IOException;
  public void write(byte[] b) throws IOException;
  public void write(byte[] b, int off, int len) throws IOException;
  public void writeBoolean(boolean v) throws IOException;
  public void writeByte(int v) throws IOException;
  public void writeShort(int v) throws IOException;
  public void writeChar(int v) throws IOException;
  public void writeInt(int v) throws IOException;
  public void writeLong(long v) throws IOException;
  public void writeFloat(float v) throws IOException;
  public void writeDouble(double v) throws IOException;
  public void writeBytes(String s) throws IOException;
  public void writeChars(String s) throws IOException;
  public void writeUTF(String s) throws IOException;

  /**
   * Writes byte elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeBytes(byte[] v, int k, int n) throws IOException;

  /**
   * Writes byte elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeBytes(byte[] v) throws IOException;

  /**
   * Writes byte elements from a specified array.
   * @param v the array.
   */
  public void writeBytes(byte[][] v) throws IOException;

  /**
   * Writes byte elements from a specified array.
   * @param v the array.
   */
  public void writeBytes(byte[][][] v) throws IOException;

  /**
   * Writes char elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeChars(char[] v, int k, int n) throws IOException;

  /**
   * Writes char elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeChars(char[] v) throws IOException;

  /**
   * Writes char elements from a specified array.
   * @param v the array.
   */
  public void writeChars(char[][] v) throws IOException;

  /**
   * Writes char elements from a specified array.
   * @param v the array.
   */
  public void writeChars(char[][][] v) throws IOException;

  /**
   * Writes short elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeShorts(short[] v, int k, int n) throws IOException;

  /**
   * Writes shorts elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeShorts(short[] v) throws IOException;

  /**
   * Writes short elements from a specified array.
   * @param v the array.
   */
  public void writeShorts(short[][] v) throws IOException;

  /**
   * Writes short elements from a specified array.
   * @param v the array.
   */
  public void writeShorts(short[][][] v) throws IOException;

  /**
   * Writes int elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeInts(int[] v, int k, int n) throws IOException;

  /**
   * Writes int elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeInts(int[] v) throws IOException;

  /**
   * Writes int elements from a specified array.
   * @param v the array.
   */
  public void writeInts(int[][] v) throws IOException;

  /**
   * Writes int elements from a specified array.
   * @param v the array.
   */
  public void writeInts(int[][][] v) throws IOException;

  /**
   * Writes long elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeLongs(long[] v, int k, int n) throws IOException;

  /**
   * Writes long elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeLongs(long[] v) throws IOException;

  /**
   * Writes long elements from a specified array.
   * @param v the array.
   */
  public void writeLongs(long[][] v) throws IOException;

  /**
   * Writes long elements from a specified array.
   * @param v the array.
   */
  public void writeLongs(long[][][] v) throws IOException;

  /**
   * Writes float elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeFloats(float[] v, int k, int n) throws IOException;

  /**
   * Writes float elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeFloats(float[] v) throws IOException;

  /**
   * Writes float elements from a specified array.
   * @param v the array.
   */
  public void writeFloats(float[][] v) throws IOException;

  /**
   * Writes float elements from a specified array.
   * @param v the array.
   */
  public void writeFloats(float[][][] v) throws IOException;

  /**
   * Writes double elements from a specified array.
   * @param v the array.
   * @param k the index of the first element to write.
   * @param n the number of elements to write.
   */
  public void writeDoubles(double[] v, int k, int n) throws IOException;

  /**
   * Writes double elements from a specified array.
   * The array length equals the number of elements to write.
   * @param v the array.
   */
  public void writeDoubles(double[] v) throws IOException;

  /**
   * Writes double elements from a specified array.
   * @param v the array.
   */
  public void writeDoubles(double[][] v) throws IOException;

  /**
   * Writes double elements from a specified array.
   * @param v the array.
   */
  public void writeDoubles(double[][][] v) throws IOException;
}
