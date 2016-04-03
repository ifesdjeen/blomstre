package com.ifesdjeen.blomstre;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class Converters {
  static class IntToByteBuffer implements Function<Integer, ByteBuffer> {
    @Override
    public ByteBuffer apply(Integer data) {
      ByteBuffer bb = ByteBuffer.allocateDirect(4);
      bb.putInt(data);
      return bb;
    }
  }

  static class LongToByteBuffer implements Function<Long, ByteBuffer> {
    @Override
    public ByteBuffer apply(Long data) {
      ByteBuffer bb = ByteBuffer.allocateDirect(8);
      bb.putLong(data);
      return bb;
    }
  }

  static class StringToByteBuffer implements Function<String, ByteBuffer> {
    @Override
    public ByteBuffer apply(String data) {
      byte[] bytes = data.getBytes();
      ByteBuffer bb = ByteBuffer.allocateDirect(bytes.length);
      bb.put(bytes);
      return bb;
    }
  }

  public static IntToByteBuffer intToByteBufferConverter;
  public static LongToByteBuffer longToByteBufferConverter;
  public static StringToByteBuffer stringToByteBufferConverter;

  static {
    intToByteBufferConverter = new IntToByteBuffer();
    longToByteBufferConverter = new LongToByteBuffer();
    stringToByteBufferConverter = new StringToByteBuffer();
  }
}
