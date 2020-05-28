package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public final class EnchantItemRequest extends AbstractRequest {

    private volatile int enchantingItemObjectId;
    private volatile int enchantingScrollObjectId;
    private volatile int supportItemObjectId;

    public EnchantItemRequest(Player player, int enchantingScrollObjectId) {
        super(player);
        this.enchantingScrollObjectId = enchantingScrollObjectId;
    }

    public Item getEnchantingItem() {
        return player.getInventory().getItemByObjectId(enchantingItemObjectId);
    }

    public void setEnchantingItem(int objectId) {
        enchantingItemObjectId = objectId;
    }

    public Item getEnchantingScroll() {
        return player.getInventory().getItemByObjectId(enchantingScrollObjectId);
    }

    public void setEnchantingScroll(int objectId) {
        enchantingScrollObjectId = objectId;
    }

    public Item getSupportItem() {
        return getPlayer().getInventory().getItemByObjectId(supportItemObjectId);
    }

    public void setSupportItem(int objectId) {
        supportItemObjectId = objectId;
    }

    @Override
    public boolean isItemRequest() {
        return true;
    }

    @Override
    public boolean canWorkWith(AbstractRequest request) {
        return !request.isItemRequest();
    }

    @Override
    public boolean isUsing(int objectId) {
        return (objectId > 0) && ((objectId == enchantingItemObjectId) || (objectId == enchantingScrollObjectId) || (objectId == supportItemObjectId));
    }
}
