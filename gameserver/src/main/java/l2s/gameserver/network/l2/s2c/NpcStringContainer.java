package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.network.l2.components.NpcString;

/**
 * @author VISTALL
 * @date 16:43/25.03.2011
 */
public abstract class NpcStringContainer extends L2GameServerPacket
{
	private final NpcString _npcString;
	private final String[] _parameters;

	protected NpcStringContainer(NpcString npcString, String... arg)
	{
		_npcString = npcString;
		_parameters = arg;
	}

	protected void writeElements()
	{
		writeD(_npcString.getId());
		for(String st : _parameters)
			writeS(st);
	}
}
