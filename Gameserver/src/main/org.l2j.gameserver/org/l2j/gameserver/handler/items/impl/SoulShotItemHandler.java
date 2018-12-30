package org.l2j.gameserver.handler.items.impl;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.SoulShotType;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.item.ItemGrade;
import org.l2j.gameserver.templates.item.WeaponTemplate;

public class SoulShotItemHandler extends DefaultItemHandler
{
	private static final IntIntMap SHOT_SKILLS = new HashIntIntMap();
	static
	{
		SHOT_SKILLS.put(ItemGrade.NONE.ordinal(), 2039); // None Grade
		SHOT_SKILLS.put(ItemGrade.D.ordinal(), 2150); // D Grade
		SHOT_SKILLS.put(ItemGrade.C.ordinal(), 2151); // C Grade
		SHOT_SKILLS.put(ItemGrade.B.ordinal(), 2152); // B Grade
		SHOT_SKILLS.put(ItemGrade.A.ordinal(), 2153); // A Grade
		SHOT_SKILLS.put(ItemGrade.S.ordinal(), 2154); // S Grade
		SHOT_SKILLS.put(ItemGrade.R.ordinal(), 9193); // R Grade
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		// soulshot is already active
		if(player.getChargedSoulshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.isAutoShot(shotId))
			isAutoSoulShot = true;

		if(player.getActiveWeaponInstance() == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		int ssConsumption = weaponItem.getSoulShotCount();
		if(ssConsumption <= 0)
		{
			// Can't use soulshots
			if(isAutoSoulShot)
			{
				player.removeAutoShot(shotId, true, SoulShotType.SOULSHOT);
				return false;
			}
			player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		int[] reducedSoulshot = weaponItem.getReducedSoulshot();
		if(reducedSoulshot[0] > 0 && Rnd.chance(reducedSoulshot[0]))
			ssConsumption = reducedSoulshot[1];

		if(ssConsumption <= 0)
			return false;

		ItemGrade grade = weaponItem.getGrade().extGrade();
		if(grade != item.getGrade())
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
			return false;
		}

		if(!player.getInventory().destroyItem(item, ssConsumption))
		{
			if(isAutoSoulShot)
			{
				player.removeAutoShot(shotId, true, SoulShotType.SOULSHOT);
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
			return false;
		}

		SkillEntry skillEntry = item.getTemplate().getFirstSkill();
		if(skillEntry == null)
			skillEntry = SkillHolder.getInstance().getSkillEntry(SHOT_SKILLS.get(grade.ordinal()), 1);

		player.forceUseSkill(skillEntry.getTemplate(), player);
		return true;
	}

	@Override
	public boolean isAutoUse()
	{
		return true;
	}
}