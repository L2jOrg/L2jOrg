package org.l2j.gameserver.skills.skillclasses;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.ChestInstance;
import org.l2j.gameserver.model.instances.DoorInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.templates.StatsSet;

public class Unlock extends Skill
{
	private final int _unlockPower;

	public Unlock(StatsSet set)
	{
		super(set);
		_unlockPower = set.getInteger("unlockPower", 0) + 100;
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(target == null || target instanceof ChestInstance && target.isDead())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if(target instanceof ChestInstance && activeChar.isPlayer())
			return true;

		if(!target.isDoor() || _unlockPower == 0)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		DoorInstance door = (DoorInstance) target;

		if(door.isOpen())
		{
			activeChar.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
			return false;
		}

		if(!door.isUnlockable())
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		if(door.getKey() > 0) // ключ не подходит к двери
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		if(_unlockPower - door.getLevel() * 100 < 0) // Дверь слишком высокого уровня
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!activeChar.isPlayer())
			return;

		if(target.isDoor())
		{
			final DoorInstance door = (DoorInstance) target;
			if(!door.isOpen() && (door.getKey() > 0 || Rnd.chance(_unlockPower - door.getLevel() * 100)))
				door.openMe(activeChar.getPlayer(), true);
			else
				activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
		}
		else if(target instanceof ChestInstance)
		{
			final ChestInstance chest = (ChestInstance) target;
			if(!chest.isDead())
				chest.tryOpen(activeChar.getPlayer(), this);
		}
	}
}