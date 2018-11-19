package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.dao.PetDAO;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Log;

/**
 * format:		cdd
 * format:		cdQ - Gracia Final
 */
public class RequestDestroyItem extends L2GameClientPacket
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

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
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

		long count = _count;

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(count < 1)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
			return;
		}

		if(!activeChar.isGM() && item.isHeroWeapon())
		{
			activeChar.sendPacket(SystemMsg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
			return;
		}

		if(activeChar.getPet() != null && activeChar.getPet().getControlItemObjId() == item.getObjectId())
		{
			activeChar.sendPacket(SystemMsg.AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED);
			return;
		}

		if(!activeChar.isGM() && !item.canBeDestroyed(activeChar))
		{
			activeChar.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_DISCARDED);
			return;
		}

		if(_count > item.getCount())
			count = item.getCount();

		Log.LogItem(activeChar, Log.Delete, item, count);

		if(!activeChar.getInventory().destroyItemByObjectId(_objectId, count))
		{
			activeChar.sendActionFailed();
			return;
		}

		// При удалении ошейника, удалить пета
		if(PetDataHolder.getInstance().isControlItem(item.getItemId()))
			PetDAO.getInstance().deletePet(item, activeChar);

		boolean crystallize = item.canBeCrystallized(activeChar);
		if(!crystallize)
		{
			activeChar.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), count));
			activeChar.sendChanges();
		}
		else
			activeChar.sendActionFailed();
	}
}