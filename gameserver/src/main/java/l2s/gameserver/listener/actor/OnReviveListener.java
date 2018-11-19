package l2s.gameserver.listener.actor;

import l2s.gameserver.model.Creature;
import l2s.gameserver.listener.CharListener;

public interface OnReviveListener extends CharListener
{
    public void onRevive(Creature actor);
}