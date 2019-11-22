package org.l2j.gameserver.model.actor.transform;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.LinkedHashIntMap;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class TransformTemplate {
    private final Double collisionRadius;
    private final Double collisionHeight;
    private final WeaponType baseAttackType;
    private final IntMap<TransformLevelData> data = new LinkedHashIntMap<>(100);
    private List<SkillHolder> skills;
    private List<AdditionalSkillHolder> additionalSkills;
    private List<AdditionalItemHolder> additionalItems;
    private EnumMap<InventorySlot, Integer> baseDefense ;
    private IntMap<Double> baseStats;
    private ExBasicActionList list;

    public TransformTemplate(StatsSet set) {
        collisionRadius = set.contains("radius") ? set.getDouble("radius") : null;
        collisionHeight = set.contains("height") ? set.getDouble("height") : null;

        baseAttackType = set.getEnum("attackType", WeaponType.class, null);

        if (set.contains("range")) {
            addStats(Stats.PHYSICAL_ATTACK_RANGE, set.getDouble("range", 0));
        }
        if (set.contains("randomDamage")) {
            addStats(Stats.RANDOM_DAMAGE, set.getDouble("randomDamage", 0));
        }
        if (set.contains("walk")) {
            addStats(Stats.WALK_SPEED, set.getDouble("walk", 0));
        }
        if (set.contains("run")) {
            addStats(Stats.RUN_SPEED, set.getDouble("run", 0));
        }
        if (set.contains("waterWalk")) {
            addStats(Stats.SWIM_WALK_SPEED, set.getDouble("waterWalk", 0));
        }
        if (set.contains("waterRun")) {
            addStats(Stats.SWIM_RUN_SPEED, set.getDouble("waterRun", 0));
        }
        if (set.contains("flyWalk")) {
            addStats(Stats.FLY_WALK_SPEED, set.getDouble("flyWalk", 0));
        }
        if (set.contains("flyRun")) {
            addStats(Stats.FLY_RUN_SPEED, set.getDouble("flyRun", 0));
        }
        if (set.contains("pAtk")) {
            addStats(Stats.PHYSICAL_ATTACK, set.getDouble("pAtk", 0));
        }
        if (set.contains("mAtk")) {
            addStats(Stats.MAGIC_ATTACK, set.getDouble("mAtk", 0));
        }
        if (set.contains("range")) {
            addStats(Stats.PHYSICAL_ATTACK_RANGE, set.getInt("range", 0));
        }
        if (set.contains("attackSpeed")) {
            addStats(Stats.PHYSICAL_ATTACK_SPEED, set.getInt("attackSpeed", 0));
        }
        if (set.contains("critRate")) {
            addStats(Stats.CRITICAL_RATE, set.getInt("critRate", 0));
        }
        if (set.contains("str")) {
            addStats(Stats.STAT_STR, set.getInt("str", 0));
        }
        if (set.contains("int")) {
            addStats(Stats.STAT_INT, set.getInt("int", 0));
        }
        if (set.contains("con")) {
            addStats(Stats.STAT_CON, set.getInt("con", 0));
        }
        if (set.contains("dex")) {
            addStats(Stats.STAT_DEX, set.getInt("dex", 0));
        }
        if (set.contains("wit")) {
            addStats(Stats.STAT_WIT, set.getInt("wit", 0));
        }
        if (set.contains("men")) {
            addStats(Stats.STAT_MEN, set.getInt("men", 0));
        }

        if (set.contains("chest")) {
            addDefense(CHEST, set.getInt("chest", 0));
        }
        if (set.contains("legs")) {
            addDefense(LEGS, set.getInt("legs", 0));
        }
        if (set.contains("head")) {
            addDefense(HEAD, set.getInt("head", 0));
        }
        if (set.contains("feet")) {
            addDefense(FEET, set.getInt("feet", 0));
        }
        if (set.contains("gloves")) {
            addDefense(GLOVES, set.getInt("gloves", 0));
        }
        if (set.contains("underwear")) {
            addDefense(UNDERWEAR, set.getInt("underwear", 0));
        }
        if (set.contains("cloak")) {
            addDefense(BACK, set.getInt("cloak", 0));
        }
        if (set.contains("rear")) {
            addDefense(RIGHT_EAR, set.getInt("rear", 0));
        }
        if (set.contains("lear")) {
            addDefense(LEFT_EAR, set.getInt("lear", 0));
        }
        if (set.contains("rfinger")) {
            addDefense(RIGHT_FINGER, set.getInt("rfinger", 0));
        }
        if (set.contains("lfinger")) {
            addDefense(LEFT_FINGER, set.getInt("lfinger", 0));
        }
        if (set.contains("neck")) {
            addDefense(NECK, set.getInt("neck", 0));
        }
    }

    private void addDefense(InventorySlot slot, int val) {
        if (isNull(baseDefense)) {
            baseDefense = new EnumMap<>(InventorySlot.class);
        }
        baseDefense.put(slot, val);
    }

    /**
     * @param slot         the slot type for where to search defense.
     * @param defaultValue value to be used if no value for the type is found.
     * @return altered value if its present, or {@code defaultValue} if no such type is assigned to this template.
     */
    public int getDefense(InventorySlot slot, int defaultValue) {
        return isNull(baseDefense) ? defaultValue : baseDefense.getOrDefault(slot, defaultValue);
    }

    private void addStats(Stats stats, double val) {
        if (baseStats == null) {
            baseStats = new HashIntMap<>();
        }
        baseStats.put(stats.ordinal(), val);
    }

    /**
     * @param stats        the stat value to search for.
     * @param defaultValue value to be used if no such stat is found.
     * @return altered stat if its present, or {@code defaultValue} if no such stat is assigned to this template.
     */
    public double getStats(Stats stats, double defaultValue) {
        return baseStats == null ? defaultValue : baseStats.getOrDefault(stats.ordinal(), defaultValue);
    }

    /**
     * @return collision radius if set, {@code null} otherwise.
     */
    public Double getCollisionRadius() {
        return collisionRadius;
    }

    /**
     * @return collision height if set, {@code null} otherwise.
     */
    public Double getCollisionHeight() {
        return collisionHeight;
    }

    public WeaponType getBaseAttackType() {
        return baseAttackType;
    }

    public void addSkill(SkillHolder holder) {
        if (skills == null) {
            skills = new ArrayList<>();
        }
        skills.add(holder);
    }

    public List<SkillHolder> getSkills() {
        return skills != null ? skills : Collections.emptyList();
    }

    public void addAdditionalSkill(AdditionalSkillHolder holder) {
        if (additionalSkills == null) {
            additionalSkills = new ArrayList<>();
        }
        additionalSkills.add(holder);
    }

    public List<AdditionalSkillHolder> getAdditionalSkills() {
        return additionalSkills != null ? additionalSkills : Collections.emptyList();
    }

    public void addAdditionalItem(AdditionalItemHolder holder) {
        if (additionalItems == null) {
            additionalItems = new ArrayList<>();
        }
        additionalItems.add(holder);
    }

    public List<AdditionalItemHolder> getAdditionalItems() {
        return additionalItems != null ? additionalItems : Collections.emptyList();
    }

    public ExBasicActionList getBasicActionList() {
        return list;
    }

    public void setBasicActionList(ExBasicActionList list) {
        this.list = list;
    }

    public boolean hasBasicActionList() {
        return list != null;
    }

    public void addLevelData(TransformLevelData data) {
        this.data.put(data.getLevel(), data);
    }

    public TransformLevelData getData(int level) {
        return data.get(level);
    }
}
