package org.l2j.gameserver.engine.elemental;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class ElementalSpiritTemplate {

    private final byte type;
    private final byte stage;
    private final int npcId;
    private final int maxCharacteristics;
    private final int extractItem;

    private IntMap<SpiritLevel> levels;
    private List<ItemHolder> itemsToEvolve;
    private List<AbsorbItem> absorbItems;

    ElementalSpiritTemplate(byte type, byte stage, int npcId, int extractItem, int maxCharacteristics) {
        this.type = type;
        this.stage = stage;
        this.npcId = npcId;
        this.extractItem = extractItem;
        this.maxCharacteristics = maxCharacteristics;
        this.levels = new HashIntMap<>(10);

    }

    void addLevelInfo(int level, int attack, int defense, int criticalRate, int criticalDamage, long maxExperience) {
        SpiritLevel spiritLevel = new SpiritLevel();
        spiritLevel.attack = attack;
        spiritLevel.defense = defense;
        spiritLevel.criticalRate = criticalRate;
        spiritLevel.criticalDamage = criticalDamage;
        spiritLevel.maxExperience = maxExperience;
        levels.put(level, spiritLevel);
    }

    void addItemToEvolve(Integer itemId, Integer count) {
        if(isNull(itemsToEvolve)) {
            itemsToEvolve  = new ArrayList<>(2);
        }
        itemsToEvolve.add(new ItemHolder(itemId, count));
    }

    public byte getType() {
        return type;
    }

    public byte getStage() {
        return stage;
    }

    public int getNpcId() {
        return npcId;
    }

    public long getMaxExperienceAtLevel(byte level) {
        return levels.get(level).maxExperience;
    }

    public int getMaxLevel() {
        return levels.size();
    }

    public int getAttackAtLevel(byte level) {
        return levels.get(level).attack;
    }

    public int getDefenseAtLevel(byte level) {
        return levels.get(level).defense;
    }

    public int getCriticalRateAtLevel(byte level) {
        return levels.get(level).criticalRate;
    }

    public int getCriticalDamageAtLevel(byte level) {
        return levels.get(level).criticalDamage;
    }

    public int getMaxCharacteristics() {
        return maxCharacteristics;
    }

    public List<ItemHolder> getItemsToEvolve() {
        return isNull(itemsToEvolve) ? Collections.emptyList() : itemsToEvolve;
    }

    void addAbsorbItem(Integer itemId, Integer experience) {
        if(isNull(absorbItems)) {
            absorbItems = new ArrayList<>();
        }
        absorbItems.add(new AbsorbItem(itemId, experience));
    }

   public List<AbsorbItem> getAbsorbItems() {
        return isNull(absorbItems) ? Collections.emptyList() : absorbItems;
    }

    public int getExtractItem() {
        return extractItem;
    }

    private static class SpiritLevel {
        long maxExperience;
        int criticalDamage;
        int criticalRate;
        int defense;
        int attack;
    }
}
