package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;

public interface OnParticipateInCastleSiegeListener extends PlayerListener
{
    public void onParticipateInCastleSiege(Player player, CastleSiegeEvent siegeEvent);
}