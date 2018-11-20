package org.l2j.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Map;

import org.l2j.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 20:24/16.05.2011
 */
public class PackageToListPacket extends L2GameServerPacket
{
	private Map<Integer, String> _characters = Collections.emptyMap();

	public PackageToListPacket(Player player)
	{
		_characters = player.getAccountChars();
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_characters.size());
		for(Map.Entry<Integer, String> entry : _characters.entrySet())
		{
			writeInt(entry.getKey());
			writeString(entry.getValue());
		}
	}
}
