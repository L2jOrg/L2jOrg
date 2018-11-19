package l2s.gameserver.handler.items.impl;

import l2s.gameserver.data.xml.holder.FishDataHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.SoulShotType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.fish.RodTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;

/**
 * @author Bonux
 **/
public class FishShotItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		// spiritshot is already active
		if(player.getChargedFishshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.isAutoShot(shotId))
			isAutoSoulShot = true;

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();
		if(player.getActiveWeaponInstance() == null || weaponItem.getItemType() != WeaponTemplate.WeaponType.ROD)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		RodTemplate rod = FishDataHolder.getInstance().getRod(weaponItem.getItemId());
		if(rod == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		if(player.getInventory().destroyItem(item, rod.getShotConsumeCount()))
		{
			SkillEntry skillEntry = item.getTemplate().getFirstSkill();
			if(skillEntry == null)
			{
				if(isAutoSoulShot)
				{
					player.removeAutoShot(shotId, true, SoulShotType.SOULSHOT);
					return false;
				}
			}

			player.forceUseSkill(skillEntry.getTemplate(), player);
		}
		else
		{
			if(isAutoSoulShot)
			{
				player.removeAutoShot(shotId, true, SoulShotType.SOULSHOT);
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}
		return true;
	}

	@Override
	public boolean isAutoUse()
	{
		return true;
	}
}