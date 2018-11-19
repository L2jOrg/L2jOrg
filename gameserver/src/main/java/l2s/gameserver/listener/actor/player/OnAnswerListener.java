package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;

/**
 * @author VISTALL
 * @date 9:37/15.04.2011
 */
public interface OnAnswerListener extends PlayerListener
{
	void sayYes();

	void sayNo();
}