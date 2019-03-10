package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.L2Seed;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author l3x
 */
public class ExShowCropInfo extends IClientOutgoingPacket {
    private final List<CropProcure> _crops;
    private final int _manorId;
    private final boolean _hideButtons;

    public ExShowCropInfo(int manorId, boolean nextPeriod, boolean hideButtons) {
        _manorId = manorId;
        _hideButtons = hideButtons;

        final CastleManorManager manor = CastleManorManager.getInstance();
        _crops = (nextPeriod && !manor.isManorApproved()) ? null : manor.getCropProcure(manorId, nextPeriod);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_CROP_INFO.writeId(packet);

        packet.put((byte)(_hideButtons ? 0x01 : 0x00)); // Hide "Crop Sales" button
        packet.putInt(_manorId); // Manor ID
        packet.putInt(0x00);
        if (_crops != null) {
            packet.putInt(_crops.size());
            for (CropProcure crop : _crops) {
                packet.putInt(crop.getId()); // Crop id
                packet.putLong(crop.getAmount()); // Buy residual
                packet.putLong(crop.getStartAmount()); // Buy
                packet.putLong(crop.getPrice()); // Buy price
                packet.put((byte) crop.getReward()); // Reward
                final L2Seed seed = CastleManorManager.getInstance().getSeedByCrop(crop.getId());
                if (seed == null) {
                    packet.putInt(0); // Seed level
                    packet.put((byte) 0x01); // Reward 1
                    packet.putInt(0); // Reward 1 - item id
                    packet.put((byte) 0x01); // Reward 2
                    packet.putInt(0); // Reward 2 - item id
                } else {
                    packet.putInt(seed.getLevel()); // Seed level
                    packet.put((byte) 0x01); // Reward 1
                    packet.putInt(seed.getReward(1)); // Reward 1 - item id
                    packet.put((byte) 0x01); // Reward 2
                    packet.putInt(seed.getReward(2)); // Reward 2 - item id
                }
            }
        }
    }
}