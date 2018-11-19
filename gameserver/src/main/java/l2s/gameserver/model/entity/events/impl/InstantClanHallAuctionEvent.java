package l2s.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.dao.JdbcEntityState;
import l2s.commons.util.Rnd;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.actions.StartStopAction;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.clanhall.InstantClanHall;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.ItemTemplate;

public class InstantClanHallAuctionEvent extends SiegeEvent<InstantClanHall, SiegeClanObject>
{
	private Calendar _endAuctionDate = Calendar.getInstance();

	public InstantClanHallAuctionEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public int getId()
	{
		return Residence.getInstantResidenceId(super.getId());
	}

	@Override
	public void reCalcNextTime(boolean onStart)
	{
		clearActions();
		_onTimeActions.clear();
		setEndAuctionDate();

		Calendar siegeDate = getResidence().getSiegeDate();

		if(_endAuctionDate.getTimeInMillis() <= System.currentTimeMillis())
		{
			if(onStart)
				checkWinners();

			siegeDate.setTimeInMillis(getResidence().getFirstLotteryDate().getTimeInMillis());
			siegeDate.set(Calendar.DAY_OF_WEEK, 7);
			siegeDate.set(Calendar.HOUR_OF_DAY, 0);
			siegeDate.set(Calendar.MINUTE, 1);
			siegeDate.set(Calendar.SECOND, 0);
			siegeDate.set(Calendar.MILLISECOND, 0);

			while(siegeDate.getTimeInMillis() <= System.currentTimeMillis())
				siegeDate.add(Calendar.DAY_OF_MONTH, getResidence().getRentalPeriod());

			siegeDate.set(Calendar.DAY_OF_WEEK, 7);

			while(siegeDate.getTimeInMillis() <= System.currentTimeMillis())
				siegeDate.add(Calendar.DAY_OF_MONTH, 7);

			getResidence().setJdbcState(JdbcEntityState.UPDATED);
			getResidence().update();

			setEndAuctionDate();
		}

		addOnTimeAction(0, new StartStopAction(EVENT, true));
		addOnTimeAction((int) ((_endAuctionDate.getTimeInMillis() - siegeDate.getTimeInMillis()) / 1000L), new StartStopAction(EVENT, false));

		registerActions();
	}

	private void setEndAuctionDate()
	{
		_endAuctionDate.setTimeInMillis(getSiegeDate().getTimeInMillis() + getResidence().getApplyPeriod() * 60 * 60 * 1000L);
		_endAuctionDate.set(Calendar.MINUTE, 55);
		_endAuctionDate.add(Calendar.HOUR_OF_DAY, -1);
		_endAuctionDate.set(Calendar.SECOND, 0);
		_endAuctionDate.set(Calendar.MILLISECOND, 0);
	}

	@Override
	public void startEvent()
	{
		for(Clan clan : getResidence().getOwners())
		{
			getResidence().removeOwner(clan, true);
			clan.setHasHideout(0);
			clan.broadcastClanStatus(true, false, false);
		}

		super.startEvent();
	}

	@Override
	public void stopEvent(boolean force)
	{
		checkWinners();
		reCalcNextTime(false);
		super.stopEvent(force);
	}

	@SuppressWarnings("rowtypes")
	private void checkWinners()
	{
		List<SiegeClanObject> siegeClanObjects = removeObjects(ATTACKERS);
		SiegeClanObject winnerSiegeClan;
		if(!siegeClanObjects.isEmpty())
		{
			Iterator<SiegeClanObject> itr = siegeClanObjects.iterator();
			while(itr.hasNext())
			{
				SiegeClanObject siegeClan = itr.next();
				if(siegeClan.getClan().getHasHideout() != 0)
					itr.remove();
			}
			List<SiegeClanObject> winnersSiegeClans = new ArrayList<SiegeClanObject>();

			if(siegeClanObjects.size() <= getResidence().getMaxCount())
				winnersSiegeClans.addAll(siegeClanObjects);
			else
			{
				while(winnersSiegeClans.size() < getResidence().getMaxCount())
				{
					winnerSiegeClan = Rnd.get(siegeClanObjects);
					winnersSiegeClans.add(winnerSiegeClan);
					siegeClanObjects.remove(winnerSiegeClan);
				}

				for(SiegeClanObject siegeClan : siegeClanObjects)
				{
					siegeClan.getClan().broadcastToOnlineMembers(SystemMsg.YOUR_BID_FOR_THE_PROVISIONAL_CLAN_HALL_LOST);
					siegeClan.getClan().getWarehouse().addItem(ItemTemplate.ITEM_ID_ADENA, getResidence().getRentalFee() * (100 - getResidence().getCommissionPercent()) / 100L);
				}
			}

			if(!winnersSiegeClans.isEmpty())
			{
				for(SiegeClanObject siegeClan : winnersSiegeClans)
				{
					Clan clan = siegeClan.getClan();
					getResidence().addOwner(clan, true);
					clan.setHasHideout(getResidence().getId());
					clan.broadcastClanStatus(true, false, false);
					clan.broadcastToOnlineMembers(SystemMsg.YOUR_BID_FOR_THE_PROVISIONAL_CLAN_HALL_WON);
				}
			}
		}

		SiegeClanDAO.getInstance().delete(getResidence());
	}

	@Override
	public void findEvent(Player player)
	{
		//
	}

	@Override
	public Calendar getSiegeDate()
	{
		return getResidence().getSiegeDate();
	}

	public Calendar getEndAuctionDate()
	{
		return _endAuctionDate;
	}

	public int getParticipantsCount()
	{
		return getObjects(ATTACKERS).size();
	}
}