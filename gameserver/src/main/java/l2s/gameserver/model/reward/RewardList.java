package l2s.gameserver.model.reward;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @reworked VISTALL
 */
@SuppressWarnings("serial")
public class RewardList extends ArrayList<RewardGroup>
{
	public static final int MAX_CHANCE = 1000000;
	private final RewardType _type;
	private final boolean _autoLoot;

	public RewardList(RewardType rewardType, boolean a)
	{
		super(5);
		_type = rewardType;
		_autoLoot = a;
	}

	public List<RewardItem> roll(Player player)
	{
		return roll(player, 1.0, null);
	}

	public List<RewardItem> roll(Player player, double penaltyMod)
	{
		return roll(player, penaltyMod, null);
	}

	public List<RewardItem> roll(Player player, double penaltyMod, NpcInstance npc)
	{
		List<RewardItem> temp = new ArrayList<RewardItem>();
		for(RewardGroup g : this)
			temp.addAll(g.roll(_type, player, penaltyMod, npc));
		return temp;
	}

	public boolean isAutoLoot()
	{
		return _autoLoot;
	}

	public RewardType getType()
	{
		return _type;
	}
}