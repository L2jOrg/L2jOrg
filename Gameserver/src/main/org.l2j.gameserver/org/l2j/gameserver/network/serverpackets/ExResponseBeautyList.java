package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author Sdw
 */
public class ExResponseBeautyList extends IClientOutgoingPacket {
    public static final int SHOW_FACESHAPE = 1;
    public static final int SHOW_HAIRSTYLE = 0;
    private final L2PcInstance _activeChar;
    private final int _type;
    private final Map<Integer, BeautyItem> _beautyItem;

    public ExResponseBeautyList(L2PcInstance activeChar, int type) {
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
        writeId(OutgoingPackets.EX_RESPONSE_BEAUTY_LIST);

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
