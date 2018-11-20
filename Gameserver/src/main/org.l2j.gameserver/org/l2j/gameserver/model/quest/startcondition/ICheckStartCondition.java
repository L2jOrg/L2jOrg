package org.l2j.gameserver.model.quest.startcondition;

import org.l2j.gameserver.model.Player;

/**
 * @author : Ragnarok
 * @date : 07.02.12  3:21
 */
public interface ICheckStartCondition
{
	public boolean checkCondition(Player player);
}