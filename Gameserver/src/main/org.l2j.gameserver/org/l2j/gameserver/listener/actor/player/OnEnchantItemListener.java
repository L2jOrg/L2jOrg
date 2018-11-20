package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

public interface OnEnchantItemListener extends PlayerListener
{
    public void onEnchantItem(Player player, ItemInstance item, boolean success);
}