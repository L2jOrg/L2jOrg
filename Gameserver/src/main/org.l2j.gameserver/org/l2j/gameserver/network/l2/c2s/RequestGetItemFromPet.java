package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.model.items.PetInventory;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class RequestGetItemFromPet extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestGetItemFromPet.class);

	private int _objectId;
	private long _amount;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_objectId = buffer.getInt();
		_amount = buffer.getLong();
		_unknown = buffer.getInt(); // = 0 for most trades
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null || _amount < 1)
			return;

		PetInstance pet = activeChar.getPet();
		if(pet == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isOutOfControl())
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

		PetInventory petInventory = pet.getInventory();
		PcInventory playerInventory = activeChar.getInventory();

		ItemInstance item = petInventory.getItemByObjectId(_objectId);
		if(item == null || item.getCount() < _amount || item.isEquipped())
		{
			activeChar.sendActionFailed();
			return;
		}

		int slots = 0;
		long weight = item.getTemplate().getWeight() * _amount;
		if(!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
			slots = 1;

		if(!activeChar.getInventory().validateWeight(weight))
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}

		if(!activeChar.getInventory().validateCapacity(slots))
		{
			activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		playerInventory.addItem(petInventory.removeItemByObjectId(_objectId, _amount));

		pet.sendChanges();
		activeChar.sendChanges();
	}
}