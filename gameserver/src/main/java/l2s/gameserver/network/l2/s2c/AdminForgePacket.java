package l2s.gameserver.network.l2.s2c;

import java.math.BigInteger;
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
	protected boolean writeOpcodes()
	{
		return true;
	}

	@Override
	protected void writeImpl()
	{
		for(Part p : _parts)
		{
			generate(p.b, p.str);
		}

	}

	public boolean generate(byte b, String string)
	{
		if((b == 'C') || (b == 'c'))
		{
			writeC(Integer.decode(string));
			return true;
		}
		else if((b == 'D') || (b == 'd'))
		{
			writeD(Integer.decode(string));
			return true;
		}
		else if((b == 'H') || (b == 'h'))
		{
			writeH(Integer.decode(string));
			return true;
		}
		else if((b == 'F') || (b == 'f'))
		{
			writeF(Double.parseDouble(string));
			return true;
		}
		else if((b == 'S') || (b == 's'))
		{
			writeS(string);
			return true;
		}
		else if((b == 'B') || (b == 'b') || (b == 'X') || (b == 'x'))
		{
			writeB(new BigInteger(string).toByteArray());
			return true;
		}
		else if((b == 'Q') || (b == 'q'))
		{
			writeQ(Long.decode(string));
			return true;
		}
		return false;
	}

	public void addPart(byte b, String string)
	{
		_parts.add(new Part(b, string));
	}

}