package org.l2j.gameserver.engine.costume;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.Set;

/**
 * @author JoeAlisson
 */
public record Costume(int id, Skill skill, int evolutionFee, int extractItem, Set<ItemHolder> extractCost) {
}
