package l2s.commons.net.nio;

public abstract class ReceivablePacket<T> extends AbstractPacket<T> implements Runnable
{
	protected int getAvaliableBytes()
	{
		return getByteBuffer().remaining();
	}

	protected void readB(byte[] dst)
	{
		getByteBuffer().get(dst);
	}

	protected void readB(byte[] dst, int offset, int len)
	{
		getByteBuffer().get(dst, offset, len);
	}

	protected int readC()
	{
		return getByteBuffer().get() & 0xFF;
	}

	protected int readH()
	{
		return getByteBuffer().getShort() & 0xFFFF;
	}

	protected int readD()
	{
		return getByteBuffer().getInt();
	}

	protected long readQ()
	{
		return getByteBuffer().getLong();
	}

	protected double readF()
	{
		return getByteBuffer().getDouble();
	}

	protected String readS()
	{
		StringBuilder sb = new StringBuilder();
		char ch;
		while((ch = getByteBuffer().getChar()) != 0)
			sb.append(ch);
		return sb.toString();
	}

	protected abstract boolean read();
}
