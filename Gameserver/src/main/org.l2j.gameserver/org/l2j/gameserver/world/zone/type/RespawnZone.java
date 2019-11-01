package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Respawn zone implementation.
 *
 * @author Nyaran
 */
public class RespawnZone extends SpawnZone {
    private final Map<Race, String> raceRespawnPoint = new HashMap<>();

    public RespawnZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
    }

    @Override
    protected void onExit(Creature creature) {
    }

    public void addRaceRespawnPoint(String race, String point) {
        raceRespawnPoint.put(Race.valueOf(race), point);
    }

    public Map<Race, String> getAllRespawnPoints() {
        return raceRespawnPoint;
    }

    public String getRespawnPoint(Player player) {
        return raceRespawnPoint.get(player.getRace());
    }
}
