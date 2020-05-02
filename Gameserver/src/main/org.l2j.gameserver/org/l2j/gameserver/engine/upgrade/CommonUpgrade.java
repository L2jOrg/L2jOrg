package org.l2j.gameserver.engine.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.List;

/**
 * @author JoeAlisson
 */
public record CommonUpgrade(int id, int item, int enchantment, long commission, List<ItemHolder> results, int chance, List<ItemHolder> material, List<ItemHolder> failItems, List<ItemHolder> bonusItems) implements Upgrade {
}
