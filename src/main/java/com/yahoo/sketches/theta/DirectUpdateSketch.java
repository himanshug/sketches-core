/*
 * Copyright 2015, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */
package com.yahoo.sketches.theta;

import static com.yahoo.sketches.theta.PreambleUtil.MAX_THETA_LONG;
import static com.yahoo.sketches.theta.PreambleUtil.computeSeedHash;

/**
 * @author Lee Rhodes
 */
abstract class DirectUpdateSketch extends UpdateSketch {
  final long seed_;
  final int lgNomLongs_;
  final float p_;
  final ResizeFactor rf_;

  DirectUpdateSketch(int lgNomLongs, long seed, float p, ResizeFactor rf) {
    seed_ = seed;
    lgNomLongs_ = lgNomLongs;
    p_ = p;
    rf_ = rf;
  }

  //Sketch
  
  @Override
  public abstract byte[] toByteArray();
//  public byte[] toByteArray() { // even if noRebuild it will still be valid
//    int curBytes = getCurrentBytes(false);
//    byte[] byteArrOut = new byte[curBytes];
//    Memory mem = getMemory();
//    mem.getByteArray(0, byteArrOut, 0, curBytes);
//    return byteArrOut;
//  }
  
  //restricted methods
  
  @Override
  public boolean isCompact() {
    return false;
  }
  
  @Override
  public boolean isDirect() {
    return true; 
  }
  
  @Override
  abstract int getPreambleLongs();
  
  
  //Update Internals

  @Override
  int getLgNomLongs() {
    return lgNomLongs_;
  }

  @Override
  int getLgResizeFactor() {
    return rf_.lg();
  }

  @Override
  long getSeed() {
    return seed_;
  }
  
  @Override
  float getP() {
    return p_;
  }

  @Override
  double getTheta() {
    return getThetaLong()/MAX_THETA_LONG;
  }
  
  //Set Arguments
  
  @Override
  short getSeedHash() {
    return computeSeedHash(getSeed());
  }
  
  @Override
  abstract long[] getCache();
//  long[] getCache() {
//    int arrLongs = 1 << getLgArrLongs();
//    long[] cache = new long[arrLongs];
//    Memory mem = getMemory();
//    int preLongs = mem.getByte(PREAMBLE_LONGS_BYTE) & 0X3F;
//    checkQSPreLongsAndFamily(preLongs);
//    mem.getLongArray(preLongs << 3, cache, 0, arrLongs);
//    return cache;
//  }
  
}