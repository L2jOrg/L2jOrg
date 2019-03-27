package org.l2j.gameserver.model.zone.type;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.L2Spawn;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.model.zone.AbstractZoneSettings;
import org.l2j.gameserver.model.zone.L2ZoneRespawn;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExOlympiadMatchEnd;

import java.util.ArrayList;
import java.util.List;

/**
 * An olympiad stadium
 *
 * @author durgus, DS
 */
public class L2OlympiadStadiumZone extends L2ZoneRespawn {
    private final List<L2DoorInstance> _doors = new ArrayList<>(2);
    private final List<L2Spawn> _buffers = new ArrayList<>(2);
    private final List<Location> _spectatorLocations = new ArrayList<>(1);

    public L2OlympiadStadiumZone(int id) {
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
    protected final void onEnter(L2Character character) {
        if (getSettings().getOlympiadTask() != null) {
            if (getSettings().getOlympiadTask().isBattleStarted()) {
                character.setInsideZone(ZoneId.PVP, true);
                if (character.isPlayer()) {
                    character.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                    getSettings().getOlympiadTask().getGame().sendOlympiadInfo(character);
                }
            }
        }

        if (character.isPlayable()) {
            final L2PcInstance player = character.getActingPlayer();
            if (player != null) {
                // only participants, observers and GMs allowed
                if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && !player.isInOlympiadMode() && !player.inObserverMode()) {
                    ThreadPoolManager.getInstance().execute(new KickPlayer(player));
                } else {
                    // check for pet
                    final L2Summon pet = player.getPet();
                    if (pet != null) {
                        pet.unSummon(player);
                    }
                }
            }
        }
    }

    @Override
    protected final void onExit(L2Character character) {
        if (getSettings().getOlympiadTask() != null) {
            if (getSettings().getOlympiadTask().isBattleStarted()) {
                character.setInsideZone(ZoneId.PVP, false);
                if (character.isPlayer()) {
                    character.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                    character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
                }
            }
        }
    }

    public List<L2DoorInstance> getDoors() {
        return _doors;
    }

    public List<L2Spawn> getBuffers() {
        return _buffers;
    }

    public List<Location> getSpectatorSpawns() {
        return _spectatorLocations;
    }

    private static final class KickPlayer implements Runnable {
        private L2PcInstance _player;

        protected KickPlayer(L2PcInstance player) {
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
