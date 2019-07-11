package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.zone.ZoneRespawn;

import java.util.HashMap;
import java.util.Map;

/**
 * Respawn zone implementation.
 *
 * @author Nyaran
 */
public class RespawnZone extends ZoneRespawn {
    private final Map<Race, String> _raceRespawnPoint = new HashMap<>();

    public RespawnZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
    }

    @Override
    protected void onExit(Creature character) {
    }

    public void addRaceRespawnPoint(String race, String point) {
        _raceRespawnPoint.put(Race.valueOf(race), point);
    }

    public Map<Race, String> getAllRespawnPoints() {
        return _raceRespawnPoint;
    }

    public String getRespawnPoint(Player activeChar) {
        return _raceRespawnPoint.get(activeChar.getRace());
    }
}
