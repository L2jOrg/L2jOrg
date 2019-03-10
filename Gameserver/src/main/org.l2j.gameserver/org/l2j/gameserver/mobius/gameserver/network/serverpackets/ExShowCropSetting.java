package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.L2Seed;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author l3x
 */
public class ExShowCropSetting extends IClientOutgoingPacket {
    private final int _manorId;
    private final Set<L2Seed> _seeds;
    private final Map<Integer, CropProcure> _current = new HashMap<>();
    private final Map<Integer, CropProcure> _next = new HashMap<>();

    public ExShowCropSetting(int manorId) {
        final CastleManorManager manor = CastleManorManager.getInstance();
        _manorId = manorId;
        _seeds = manor.getSeedsForCastle(_manorId);
        for (L2Seed s : _seeds) {
            // Current period
            CropProcure cp = manor.getCropProcure(manorId, s.getCropId(), false);
            if (cp != null) {
                _current.put(s.getCropId(), cp);
            }
            // Next period
            cp = manor.getCropProcure(manorId, s.getCropId(), true);
            if (cp != null) {
                _next.put(s.getCropId(), cp);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_CROP_SETTING.writeId(packet);

        packet.putInt(_manorId); // manor id
        packet.putInt(_seeds.size()); // size

        for (L2Seed s : _seeds) {
            packet.putInt(s.getCropId()); // crop id
            packet.putInt(s.getLevel()); // seed level
            packet.put((byte) 1);
            packet.putInt(s.getReward(1)); // reward 1 id
            packet.put((byte) 1);
            packet.putInt(s.getReward(2)); // reward 2 id
            packet.putInt(s.getCropLimit()); // next sale limit
            packet.putInt(0); // ???
            packet.putInt(s.getCropMinPrice()); // min crop price
            packet.putInt(s.getCropMaxPrice()); // max crop price
            // Current period
            if (_current.containsKey(s.getCropId())) {
                final CropProcure cp = _current.get(s.getCropId());
                packet.putLong(cp.getStartAmount()); // buy
                packet.putLong(cp.getPrice()); // price
                packet.put((byte) cp.getReward()); // reward
            } else {
                packet.putLong(0);
                packet.putLong(0);
                packet.put((byte) 0);
            }
            // Next period
            if (_next.containsKey(s.getCropId())) {
                final CropProcure cp = _next.get(s.getCropId());
                packet.putLong(cp.getStartAmount()); // buy
                packet.putLong(cp.getPrice()); // price
                packet.put((byte) cp.getReward()); // reward
            } else {
                packet.putLong(0);
                packet.putLong(0);
                packet.put((byte) 0);
            }
        }
        _next.clear();
        _current.clear();
    }
}