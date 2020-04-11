package org.l2j.gameserver.engine.costume;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.Set;

/**
 * @author JoeAlisson
 */
public record Costume(int id, int skill, int evolutionFee, int extractItem, Set<ItemHolder> extractCost) {
}
