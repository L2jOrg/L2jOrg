package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Map;

/**
 * @author Sdw
 */
public class ExResponseBeautyList extends ServerPacket {
    public static final int SHOW_FACESHAPE = 1;
    public static final int SHOW_HAIRSTYLE = 0;
    private final Player _activeChar;
    private final int _type;
    private final Map<Integer, BeautyItem> _beautyItem;

    public ExResponseBeautyList(Player activeChar, int type) {
        _activeChar = activeChar;
        _type = type;
        if (type == SHOW_HAIRSTYLE) {
            _beautyItem = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType()).getHairList();
        } else {
            _beautyItem = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType()).getFaceList();
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_RESPONSE_BEAUTY_LIST);

        writeLong(_activeChar.getAdena());
        writeLong(_activeChar.getBeautyTickets());
        writeInt(_type);
        writeInt(_beautyItem.size());
        for (BeautyItem item : _beautyItem.values()) {
            writeInt(item.getId());
            writeInt(1); // Limit
        }
        writeInt(0);
    }

}
