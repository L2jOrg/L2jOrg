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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.world.zone.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.*;
import static org.l2j.commons.util.Util.isInteger;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * another type of damage zone with skills
 *
 * @author kerberos
 */
public final class EffectZone extends Zone {

    private static final Logger LOGGER = LoggerFactory.getLogger(EffectZone.class);

    private final Object taskLock = new Object();
    private boolean bypassConditions;
    private List<SkillHolder> skills;
    private int chance;
    private int initialDelay;
    private int reuse;
    private boolean isShowDangerIcon;

    public EffectZone(int id) {
        super(id);

        chance = 100;
        reuse = 30000;

        setTargetType(InstanceType.Playable); // default only playable

        isShowDangerIcon = true;
        AbstractZoneSettings settings = requireNonNullElseGet(ZoneManager.getSettings(getName()), TaskZoneSettings::new);
        setSettings(settings);
    }

    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings) super.getSettings();
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "chance" -> chance = Integer.parseInt(value);
            case "initialDelay" -> initialDelay = Integer.parseInt(value);
            case "reuse" -> reuse = Integer.parseInt(value);
            case "bypassSkillConditions" -> bypassConditions = Boolean.parseBoolean(value);
            case "maxDynamicSkillCount" -> skills = new ArrayList<>(Integer.parseInt(value));
            case "showDangerIcon" -> isShowDangerIcon = Boolean.parseBoolean(value);
            case "skillIdLvl" -> parseSkills(value);
            default -> super.setParameter(name, value);
        }
    }

    private void parseSkills(String value) {
        final String[] propertySplit = value.split(";");

        skills = Arrays.stream(propertySplit)
                .map(s -> s.split("-"))
                .filter(this::validSkillProperty)
                .map(s -> new SkillHolder(Integer.parseInt(s[0]), Integer.parseInt(s[1])))
                .collect(Collectors.toList());
    }

    private boolean validSkillProperty(String[] skillIdLvl) {
        if (skillIdLvl.length != 2 || !isInteger(skillIdLvl[0]) || !isInteger(skillIdLvl[1])) {
            LOGGER.warn("invalid config property -> skillsIdLvl '{}'", (Object) skillIdLvl);
            return false;
        }
        return true;
    }

    public int getSkillLevel(int skillId)
    {
        if ((skills == null) || skillId > skills.size() || skills.get(skillId) == null)
        {
            return 0;
        }
        return skills.get(skillId).getLevel();
    }

    @Override
    protected void onEnter(Creature creature) {
        if (nonNull(skills) && isNull(getSettings().getTask())) {
            synchronized (taskLock) {
                if (getSettings().getTask() == null) {
                    getSettings().setTask(ThreadPool.scheduleAtFixedRate(new ApplySkill(), initialDelay, reuse));
                }
            }
        }

        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.ALTERED, true);
            if (isShowDangerIcon) {
                creature.setInsideZone(ZoneType.DANGER_AREA, true);
                creature.sendPacket(new EtcStatusUpdate(creature.getActingPlayer()));
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.ALTERED, false);
            if (isShowDangerIcon) {
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
        ApplySkill() {
            if (isNull(skills)) {
                throw new IllegalStateException("No skills defined.");
            }
        }

        @Override
        public void run() {
            if (isEnabled()) {
                skills.stream()
                    .map(SkillHolder::getSkill)
                    .forEach(s -> forEachCreature(c -> s.activateSkill(c, c), c -> canApplySkill(c) && checkSkillCondition(s, c)));
            }
        }

        private boolean checkSkillCondition(Skill skill, Creature creature) {
            return nonNull(skill) && (bypassConditions || skill.checkCondition(creature, creature)) && creature.getAffectedSkillLevel(skill.getId()) < skill.getLevel();
        }

        private boolean canApplySkill(Creature creature) {
            return nonNull(creature) && !creature.isDead() && Rnd.chance(chance);
        }
    }
}