package org.l2j.gameserver.listener.game;

import org.l2j.gameserver.listener.GameListener;

public interface OnDayNightChangeListener extends GameListener
{
	public void onDay();

	public void onNight();
}