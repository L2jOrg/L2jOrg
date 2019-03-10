package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

import java.nio.ByteBuffer;

/**
 * @author KenM, Gnacik
 */
public class RequestChangeNicknameColor extends IClientIncomingPacket {
    private static final int COLORS[] =
            {
                    0x9393FF, // Pink
                    0x7C49FC, // Rose Pink
                    0x97F8FC, // Lemon Yellow
                    0xFA9AEE, // Lilac
                    0xFF5D93, // Cobalt Violet
                    0x00FCA0, // Mint Green
                    0xA0A601, // Peacock Green
                    0x7898AF, // Yellow Ochre
                    0x486295, // Chocolate
                    0x999999, // Silver
            };

    private int _colorNum;
    private int _itemObjectId;
    private String _title;

    @Override
    public void readImpl(ByteBuffer packet) {
        _colorNum = packet.getInt();
        _title = readString(packet);
        _itemObjectId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if ((_colorNum < 0) || (_colorNum >= COLORS.length)) {
            return;
        }

        final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjectId);
        if ((item == null) || (item.getEtcItem() == null) || (item.getEtcItem().getHandlerName() == null) || !item.getEtcItem().getHandlerName().equalsIgnoreCase("NicknameColor")) {
            return;
        }

        if (activeChar.destroyItem("Consume", item, 1, null, true)) {
            activeChar.setTitle(_title);
            activeChar.getAppearance().setTitleColor(COLORS[_colorNum]);
            activeChar.broadcastUserInfo();
        }
    }
}
