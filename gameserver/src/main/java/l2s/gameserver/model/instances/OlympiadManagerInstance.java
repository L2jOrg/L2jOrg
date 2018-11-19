package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.olympiad.CompType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestBypassToServer;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExReceiveOlympiadPacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;

public class OlympiadManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public OlympiadManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		Olympiad.addOlympiadNpc(this);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == -50)
		{
			if(player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal() && player.getLevel() >= Config.OLYMPIAD_MIN_LEVEL)
				showChatWindow(player, "olympiad/olympiad_operator001.htm", false);
			else
				showChatWindow(player, "olympiad/olympiad_operator002.htm", false);
		}
		else if(ask == -51)
		{
			if(!Olympiad.isRegistered(player, false))
			{
				if(!Olympiad.isRegistrationActive())
					showChatWindow(player, "olympiad/olympiad_operator010p.htm", false);
				else if(Olympiad.isClassedBattlesAllowed())
					showChatWindow(player, "olympiad/olympiad_operator010a.htm", false, "<?olympiad_round?>", Olympiad.getCurrentCycle(), "<?olympiad_week?>", Olympiad.getCompWeek(), "<?olympiad_participant?>", Olympiad.getParticipantsCount());
				else
					showChatWindow(player, "olympiad/olympiad_operator010b.htm", false, "<?olympiad_round?>", Olympiad.getCurrentCycle(), "<?olympiad_week?>", Olympiad.getCompWeek(), "<?olympiad_participant?>", Olympiad.getParticipantsCount());
			}
			else
				showChatWindow(player, "olympiad/olympiad_operator010n.htm", false);
		}
		else if(ask == -52)
		{
			switch((int) reply)
			{
				case 0:
				{
					showChatWindow(player, "olympiad/olympiad_operator001.htm", false);
					break;
				}
				case 1:
				{
					showChatWindow(player, "olympiad/olympiad_operator010a.htm", false);
					break;
				}
				case 2:
				{
					showChatWindow(player, "olympiad/olympiad_operator010b.htm", false);
					break;
				}
				case 3:
				{
					int[] waitingCounts = Olympiad.getWaitingList();

					int classedWaitingCount = waitingCounts[0];
					int teamWaitingCount = 0;
					int nonClassedWaitingCount = waitingCounts[0];

					String WaitingCount = classedWaitingCount < 100 ? HtmlUtils.htmlNpcString(1000504, 100) : HtmlUtils.htmlNpcString(1000505, 100);
					String TeamWaitingCount = teamWaitingCount < 100 ? HtmlUtils.htmlNpcString(1000504, 100) : HtmlUtils.htmlNpcString(1000505, 100);
					String ClassFreeWaitingCount = nonClassedWaitingCount < 100 ? HtmlUtils.htmlNpcString(1000504, 100) : HtmlUtils.htmlNpcString(1000505, 100);

					showChatWindow(player, "olympiad/olympiad_operator010f.htm", false, "<?WaitingCount?>", WaitingCount, "<?TeamWaitingCount?>", TeamWaitingCount, "<?ClassFreeWaitingCount?>", ClassFreeWaitingCount);
					break;
				}
				case 4:
				{
					showChatWindow(player, "olympiad/olympiad_operator010g.htm", false);
					break;
				}
				case 5:
				{
					showChatWindow(player, "olympiad/olympiad_operator010h.htm", false, "<?WaitingCount?>", Olympiad.getParticipantPoints(player.getObjectId()));
					break;
				}
				case 6:
				case 7:
				{
					showChatWindow(player, "olympiad/olympiad_operator010m.htm", false);
					break;
				}
			}
		}
		else if(ask == -53)
		{
			if(reply == 0)
				showChatWindow(player, "olympiad/olympiad_operator001.htm", false);
			else if(reply == 1)
			{
				if(player.isBaseClassActive())
				{
					if(player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal() && player.getLevel() >= Config.OLYMPIAD_MIN_LEVEL)
					{
						Olympiad.addParticipant(player);

						if(Olympiad.getParticipantPoints(player.getObjectId()) > 0)
						{
							if(!player.isQuestContinuationPossible(true))
								return;

							if(Olympiad.registerParticipant(player, CompType.NON_CLASSED))
								showChatWindow(player, "olympiad/olympiad_operator010d.htm", false);
						}
						else
							showChatWindow(player, "olympiad/olympiad_operator010i.htm", false);
					}
					else
						showChatWindow(player, "olympiad/olympiad_operator010j.htm", false);
				}
				else
					showChatWindow(player, "olympiad/olympiad_operator010c.htm", false);
			}
		}
		else if(ask == -54)
		{
			if(reply == 0)
				showChatWindow(player, "olympiad/olympiad_operator001.htm", false);
			else if(reply == 1)
			{
				if(player.isBaseClassActive())
				{
					if(player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal() && player.getLevel() >= Config.OLYMPIAD_MIN_LEVEL)
					{
						if(Olympiad.getParticipantPoints(player.getObjectId()) > 0)
						{
							if(!player.isQuestContinuationPossible(true))
								return;

							if(Olympiad.registerParticipant(player, CompType.CLASSED))
								showChatWindow(player, "olympiad/olympiad_operator010e.htm", false);
						}
						else
							showChatWindow(player, "olympiad/olympiad_operator010i.htm", false);
					}
					else
						showChatWindow(player, "olympiad/olympiad_operator010j.htm", false);
				}
				else
					showChatWindow(player, "olympiad/olympiad_operator010c.htm", false);
			}
		}
		else if(ask == -55)
			showChatWindow(player, "olympiad/olympiad_operator030.htm", false);
		else if(ask == -56)
		{
		}
		else if(ask == -57)
		{
		}
		else if(ask == -58)
			Olympiad.unregisterParticipant(player);
		else if(ask == -60)
		{
			if(reply == 0)
			{
				if(player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal() && player.getLevel() >= Config.OLYMPIAD_MIN_LEVEL)
					showChatWindow(player, "olympiad/olympiad_operator001.htm", false);
				else
					showChatWindow(player, "olympiad/olympiad_operator002.htm", false);
			}
		}
		else if(ask == -61)
			showChatWindow(player, "olympiad/olympiad_operator020.htm", false);
		else if(ask == -70)
		{
			if(reply == 0)
				showChatWindow(player, "olympiad/olympiad_operator001.htm", false);
			else if(reply == 1)
			{
				int passes = Olympiad.getParticipantRewardCount(player, false);
				if(passes == 0)
					showChatWindow(player, "olympiad/olympiad_operator031a.htm", false);
				else if(passes < 20)
				{
					if(player.isHero() || Hero.getInstance().isInactiveHero(player.getObjectId()))
						showChatWindow(player, "olympiad/olympiad_operator031b.htm", false);
					else
						showChatWindow(player, "olympiad/olympiad_operator031a.htm", false);
				}
				else
					showChatWindow(player, "olympiad/olympiad_operator031.htm", false);
			}
			else if(reply == 2)
				showChatWindow(player, "olympiad/olympiad_operator010l.htm", false, "<?WaitingCount?>", Olympiad.getParticipantPointsPast(player.getObjectId()));
			else if(reply == 603)
				MultiSellHolder.getInstance().SeparateAndSend((int) reply, player, 0.0);
		}
		else if(ask == -71)
		{
			if(reply == 0)
				showChatWindow(player, "olympiad/olympiad_operator030.htm", false);
			else if(reply == 1)
			{
				if(!player.isQuestContinuationPossible(true))
					return;

				int passes = Olympiad.getParticipantRewardCount(player, true);
				if(passes > 0)
					ItemFunctions.addItem(player, Config.ALT_OLY_COMP_RITEM, passes);
			}
		}
		else if(ask != -80)
		{
			if(ask == -130)
			{
				if(!Config.ENABLE_OLYMPIAD_SPECTATING)
					return;

				Olympiad.addObserver((int) reply, player);
			}
			else
				super.onMenuSelect(player, ask, reply);
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("_olympiad?"))
		{
			if(command.startsWith("_olympiad?command=op_field_list"))
			{
				if(!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd())
				{
					player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
					return;
				}
				player.sendPacket(new ExReceiveOlympiadPacket.MatchList());
			}
			else if(command.startsWith("_olympiad?command=move_op_field"))
			{
				String[] ar = command.split("&");
				if(ar.length < 2)
					return;

				if(!Config.ENABLE_OLYMPIAD_SPECTATING)
					return;

				String[] command2 = ar[1].split("=");
				if(command2.length < 2)
					return;

				Olympiad.addObserver(Integer.parseInt(command2[1]) - 1, player);
			}
			return;
		}

		super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "olympiad/";
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
		{
			String fileName = "olympiad/";
			if(player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal() && player.getLevel() >= Config.OLYMPIAD_MIN_LEVEL)
				fileName += "olympiad_operator001.htm";
			else
				fileName += "olympiad_operator002.htm";

			showChatWindow(player, fileName, firstTalk);
		}
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}

	@Override
	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		return packet == RequestBypassToServer.class && arg.length == 1 && (arg[0].equals("_olympiad?command=op_field_list") || arg[0].equals("_olympiad?command=move_op_field"));
	}
}