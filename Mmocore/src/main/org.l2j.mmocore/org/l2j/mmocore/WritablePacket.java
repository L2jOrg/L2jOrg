package org.l2j.mmocore;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Math.max;
import static java.lang.System.arraycopy;
import static java.util.Objects.nonNull;

public abstract class WritablePacket<T extends Client<Connection<T>>> extends AbstractPacket<T> {

    protected WritablePacket() { }

	/**
	 * Write <B>byte</B> to the buffer. <BR>
	 * 8bit integer (00)
	 * @param value to be written
	 */
	protected final void writeByte(final byte value) {
	    try {
            data[dataIndex++] = value;
        } catch (IndexOutOfBoundsException e) {
	        byte[] tmp =  new byte[(int) (data.length * 1.2)];
	        arraycopy(data, 0, tmp, 0, data.length);
	        data = tmp;
	        data[dataIndex] = value;
        }
	}

    /**
     * Write <B>byte</B> to the buffer. <BR>
     * 8bit integer (00)
     * @param value to be written
     */
	protected final void writeByte(final int value) {
	    writeByte((byte) value);
    }

    /**
     * Write <B>boolean</B> to the buffer. <BR>
     *  8bit integer (00)
     * @param value to be written
     */
    protected final void writeByte(final boolean value) {
        writeByte((byte) (value ? 0x01 : 0x00));
    }


    /**
	 * Write <B>double</B> to the buffer. <BR>
	 * 64bit double precision float (00 00 00 00 00 00 00 00)
	 * @param value to be put on data
	 */
	protected final void writeDouble(final double value) {
	    var x = doubleToRawLongBits(value);
	    writeLong(x);
	}
	
	/**
	 * Write <B>short</B> to the buffer. <BR>
	 * 16bit integer (00 00)
	 * @param value to be put on data
	 */
	protected final void writeShort(final int value) {
		var x = convertEndian((short) value);
		writeShortParts((byte) x,
                        (byte) (x >>> 8));
	}

    /**
     * Write <B>boolean</B> to the buffer. <BR>
     *  16bit integer (00 00)
     * @param value to be written
     */
    protected final void writeShort(final boolean value) {
        writeShort(value ? 0x01 : 0x00);
    }

    private void writeShortParts(byte b0, byte b1) {
	    writeByte(pickByte(b0, b1));
	    writeByte(pickByte(b1, b0));
    }

    /**
	 * Write <B>int</B> to the buffer. <BR>
	 * 32bit integer (00 00 00 00)
	 * @param value to be put on data
	 */
	protected final void writeInt(final int value) {
	    var x  = convertEndian(value);
	    writeIntParts((byte) x,
                      (byte) (x >>> 8),
                      (byte) (x >>> 16),
                      (byte) (x >>> 24));
	}

	/**
	 * Write <B>byte</B> to the buffer. <BR>
	 *  32bit integer (00 00 00 00)
	 * @param value to be written
	 */
	protected final void writeInt(final boolean value) {
		writeInt(value ? 0x01 : 0x00);
	}

    private void writeIntParts(byte b0, byte b1, byte b2, byte b3) {
	    writeByte(pickByte(b0, b3));
        writeByte(pickByte(b1, b2));
        writeByte(pickByte(b2, b1));
        writeByte(pickByte(b3, b0));
    }

    /**
	 * Write <B>long</B> to the buffer. <BR>
	 * 64bit integer (00 00 00 00 00 00 00 00)
	 * @param value to be put on data
	 */
	protected final void writeLong(final long value) {
        var x = convertEndian(value);
        writeLongParts((byte) x,
                       (byte) (x >>> 8),
                       (byte) (x >>> 16),
                       (byte) (x >>> 24),
                       (byte) (x >>> 32),
                       (byte) (x >>> 40),
                       (byte) (x >>> 48),
                       (byte) (x >>> 56));
	}

    private void writeLongParts(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
	    writeByte(pickByte(b0, b7));
        writeByte(pickByte(b1, b6));
        writeByte(pickByte(b2, b5));
        writeByte(pickByte(b3, b4));
        writeByte(pickByte(b4, b3));
        writeByte(pickByte(b5, b2));
        writeByte(pickByte(b6, b1));
        writeByte(pickByte(b7, b0));
    }

    /**
	 * Write <B>byte[]</B> to the buffer. <BR>
	 * 8bit integer array (00 ...)
	 * @param bytes to be put on data
	 */
	protected final void writeBytes(final byte[] bytes) {
	    arraycopy(bytes, 0, data, dataIndex, bytes.length);
		dataIndex += bytes.length;
	}

	protected  final void writeChar(final char value) {
        short x =  (short) convertEndian(value);
        writeShortParts((byte) x,
                        (byte) (x >>> 8));
    }
	
	/**
	 * Write <B>String</B> to the buffer.
	 * @param text to be put on data
	 */
	protected final void writeString(final String text) {
		if (nonNull(text)) {
			final int len = text.length();
			for (int i = 0; i < len; i++) {
			    writeChar(text.charAt(i));
			}
		}
		writeChar('\000');
	}

	protected final void writeSizedString(final String text) {
	    if(nonNull(text)) {
	        final int len = text.length();
	        writeShort(len);
	        writeString(text);
	        dataIndex -= 2; // the termination char is not necessary
        } else {
	        writeShort(0);
        }
    }

    int writeData() {
		data = new byte[max(2, packetSize())];
		dataIndex += ReadHandler.HEADER_SIZE;
        write();
        return dataIndex;
    }

    private static byte pickByte(byte  le, byte  be) { return isBigEndian ? be : le; }

    protected int packetSize() {
        return  ResourcePool.bufferSize;
    }

	protected abstract void write();

    void writeHeader(int dataSize) {
        var header = convertEndian((short) dataSize);
        var tmp = (byte) (header >>> 8);
        data[0] = pickByte((byte) header, tmp);
        data[1] = pickByte(tmp, (byte) header);
    }
}