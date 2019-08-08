package org.l2j.gameserver.world.zone.type;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.world.zone.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElseGet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * another type of damage zone with skills
 *
 * @author kerberos
 */
public final class EffectZone extends Zone {

    protected boolean bypassConditions;
    protected volatile Map<Integer, Integer> skills;
    private int chance;
    private int _initialDelay;
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
            case "initialDelay" -> _initialDelay = Integer.parseInt(value);
            case "reuse" -> reuse = Integer.parseInt(value);
            case "bypassSkillConditions" -> bypassConditions = Boolean.parseBoolean(value);
            case "maxDynamicSkillCount" -> skills = new ConcurrentHashMap<>(Integer.parseInt(value));
            case "showDangerIcon" -> isShowDangerIcon = Boolean.parseBoolean(value);
            case "skillIdLvl" -> parseSkills(value);
            default -> super.setParameter(name, value);
        }
    }

    private void parseSkills(String value) {
        final String[] propertySplit = value.split(";");
        skills = new ConcurrentHashMap<>(propertySplit.length);

        for (String skill : propertySplit) {
            final String[] skillSplit = skill.split("-");
            if (skillSplit.length != 2) {
                LOGGER.warn("invalid config property -> skillsIdLvl '{}'", skill);
            } else {
                try {
                    skills.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                } catch (NumberFormatException nfe) {
                    if (!skill.isEmpty()) {
                        LOGGER.warn("invalid config property -> skillsIdLvl '{}' '{}'", skillSplit[0], skillSplit[1]);
                    }
                }
            }
        }
    }

    @Override
    protected void onEnter(Creature character) {
        if (nonNull(skills)) {
            if (getSettings().getTask() == null) {
                synchronized (this) {
                    if (getSettings().getTask() == null) {
                        getSettings().setTask(ThreadPoolManager.scheduleAtFixedRate(new ApplySkill(), _initialDelay, reuse));
                    }
                }
            }
        }

        if (isPlayer(character)) {
            character.setInsideZone(ZoneType.ALTERED, true);
            if (isShowDangerIcon) {
                character.setInsideZone(ZoneType.DANGER_AREA, true);
                character.sendPacket(new EtcStatusUpdate(character.getActingPlayer()));
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            character.setInsideZone(ZoneType.ALTERED, false);
            if (isShowDangerIcon) {
                character.setInsideZone(ZoneType.DANGER_AREA, false);
                if (!character.isInsideZone(ZoneType.DANGER_AREA)) {
                    character.sendPacket(new EtcStatusUpdate(character.getActingPlayer()));
                }
            }
        }

        if (creatures.isEmpty() && (getSettings().getTask() != null)) {
            getSettings().clear();
        }
    }

    public int getChance() {
        return chance;
    }

    public void addSkill(int skillId, int skillLvL) {
        if (skillLvL < 1) // remove skill
        {
            removeSkill(skillId);
            return;
        }

        if (skills == null) {
            synchronized (this) {
                if (skills == null) {
                    skills = new ConcurrentHashMap<>(3);
                }
            }
        }
        skills.put(skillId, skillLvL);
    }

    public void removeSkill(int skillId) {
        if (skills != null) {
            skills.remove(skillId);
        }
    }

    public void clearSkills() {
        if (skills != null) {
            skills.clear();
        }
    }

    public int getSkillLevel(int skillId) {
        if ((skills == null) || !skills.containsKey(skillId)) {
            return 0;
        }
        return skills.get(skillId);
    }

    private final class ApplySkill implements Runnable {
        protected ApplySkill() {
            if (skills == null) {
                throw new IllegalStateException("No skills defined.");
            }
        }

        @Override
        public void run() {
            if (isEnabled()) {
                getCharactersInside().forEach(character ->
                {
                    if ((character != null) && !character.isDead() && (Rnd.get(100) < chance)) {
                        for (Entry<Integer, Integer> e : skills.entrySet()) {
                            final Skill skill = SkillData.getInstance().getSkill(e.getKey(), e.getValue());
                            if ((skill != null) && (bypassConditions || skill.checkCondition(character, character))) {
                                if (character.getAffectedSkillLevel(skill.getId()) < skill.getLevel()) {
                                    skill.activateSkill(character, character);
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}