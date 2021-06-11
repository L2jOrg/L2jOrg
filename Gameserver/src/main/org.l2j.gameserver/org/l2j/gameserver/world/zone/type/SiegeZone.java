/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.entity.Siegable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.settings.FeatureSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import static org.l2j.gameserver.model.skills.CommonSkill.BATTLEGROUND_DEATH_SYNDROME;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A siege zone
 *
 * @author durgus
 */
public class SiegeZone extends Zone {
    private static final int DISMOUNT_DELAY = 5;
    private final int castleId;

    public SiegeZone(int id, int castleId) {
        super(id);
        this.castleId = castleId;
        setSettings(new Settings());
    }

    @Override
    public Settings getSettings() {
        return (Settings) super.getSettings();
    }

    @Override
    protected void onEnter(Creature creature) {
        if (getSettings().isActiveSiege()) {
            creature.setInsideZone(ZoneType.PVP, true);
            creature.setInsideZone(ZoneType.SIEGE, true);
            creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true); // FIXME: Custom ?

            if (isPlayer(creature)) {
                final Player player = creature.getActingPlayer();
                if (player.isRegisteredOnThisSiegeField(castleId)) {
                    player.setIsInSiege(true); // in siege
                    Siegable siegable;
                    if ((siegable = getSettings().getSiege()).giveFame() && (siegable.getFameFrequency() > 0)) {
                        player.startFameTask(siegable.getFameFrequency() * 1000L, siegable.getFameAmount());
                    }
                }

                creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                if (!FeatureSettings.allowWyvernInSiege() && (player.getMountType() == MountType.WYVERN)) {
                    player.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                    player.enteredNoLanding(DISMOUNT_DELAY);
                }

                if (!FeatureSettings.allowRideInSiege() && player.isMounted()) {
                    player.dismount();
                }

                if (!FeatureSettings.allowRideInSiege() && player.getTransformation().map(Transform::isRiding).orElse(false)) {
                    player.untransform();
                }
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.PVP, false);
        creature.setInsideZone(ZoneType.SIEGE, false);
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false); // FIXME: Custom ?

        if (getSettings().isActiveSiege()) {
            if (isPlayer(creature)) {
                final Player player = creature.getActingPlayer();
                creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                if (player.getMountType() == MountType.WYVERN) {
                    player.exitedNoLanding();
                }
                // Set pvp flag
                if (player.getPvpFlag() == 0) {
                    player.startPvPFlag();
                }
            }
        }

        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            player.stopFameTask();
            player.setIsInSiege(false);
        }
    }

    @Override
    public void onDieInside(Creature creature) {
        if (getSettings().isActiveSiege() && creature instanceof Player player && player.isRegisteredOnThisSiegeField(castleId)) {
            var level = 1;
            final var buff = creature.getEffectList().getBuffInfoBySkillId(BATTLEGROUND_DEATH_SYNDROME.getId());
            if (buff != null) {
                level = Math.min(level + buff.getSkill().getLevel(), 5);
            }

            final var skill = SkillEngine.getInstance().getSkill(BATTLEGROUND_DEATH_SYNDROME.getId(), level);
            if (skill != null) {
                skill.applyEffects(creature, creature);
            }
        }
    }

    @Override
    public void onPlayerLogoutInside(Player player) {
        var clan = player.getClan();
        if(clan == null || clan.getCastleId() != castleId) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }
    }

    public void updateZoneStatusForCharactersInside() {
        if (getSettings().isActiveSiege()) {
            forEachCreature(this::onEnter);
        } else {

            forEachCreature( creature ->  {
                creature.setInsideZone(ZoneType.PVP, false);
                creature.setInsideZone(ZoneType.SIEGE, false);
                creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);

                if (isPlayer(creature)) {
                    var player = creature.getActingPlayer();
                    creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                    player.stopFameTask();
                    if (player.getMountType() == MountType.WYVERN) {
                        player.exitedNoLanding();
                    }
                }
            });
        }
    }

    public int getSiegeObjectId() {
        return castleId;
    }

    public boolean isActive() {
        return getSettings().isActiveSiege();
    }

    public void setIsActive(boolean val) {
        getSettings().setActiveSiege(val);
    }

    public void setSiegeInstance(Siegable siege) {
        getSettings().setSiege(siege);
    }

    public static final class Settings extends AbstractZoneSettings {
        private Siegable siege = null;
        private boolean isActiveSiege = false;

       private Settings() {
        }

        public Siegable getSiege() {
            return siege;
        }

        public void setSiege(Siegable s) {
            siege = s;
        }

        public boolean isActiveSiege() {
            return isActiveSiege;
        }

        public void setActiveSiege(boolean val) {
            isActiveSiege = val;
        }

        @Override
        public void clear() {
            siege = null;
            isActiveSiege = false;
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var castleId = reader.parseInt(zoneNode.getAttributes(), "castle-id");
            var zone = new SiegeZone(id, castleId);
            var castle = CastleManager.getInstance().getCastleById(castleId);
            if(castle != null) {
                castle.setSiegeZone(zone);
            }
            return zone;
        }

        @Override
        public String type() {
            return "siege";
        }
    }
}
