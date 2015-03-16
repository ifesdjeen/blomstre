package ifesdjeen.blomstre;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

public class ConcurrentBitSet {

  /**
   * STATE
   */
  private static final int  BASE              = 64;
  private static final long MAX_UNSIGNED_LONG = -1L;

  private final AtomicLongArray buckets;

  public ConcurrentBitSet(long bitsCount) {
    int bucketsCount = (int) bitsCount / BASE;
    this.buckets = new AtomicLongArray(bucketsCount);

    for (int i = 0; i < buckets.length(); i++) {
      this.buckets.set(i, 0);
    }
  }

  /**
   * API
   */

  public void set(long idx) {
    int bucketIdx = (int) idx / BASE;
    atomicSet(bucketIdx, (int) idx - (BASE * bucketIdx));
  }

  public boolean get(long idx) {
    int bucketIdx = (int) idx / BASE;
    return atomicGet(bucketIdx, (int) idx - (BASE * bucketIdx));
  }

//  public long next() {
//    for(int i = 0; i < buckets.length(); i++) {
//      int id = atomicGetAndSetFirstAvailable(i);
//
//      if (id >= 0) {
//        return id + (BASE * i);
//      }
//    }
//
//    throw new RuntimeException("No available bits");
//  }

//  public void clear(long idx) {
//    int bucketIdx = (int) idx / BASE;
//    atomicClear(bucketIdx, (int) idx - (BASE * bucketIdx));
//  }

  public void clear() {
    throw new RuntimeException("not implemented");
  }

  public long capacity() {
    return this.buckets.length() * 64;
  }

  /**
   * IMLEMENTATION
   */

  // Returns >= 0 if found and set an id, -1 if no bits are available.
//  private int atomicGetAndSetFirstAvailable(int idx) {
//    while (true) {
//      final long l = buckets.get(idx);
//      if (l == 0)
//        return -1;
//
//      // Find the position of the right-most 1-bit
//      final int id = Long.numberOfTrailingZeros(l);
//      if (buckets.compareAndSet(idx, l, l ^ mask(id)))
//        return id;
//    }
//  }

  private boolean atomicGet(int bucketIdx, int toGet) {
    final long l = buckets.get(bucketIdx);

    long idxMask = mask(toGet);
    boolean result = (l & idxMask) == idxMask;
    return result;
  }

  private void atomicSet(int bucketIdx, int toSet) {
    while (true) {
      final long l = buckets.get(bucketIdx);

      if (buckets.compareAndSet(bucketIdx, l, l | mask(toSet)))
        return;
    }
  }

//  private void atomicClear(int bucketIdx, int toClear) {
//    while (true) {
//      final long l = buckets.get(bucketIdx);
//
//      if (buckets.compareAndSet(bucketIdx, l, l | mask(toClear)))
//        return;
//    }
//  }

  private static long mask(int id) {
    return 1L << id;
  }

  public String longToBinaryStr(long num) {
    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < BASE; i++) {
      long idxMask = mask(i);
      stringBuilder.append( (num & idxMask) == idxMask ? "1" : "0" );
    }

    return stringBuilder.toString();
  }
}
