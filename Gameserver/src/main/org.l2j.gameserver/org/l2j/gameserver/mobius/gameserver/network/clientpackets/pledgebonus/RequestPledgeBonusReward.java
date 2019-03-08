package org.l2j.gameserver.mobius.gameserver.network.clientpackets.pledgebonus;

import org.l2j.gameserver.mobius.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestPledgeBonusReward extends IClientIncomingPacket {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestPledgeBonusReward.class);
	private int _type;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_type = packet.get();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = client.getActiveChar();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		if ((_type < 0) || (_type > ClanRewardType.values().length))
		{
			return;
		}
		
		final L2Clan clan = player.getClan();
		final ClanRewardType type = ClanRewardType.values()[_type];
		final L2ClanMember member = clan.getClanMember(player.getObjectId());
		if (clan.canClaimBonusReward(player, type))
		{
			final ClanRewardBonus bonus = type.getAvailableBonus(player.getClan());
			if (bonus != null)
			{
				final ItemHolder itemReward = bonus.getItemReward();
				final SkillHolder skillReward = bonus.getSkillReward();
				if (itemReward != null)
				{
					player.addItem("ClanReward", itemReward.getId(), itemReward.getCount(), player, true);
				}
				else if (skillReward != null)
				{
					skillReward.getSkill().activateSkill(player, player);
				}
				member.setRewardClaimed(type);
			}
			else
			{
				LOGGER.warn(player + " Attempting to claim reward but clan(" + clan + ") doesn't have such!");
			}
		}
	}
}
