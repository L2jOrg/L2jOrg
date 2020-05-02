package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * A simple DTO for items; contains item ID and count.<br>
 * Extended by {@link ItemChanceHolder}, {@link QuestItemHolder}, {@link UniqueItemHolder}.
 *
 * @author UnAfraid
 */
public class ItemHolder implements IIdentifiable {
    private final int id;
    private final long count;
    private final int enchantment;

    public ItemHolder(int id, long count) {
        this(id, count, 0);
    }

    public ItemHolder(int id, long count, int enchantment) {
        this.id = id;
        this.count = count;
        this.enchantment = enchantment;
    }

    @Override
    public int getId() {
        return id;
    }

    public long getCount() {
        return count;
    }

    public int getEnchantment() {
        return enchantment;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemHolder objInstance)) {
            return false;
        } else if (obj == this) {
            return true;
        }
        return (id == objInstance.getId()) && (count == objInstance.getCount());
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] ID: " + id + ", count: " + count;
    }
}
