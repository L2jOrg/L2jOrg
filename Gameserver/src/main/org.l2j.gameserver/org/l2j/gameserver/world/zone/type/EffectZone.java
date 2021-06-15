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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.TaskZoneSettings;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * another type of damage zone with skills
 *
 * @author kerberos
 * @author JoeAlisson
 */
public final class EffectZone extends Zone {

    private final Object taskLock = new Object();
    private boolean bypassConditions;
    private final List<Skill> skills = new ArrayList<>();
    private float chance;
    private int startTime;
    private int delay;
    private boolean showDangerIcon;

    private EffectZone(int id) {
        super(id);
        setSettings(new TaskZoneSettings());
    }

    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings) super.getSettings();
    }

    public int getSkillLevel(int skillId) {
        for (Skill skill : skills) {
            if(skill.getId() == skillId) {
                return skill.getLevel();
            }
        }
        return 0;
    }

    @Override
    protected boolean isAffected(Creature creature) {
        return super.isAffected(creature) && isPlayable(creature);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (!skills.isEmpty() && isNull(getSettings().getTask())) {
            synchronized (taskLock) {
                if (getSettings().getTask() == null) {
                    getSettings().setTask(ThreadPool.scheduleAtFixedRate(new ApplySkill(), startTime, delay));
                }
            }
        }

        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.ALTERED, true);
            if (showDangerIcon) {
                creature.setInsideZone(ZoneType.DANGER_AREA, true);
                creature.sendPacket(new EtcStatusUpdate(creature.getActingPlayer()));
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.ALTERED, false);
            if (showDangerIcon) {
                creature.setInsideZone(ZoneType.DANGER_AREA, false);
                if (!creature.isInsideZone(ZoneType.DANGER_AREA)) {
                    creature.sendPacket(new EtcStatusUpdate(creature.getActingPlayer()));
                }
            }
        }

        if (creatures.isEmpty() && nonNull(getSettings().getTask())) {
            getSettings().clear();
        }
    }

    private final class ApplySkill implements Runnable {

        @Override
        public void run() {
            if (isEnabled()) {
                for (Skill skill : skills) {
                    forEachCreature(c -> skill.activateSkill(c, c),  c -> canApplySkill(c, skill));
                }
            }
        }

        private boolean checkSkillCondition(Skill skill, Creature creature) {
            return nonNull(skill) && (bypassConditions || skill.checkCondition(creature, creature)) && creature.getAffectedSkillLevel(skill.getId()) < skill.getLevel();
        }

        private boolean canApplySkill(Creature creature, Skill skill) {
            return nonNull(creature) && !creature.isDead() && Rnd.chance(chance) && checkSkillCondition(skill, creature);
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var zone = new EffectZone(id);
            for(var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if(node.getNodeName().equals("attributes")) {
                    parseAttributes(zone, node, reader);
                } else if(node.getNodeName().equals("skill")) {
                    var skill = reader.parseSkillInfo(node);
                    if(nonNull(skill)) {
                        zone.skills.add(skill);
                    }
                }
            }
            return zone;
        }

        private void parseAttributes(EffectZone zone, Node node, GameXmlReader reader) {
            var attr = node.getAttributes();
            zone.chance = reader.parseFloat(attr, "chance");
            zone.bypassConditions = reader.parseBoolean(attr, "bypass-conditions");
            zone.showDangerIcon = reader.parseBoolean(attr, "show-danger-icon");
            zone.startTime = reader.parseInt(attr, "start-time");
            zone.delay = reader.parseInt(attr, "delay");
        }

        @Override
        public String type() {
            return "effect";
        }
    }
}