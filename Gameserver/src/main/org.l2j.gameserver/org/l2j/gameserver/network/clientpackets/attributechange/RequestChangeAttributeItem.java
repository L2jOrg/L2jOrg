package org.l2j.gameserver.network.clientpackets.attributechange;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.PlayerInventory;
import org.l2j.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeOk;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author Mobius
 */
public class RequestChangeAttributeItem extends ClientPacket {
    private int _consumeItemId;
    private int _itemObjId;
    private int _newElementId;

    @Override
    public void readImpl() {
        _consumeItemId = readInt();
        _itemObjId = readInt();
        _newElementId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final PlayerInventory inventory = activeChar.getInventory();
        final Item item = inventory.getItemByObjectId(_itemObjId);

        // attempting to destroy item
        if (activeChar.getInventory().destroyItemByItemId("ChangeAttribute", _consumeItemId, 1, activeChar, item) == null) {
            client.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            client.sendPacket(ExChangeAttributeFail.STATIC);
            GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to change attribute without an attribute change crystal.");
            return;
        }

        // get values
        final int oldElementId = item.getAttackAttributeType().getClientId();
        final int elementValue = item.getAttackAttribute().getValue();
        item.clearAllAttributes();
        item.setAttribute(new AttributeHolder(AttributeType.findByClientId(_newElementId), elementValue), true);

        // send packets
        final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE);
        msg.addItemName(item);
        msg.addAttribute(oldElementId);
        msg.addAttribute(_newElementId);
        activeChar.sendPacket(msg);
        InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(item);
        for (Item i : activeChar.getInventory().getItemsByItemId(_consumeItemId)) {
            iu.addItem(i);
        }
        activeChar.sendPacket(iu);
        activeChar.broadcastUserInfo();
        activeChar.sendPacket(ExChangeAttributeOk.STATIC);
    }
}
