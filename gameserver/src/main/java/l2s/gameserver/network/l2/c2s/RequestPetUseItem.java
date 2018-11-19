package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public class RequestPetUseItem extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
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

		if(activeChar.isFishing())
			return;

		if(activeChar.isInTrainingCamp())
			return;

		PetInstance pet = activeChar.getPet();
		if(pet == null)
			return;

		ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);
		if(item == null || item.getCount() < 1)
			return;

		if(pet.isAlikeDead() || pet.isDead() || pet.isOutOfControl())
			return;

		if(pet.useItem(item, false, true))
			return;

		if(!item.isEquipped() && !item.getTemplate().testCondition(pet, item, false))
		{
			activeChar.sendPacket(SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return;
		}

		if(pet.isSharedGroupDisabled(item.getTemplate().getReuseGroup()))
			return;

		if(activeChar.getInventory().isLockedItem(item)) //TODO: [Bonux] проверить.
			return;

		pet.useItem(item, false, false);
	}
}