package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.listener.CharListener;

public interface OnReviveListener extends CharListener
{
    public void onRevive(Creature actor);
}