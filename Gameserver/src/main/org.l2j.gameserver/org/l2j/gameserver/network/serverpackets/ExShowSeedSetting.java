package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2Seed;
import org.l2j.gameserver.model.SeedProduction;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author l3x
 */
public class ExShowSeedSetting extends IClientOutgoingPacket {
    private final int _manorId;
    private final Set<L2Seed> _seeds;
    private final Map<Integer, SeedProduction> _current = new HashMap<>();
    private final Map<Integer, SeedProduction> _next = new HashMap<>();

    public ExShowSeedSetting(int manorId) {
        final CastleManorManager manor = CastleManorManager.getInstance();
        _manorId = manorId;
        _seeds = manor.getSeedsForCastle(_manorId);
        for (L2Seed s : _seeds) {
            // Current period
            SeedProduction sp = manor.getSeedProduct(manorId, s.getSeedId(), false);
            if (sp != null) {
                _current.put(s.getSeedId(), sp);
            }
            // Next period
            sp = manor.getSeedProduct(manorId, s.getSeedId(), true);
            if (sp != null) {
                _next.put(s.getSeedId(), sp);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_SEED_SETTING.writeId(packet);

        packet.putInt(_manorId); // manor id
        packet.putInt(_seeds.size()); // size

        for (L2Seed s : _seeds) {
            packet.putInt(s.getSeedId()); // seed id
            packet.putInt(s.getLevel()); // level
            packet.put((byte) 1);
            packet.putInt(s.getReward(1)); // reward 1 id
            packet.put((byte) 1);
            packet.putInt(s.getReward(2)); // reward 2 id
            packet.putInt(s.getSeedLimit()); // next sale limit
            packet.putInt(s.getSeedReferencePrice()); // price for castle to produce 1
            packet.putInt(s.getSeedMinPrice()); // min seed price
            packet.putInt(s.getSeedMaxPrice()); // max seed price
            // Current period
            if (_current.containsKey(s.getSeedId())) {
                final SeedProduction sp = _current.get(s.getSeedId());
                packet.putLong(sp.getStartAmount()); // sales
                packet.putLong(sp.getPrice()); // price
            } else {
                packet.putLong(0);
                packet.putLong(0);
            }
            // Next period
            if (_next.containsKey(s.getSeedId())) {
                final SeedProduction sp = _next.get(s.getSeedId());
                packet.putLong(sp.getStartAmount()); // sales
                packet.putLong(sp.getPrice()); // price
            } else {
                packet.putLong(0);
                packet.putLong(0);
            }
        }
        _current.clear();
        _next.clear();
    }
}