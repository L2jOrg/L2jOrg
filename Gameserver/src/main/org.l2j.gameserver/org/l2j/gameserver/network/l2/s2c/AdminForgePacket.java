package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AdminForgePacket extends L2GameServerPacket
{
	private List<Part> _parts = new ArrayList<Part>();

	private static class Part
	{
		public byte b;
		public String str;

		public Part(byte bb, String string)
		{
			b = bb;
			str = string;
		}
	}

	public AdminForgePacket()
	{
		//
	}

	@Override
	protected boolean writeOpcodes(ByteBuffer buffer)
	{
		return true;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		for(Part p : _parts)
		{
			generate(buffer, p.b, p.str);
		}

	}

	public boolean generate(ByteBuffer buffer, byte b, String string)
	{
		if((b == 'C') || (b == 'c'))
		{
			buffer.put(Byte.decode(string));
			return true;
		}
		else if((b == 'D') || (b == 'd'))
		{
			buffer.putInt(Integer.decode(string));
			return true;
		}
		else if((b == 'H') || (b == 'h'))
		{
			buffer.putShort(Short.decode(string));
			return true;
		}
		else if((b == 'F') || (b == 'f'))
		{
			buffer.putDouble(Double.parseDouble(string));
			return true;
		}
		else if((b == 'S') || (b == 's'))
		{
			writeString(string, buffer);
			return true;
		}
		else if((b == 'B') || (b == 'b') || (b == 'X') || (b == 'x'))
		{
			buffer.put(new BigInteger(string).toByteArray());
			return true;
		}
		else if((b == 'Q') || (b == 'q'))
		{
			buffer.putLong(Long.decode(string));
			return true;
		}
		return false;
	}

	public void addPart(byte b, String string)
	{
		_parts.add(new Part(b, string));
	}

}