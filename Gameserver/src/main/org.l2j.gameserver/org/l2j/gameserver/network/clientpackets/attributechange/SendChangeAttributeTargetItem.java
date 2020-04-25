package org.l2j.gameserver.network.clientpackets.attributechange;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeInfo;

/**
 * @author Mobius
 */
public class SendChangeAttributeTargetItem extends ClientPacket {
    private int _crystalItemId;
    private int _itemObjId;

    @Override
    public void readImpl() {
        _crystalItemId = readInt();
        _itemObjId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Item item = activeChar.getInventory().getItemByObjectId(_itemObjId);
        if ((item == null) || !item.isWeapon()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        activeChar.sendPacket(new ExChangeAttributeInfo(_crystalItemId, item));
    }
}
