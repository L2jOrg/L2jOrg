package org.l2j.gameserver.network.serverpackets.appearance;

import org.l2j.gameserver.model.items.appearance.AppearanceStone;
import org.l2j.gameserver.model.items.appearance.AppearanceTargetType;
import org.l2j.gameserver.model.items.appearance.AppearanceType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExChooseShapeShiftingItem extends IClientOutgoingPacket {
    private final AppearanceType _type;
    private final AppearanceTargetType _targetType;
    private final int _itemId;

    public ExChooseShapeShiftingItem(AppearanceStone stone) {
        _type = stone.getType();
        _targetType = stone.getTargetTypes().size() > 1 ? AppearanceTargetType.ALL : stone.getTargetTypes().get(0);
        _itemId = stone.getId();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHOOSE_SHAPE_SHIFTING_ITEM.writeId(packet);

        packet.putInt(_targetType != null ? _targetType.ordinal() : 0);
        packet.putInt(_type != null ? _type.ordinal() : 0);
        packet.putInt(_itemId);
    }
}
