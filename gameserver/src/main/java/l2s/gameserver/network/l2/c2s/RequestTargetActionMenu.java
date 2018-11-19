package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.MyTargetSelectedPacket;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;

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