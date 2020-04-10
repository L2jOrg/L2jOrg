package org.l2j.gameserver.engine.costume;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public class Costume {

    private final int id;
    private final int skill;
    private final int evolutionFee;
    private ItemHolder consumeItem;
    private Set<ItemHolder> extractCost = new HashSet<>();
    private ItemHolder extractItem;

    Costume(int id, int skill, int evolutionFee) {
        this.id = id;
        this.skill = skill;
        this.evolutionFee = evolutionFee;
    }

    void setConsumeItem(ItemHolder consumeItem) {
        this.consumeItem = consumeItem;
    }

    void setExtractItem(ItemHolder extractItem) {
        this.extractItem = extractItem;
    }

    void addExtractCost(ItemHolder costItem) {
        extractCost.add(costItem);
    }

    public int getId() {
        return id;
    }

    public int getSkill() {
        return skill;
    }
}
