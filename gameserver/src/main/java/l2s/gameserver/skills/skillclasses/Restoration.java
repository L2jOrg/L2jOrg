package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.skill.restoration.RestorationInfo;
import l2s.gameserver.templates.skill.restoration.RestorationItem;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class Restoration extends Skill
{
	private static final Logger _log = LoggerFactory.getLogger(Restoration.class);

	private final RestorationInfo _restoration;

	public Restoration(final StatsSet set)
	{
		super(set);

		_restoration = (RestorationInfo) set.get("restoration");
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(_restoration == null)
		{
			_log.warn(getClass().getSimpleName() + ": Cannot find restoration info for skill[" + getId() + "-" + getLevel() + "]");
			return false;
		}

		if(!activeChar.isPlayable())
			return false;

		if(activeChar.isPlayer())
		{
			Player player = (Player) activeChar;
			if(player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
			{
				player.sendPacket(SystemMsg.THE_CORRESPONDING_WORK_CANNOT_BE_PROCEEDED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
				return false;
			}
		}
		return true;
	}

	@Override
	public void onEndCast(final Creature activeChar, final List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayable())
			return;

		final Playable playable = (Playable) activeChar;
		final int itemConsumeId = _restoration.getItemConsumeId();
		final int itemConsumeCount = _restoration.getItemConsumeCount();
		if(itemConsumeId > 0 && itemConsumeCount > 0)
		{
			if(ItemFunctions.getItemCount(playable, itemConsumeId) < itemConsumeCount)
			{
				playable.sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
				return;
			}

			ItemFunctions.deleteItem(playable, itemConsumeId, itemConsumeCount, true);
		}

		final List<RestorationItem> restorationItems = _restoration.getRandomGroupItems();
		if(restorationItems == null || restorationItems.size() == 0)
		{
			SystemMsg msg = _restoration.getOnFailMessage();
			if (msg != null)
				playable.sendPacket(msg);
			return;
		}

		for(Creature target : targets)
		{
			if(target != null)
			{
				for(RestorationItem item : restorationItems)
					ItemFunctions.addItem(playable, item.getId(), item.getRandomCount(), item.getEnchantLevel(), true);
			}
		}
	}
}