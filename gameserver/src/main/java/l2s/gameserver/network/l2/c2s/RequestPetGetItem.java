package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ItemFunctions;

public class RequestPetGetItem extends L2GameClientPacket
{
	// format: cd
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

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		PetInstance pet = activeChar.getPet();
		if(pet == null || pet.isDead() || pet.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		ItemInstance item = (ItemInstance) activeChar.getVisibleObject(_objectId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!ItemFunctions.checkIfCanPickup(pet, item))
		{
			SystemMessagePacket sm;
			if(item.getItemId() == 57)
			{
				sm = new SystemMessagePacket(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
				sm.addLong(item.getCount());
			}
			else
			{
				sm = new SystemMessagePacket(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1);
				sm.addItemName(item.getItemId());
			}
			sendPacket(sm);
			activeChar.sendActionFailed();
			return;
		}

		pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item, null);
	}
}