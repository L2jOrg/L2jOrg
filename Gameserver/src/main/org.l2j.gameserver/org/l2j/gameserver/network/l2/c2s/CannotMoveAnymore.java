package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.ObservePoint;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class CannotMoveAnymore extends L2GameClientPacket
{
	private Location _loc = new Location();

	/**
	 * packet type id 0x47
	 *
	 * sample
	 *
	 * 36
	 * a8 4f 02 00 // x
	 * 17 85 01 00 // y
	 * a7 00 00 00 // z
	 * 98 90 00 00 // heading?
	 *
	 * format:		cdddd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer) {
		_loc.x = buffer.getInt();
		_loc.y = buffer.getInt();
		_loc.z = buffer.getInt();
		_loc.h = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isInObserverMode())
		{
			ObservePoint observer = activeChar.getObservePoint();
			if(observer != null)
				observer.stopMove();
			return;
		}

		activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, _loc, null);
	}
}