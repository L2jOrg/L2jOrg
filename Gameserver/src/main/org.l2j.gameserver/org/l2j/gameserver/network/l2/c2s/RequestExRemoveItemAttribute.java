package org.l2j.gameserver.network.l2.c2s;

import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.items.ItemAttributes;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ActionFailPacket;
import org.l2j.gameserver.network.l2.s2c.ExBaseAttributeCancelResult;
import org.l2j.gameserver.network.l2.s2c.ExShowBaseAttributeCancelWindow;
import org.l2j.gameserver.network.l2.s2c.InventoryUpdatePacket;

import java.nio.ByteBuffer;

/**
 * @author SYS
 */
public class RequestExRemoveItemAttribute extends L2GameClientPacket
{
	// Format: chd
	private int _objectId;
	private int _attributeId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_objectId = buffer.getInt();
		_attributeId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled() || activeChar.isInStoreMode() || activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		ItemInstance itemToUnnchant = inventory.getItemByObjectId(_objectId);

		if(itemToUnnchant == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		ItemAttributes set = itemToUnnchant.getAttributes();
		Element element = Element.getElementById(_attributeId);

		if(element == Element.NONE || set.getValue(element) <= 0)
		{
			activeChar.sendPacket(new ExBaseAttributeCancelResult(false, itemToUnnchant, element), ActionFailPacket.STATIC);
			return;
		}

		// проверка делается клиентом, если зашло в эту проверку знач чит
		if(!activeChar.reduceAdena(ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemToUnnchant), true))
		{
			activeChar.sendPacket(new ExBaseAttributeCancelResult(false, itemToUnnchant, element), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, ActionFailPacket.STATIC);
			return;
		}

		boolean equipped = false;
		if(equipped = itemToUnnchant.isEquipped())
			activeChar.getInventory().unEquipItem(itemToUnnchant);

		itemToUnnchant.setAttributeElement(element, 0);
		itemToUnnchant.setJdbcState(JdbcEntityState.UPDATED);
		itemToUnnchant.update();

		if(equipped)
			activeChar.getInventory().equipItem(itemToUnnchant);

		activeChar.sendPacket(new InventoryUpdatePacket().addModifiedItem(activeChar, itemToUnnchant));
		activeChar.sendPacket(new ExBaseAttributeCancelResult(true, itemToUnnchant, element));

		activeChar.updateStats();
	}
}