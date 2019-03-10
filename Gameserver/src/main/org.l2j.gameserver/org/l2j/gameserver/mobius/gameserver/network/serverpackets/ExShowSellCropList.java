package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.L2Seed;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author l3x
 */
public final class ExShowSellCropList extends IClientOutgoingPacket {
    private final int _manorId;
    private final Map<Integer, L2ItemInstance> _cropsItems = new HashMap<>();
    private final Map<Integer, CropProcure> _castleCrops = new HashMap<>();

    public ExShowSellCropList(PcInventory inventory, int manorId) {
        _manorId = manorId;
        for (int cropId : CastleManorManager.getInstance().getCropIds()) {
            final L2ItemInstance item = inventory.getItemByItemId(cropId);
            if (item != null) {
                _cropsItems.put(cropId, item);
            }
        }

        for (CropProcure crop : CastleManorManager.getInstance().getCropProcure(_manorId, false)) {
            if (_cropsItems.containsKey(crop.getId()) && (crop.getAmount() > 0)) {
                _castleCrops.put(crop.getId(), crop);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_SELL_CROP_LIST.writeId(packet);

        packet.putInt(_manorId); // manor id
        packet.putInt(_cropsItems.size()); // size
        for (L2ItemInstance item : _cropsItems.values()) {
            final L2Seed seed = CastleManorManager.getInstance().getSeedByCrop(item.getId());
            packet.putInt(item.getObjectId()); // Object id
            packet.putInt(item.getId()); // crop id
            packet.putInt(seed.getLevel()); // seed level
            packet.put((byte) 0x01);
            packet.putInt(seed.getReward(1)); // reward 1 id
            packet.put((byte) 0x01);
            packet.putInt(seed.getReward(2)); // reward 2 id
            if (_castleCrops.containsKey(item.getId())) {
                final CropProcure crop = _castleCrops.get(item.getId());
                packet.putInt(_manorId); // manor
                packet.putLong(crop.getAmount()); // buy residual
                packet.putLong(crop.getPrice()); // buy price
                packet.put((byte) crop.getReward()); // reward
            } else {
                packet.putInt(0xFFFFFFFF); // manor
                packet.putLong(0x00); // buy residual
                packet.putLong(0x00); // buy price
                packet.put((byte) 0x00); // reward
            }
            packet.putLong(item.getCount()); // my crops
        }
    }
}