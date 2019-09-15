package org.l2j.gameserver.model.teleporter;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.network.NpcStringId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid
 */
public class TeleportLocation extends Location {
    private final int _id;
    private final String _name;
    private final int _npcStringId;
    private final int _questZoneId;
    private final int _feeId;
    private final long _feeCount;
    private final List<Integer> _castleId;

    public TeleportLocation(int id, StatsSet set) {
        super(set);
        _id = id;
        _name = set.getString("name", null);
        _npcStringId = set.getInt("npcStringId", -1);
        _questZoneId = set.getInt("questZoneId", 0);
        _feeId = set.getInt("feeId", CommonItem.ADENA);
        _feeCount = set.getLong("feeCount", 0);

        final String castleIds = set.getString("castleId", "");
        if (castleIds.isEmpty()) {
            _castleId = Collections.emptyList();
        } else if (!castleIds.contains(";")) {
            _castleId = Collections.singletonList(Integer.parseInt(castleIds));
        } else {
            _castleId = new ArrayList<>();
            for (String castleId : castleIds.split(";")) {
                _castleId.add(Integer.parseInt(castleId));
            }
        }
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getNpcStringId() {
        return _npcStringId;
    }

    public int getQuestZoneId() {
        return _questZoneId;
    }

    public int getFeeId() {
        return _feeId;
    }

    public long getFeeCount() {
        return _feeCount;
    }

    public List<Integer> getCastleId() {
        return _castleId;
    }
}