package l2s.gameserver.stats;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;

/**
 * An Env object is just a class to pass parameters to a calculator such as L2Player,
 * L2ItemInstance, Initial value.
 */
public final class Env
{
	public Creature character;
	public Creature target;
	public ItemInstance item;
	public Skill skill;
	public double value;
	public boolean reflected;

	public Env()
	{}

	public Env(Creature cha, Creature tar, Skill sk)
	{
		character = cha;
		target = tar;
		skill = sk;
	}
}