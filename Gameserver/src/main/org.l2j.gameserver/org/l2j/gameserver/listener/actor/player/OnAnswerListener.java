package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;

/**
 * @author VISTALL
 * @date 9:37/15.04.2011
 */
public interface OnAnswerListener extends PlayerListener
{
	void sayYes();

	void sayNo();
}