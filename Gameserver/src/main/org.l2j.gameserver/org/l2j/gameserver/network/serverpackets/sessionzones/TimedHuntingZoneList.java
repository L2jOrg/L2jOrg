package org.l2j.gameserver.network.serverpackets.sessionzones;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.clientpackets.sessionzones.ExTimedHuntingZoneList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mobius
 */
public class TimedHuntingZoneList extends ServerPacket {
	private static Logger LOGGER = LoggerFactory.getLogger(TimedHuntingZoneList.class);
	private final Player _player;
	private final boolean _isInTimedHuntingZone;

	public TimedHuntingZoneList(Player player) {
		_player = player;
		_isInTimedHuntingZone = player.isInTimedHuntingZone();
	}

	@Override
	protected void writeImpl(GameClient client) {
		{
			writeId(ServerPacketId.EX_TIME_RESTRICT_FIELD_LIST);

			final long currentTime = System.currentTimeMillis();
			long endTime;
			writeInt(1); // zone count

			// Ancient Pirates' Tomb
			writeInt(1); // required item count
			writeInt(57); // item id
			writeLong(Config.TIME_LIMITED_ZONE_TELEPORT_FEE); // item count
			writeInt(1); // reset cycle
			writeInt(2); // zone id
			writeInt(78); // min level
			writeInt(999); // max level
			writeInt(0); // remain time base?
			endTime = _player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_RESET_TIME + 2, 0);
			if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime) {
				endTime = currentTime + Config.TIME_LIMITED_ZONE_INITIAL_TIME;
			}
			writeInt((int) (Math.max(endTime - currentTime, 0)) / 1000); // remain time
			writeInt((int) (Config.TIME_LIMITED_MAX_ADDED_TIME / 1000));
			writeInt(3600); // remain refill time
			writeInt(3600); // refill time max
			writeByte(_isInTimedHuntingZone ? 0 : 1); // field activated
			writeByte(false); // bUserBound
			writeByte(true); // bCanReEnter
		}
	}
}