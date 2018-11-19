package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.CastleSiegeDefenderListPacket;

/**
 * @reworked VISTALL
 */
public class RequestConfirmCastleSiegeWaitingList extends L2GameClientPacket
{
	private boolean _approved;
	private int _unitId;
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_unitId = readD();
		_clanId = readD();
		_approved = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _unitId);
		if(castle == null)
		{
			player.sendActionFailed();
			return;
		}

		if(!player.isGM() && (player.getClan() == null || player.getClan().getCastle() != castle.getId() || !player.isClanLeader()))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST);
			return;
		}

		CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

		SiegeClanObject siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, _clanId);
		if(siegeClan == null)
			siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS, _clanId);

		if(siegeClan == null)
			return;

		if(siegeEvent.isRegistrationOver())
		{
			player.sendPacket(SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED);
			return;
		}

		int allSize = siegeEvent.getObjects(CastleSiegeEvent.DEFENDERS).size();
		if(allSize >= CastleSiegeEvent.MAX_SIEGE_CLANS)
		{
			player.sendPacket(SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
			return;
		}

		siegeEvent.removeObject(siegeClan.getType(), siegeClan);

		if(_approved)
			siegeClan.setType(CastleSiegeEvent.DEFENDERS);
		else
			siegeClan.setType(CastleSiegeEvent.DEFENDERS_REFUSED);

		siegeEvent.addObject(siegeClan.getType(), siegeClan);

		SiegeClanDAO.getInstance().update(castle, siegeClan);

		player.sendPacket(new CastleSiegeDefenderListPacket(castle));
	}
}