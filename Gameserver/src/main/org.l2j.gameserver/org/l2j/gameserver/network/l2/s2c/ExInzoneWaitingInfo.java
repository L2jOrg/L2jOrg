package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;
import io.github.joealisson.primitive.pair.IntIntPair;
import org.l2j.gameserver.data.xml.holder.InstantZoneHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExInzoneWaitingInfo extends L2GameServerPacket
{
	private final boolean _openWindow;
	private int _currentInzoneID = -1;
	private IntIntMap _instanceTimes;

	public ExInzoneWaitingInfo(Player player, boolean openWindow)
	{
		_openWindow = openWindow;
		_instanceTimes = new HashIntIntMap();

		if(player.getActiveReflection() != null)
			_currentInzoneID = player.getActiveReflection().getInstancedZoneId();

		int limit;
		for(int i : player.getInstanceReuses().keySet())
		{
			limit = InstantZoneHolder.getInstance().getMinutesToNextEntrance(i, player);
			if(limit > 0)
				_instanceTimes.put(i, limit * 60);
		}
	}

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) (_openWindow ? 1 : 0));
		buffer.putInt(_currentInzoneID);
		buffer.putInt(_instanceTimes.size());

		for (IntIntPair pair : _instanceTimes.entrySet()) {
			buffer.putInt(pair.getKey());
			buffer.putInt(pair.getValue());
		}
	}
}