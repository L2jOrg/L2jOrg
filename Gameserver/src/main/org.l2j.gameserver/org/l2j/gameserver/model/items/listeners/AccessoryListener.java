package org.l2j.gameserver.model.items.listeners;

import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.skills.EffectType;
import org.l2j.gameserver.skills.EffectUseType;

public final class AccessoryListener implements OnEquipListener
{
	private static final AccessoryListener _instance = new AccessoryListener();

	public static AccessoryListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		Player player = (Player) actor;

		if(item.getBodyPart() == ItemTemplate.SLOT_L_BRACELET && item.getTemplate().getAttachedSkills().length > 0)
		{
			int agathionId = player.getAgathionId();
			int transformNpcId = player.getTransformId();
			for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
			{
				Skill skill = skillEntry.getTemplate();
				if(agathionId > 0 && skill.getNpcId() == agathionId)
					player.setAgathion(0);
				if(skill.getNpcId() == transformNpcId && skill.hasEffect(EffectUseType.NORMAL, EffectType.Transformation))
					player.setTransform(null);
			}
		}
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		//
	}
}