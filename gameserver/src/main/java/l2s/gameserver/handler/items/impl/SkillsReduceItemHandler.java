package l2s.gameserver.handler.items.impl;

/**
 * @author Bonux
 */
public class SkillsReduceItemHandler extends SkillsItemHandler
{
	@Override
	public boolean reduceAfterUse()
	{
		return true;
	}
}