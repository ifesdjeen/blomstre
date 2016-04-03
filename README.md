# Concurrent / Thread Safe BitSet and Bloom filter

This is a Concurrent / Thread Safe implementation of the [Bloom Filter](https://en.wikipedia.org/wiki/Bloom_filter)
data structure. Bloom Filter allows you to store an approximate set presence information with constant space
guarantees. It can give a precise answer only to the question "which items are _not_ present in the set".
Positive answer to the reverse question ("which items _are_ present in the set") is approximate, which
means that even if you got the positive answer, the item still might be not in the set

Bloom Filters are implemented very simply. You use multiple hash functions and a bitset. Results of each hash
function are used to set the bits in the bloom filter. When checking the filter, same hash values are taken
and bits at corresponding positions are collected. If all bits on positions yielded by hash functions
were set, we say that element is possibly in the set. Otherwise, if at least one of the bits is unset,
the answer is element is definitely not in the set.

Hash collisions / intersections result into the false positives: if hash function yields same result for two
values, there's no way to distinguish which exactly value was meant. Given enough collisions all bits will
be set and all queries will return "probably in the set", so make sure you create a sufficiently large set.

You can find a very good, illustrative description of the Bloom Filter [here](https://www.jasondavies.com/bloomfilter/).

To include dependency, just use from Maven Central:

```xml
<dependency>
  <groupId>com.github.ifesdjeen</groupId>
  <artifactId>blomstre</artifactId>
  <version>1.0.0-RC1</version>
</dependency>
```

You can also find it on [clojars](https://clojars.org/com.github.ifesdjeen/blomstre).

# Usage

```java
import com.ifesdjeen.blomstre.BloomFilter;

// Make a filter that'd accept Strings, give a converter to byte buffer for hash function calculation
// Maximum number of elements is 6000, max acceptable false positive probability is 0.0001
BloomFilter<String> filter = BloomFilter.makeFilter(Converters.stringToByteBufferConverter, 6000, 0.0001);

// Add an item to the filter
filter.add("abcdef");

filter.isPresent("abcdef");
// => "probably" true
```

# Thread Safety

Thread safety is achieved through using the concurrent `BitSet`, which is backed by `AtomicLongArray`. That
means that write operations may sometimes retry, although will never affect reads or interfere with
other writes in an unpredictable way.

# Copyright / License

Licensed under Apache 2.0 License.

Original `Bloom Filter` can be found [here](https://github.com/apache/cassandra/blob/trunk/src/java/org/apache/cassandra/utils/BloomFilter.java)
and it's license [here](https://github.com/apache/cassandra/blob/trunk/LICENSE.txt).
