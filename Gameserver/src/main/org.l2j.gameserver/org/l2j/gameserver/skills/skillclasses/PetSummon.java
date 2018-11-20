package org.l2j.gameserver.skills.skillclasses;

import java.util.List;

import org.l2j.gameserver.data.xml.holder.PetDataHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.templates.pet.PetData;

public class PetSummon extends Skill
{
	public PetSummon(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		Player player = activeChar.getPlayer();
		if(player == null)
			return false;

		if(player.getPetControlItem() == null)
			return false;

		PetData petTemplate = PetDataHolder.getInstance().getTemplateByItemId(player.getPetControlItem().getItemId());
		if(petTemplate == null)
			return false;

		if(player.isInStoreMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
			return false;
		}

		if(player.isInCombat())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
			return false;
		}

		if(player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		if(player.isMounted() || player.getPet() != null)
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}

		if(player.isInBoat())
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
			return false;
		}

		if(player.isInFlyingTransform())
			return false;

		if(player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}
		
		for(GameObject o : World.getAroundObjects(player, 120, 200))
			if(o.isDoor())
			{
				player.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
				return false;
			}

		return true;
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayer())
			return;

		activeChar.getPlayer().summonPet();
	}
}