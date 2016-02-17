# Concurrent / Thread Safe BitSet and Bloom filter

This is an implementation of [Bloom Filter](https://en.wikipedia.org/wiki/Bloom_filter)
extracted from Apache Cassandra, mostly to enable quick embedding of that code.

The only addition / change made by this library is that the concurrent / atomic / thread-safe
bitset implementation is used by default, which might be useful in some cases.

To include dependency, just use:

```xml
<dependency>
  <groupId>ifesdjeen</groupId>
  <artifactId>blomstre</artifactId>
  <version>0.1.0</version>
</dependency>
```

# Copyright / License

Code is NOT relicensed, ALL the credits and copyrights belong to their corresponding
and appropriate owners.
