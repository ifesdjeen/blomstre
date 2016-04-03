package com.ifesdjeen.blomstre;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BloomFilterTest {

  @Test
  public void bloomFilter0Test() {
    BloomFilter<String> filter = BloomFilter.makeFilter(Converters.stringToByteBufferConverter, 6000, 0.0001);

    int i = 0;
    filter.add(String.format("%d.%d.%d.%d",
                             i,
                             i,
                             i,
                             i));

    assertTrue(filter.isPresent(String.format("%d.%d.%d.%d",
                                              i,
                                              i,
                                              i,
                                              i)));


  }


  @Test
  public void bloomFilter1Test() {
    BloomFilter<String> filter = BloomFilter.makeFilter(Converters.stringToByteBufferConverter, 6000, 0.0001);

    int i = 1;
    filter.add(String.format("%d.%d.%d.%d",
                             i,
                             i,
                             i,
                             i));

    assertTrue(filter.isPresent(String.format("%d.%d.%d.%d",
                                              i,
                                              i,
                                              i,
                                              i)));


  }

  @Test
  public void bloomFilterTest() {
    BloomFilter<String> filter = BloomFilter.makeFilter(Converters.stringToByteBufferConverter, 6000, 0.0001);

    for(int i = 0; i < 1000; i++) {
      filter.add(String.format("%d.%d.%d.%d",
                               i,
                               i,
                               i,
                               i));
    }

    for(int i = 0; i < 1000; i++) {
      assertTrue(filter.isPresent(String.format("%d.%d.%d.%d",
                                                i,
                                                i,
                                                i,
                                                i)));
    }

    for(int i = 1000; i < 5000; i++) {
      assertFalse(filter.isPresent(String.format("%d.%d.%d.%d",
                                                i,
                                                i,
                                                i,
                                                i)));
    }
  }

  @Test
  public void concurrentBloomFilterTest() throws InterruptedException {
    final BloomFilter<String> filter = BloomFilter.makeFilter(Converters.stringToByteBufferConverter, 6000, 0.01);

    final int threads = 10;
    final CountDownLatch latch = new CountDownLatch(threads);
    final CountDownLatch finishedLatch = new CountDownLatch(threads);

    for(int thread = 0; thread < threads; thread++) {
      final int finalThread = thread;
      new Thread(new Runnable() {
        @Override
        public void run() {
          final int localFinalThread = finalThread;
          latch.countDown();
          try {
            latch.await();
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          for (int i = 0; i < 100; i++) {
            final int finalI = localFinalThread * 100 + i;
            filter.add(String.format("%d.%d.%d.%d",
                                     finalI,
                                     finalI,
                                     finalI,
                                     finalI));
          }
          finishedLatch.countDown();
        }
      }).start();
    }

    finishedLatch.await();

    for(int i = 0; i < 1000; i++) {
      assertTrue(filter.isPresent(String.format("%d.%d.%d.%d",
                                                i,
                                                i,
                                                i,
                                                i)));
    }


  }

}
