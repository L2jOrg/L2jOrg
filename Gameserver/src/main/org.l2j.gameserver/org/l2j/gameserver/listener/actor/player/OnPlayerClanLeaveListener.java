package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;

public interface OnPlayerClanLeaveListener extends PlayerListener
{
    public void onClanLeave(Player player);
}