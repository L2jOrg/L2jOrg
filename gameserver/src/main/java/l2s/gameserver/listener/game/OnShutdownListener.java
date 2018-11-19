package l2s.gameserver.listener.game;

import l2s.gameserver.listener.GameListener;

public interface OnShutdownListener extends GameListener
{
	public void onShutdown();
}