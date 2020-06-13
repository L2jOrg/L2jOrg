/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.entity.Siegable;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A siege zone
 *
 * @author durgus
 */
public class SiegeZone extends Zone {
    private static final int DISMOUNT_DELAY = 5;

    public SiegeZone(int id) {
        super(id);
        AbstractZoneSettings settings = ZoneManager.getSettings(getName());
        if (isNull(settings)) {
            settings = new Settings();
        }
        setSettings(settings);
    }

    @Override
    public Settings getSettings() {
        return (Settings) super.getSettings();
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("castleId")) {
            if (getSettings().getSiegeableId() != -1) {
                throw new IllegalArgumentException("Siege object already defined!");
            }
            getSettings().setSiegeableId(Integer.parseInt(value));
        } else if (name.equals("fortId")) {
            if (getSettings().getSiegeableId() != -1) {
                throw new IllegalArgumentException("Siege object already defined!");
            }
            getSettings().setSiegeableId(Integer.parseInt(value));
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        if (getSettings().isActiveSiege()) {
            creature.setInsideZone(ZoneType.PVP, true);
            creature.setInsideZone(ZoneType.SIEGE, true);
            creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true); // FIXME: Custom ?

            if (isPlayer(creature)) {
                final Player player = creature.getActingPlayer();
                if (player.isRegisteredOnThisSiegeField(getSettings().getSiegeableId())) {
                    player.setIsInSiege(true); // in siege
                    Siegable siegable;
                    if ((siegable = getSettings().getSiege()).giveFame() && (siegable.getFameFrequency() > 0)) {
                        player.startFameTask(siegable.getFameFrequency() * 1000, siegable.getFameAmount());
                    }
                }

                creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                if (!Config.ALLOW_WYVERN_DURING_SIEGE && (player.getMountType() == MountType.WYVERN)) {
                    player.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                    player.enteredNoLanding(DISMOUNT_DELAY);
                }

                if (!Config.ALLOW_MOUNTS_DURING_SIEGE && player.isMounted()) {
                    player.dismount();
                }

                if (!Config.ALLOW_MOUNTS_DURING_SIEGE && player.getTransformation().map(Transform::isRiding).orElse(false)) {
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
        if (getSettings().isActiveSiege()) {
            // debuff participants only if they die inside siege zone
            if (isPlayer(creature) && creature.getActingPlayer().isRegisteredOnThisSiegeField(getSettings().getSiegeableId())) {
                int lvl = 1;
                final BuffInfo info = creature.getEffectList().getBuffInfoBySkillId(5660);
                if (info != null) {
                    lvl = Math.min(lvl + info.getSkill().getLevel(), 5);
                }

                final Skill skill = SkillEngine.getInstance().getSkill(5660, lvl);
                if (skill != null) {
                    skill.applyEffects(creature, creature);
                }
            }
        }
    }

    @Override
    public void onPlayerLogoutInside(Player player) {
        if (player.getClanId() != getSettings().getSiegeableId()) {
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
        return getSettings().getSiegeableId();
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

    /**
     * Removes all foreigners from the zone
     *
     * @param owningClanId
     */
    public void banishForeigners(int owningClanId) {
        forEachPlayer(p -> p.teleToLocation(TeleportWhereType.TOWN), p -> p.getClanId() != owningClanId);
    }

    public static final class Settings extends AbstractZoneSettings {
        private int siegableId = -1;
        private Siegable siege = null;
        private boolean isActiveSiege = false;

        protected Settings() {
        }

        public int getSiegeableId() {
            return siegableId;
        }

        protected void setSiegeableId(int id) {
            siegableId = id;
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
            siegableId = -1;
            siege = null;
            isActiveSiege = false;
        }
    }
}
