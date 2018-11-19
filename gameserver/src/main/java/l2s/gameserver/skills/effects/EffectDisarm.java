package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectDisarm extends Effect
{
	public EffectDisarm(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;
		Player player = getEffected().getPlayer();
		// Нельзя снимать/одевать флаги
		if(player.getActiveWeaponFlagAttachment() != null)
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		Player player = (Player) getEffected();

		ItemInstance wpn = player.getActiveWeaponInstance();
		if(wpn != null)
		{
			player.getInventory().unEquipItem(wpn);
			player.sendDisarmMessage(wpn);
		}
		player.getFlags().getWeaponEquipBlocked().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getWeaponEquipBlocked().stop(this);
	}
}