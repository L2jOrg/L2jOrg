package l2s.gameserver.model.quest.startcondition;

import l2s.gameserver.model.Player;

/**
 * @author : Ragnarok
 * @date : 07.02.12  3:21
 */
public interface ICheckStartCondition
{
	public boolean checkCondition(Player player);
}