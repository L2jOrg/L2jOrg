package org.l2j.gameserver.model.entity.residence;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.entity.residence.clanhall.InstantClanHall;
import org.l2j.gameserver.dao.ClanHallDAO;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.templates.StatsSet;

public abstract class ClanHall extends Residence
{
	private static final long serialVersionUID = 1L;

	public ClanHall(StatsSet set)
	{
		super(set);
	}

	@Override
	public void init()
	{
		initEvent();

		loadData();
		loadFunctions();
		rewardSkills();
	}

	public int getInstantId()
	{
		return 0;
	}

	@Override
	public void changeOwner(Clan clan)
	{
		Clan oldOwner = getOwner();

		if(oldOwner != null && (clan == null || clan.getClanId() != oldOwner.getClanId()))
		{
			removeSkills();
			oldOwner.setHasHideout(0);

			cancelCycleTask();
		}

		setOwner(clan);

		removeFunctions();

		if(clan != null)
		{
			InstantClanHall instantClanHall = ResidenceHolder.getInstance().getResidence(InstantClanHall.class, clan.getHasHideout());
			if(instantClanHall != null)
			{
				instantClanHall.removeOwner(clan, true);
				clan.broadcastToOnlineMembers(SystemMsg.YOU_HAVE_ACQUIRED_A_CLAN_HALL_OF_HIGHER_VALUE_THAN_THE_PROVISIONAL_CLAN_HALL_THE_PROVISIONAL_CLAN_HALL_OWNERSHIP_WILL_AUTOMATICALLY_BE_FORFEITED);
			}
			clan.setHasHideout(getId());
			clan.broadcastClanStatus(true, false, false);
		}

		rewardSkills();

		update();
	}

	@Override
	public ResidenceType getType()
	{
		return ResidenceType.CLANHALL;
	}

	@Override
	protected void loadData()
	{
		ClanHallDAO.getInstance().select(this);
	}

	public int getGrade()
	{
		return 0;
	}

	@Override
	public void update()
	{
		ClanHallDAO.getInstance().update(this);
	}

	public int getAuctionLength()
	{
		return 0;
	}

	public void setAuctionLength(int auctionLength)
	{}

	public String getAuctionDescription()
	{
		return "";
	}

	public void setAuctionDescription(String auctionDescription)
	{}

	public long getAuctionMinBid()
	{
		return 0L;
	}

	public void setAuctionMinBid(long auctionMinBid)
	{}

	public int getFeeItemId()
	{
		return 0;
	}

	public long getRentalFee()
	{
		return 0L;
	}

	public long getBaseMinBid()
	{
		return 0L;
	}

	public long getDeposit()
	{
		return 0L;
	}

	@Override
	public void chanceCycle()
	{
		super.chanceCycle();

		setPaidCycle(getPaidCycle() + 1);
	}

	public int getInstantZoneId()
	{
		return -1;
	}

	public abstract ClanHallType getClanHallType();
}