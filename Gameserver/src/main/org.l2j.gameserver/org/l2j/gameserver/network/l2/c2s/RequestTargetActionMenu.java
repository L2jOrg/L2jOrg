package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.MyTargetSelectedPacket;
import org.l2j.gameserver.network.l2.s2c.StatusUpdatePacket;

public class RequestTargetActionMenu extends L2GameClientPacket
{
	private int _targetObjectId;

	@Override
	protected void readImpl()
	{
		_targetObjectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		GameObject target = GameObjectsStorage.findObject(_targetObjectId);
		if(target == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new MyTargetSelectedPacket(activeChar, target, true));
		if(target.isCreature())
		{
			Creature targetCreature = (Creature) target;
			activeChar.sendPacket(targetCreature.makeStatusUpdate(null, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.MAX_HP));
			activeChar.sendPacket(targetCreature.getAbnormalStatusUpdate());
		}
	}
}