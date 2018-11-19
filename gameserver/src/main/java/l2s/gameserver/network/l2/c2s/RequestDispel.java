package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;

public class RequestDispel extends L2GameClientPacket
{
	private int _objectId, _id, _level;

	@Override
	protected void readImpl() throws Exception
	{
		_objectId = readD();
		_id = readD();
		_level = readD();
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getObjectId() != _objectId && !activeChar.isMyServitor(_objectId))
			return;

		Creature target = activeChar;
		if(activeChar.getObjectId() != _objectId)
			target = activeChar.getServitor(_objectId);

		for(Abnormal e : target.getAbnormalList())
		{
			if(e.getDisplayId() == _id && e.getDisplayLevel() == _level)
			{
				if(e.getSkill().getId() == 11541 || e.isSelfDispellable())
					e.exit();
				else
					return;
			}
		}
	}
}