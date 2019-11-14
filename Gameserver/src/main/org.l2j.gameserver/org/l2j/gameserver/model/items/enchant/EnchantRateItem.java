package org.l2j.gameserver.model.items.enchant;

import org.l2j.gameserver.model.items.ItemTemplate;

/**
 * @author UnAfraid
 */
public final class EnchantRateItem {
    private final String _name;
    private int _itemId;
    private long _slot;
    private Boolean _isMagicWeapon = null;

    public EnchantRateItem(String name) {
        _name = name;
    }

    /**
     * @return name of enchant group.
     */
    public String getName() {
        return _name;
    }

    /**
     * Adds item id verification.
     *
     * @param id
     */
    public void setItemId(int id) {
        _itemId = id;
    }

    /**
     * Adds body slot verification.
     *
     * @param slot
     */
    public void addSlot(long slot) {
        _slot |= slot;
    }

    /**
     * Adds magic weapon verification.
     *
     * @param magicWeapon
     */
    public void setMagicWeapon(boolean magicWeapon) {
        _isMagicWeapon = magicWeapon;
    }

    /**
     * @param item
     * @return {@code true} if item can be used with this rate group, {@code false} otherwise.
     */
    public boolean validate(ItemTemplate item) {
        if ((_itemId != 0) && (_itemId != item.getId())) {
            return false;
        } else if ((_slot != 0) && ((item.getBodyPart().getId() & _slot) == 0)) {
            return false;
        } else if ((_isMagicWeapon != null) && (item.isMagicWeapon() != _isMagicWeapon)) {
            return false;
        }
        return true;
    }
}
