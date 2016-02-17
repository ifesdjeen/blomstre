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
    final int bucketIdx = (int) idx / BASE;
    atomicSet(bucketIdx, (int) idx - (BASE * bucketIdx));
  }

  public boolean get(long idx) {
    final int bucketIdx = (int) idx / BASE;
    return atomicGet(bucketIdx, (int) idx - (BASE * bucketIdx));
  }

  public void clear() {
    throw new RuntimeException("not implemented");
  }

  public long capacity() {
    return this.buckets.length() * 64;
  }

  /**
   * IMLEMENTATION
   */

  private boolean atomicGet(int bucketIdx, int toGet) {
    final long l = buckets.get(bucketIdx);
    final long idxMask = mask(toGet);
    return (l & idxMask) == idxMask;
  }

  private void atomicSet(int bucketIdx, int toSet) {
    while (true) {
      final long l = buckets.get(bucketIdx);

      if (buckets.compareAndSet(bucketIdx, l, l | mask(toSet)))
        return;
    }
  }

  private static long mask(int id) {
    return 1L << id;
  }

  public String longToBinaryStr(long num) {
    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < BASE; i++) {
      final long idxMask = mask(i);
      stringBuilder.append( (num & idxMask) == idxMask ? "1" : "0" );
    }

    return stringBuilder.toString();
  }
}
