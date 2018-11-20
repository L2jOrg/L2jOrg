package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;

public interface OnPlayerSummonServitorListener extends PlayerListener
{
    public void onSummonServitor(Player player, Servitor servitor);
}