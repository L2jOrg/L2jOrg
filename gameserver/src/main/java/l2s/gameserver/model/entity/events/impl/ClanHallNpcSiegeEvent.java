package l2s.gameserver.model.entity.events.impl;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.clanhall.SiegeableClanHall;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * @author VISTALL
 * @date 18:26/03.03.2011
 */
public class ClanHallNpcSiegeEvent extends SiegeEvent<SiegeableClanHall, SiegeClanObject>
{
	public ClanHallNpcSiegeEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void startEvent()
	{
		_oldOwner = getResidence().getOwner();

		broadcastInZone(new SystemMessagePacket(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()));

		super.startEvent();
	}

	@Override
	public void stopEvent(boolean force)
	{
		Clan newOwner = getResidence().getOwner();
		if(newOwner != null)
		{
			if(_oldOwner != newOwner)
			{
				newOwner.broadcastToOnlineMembers(PlaySoundPacket.SIEGE_VICTORY);

				newOwner.incReputation(1700, false, toString());

				if(_oldOwner != null)
					_oldOwner.incReputation(-1700, false, toString());
			}

			broadcastInZone(new SystemMessagePacket(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()));
			broadcastInZone(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));
		}
		else
			broadcastInZone(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));

		super.stopEvent(force);

		_oldOwner = null;
	}

	@Override
	public void processStep(Clan clan)
	{
		if(clan != null)
			getResidence().changeOwner(clan);

		stopEvent(true);
	}

	@Override
	public void loadSiegeClans()
	{
		//
	}

	@Override
	public boolean canResurrect(Creature active, Creature target, boolean force, boolean quiet)
	{
		return true;
	}
}