package ifesdjeen.blomstre;

import ifesdjeen.blomstre.nonconcurrent.OpenBitSet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Function;

public class NonConcurrentBloomFilter<T> {
  private static final long BITSET_EXCESS = 20;

  private final Function<T, ByteBuffer> converter;
  public final  OpenBitSet              bitset;
  public final  int                     hashCount;

  NonConcurrentBloomFilter(Function<T, ByteBuffer> converter,
                           int hashes,
                           OpenBitSet bitset
                          ) {
    this.converter = converter;
    this.hashCount = hashes;
    this.bitset = bitset;
  }

  private long[] getHashBuckets(ByteBuffer key) {
    return getHashBuckets(key, hashCount, bitset.capacity());
  }

  protected long[] hash(ByteBuffer b,
                        long seed) {
    return MurmurHash.hash3_x64_128(b, 0, b.capacity(), seed);
  }

  // Murmur is faster than an SHA-based approach and provides as-good collision
  // resistance.  The combinatorial generation approach described in
  // http://www.eecs.harvard.edu/~kirsch/pubs/bbbf/esa06.pdf
  // does prove to work in actual tests, and is obviously faster
  // than performing further iterations of murmur.
  long[] getHashBuckets(ByteBuffer b,
                        int hashCount,
                        long max) {
    long[] result = new long[hashCount];
    long[] hash = this.hash(b, 0L);
    for (int i = 0; i < hashCount; ++i) {
      result[i] = Math.abs((hash[0] + (long) i * hash[1]) % max);
    }
    return result;
  }

  public void add(T key) {
    add(converter.apply(key));
  }

  protected void add(ByteBuffer key) {
    for (long bucketIndex : getHashBuckets(key)) {
      bitset.set(bucketIndex);
    }
  }

  public boolean isPresent(T key) {
    return isPresent(converter.apply(key));
  }

  protected boolean isPresent(ByteBuffer key) {
    for (long bucketIndex : getHashBuckets(key)) {
      if (!bitset.get(bucketIndex)) {
        return false;
      }
    }
    return true;
  }

  public void clear() {
    bitset.clear();
  }

  public static <T> NonConcurrentBloomFilter<T> makeFilter(Function<T, ByteBuffer> converter,
                                              int numElements,
                                              double maxFalsePosProbability) {
    int maxBucketsPerElement = BloomCalculations.maxBucketsPerElement(numElements);
    BloomCalculations.BloomSpecification spec = BloomCalculations.computeBloomSpec(maxBucketsPerElement,
                                                                                   maxFalsePosProbability);

    long numBits = (numElements * spec.bucketsPerElement) + BITSET_EXCESS;
    return new NonConcurrentBloomFilter<T>(converter, spec.K, new OpenBitSet(numBits));
  }
}
