package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import org.l2j.gameserver.mobius.gameserver.enums.Race;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneRespawn;

import java.util.HashMap;
import java.util.Map;

/**
 * Respawn zone implementation.
 *
 * @author Nyaran
 */
public class L2RespawnZone extends L2ZoneRespawn {
    private final Map<Race, String> _raceRespawnPoint = new HashMap<>();

    public L2RespawnZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character) {
    }

    @Override
    protected void onExit(L2Character character) {
    }

    public void addRaceRespawnPoint(String race, String point) {
        _raceRespawnPoint.put(Race.valueOf(race), point);
    }

    public Map<Race, String> getAllRespawnPoints() {
        return _raceRespawnPoint;
    }

    public String getRespawnPoint(L2PcInstance activeChar) {
        return _raceRespawnPoint.get(activeChar.getRace());
    }
}
