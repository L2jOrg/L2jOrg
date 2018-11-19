package l2s.gameserver.model.items.listeners;

import l2s.gameserver.data.xml.holder.OptionDataHolder;
import l2s.gameserver.listener.inventory.OnEquipListener;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.SkillCoolTimePacket;
import l2s.gameserver.templates.OptionDataTemplate;

public final class ItemAugmentationListener implements OnEquipListener
{
	private static final ItemAugmentationListener _instance = new ItemAugmentationListener();

	public static ItemAugmentationListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;
		if(!item.isAugmented())
			return;

		Player player = actor.getPlayer();

		boolean updateStats = false;
		boolean sendSkillList = false;

		int[] stats = { item.getVariation1Id(), item.getVariation2Id() };
		for(int i : stats)
		{
			OptionDataTemplate template = player.removeOptionData(i);
			if(template == null)
				continue;

			updateStats = true;

			if(!template.getSkills().isEmpty())
				sendSkillList = true;
		}

		if(updateStats)
		{
			if(sendSkillList)
				player.sendSkillList();

			player.updateStats();
		}
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;
		if(!item.isAugmented())
			return;

		Player player = actor.getPlayer();

		// При несоотвествии грейда аугмент не применяется
		if(player.getExpertisePenalty(item) > 0)
			return;

		boolean updateStats = false;
		boolean sendSkillList = false;

		int[] stats = { item.getVariation1Id(), item.getVariation2Id() };
		for(int i : stats)
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if(template == null)
				continue;

			if(player.addOptionData(template) == template)
				continue;

			updateStats = true;

			if(!template.getSkills().isEmpty())
				sendSkillList = true;
		}

		if(updateStats)
		{
			if(sendSkillList)
				player.sendSkillList();

			player.sendChanges();
		}
	}
}