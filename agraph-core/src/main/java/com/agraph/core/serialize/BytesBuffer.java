/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.agraph.core.serialize;

import com.agraph.common.util.Bytes;
import com.agraph.common.util.Strings;
import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * class BytesBuffer is a util for read/write binary
 */
@SuppressWarnings({"UnusedReturnValue", "ConstantConditions", "PointlessBitwiseExpression"})
public final class BytesBuffer {

    public static final int BYTE_LEN = Byte.BYTES;
    public static final int SHORT_LEN = Short.BYTES;
    public static final int INT_LEN = Integer.BYTES;
    public static final int LONG_LEN = Long.BYTES;
    public static final int CHAR_LEN = Character.BYTES;
    public static final int FLOAT_LEN = Float.BYTES;
    public static final int DOUBLE_LEN = Double.BYTES;

    public static final int UINT8_MAX = ((byte) -1) & 0xff;
    public static final int UINT16_MAX = ((short) -1) & 0xffff;
    public static final long UINT32_MAX = 0xffffffffL;

    // NOTE: +1 to let code 0 represent length 1
    public static final int ID_LEN_MASK = 0x7f;
    public static final int ID_LEN_MAX = 0x7f + 1; // 128
    public static final int BIG_ID_LEN_MAX = 0x7fff + 1; // 32768

    public static final byte STRING_ENDING_BYTE = (byte) 0xff;

    // The value must be in range [8, ID_LEN_MAX]
    public static final int INDEX_HASH_ID_THRESHOLD = 32;

    public static final int DEFAULT_CAPACITY = 64;
    public static final int MAX_BUFFER_CAPACITY = 128 * 1024 * 1024; // 128M

    public static final int BUF_EDGE_ID = 128;
    public static final int BUF_PROPERTY = 64;

    private ByteBuffer buffer;

    public BytesBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public BytesBuffer(int capacity) {
        Preconditions.checkArgument(capacity <= MAX_BUFFER_CAPACITY,
                "Capacity exceeds max buffer capacity: %s",
                MAX_BUFFER_CAPACITY);
        this.buffer = ByteBuffer.allocate(capacity);
    }

    public BytesBuffer(ByteBuffer buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        this.buffer = buffer;
    }

    public static BytesBuffer allocate(int capacity) {
        return new BytesBuffer(capacity);
    }

    public static BytesBuffer wrap(ByteBuffer buffer) {
        return new BytesBuffer(buffer);
    }

    public static BytesBuffer wrap(byte[] array) {
        return new BytesBuffer(ByteBuffer.wrap(array));
    }

    public static BytesBuffer wrap(byte[] array, int offset, int length) {
        return new BytesBuffer(ByteBuffer.wrap(array, offset, length));
    }

    public ByteBuffer asByteBuffer() {
        return this.buffer;
    }

    public BytesBuffer flip() {
        this.buffer.flip();
        return this;
    }

    public byte[] array() {
        return this.buffer.array();
    }

    public byte[] bytes() {
        byte[] bytes = this.buffer.array();
        if (this.buffer.position() == bytes.length) {
            return bytes;
        } else {
            return Arrays.copyOf(bytes, this.buffer.position());
        }
    }

    public BytesBuffer copyFrom(BytesBuffer other) {
        return this.put(other.bytes());
    }

    public int remaining() {
        return this.buffer.remaining();
    }

    private void require(int size) {
        // Does need to resize?
        if (this.buffer.limit() - this.buffer.position() >= size) {
            return;
        }

        // Extra capacity as buffer
        int newCapacity = size + this.buffer.limit() + DEFAULT_CAPACITY;
        Preconditions.checkArgument(newCapacity <= MAX_BUFFER_CAPACITY,
                "Capacity exceeds max buffer capacity: %s",
                MAX_BUFFER_CAPACITY);
        ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
        this.buffer.flip();
        newBuffer.put(this.buffer);
        this.buffer = newBuffer;
    }

    public BytesBuffer put(byte val) {
        require(BYTE_LEN);
        this.buffer.put(val);
        return this;
    }

    public BytesBuffer put(int val) {
        assert val <= UINT8_MAX;
        require(BYTE_LEN);
        this.buffer.put((byte) val);
        return this;
    }

    public BytesBuffer put(byte[] val) {
        require(BYTE_LEN * val.length);
        this.buffer.put(val);
        return this;
    }

    public BytesBuffer put(byte[] val, int offset, int length) {
        require(BYTE_LEN * length);
        this.buffer.put(val, offset, length);
        return this;
    }

    public BytesBuffer putBoolean(boolean val) {
        return this.put(val ? 1 : 0);
    }

    public BytesBuffer putChar(char val) {
        require(CHAR_LEN);
        this.buffer.putChar(val);
        return this;
    }

    public BytesBuffer putShort(short val) {
        require(SHORT_LEN);
        this.buffer.putShort(val);
        return this;
    }

    public BytesBuffer putInt(int val) {
        require(INT_LEN);
        this.buffer.putInt(val);
        return this;
    }

    public BytesBuffer putLong(long val) {
        require(LONG_LEN);
        this.buffer.putLong(val);
        return this;
    }

    public BytesBuffer putFloat(float val) {
        require(FLOAT_LEN);
        this.buffer.putFloat(val);
        return this;
    }

    public BytesBuffer putDouble(double val) {
        require(DOUBLE_LEN);
        this.buffer.putDouble(val);
        return this;
    }

    public BytesBuffer putUUID(UUID uuid) {
        this.putLong(uuid.getMostSignificantBits());
        this.putLong(uuid.getLeastSignificantBits());
        return this;
    }

    public BytesBuffer putDate(Date date) {
        this.putLong(date.getTime());
        return this;
    }

    public BytesBuffer putString(String val) {
        byte[] bytes = Strings.toBytes(val);
        this.putVBytes(bytes);
        return this;
    }

    public byte peek() {
        return this.buffer.get(this.buffer.position());
    }

    public byte peekLast() {
        return this.buffer.get(this.buffer.capacity() - 1);
    }

    public byte get() {
        return this.buffer.get();
    }

    public byte[] get(int length) {
        byte[] bytes = new byte[length];
        this.buffer.get(bytes);
        return bytes;
    }

    public boolean getBoolean() {
        return this.buffer.get() != 0;
    }

    public char getChar() {
        return this.buffer.getChar();
    }

    public short getShort() {
        return this.buffer.getShort();
    }

    public int getInt() {
        return this.buffer.getInt();
    }

    public long getLong() {
        return this.buffer.getLong();
    }

    public float getFloat() {
        return this.buffer.getFloat();
    }

    public double getDouble() {
        return this.buffer.getDouble();
    }

    public UUID getUUID() {
        long high  = this.buffer.getLong();
        long low  = this.buffer.getLong();
        return new UUID(high, low);
    }

    public Date getDate() {
        return new Date(this.getLong());
    }

    public String getString() {
        return Strings.fromBytes(this.getVBytes());
    }

    /**
     * Store byte array with variable length
     * @param bytes byte array to be stored
     * @return Current buffer
     */
    public BytesBuffer putVBytes(byte[] bytes) {
        Preconditions.checkArgument(bytes.length <= UINT16_MAX,
                "The max length of bytes is %s, but got %s",
                UINT16_MAX, bytes.length);
        require(SHORT_LEN + bytes.length);
        this.writeVInt(bytes.length);
        this.put(bytes);
        return this;
    }

    /**
     * Read variable length byte array stored in the buffer
     * @return byte array data
     */
    public byte[] getVBytes() {
        int length = this.readVInt();
        assert length >= 0;
        return this.get(length);
    }

    public BytesBuffer writeStringWithEnding(String val) {
        if (!val.isEmpty()) {
            byte[] bytes = Strings.toBytes(val);
            // assert '0xff' not exist in string-id-with-ending (utf8 bytes)
            assert !Bytes.contains(bytes, STRING_ENDING_BYTE);
            this.put(bytes);
        }
        /*
         * A reasonable ending symbol should be 0x00(to ensure order), but
         * considering that some backends like PG do not support 0x00 string,
         * so choose 0xFF currently.
         */
        this.put(STRING_ENDING_BYTE);
        return this;
    }

    public String readStringWithEnding() {
        return Strings.fromBytes(this.readBytesWithEnding());
    }

    public BytesBuffer writeStringToRemaining(String value) {
        byte[] bytes = Strings.toBytes(value);
        this.put(bytes);
        return this;
    }

    public String readStringFromRemaining() {
        byte[] bytes = new byte[this.buffer.remaining()];
        this.buffer.get(bytes);
        return Strings.fromBytes(bytes);
    }

    public BytesBuffer writeUInt8(int val) {
        assert val <= UINT8_MAX;
        this.put(val);
        return this;
    }

    public int readUInt8() {
        return this.get() & 0x000000ff;
    }

    public BytesBuffer writeUInt16(int val) {
        assert val <= UINT16_MAX;
        this.putShort((short) val);
        return this;
    }

    public int readUInt16() {
        return this.getShort() & 0x0000ffff;
    }

    public BytesBuffer writeUInt32(long val) {
        assert val <= UINT32_MAX;
        this.putInt((int) val);
        return this;
    }

    public long readUInt32() {
        return this.getInt() & 0xffffffffL;
    }

    public BytesBuffer writeVInt(int value) {
        // NOTE: negative numbers are not compressed
        if (value > 0x0fffffff || value < 0) {
            this.put(0x80 | ((value >>> 28) & 0x7f));
        }
        if (value > 0x1fffff || value < 0) {
            this.put(0x80 | ((value >>> 21) & 0x7f));
        }
        if (value > 0x3fff || value < 0) {
            this.put(0x80 | ((value >>> 14) & 0x7f));
        }
        if (value > 0x7f || value < 0) {
            this.put(0x80 | ((value >>> 7) & 0x7f));
        }
        this.put(value & 0x7f);

        return this;
    }

    public int readVInt() {
        byte leading = this.get();
        Preconditions.checkArgument(leading != 0x80,
                "Unexpected varint with leading byte '0x%s'",
                Bytes.toHex(leading));
        int value = leading & 0x7f;
        if (leading >= 0) {
            assert (leading & 0x80) == 0;
            return value;
        }

        int i = 1;
        for (; i < 5; i++) {
            byte b = this.get();
            if (b >= 0) {
                value = b | (value << 7);
                break;
            } else {
                value = (b & 0x7f) | (value << 7);
            }
        }

        Preconditions.checkArgument(i < 5,
                "Unexpected varint %s with too many bytes(%s)",
                value, i + 1);
        Preconditions.checkArgument(i < 4 || (leading & 0x70) == 0,
                "Unexpected varint %s with leading byte '0x%s'",
                value, Bytes.toHex(leading));
        return value;
    }

    public BytesBuffer writeVLong(long value) {
        if (value < 0) {
            this.put((byte) 0x81);
        }
        if (value > 0xffffffffffffffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 56) & 0x7f));
        }
        if (value > 0x1ffffffffffffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 49) & 0x7f));
        }
        if (value > 0x3ffffffffffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 42) & 0x7f));
        }
        if (value > 0x7ffffffffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 35) & 0x7f));
        }
        if (value > 0xfffffffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 28) & 0x7f));
        }
        if (value > 0x1fffffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 21) & 0x7f));
        }
        if (value > 0x3fffL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 14) & 0x7f));
        }
        if (value > 0x7fL || value < 0L) {
            this.put(0x80 | ((int) (value >>> 7) & 0x7f));
        }
        this.put((int) value & 0x7f);

        return this;
    }

    public long readVLong() {
        byte leading = this.get();
        Preconditions.checkArgument(leading != 0x80,
                "Unexpected varlong with leading byte '0x%s'",
                Bytes.toHex(leading));
        long value = leading & 0x7fL;
        if (leading >= 0) {
            assert (leading & 0x80) == 0;
            return value;
        }

        int i = 1;
        for (; i < 10; i++) {
            byte b = this.get();
            if (b >= 0) {
                value = b | (value << 7);
                break;
            } else {
                value = (b & 0x7f) | (value << 7);
            }
        }

        Preconditions.checkArgument(i < 10,
                "Unexpected varlong %s with too many bytes(%s)",
                value, i + 1);
        Preconditions.checkArgument(i < 9 || (leading & 0x7e) == 0,
                "Unexpected varlong %s with leading byte '0x%s'",
                value, Bytes.toHex(leading));
        return value;
    }

    private void writeNumber(long val) {
        /*
         * 8 kinds of number, 2 ~ 9 bytes number:
         * 0b 0kkksxxx X...
         * 0(1 bit) + kind(3 bits) + signed(1 bit) + number(n bits)
         *
         * 2 byte : 0b 0000 1xxx X(8 bits)                  [0, 2047]
         *          0b 0000 0xxx X(8 bits)                  [-2048, -1]
         * 3 bytes: 0b 0001 1xxx X X                        [0, 524287]
         *          0b 0001 0xxx X X                        [-524288, -1]
         * 4 bytes: 0b 0010 1xxx X X X                      [0, 134217727]
         *          0b 0010 0xxx X X X                      [-134217728, -1]
         * 5 bytes: 0b 0011 1xxx X X X X                    [0, 2^35 - 1]
         *          0b 0011 0xxx X X X X                    [-2^35, -1]
         * 6 bytes: 0b 0100 1xxx X X X X X                  [0, 2^43 - 1]
         *          0b 0100 0xxx X X X X X                  [-2^43, -1]
         * 7 bytes: 0b 0101 1xxx X X X X X X                [0, 2^51 - 1]
         *          0b 0101 0xxx X X X X X X                [-2^51, -1]
         * 8 bytes: 0b 0110 1xxx X X X X X X X              [0, 2^59 - 1]
         *          0b 0110 0xxx X X X X X X X              [-2^59, -1]
         * 9 bytes: 0b 0111 1000 X X X X X X X X            [0, 2^64 - 1]
         *          0b 0111 0000 X X X X X X X X            [-2^64, -1]
         *
         * NOTE:    0b 0111 1111 is used by 128 bits UUID
         *          0b 0111 1110 is used by EdgeId
         */
        int positive = val >= 0 ? 0x08 : 0x00;
        if (~0x7ffL <= val && val <= 0x7ffL) {
            int high3bits = (int) (val >> 8) & 0x07;
            this.writeUInt8(0x00 | positive | high3bits);
            this.writeUInt8((byte) val);
        } else if (~0x7ffffL <= val && val <= 0x7ffffL) {
            int high3bits = (int) (val >> 16) & 0x07;
            this.writeUInt8(0x10 | positive | high3bits);
            this.putShort((short) val);
        } else if (~0x7ffffffL <= val && val <= 0x7ffffffL) {
            int high3bits = (int) (val >> 24 & 0x07);
            this.writeUInt8(0x20 | positive | high3bits);
            this.put((byte) (val >> 16));
            this.putShort((short) val);
        } else if (~0x7ffffffffL <= val && val <= 0x7ffffffffL) {
            int high3bits = (int) (val >> 32) & 0x07;
            this.writeUInt8(0x30 | positive | high3bits);
            this.putInt((int) val);
        } else if (~0x7ffffffffffL <= val && val <= 0x7ffffffffffL) {
            int high3bits = (int) (val >> 40) & 0x07;
            this.writeUInt8(0x40 | positive | high3bits);
            this.put((byte) (val >> 32));
            this.putInt((int) val);
        } else if (~0x7ffffffffffffL <= val && val <= 0x7ffffffffffffL) {
            int high3bits = (int) (val >> 48) & 0x07;
            this.writeUInt8(0x50 | positive | high3bits);
            this.putShort((short) (val >> 32));
            this.putInt((int) val);
        } else if (~0x7ffffffffffffffL <= val && val <= 0x7ffffffffffffffL) {
            int high3bits = (int) (val >> 56) & 0x07;
            this.writeUInt8(0x60 | positive | high3bits);
            this.put((byte) (val >> 48));
            this.putShort((short) (val >> 32));
            this.putInt((int) val);
        } else {
            // high3bits is always 0b000 for 9 bytes number
            this.writeUInt8(0x70 | positive);
            this.putLong(val);
        }
    }

    private long readNumber(byte b) {
        Preconditions.checkArgument((b & 0x80) == 0,
                "Not a number type with prefix byte '0x%s'",
                Bytes.toHex(b));
        // Parse the kind from byte 0kkksxxx
        int kind = b >>> 4;
        boolean positive = (b & 0x08) > 0;
        long high3bits = b & 0x07;
        long value = high3bits << ((kind + 1) * 8);
        switch (kind) {
            case 0:
                value |= this.readUInt8();
                break;
            case 1:
                value |= this.readUInt16();
                break;
            case 2:
                value |= this.readUInt8() << 16 | this.readUInt16();
                break;
            case 3:
                value |= this.readUInt32();
                break;
            case 4:
                value |= (long) this.readUInt8() << 32 | this.readUInt32();
                break;
            case 5:
                value |= (long) this.readUInt16() << 32 | this.readUInt32();
                break;
            case 6:
                value |= (long) this.readUInt8() << 48 |
                        (long) this.readUInt16() << 32 |
                        this.readUInt32();
                break;
            case 7:
                assert high3bits == 0L;
                value |= this.getLong();
                break;
            default:
                throw new AssertionError("Invalid length of number: " + kind);
        }
        if (!positive && kind < 7) {
            // Restore the bits of the original negative number
            long mask = Long.MIN_VALUE >> (52 - kind * 8);
            value |= mask;
        }
        return value;
    }

    private byte[] readBytesWithEnding() {
        int start = this.buffer.position();
        boolean foundEnding = false;
        byte current;
        while (this.remaining() > 0) {
            current = this.get();
            if (current == STRING_ENDING_BYTE) {
                foundEnding = true;
                break;
            }
        }
        Preconditions.checkArgument(foundEnding, "Not found ending '0x%s'",
                Bytes.toHex(STRING_ENDING_BYTE));
        int end = this.buffer.position() - 1;
        int len = end - start;
        byte[] bytes = new byte[len];
        System.arraycopy(this.array(), start, bytes, 0, len);
        return bytes;
    }
}
