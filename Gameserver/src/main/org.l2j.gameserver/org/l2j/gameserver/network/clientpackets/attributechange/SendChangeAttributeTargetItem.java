package org.l2j.gameserver.network.clientpackets.attributechange;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
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
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);
        if ((item == null) || !item.isWeapon()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        activeChar.sendPacket(new ExChangeAttributeInfo(_crystalItemId, item));
    }
}
