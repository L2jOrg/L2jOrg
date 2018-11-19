package events;

import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.utils.SiegeUtils;

/**
 * @author Bonux
**/
public class CastleNewbieSiegeEvent extends CastleSiegeEvent
{
	private final AtomicBoolean _lordIsDead = new AtomicBoolean();

	public CastleNewbieSiegeEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public void startEvent()
	{
		Clan oldOwnerClan = getResidence().getOwner();

		getResidence().changeOwner(null);

		// если есть овнер в резиденции, делаем его аттакером
		if(oldOwnerClan != null)
		{
			SiegeClanObject siegeClan = new SiegeClanObject(ATTACKERS, oldOwnerClan, 0, startTimeMillis());
			addObject(ATTACKERS, siegeClan);

			SiegeClanDAO.getInstance().insert(getResidence(), siegeClan);
		}

		super.startEvent();
	}

	@Override
	public boolean canRegisterOnSiege(Player player, Clan clan, boolean attacker)
	{
		if(!attacker)
			return false;
		return super.canRegisterOnSiege(player, clan, attacker);
	}

	@Override
	public IBroadcastPacket checkSiegeClanLevel(Clan clan)
	{
		if(clan.getLevel() < SiegeUtils.MIN_CLAN_SIEGE_LEVEL || clan.getLevel() > 4)
			return SystemMsg.ONLY_LEVEL_34_CLANS_CAN_BE_REGISTERED_IN_A_CASTLE_SIEGE;
		return null;
	}

	@Override
	public boolean canCastSeal(Player player)
	{
		return _lordIsDead.get();
	}

	@Override
	public void onLordDie(NpcInstance npc)
	{
		super.onLordDie(npc);

		if(!_lordIsDead.compareAndSet(false, true))
			return;

		NpcString npcString;
		switch(getResidence().getId())
		{
			case 1: // Gludio
				npcString = NpcString.THE_LORD_OF_GLUDIO_IS_DEAD_THE_SEAL_IS_AVAILABLE_NOW;
				break;
			default:
				npcString = null;
				break;
		}

		broadcastTo(new ExShowScreenMessage(npcString, 10000, ScreenMessageAlign.TOP_CENTER, true, true), ATTACKERS, DEFENDERS);
	}
}
