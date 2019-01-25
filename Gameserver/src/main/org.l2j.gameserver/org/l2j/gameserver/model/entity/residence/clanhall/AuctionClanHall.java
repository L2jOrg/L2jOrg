package org.l2j.gameserver.model.entity.residence.clanhall;

import org.l2j.gameserver.Contants;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.instancemanager.PlayerMessageStack;
import org.l2j.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import org.l2j.gameserver.model.entity.residence.ClanHallType;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.UnitMember;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.templates.item.ItemTemplate;

public class AuctionClanHall extends NormalClanHall
{
	private static final long serialVersionUID = 1L;
	private static final int REWARD_CYCLE = 168;
	private int _auctionLength;
	private long _auctionMinBid;
	private String _auctionDescription = "";

	private final int _grade;
	private final int _feeItemId;
	private final long _rentalFee;
	private final long _minBid;
	private final long _deposit;

	public AuctionClanHall(StatsSet set)
	{
		super(set);
		_grade = set.getInteger("grade");
		_feeItemId = set.getInteger("fee_item_id");
		_rentalFee = set.getInteger("rental_fee");
		_minBid = set.getInteger("min_bid");
		_deposit = set.getInteger("deposit");
	}

	@Override
	public void init()
	{
		super.init();

		if(getSiegeEvent() != null && getSiegeEvent().getClass() == ClanHallAuctionEvent.class && _owner != null && getAuctionLength() == 0)
			startCycleTask();
	}

	public int getGrade()
	{
		return _grade;
	}

	@Override
	public void changeOwner(Clan clan)
	{
		super.changeOwner(clan);

		if(clan == null && getSiegeEvent().getClass() == ClanHallAuctionEvent.class)
			getSiegeEvent().reCalcNextTime(false);
	}

	public int getAuctionLength()
	{
		return _auctionLength;
	}

	public void setAuctionLength(int auctionLength)
	{
		_auctionLength = auctionLength;
	}

	public String getAuctionDescription()
	{
		return _auctionDescription;
	}

	public void setAuctionDescription(String auctionDescription)
	{
		_auctionDescription = auctionDescription == null ? "" : auctionDescription;
	}

	public long getAuctionMinBid()
	{
		return _auctionMinBid;
	}

	public void setAuctionMinBid(long auctionMinBid)
	{
		_auctionMinBid = auctionMinBid;
	}

	public int getFeeItemId()
	{
		return _feeItemId;
	}

	public long getRentalFee()
	{
		return _rentalFee;
	}

	public long getBaseMinBid()
	{
		return _minBid;
	}

	public long getDeposit()
	{
		return _deposit;
	}

	@Override
	public void chanceCycle()
	{
		super.chanceCycle();

		if(getPaidCycle() >= REWARD_CYCLE)
		{
			if(_owner.getWarehouse().getCountOf(Items.ADENA) > _rentalFee)
			{
				_owner.getWarehouse().destroyItemByItemId(Items.ADENA, _rentalFee);
				setPaidCycle(0);
			}
			else
			{
				UnitMember member = _owner.getLeader();

				if(member.isOnline())
					member.getPlayer().sendPacket(SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
				else
					PlayerMessageStack.getInstance().mailto(member.getObjectId(), SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED.packet(null));
				changeOwner(null);
			}
		}
	}

	@Override
	public int getVisibleFunctionLevel(int level)
	{
		return level;
	}

	@Override
	public ClanHallType getClanHallType()
	{
		return ClanHallType.AUCTIONABLE;
	}
}