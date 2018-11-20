package org.l2j.gameserver.listener.game;

import org.l2j.gameserver.listener.GameListener;

public interface OnShutdownListener extends GameListener
{
	public void onShutdown();
}