package org.l2j.gameserver.engine.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.List;

/**
 * @author JoeAlisson
 */
public record RareUpgrade(int id, int item, int enchantment, long commission, int result, int resultEnchantment, List<ItemHolder> material) implements Upgrade {
}
