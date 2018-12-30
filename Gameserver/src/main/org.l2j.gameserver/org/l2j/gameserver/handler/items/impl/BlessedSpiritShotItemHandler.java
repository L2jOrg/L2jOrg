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

public class BlessedSpiritShotItemHandler extends DefaultItemHandler
{
    private static final IntIntMap SHOT_SKILLS = new HashIntIntMap();
	static
	{
		SHOT_SKILLS.put(ItemGrade.NONE.ordinal(), 2061); // None Grade
		SHOT_SKILLS.put(ItemGrade.D.ordinal(), 2160); // D Grade
		SHOT_SKILLS.put(ItemGrade.C.ordinal(), 2161); // C Grade
		SHOT_SKILLS.put(ItemGrade.B.ordinal(), 2162); // B Grade
		SHOT_SKILLS.put(ItemGrade.A.ordinal(), 2163); // A Grade
		SHOT_SKILLS.put(ItemGrade.S.ordinal(), 2164); // S Grade
		SHOT_SKILLS.put(ItemGrade.R.ordinal(), 9195); // R Grade
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		// spiritshot is already active
		if(player.getChargedSpiritshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.isAutoShot(shotId))
			isAutoSoulShot = true;

		if(player.getActiveWeaponInstance() == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		int bspsConsumption = weaponItem.getSpiritShotCount();
		if(bspsConsumption <= 0)
		{
			// Can't use Spiritshots
			if(isAutoSoulShot)
			{
                player.removeAutoShot(shotId, true, SoulShotType.SPIRITSHOT);
                return false;
			}
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		int[] reducedSpiritshot = weaponItem.getReducedSpiritshot();
		if(reducedSpiritshot[0] > 0 && Rnd.chance(reducedSpiritshot[0]))
			bspsConsumption = reducedSpiritshot[1];

		if(bspsConsumption <= 0)
			return false;

        ItemGrade grade = weaponItem.getGrade().extGrade();
		if(grade != item.getGrade())
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE);
			return false;
		}

		if(!player.getInventory().destroyItem(item, bspsConsumption))
		{
			if(isAutoSoulShot)
			{
                player.removeAutoShot(shotId, true, SoulShotType.SPIRITSHOT);
                return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
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