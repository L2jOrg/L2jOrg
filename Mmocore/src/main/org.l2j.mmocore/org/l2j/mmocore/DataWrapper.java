package org.l2j.mmocore;

public final class DataWrapper extends ReadablePacket<Object> {

    private DataWrapper(byte[] data) {
        this.data = data;
    }

    @Override
    protected boolean read() {
        return false;
    }

    @Override
    public void run() { }

    static DataWrapper wrap(byte[] data) {
        return new DataWrapper(data);
    }

    public  byte get() {
        return readByte();
    }

    public short getShort() {
        return readShort();
    }

    public int getInt() {
        return readInt();
    }

    public int available() {
        return availableData();
    }

    public byte[] expose() {
        return data;
    }

}
