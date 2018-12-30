package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;
import io.github.joealisson.primitive.pair.IntIntPair;
import org.l2j.gameserver.data.xml.holder.InstantZoneHolder;
import org.l2j.gameserver.model.Player;

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

	protected void writeImpl()
	{
		writeByte(_openWindow);
		writeInt(_currentInzoneID);
		writeInt(_instanceTimes.size());

		for (IntIntPair pair : _instanceTimes.entrySet()) {
			writeInt(pair.getKey());
			writeInt(pair.getValue());
		}
	}
}