package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * @author Bonux
 */
public class RequestMoveToLocationInShuttle extends L2GameClientPacket
{
	private Location _pos = new Location();
	private Location _originPos = new Location();
	private int _shuttleId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_shuttleId = buffer.getInt();
		_pos.x = buffer.getInt();
		_pos.y = buffer.getInt();
		_pos.z = buffer.getInt();
		_originPos.x = buffer.getInt();
		_originPos.y = buffer.getInt();
		_originPos.z = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
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