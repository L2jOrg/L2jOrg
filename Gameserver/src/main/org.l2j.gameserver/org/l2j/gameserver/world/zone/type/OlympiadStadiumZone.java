package org.l2j.gameserver.world.zone.type;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.world.zone.ZoneRespawn;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExOlympiadMatchEnd;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * An olympiad stadium
 *
 * @author durgus, DS
 */
public class OlympiadStadiumZone extends ZoneRespawn {
    private final List<Door> _doors = new ArrayList<>(2);
    private final List<Spawn> _buffers = new ArrayList<>(2);
    private final List<Location> _spectatorLocations = new ArrayList<>(1);

    public OlympiadStadiumZone(int id) {
        super(id);
        AbstractZoneSettings settings = ZoneManager.getSettings(getName());
        if (settings == null) {
            settings = new Settings();
        }
        setSettings(settings);
    }

    @Override
    public Settings getSettings() {
        return (Settings) super.getSettings();
    }

    @Override
    public void parseLoc(int x, int y, int z, String type) {
        if ((type != null) && type.equals("spectatorSpawn")) {
            _spectatorLocations.add(new Location(x, y, z));
        } else {
            super.parseLoc(x, y, z, type);
        }
    }

    public final void registerTask(OlympiadGameTask task) {
        getSettings().setTask(task);
    }

    @Override
    protected final void onEnter(Creature character) {
        if (getSettings().getOlympiadTask() != null) {
            if (getSettings().getOlympiadTask().isBattleStarted()) {
                character.setInsideZone(ZoneType.PVP, true);
                if (isPlayer(character)) {
                    character.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                    getSettings().getOlympiadTask().getGame().sendOlympiadInfo(character);
                }
            }
        }

        if (isPlayable(character)) {
            final Player player = character.getActingPlayer();
            if (player != null) {
                // only participants, observers and GMs allowed
                if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && !player.isInOlympiadMode() && !player.inObserverMode()) {
                    ThreadPoolManager.execute(new KickPlayer(player));
                } else {
                    // check for pet
                    final Summon pet = player.getPet();
                    if (pet != null) {
                        pet.unSummon(player);
                    }
                }
            }
        }
    }

    @Override
    protected final void onExit(Creature character) {
        if (getSettings().getOlympiadTask() != null) {
            if (getSettings().getOlympiadTask().isBattleStarted()) {
                character.setInsideZone(ZoneType.PVP, false);
                if (isPlayer(character)) {
                    character.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                    character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
                }
            }
        }
    }

    public List<Door> getDoors() {
        return _doors;
    }

    public List<Spawn> getBuffers() {
        return _buffers;
    }

    public List<Location> getSpectatorSpawns() {
        return _spectatorLocations;
    }

    private static final class KickPlayer implements Runnable {
        private Player _player;

        protected KickPlayer(Player player) {
            _player = player;
        }

        @Override
        public void run() {
            if (_player != null) {
                _player.getServitors().values().forEach(s -> s.unSummon(_player));
                _player.teleToLocation(TeleportWhereType.TOWN, null);
                _player = null;
            }
        }
    }

    public final class Settings extends AbstractZoneSettings {
        private OlympiadGameTask _task = null;

        protected Settings() {
        }

        public OlympiadGameTask getOlympiadTask() {
            return _task;
        }

        protected void setTask(OlympiadGameTask task) {
            _task = task;
        }

        @Override
        public void clear() {
            _task = null;
        }
    }
}
