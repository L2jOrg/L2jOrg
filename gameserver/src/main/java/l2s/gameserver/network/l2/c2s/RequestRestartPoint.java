package l2s.gameserver.network.l2.c2s;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.TeleportPoint;
import l2s.gameserver.model.base.ResidenceFunctionType;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.ResidenceFunction;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.DiePacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.TeleportUtils;

import org.napile.pair.primitive.IntObjectPair;

public class RequestRestartPoint extends L2GameClientPacket
{
	private RestartType _restartType;

	@Override
	protected void readImpl()
	{
		_restartType = ArrayUtils.valid(RestartType.VALUES, readD());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(_restartType == null || activeChar == null)
			return;

		if(activeChar.isFakeDeath())
		{
			activeChar.breakFakeDeath();
			return;
		}

		if(!activeChar.isDead() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		requestRestart(activeChar, _restartType);
	}

	public static void requestRestart(Player activeChar, RestartType restartType)
	{
		switch(restartType)
		{
			case ADVENTURES_SONG:
				if(activeChar.getAbnormalList().contains(22410) || activeChar.getAbnormalList().contains(22411))
				{
					activeChar.getAbnormalList().stop(22410);
					activeChar.getAbnormalList().stop(22411);
					activeChar.doRevive(100);
				}
				else
					activeChar.sendPacket(ActionFailPacket.STATIC, new DiePacket(activeChar));
				break;
			case AGATHION:
				if(activeChar.isAgathionResAvailable())
					activeChar.doRevive(100);
				else
					activeChar.sendPacket(ActionFailPacket.STATIC, new DiePacket(activeChar));
				break;
			case FIXED:
				if(activeChar.getPlayerAccess().ResurectFixed)
					activeChar.doRevive(100);
				else if(checkFeatherOfBlessingAvailable(activeChar))
				{
					if(ItemFunctions.deleteItem(activeChar, 13300, 1, true) || ItemFunctions.deleteItem(activeChar, 10649, 1, true))
					{
						SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(7008, 1);
						if(skillEntry != null)
						{
							activeChar.sendPacket(SystemMsg.YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT);
							activeChar.doRevive(100);
							skillEntry.getEffects(activeChar, activeChar);
						}
					}
					else
						activeChar.sendPacket(ActionFailPacket.STATIC, new DiePacket(activeChar));
				}
				else
				{
					int level = activeChar.getLevel();
					if(level <= 19 && ItemFunctions.deleteItem(activeChar, 8515, 1, true))
					{
						activeChar.doRevive(100);
						return;
					}

					if(level <= 39 && ItemFunctions.deleteItem(activeChar, 8516, 1, true))
					{
						activeChar.doRevive(100);
						return;
					}

					if(level <= 51 && ItemFunctions.deleteItem(activeChar, 8517, 1, true))
					{
						activeChar.doRevive(100);
						return;
					}

					if(level <= 60 && ItemFunctions.deleteItem(activeChar, 8518, 1, true))
					{
						activeChar.doRevive(100);
						return;
					}

					if(level <= 75 && ItemFunctions.deleteItem(activeChar, 8519, 1, true))
					{
						activeChar.doRevive(100);
						return;
					}

					if(level <= 84 && ItemFunctions.deleteItem(activeChar, 8520, 1, true))
					{
						activeChar.doRevive(100);
						return;
					}

					activeChar.sendPacket(ActionFailPacket.STATIC, new DiePacket(activeChar));
				}
				break;
			default:
				TeleportPoint teleportPoint = null;
				Reflection ref = activeChar.getReflection();

				if(ref.isMain())
				{
					for(Event e : activeChar.getEvents())
					{
						Location loc = e.getRestartLoc(activeChar, restartType);
						if(loc != null)
							teleportPoint = new TeleportPoint(loc, ref);
					}
				}

				if(teleportPoint == null)
					teleportPoint = defaultPoint(restartType, activeChar);
				
				if(teleportPoint != null)
				{
					IntObjectPair<OnAnswerListener> ask = activeChar.getAskListener(false);
					if(ask != null && ask.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener) ask.getValue()).isForPet())
						activeChar.getAskListener(true);

					activeChar.setPendingRevive(true);
					activeChar.teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
				}
				else
					activeChar.sendPacket(ActionFailPacket.STATIC, new DiePacket(activeChar));
				break;
		}
	}

	private static boolean checkFeatherOfBlessingAvailable(Player player)
	{
		if(player.getAbnormalList().contains(7008))
			return false;
		if(ItemFunctions.haveItem(player, 13300, 1))
			return true;
		if(ItemFunctions.haveItem(player, 10649, 1))
			return true;
		return false;
	}

	// телепорт к флагу, не обрабатывается, по дефалту
	public static TeleportPoint defaultPoint(RestartType restartType, Player activeChar)
	{
		TeleportPoint teleportPoint = null;
		Clan clan = activeChar.getClan();

		switch(restartType)
		{
			case TO_CLANHALL:
				if(clan != null && clan.getHasHideout() != 0)
				{
					ClanHall clanHall = activeChar.getClanHall();
					teleportPoint = TeleportUtils.getRestartPoint(activeChar, RestartType.TO_CLANHALL);

					ResidenceFunction function = clanHall.getActiveFunction(ResidenceFunctionType.RESTORE_EXP);
					if(function != null)
						activeChar.restoreExp(function.getTemplate().getExpRestore() * 100);
				}
				break;
			case TO_CASTLE:
				if(clan != null && clan.getCastle() != 0)
				{
					Castle castle = activeChar.getCastle();
					teleportPoint = TeleportUtils.getRestartPoint(activeChar, RestartType.TO_CASTLE);

					ResidenceFunction function = castle.getActiveFunction(ResidenceFunctionType.RESTORE_EXP);
					if(function != null)
						activeChar.restoreExp(function.getTemplate().getExpRestore() * 100);
				}
				break;
			case TO_VILLAGE:
			default:
				teleportPoint = TeleportUtils.getRestartPoint(activeChar, RestartType.TO_VILLAGE);
				break;
		}
		return teleportPoint;
	}
}
