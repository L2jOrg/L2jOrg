package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class RequestMoveToLocationInShuttle extends L2GameClientPacket
{
	private Location _pos = new Location();
	private Location _originPos = new Location();
	private int _shuttleId;

	@Override
	protected void readImpl()
	{
		_shuttleId = readInt();
		_pos.x = readInt();
		_pos.y = readInt();
		_pos.z = readInt();
		_originPos.x = readInt();
		_originPos.y = readInt();
		_originPos.z = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Boat boat = BoatHolder.getInstance().getBoat(_shuttleId);
		if(boat == null)
		{
			player.sendActionFailed();
			return;
		}

		boat.moveInBoat(player, _originPos, _pos);
	}
}