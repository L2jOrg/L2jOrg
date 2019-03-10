package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author l3x
 */
public class ExShowProcureCropDetail extends IClientOutgoingPacket {
    private final int _cropId;
    private final Map<Integer, CropProcure> _castleCrops = new HashMap<>();

    public ExShowProcureCropDetail(int cropId) {
        _cropId = cropId;

        for (Castle c : CastleManager.getInstance().getCastles()) {
            final CropProcure cropItem = CastleManorManager.getInstance().getCropProcure(c.getResidenceId(), cropId, false);
            if ((cropItem != null) && (cropItem.getAmount() > 0)) {
                _castleCrops.put(c.getResidenceId(), cropItem);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_PROCURE_CROP_DETAIL.writeId(packet);

        packet.putInt(_cropId); // crop id
        packet.putInt(_castleCrops.size()); // size

        for (Map.Entry<Integer, CropProcure> entry : _castleCrops.entrySet()) {
            final CropProcure crop = entry.getValue();
            packet.putInt(entry.getKey()); // manor name
            packet.putLong(crop.getAmount()); // buy residual
            packet.putLong(crop.getPrice()); // buy price
            packet.put((byte) crop.getReward()); // reward type
        }
    }
}
