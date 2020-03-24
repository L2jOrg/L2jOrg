package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author l3x
 */
public class ExShowProcureCropDetail extends ServerPacket {
    private final int _cropId;
    private final Map<Integer, CropProcure> _castleCrops = new HashMap<>();

    public ExShowProcureCropDetail(int cropId) {
        _cropId = cropId;

        for (Castle c : CastleManager.getInstance().getCastles()) {
            final CropProcure cropItem = CastleManorManager.getInstance().getCropProcure(c.getId(), cropId, false);
            if ((cropItem != null) && (cropItem.getAmount() > 0)) {
                _castleCrops.put(c.getId(), cropItem);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_PROCURE_CROP_DETAIL);

        writeInt(_cropId); // crop id
        writeInt(_castleCrops.size()); // size

        for (Map.Entry<Integer, CropProcure> entry : _castleCrops.entrySet()) {
            final CropProcure crop = entry.getValue();
            writeInt(entry.getKey()); // manor name
            writeLong(crop.getAmount()); // buy residual
            writeLong(crop.getPrice()); // buy price
            writeByte((byte) crop.getReward()); // reward type
        }
    }

}
