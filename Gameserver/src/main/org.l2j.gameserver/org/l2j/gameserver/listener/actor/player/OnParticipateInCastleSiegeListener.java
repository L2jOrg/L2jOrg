package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;

public interface OnParticipateInCastleSiegeListener extends PlayerListener
{
    public void onParticipateInCastleSiege(Player player, CastleSiegeEvent siegeEvent);
}