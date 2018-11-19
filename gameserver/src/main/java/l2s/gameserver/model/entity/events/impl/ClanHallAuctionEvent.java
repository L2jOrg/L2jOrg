package l2s.gameserver.model.entity.events.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.instancemanager.PlayerMessageStack;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.actions.StartStopAction;
import l2s.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.clanhall.AuctionClanHall;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.ClanTable;

import org.apache.commons.lang3.StringUtils;

/**
 * @author VISTALL
 * @date 15:24/14.02.2011
 */
public class ClanHallAuctionEvent extends SiegeEvent<AuctionClanHall, AuctionSiegeClanObject>
{
	public ClanHallAuctionEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void reCalcNextTime(boolean onStart)
	{
		clearActions();
		_onTimeActions.clear();

		Clan owner = getResidence().getOwner();

		// первый старт
		if(getResidence().getAuctionLength() == 0 && owner == null)
		{
			final Calendar siegeDate = getResidence().getSiegeDate();
			siegeDate.setTimeInMillis(System.currentTimeMillis());
			siegeDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			siegeDate.set(Calendar.HOUR_OF_DAY, 15);
			siegeDate.add(Calendar.DAY_OF_MONTH, 7);

			validateSiegeDate(siegeDate, 7);

			getResidence().setAuctionLength(7);
			getResidence().setAuctionMinBid(getResidence().getBaseMinBid());
			getResidence().setJdbcState(JdbcEntityState.UPDATED);
			getResidence().update();

			_onTimeActions.clear();
			addOnTimeAction(0, new StartStopAction(EVENT, true));
			addOnTimeAction(getResidence().getAuctionLength() * 86400, new StartStopAction(EVENT, false));

			registerActions();
		}
		else if(getResidence().getAuctionLength() == 0 && owner != null)
		{
			// КХ куплен
		}
		else
		{
			final Calendar siegeDate = getResidence().getSiegeDate();
			if(!onStart && siegeDate.getTimeInMillis() < System.currentTimeMillis())
			{
				siegeDate.setTimeInMillis(System.currentTimeMillis());
				siegeDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				siegeDate.set(Calendar.HOUR_OF_DAY, 15);
				siegeDate.add(Calendar.DAY_OF_MONTH, getResidence().getAuctionLength());

				validateSiegeDate(siegeDate, getResidence().getAuctionLength());
			}

			_onTimeActions.clear();
			addOnTimeAction(0, new StartStopAction(EVENT, true));
			addOnTimeAction(getResidence().getAuctionLength() * 86400, new StartStopAction(EVENT, false));

			registerActions();
		}
	}

	private void generateSiegeDate()
	{
		final Calendar siegeDate = getResidence().getSiegeDate();
		final int hourOfDay = siegeDate.get(Calendar.HOUR_OF_DAY);
		siegeDate.setTimeInMillis(System.currentTimeMillis());
		siegeDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		siegeDate.add(Calendar.DAY_OF_MONTH, getResidence().getAuctionLength());

		validateSiegeDate(siegeDate, getResidence().getAuctionLength());

		getResidence().setJdbcState(JdbcEntityState.UPDATED);
		getResidence().update();
	}

	@Override
	public void stopEvent(boolean force)
	{
		List<AuctionSiegeClanObject> siegeClanObjects = removeObjects(ATTACKERS);
		// сортуруем с Макс к мин
		AuctionSiegeClanObject[] clans = siegeClanObjects.toArray(new AuctionSiegeClanObject[siegeClanObjects.size()]);
		Arrays.sort(clans, SiegeClanObject.SiegeClanComparatorImpl.getInstance());

		Clan oldOwner = getResidence().getOwner();
		AuctionSiegeClanObject winnerSiegeClan = clans.length > 0 ? clans[0] : null;

		// если есть победитель(тоисть больше 1 клана)
		if(winnerSiegeClan != null)
		{
			// розсылаем мессагу, возращаем всем деньги
			SystemMessagePacket msg = new SystemMessagePacket(SystemMsg.THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN).addString(winnerSiegeClan.getClan().getName());
			for(AuctionSiegeClanObject siegeClan : siegeClanObjects)
			{
				Player player = siegeClan.getClan().getLeader().getPlayer();
				if(player != null)
					player.sendPacket(msg);
				else
					PlayerMessageStack.getInstance().mailto(siegeClan.getClan().getLeaderId(), msg);

				if(siegeClan != winnerSiegeClan)
				{
					long returnBid = siegeClan.getParam() - (long) (siegeClan.getParam() * 0.1);

					siegeClan.getClan().getWarehouse().addItem(getResidence().getFeeItemId(), returnBid);
				}
			}

			SiegeClanDAO.getInstance().delete(getResidence());

			// если был овнер, возращаем депозит
			if(oldOwner != null)
				oldOwner.getWarehouse().addItem(getResidence().getFeeItemId(), getResidence().getDeposit() + winnerSiegeClan.getParam());

			getResidence().setAuctionLength(0);
			getResidence().setAuctionMinBid(0);
			getResidence().setAuctionDescription(StringUtils.EMPTY);
			getResidence().getSiegeDate().setTimeInMillis(0);
			getResidence().getLastSiegeDate().setTimeInMillis(0);
			getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());
			getResidence().setJdbcState(JdbcEntityState.UPDATED);

			getResidence().changeOwner(winnerSiegeClan.getClan());
			getResidence().startCycleTask();
		}
		else
		{
			if(oldOwner != null)
			{
				Player player = oldOwner.getLeader().getPlayer();
				if(player != null)
					player.sendPacket(SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED);
				else
					PlayerMessageStack.getInstance().mailto(oldOwner.getLeaderId(), SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED);
			}
			// Если КХ после окончания не обрел хозяина, то выставляем следующую дату аукциона.
			generateSiegeDate();
		}

		super.stopEvent(force);
	}

	@Override
	public void findEvent(Player player)
	{
		//
	}

	@Override
	public AuctionSiegeClanObject newSiegeClan(String type, int clanId, long param, long date)
	{
		Clan clan = ClanTable.getInstance().getClan(clanId);
		return clan == null ? null : new AuctionSiegeClanObject(type, clan, param, date);
	}

	@Override
	protected long startTimeMillis()
	{
		return (getResidence().getSiegeDate().getTimeInMillis() == 0 || getResidence().getAuctionLength() == 0) ? 0 : (getResidence().getSiegeDate().getTimeInMillis() - (getResidence().getAuctionLength() * SiegeEvent.DAY_IN_MILISECONDS));
	}
}