package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExGetCrystalizingEstimation;
import l2s.gameserver.utils.ItemFunctions;

public class RequestCrystallizeEstimate extends L2GameClientPacket
{
	private int _objectId;
	private long _count;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readQ();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!item.canBeCrystallized(activeChar))
		{
			if(item.isFlagNoCrystallize())
				ItemFunctions.deleteItem(activeChar, item, 1, true);
			else
				activeChar.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if(activeChar.isInTrainingCamp())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		int externalOrdinal = item.getTemplate().getGrade().extOrdinal();

		//can player crystallize?
		int level = activeChar.getSkillLevel(Skill.SKILL_CRYSTALLIZE);
		if(level < 1 || externalOrdinal > level)
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM);
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new ExGetCrystalizingEstimation(item));
	}
}