package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sdw
 */
public class ExBeautyItemList extends IClientOutgoingPacket {
    private static final int HAIR_TYPE = 0;
    private static final int FACE_TYPE = 1;
    private static final int COLOR_TYPE = 2;
    private final BeautyData _beautyData;
    private final Map<Integer, List<BeautyItem>> _colorData = new HashMap<>();
    private int _colorCount;

    public ExBeautyItemList(L2PcInstance activeChar) {
        _beautyData = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType());

        for (BeautyItem hair : _beautyData.getHairList().values()) {
            final List<BeautyItem> colors = new ArrayList<>();
            for (BeautyItem color : hair.getColors().values()) {
                colors.add(color);
                _colorCount++;
            }
            _colorData.put(hair.getId(), colors);
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BEAUTY_ITEM_LIST.writeId(packet);

        packet.putInt(HAIR_TYPE);
        packet.putInt(_beautyData.getHairList().size());
        for (BeautyItem hair : _beautyData.getHairList().values()) {
            packet.putInt(0); // ?
            packet.putInt(hair.getId());
            packet.putInt(hair.getAdena());
            packet.putInt(hair.getResetAdena());
            packet.putInt(hair.getBeautyShopTicket());
            packet.putInt(1); // Limit
        }

        packet.putInt(FACE_TYPE);
        packet.putInt(_beautyData.getFaceList().size());
        for (BeautyItem face : _beautyData.getFaceList().values()) {
            packet.putInt(0); // ?
            packet.putInt(face.getId());
            packet.putInt(face.getAdena());
            packet.putInt(face.getResetAdena());
            packet.putInt(face.getBeautyShopTicket());
            packet.putInt(1); // Limit
        }

        packet.putInt(COLOR_TYPE);
        packet.putInt(_colorCount);
        for (int hairId : _colorData.keySet()) {
            for (BeautyItem color : _colorData.get(hairId)) {
                packet.putInt(hairId);
                packet.putInt(color.getId());
                packet.putInt(color.getAdena());
                packet.putInt(color.getResetAdena());
                packet.putInt(color.getBeautyShopTicket());
                packet.putInt(1);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 29 + 24 * (_beautyData.getHairList().size() + _beautyData.getFaceList().size() + _colorData.size() * _colorData.values().stream().mapToInt(List::size).sum());
    }
}
