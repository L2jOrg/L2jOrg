package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author l3x
 */
public final class ExShowManorDefaultInfo extends ServerPacket {
    private final List<Seed> _crops;
    private final boolean _hideButtons;

    public ExShowManorDefaultInfo(boolean hideButtons) {
        _crops = CastleManorManager.getInstance().getCrops();
        _hideButtons = hideButtons;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_MANOR_DEFAULT_INFO);

        writeByte((byte) (_hideButtons ? 0x01 : 0x00)); // Hide "Seed Purchase" and "Crop Sales" buttons
        writeInt(_crops.size());
        for (Seed crop : _crops) {
            writeInt(crop.getCropId()); // crop Id
            writeInt(crop.getLevel()); // level
            writeInt((int) crop.getSeedReferencePrice()); // seed price
            writeInt((int) crop.getCropReferencePrice()); // crop price
            writeByte((byte) 1); // Reward 1 type
            writeInt(crop.getReward(1)); // Reward 1 itemId
            writeByte((byte) 1); // Reward 2 type
            writeInt(crop.getReward(2)); // Reward 2 itemId
        }
    }

}