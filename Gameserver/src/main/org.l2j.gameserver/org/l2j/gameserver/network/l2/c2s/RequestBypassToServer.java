package org.l2j.gameserver.network.l2.c2s;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.EventHolder;
import org.l2j.gameserver.data.xml.holder.MultiSellHolder;
import org.l2j.gameserver.handler.admincommands.AdminCommandHandler;
import org.l2j.gameserver.handler.bbs.BbsHandlerHolder;
import org.l2j.gameserver.handler.bbs.IBbsHandler;
import org.l2j.gameserver.handler.bypass.BypassHolder;
import org.l2j.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.l2j.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.l2j.gameserver.instancemanager.OfflineBufferManager;
import org.l2j.gameserver.instancemanager.OlympiadHistoryManager;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.entity.events.EventType;
import org.l2j.gameserver.model.entity.events.impl.PvPEvent;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.utils.BypassStorage.BypassType;
import org.l2j.gameserver.utils.BypassStorage.ValidBypass;
import org.l2j.gameserver.utils.MulticlassUtils;
import org.l2j.gameserver.utils.NpcUtils;
import org.l2j.gameserver.utils.WarehouseFunctions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);

	private String _bypass = null;

	@Override
	protected void readImpl()
	{
		_bypass = readString();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _bypass.isEmpty())
			return;

		ValidBypass bp = activeChar.getBypassStorage().validate(_bypass);
		if(bp == null)
		{
			_log.debug("RequestBypassToServer: Unexpected bypass : " + _bypass + " client : " + getClient() + "!");
			return;
		}

		NpcInstance npc = activeChar.getLastNpc();

		try
		{
			if(_bypass.startsWith("admin_"))
				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, _bypass);
			else if(_bypass.startsWith("pcbang?"))
			{
				String command = _bypass.substring(7).trim();
				StringTokenizer st = new StringTokenizer(command, "_");

				String cmd = st.nextToken();
				if(cmd.equalsIgnoreCase("multisell"))
				{
					int multisellId = Integer.parseInt(st.nextToken());
					if(!Config.ALT_ALLOWED_MULTISELLS_IN_PCBANG.contains(multisellId))
					{
						_log.warn("Unknown multisell list use in PC-Bang shop! List ID: " + multisellId + ", player ID: " + activeChar.getObjectId() + ", player name: " + activeChar.getName());
						return;
					}
					MultiSellHolder.getInstance().SeparateAndSend(multisellId, activeChar, 0.);
				}
			}
			else if(_bypass.startsWith("scripts_"))
			{
				_log.error("Trying to call script bypass: " + _bypass + " " + activeChar);
			}
			else if(_bypass.startsWith("htmbypass_"))
			{
				String command = _bypass.substring(10).trim();
				String word = command.split("\\s+")[0];
				String args = command.substring(word.length()).trim();

				Pair<Object, Method> b = BypassHolder.getInstance().getBypass(word);
				if(b != null)
				{
					try
					{
						b.getValue().invoke(b.getKey(), activeChar, npc, StringUtils.isEmpty(args) ? new String[0] : args.split("\\s+"));
					}
					catch (Exception e)
					{
						_log.error("Exception: " + e, e);
					}
				}
				else
					_log.warn("Cannot find html bypass: " + command);
			}
			else if(_bypass.startsWith("user_"))
			{
				String command = _bypass.substring(5).trim();
				String word = command.split("\\s+")[0];
				String args = command.substring(word.length()).trim();
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(word);

				if(vch != null)
					vch.useVoicedCommand(word, activeChar, args);
				else
					_log.warn("Unknow voiced command '" + word + "'");
			}
			else if(_bypass.startsWith("npc_"))
			{
				int endOfId = _bypass.indexOf('_', 5);
				String id;
				if(endOfId > 0)
					id = _bypass.substring(4, endOfId);
				else
					id = _bypass.substring(4);

				if(npc != null && npc.canBypassCheck(activeChar))
				{
					String command = _bypass.substring(endOfId + 1);
					npc.onBypassFeedback(activeChar, command);
				}
			}
			else if (_bypass.startsWith("npc?"))
			{
				if ((npc != null) && (npc.canBypassCheck(activeChar)))
				{
					String command = _bypass.substring(4).trim();
					npc.onBypassFeedback(activeChar, command);
				}
			}
			else if (_bypass.startsWith("item?"))
			{
				//
			}
			else if(_bypass.startsWith("class_change?"))
			{
				String command = _bypass.substring(13).trim();
				if(command.startsWith("class_name="))
				{
					if(npc != null && npc.canBypassCheck(activeChar))
					{
						int classId = Integer.parseInt(command.substring(11).trim());
						npc.onChangeClassBypass(activeChar, classId);
					}
				}
			}
			else if(_bypass.startsWith("quest_accept?"))
			{
				String command = _bypass.substring(13).trim();
				if(command.startsWith("quest_id="))
				{
					if(npc != null && npc.canBypassCheck(activeChar))
					{
						int questId = Integer.parseInt(command.substring(9).trim());
						activeChar.processQuestEvent(questId, "quest_accept", npc);
					}
				}
			}
			else if(_bypass.startsWith("_olympiad?")) // _olympiad?command=move_op_field&field=1
			{
				// Переход в просмотр олимпа разрешен только от менеджера или с арены.
				final NpcInstance manager = NpcUtils.canPassPacket(activeChar, this, _bypass.split("&")[0]);
				if (manager != null)
					manager.onBypassFeedback(activeChar, _bypass);
			}
			else if (_bypass.equalsIgnoreCase("_heroes"))
			{
				final NpcInstance manager = NpcUtils.canPassPacket(activeChar, this, _bypass);
				if(manager != null)
					manager.onBypassFeedback(activeChar, _bypass);
			}
			else if(_bypass.startsWith("_diary"))
			{
				String params = _bypass.substring(_bypass.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if(heroid > 0)
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
			}
			else if(_bypass.startsWith("_match"))
			{
				String params = _bypass.substring(_bypass.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);

				OlympiadHistoryManager.getInstance().showHistory(activeChar,  heroclass, heropage);
			}
			else if(_bypass.startsWith("manor_menu_select?")) // Navigate throught Manor windows
			{
				GameObject object = activeChar.getTarget();
				if(object != null && object.isNpc())
					((NpcInstance) object).onBypassFeedback(activeChar, _bypass);
			}
			else if(_bypass.startsWith("menu_select?"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
				{
					String params = bp.bypass.substring(bp.bypass.indexOf("?") + 1);
					StringTokenizer st = new StringTokenizer(params, "&");
					int ask = Integer.parseInt(st.nextToken().split("=")[1]);
					long reply = st.hasMoreTokens() ? Long.parseLong(st.nextToken().split("=")[1]) : 0;
					npc.onMenuSelect(activeChar, ask, reply);
				}
			}
			else if(_bypass.equals("talk_select"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					npc.showQuestWindow(activeChar);
			}
			else if(_bypass.equals("teleport_request"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					npc.onTeleportRequest(activeChar);
			}
			else if(_bypass.equals("learn_skill"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					npc.onSkillLearnBypass(activeChar);
			}
			else if(_bypass.equals("deposit"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					WarehouseFunctions.showDepositWindow(activeChar);
			}
			else if(_bypass.equals("withdraw"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					WarehouseFunctions.showRetrieveWindow(activeChar);
			}
			else if(_bypass.equals("deposit_pledge"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					WarehouseFunctions.showDepositWindowClan(activeChar);
			}
			else if(_bypass.equals("withdraw_pledge"))
			{
				if(npc != null && npc.canBypassCheck(activeChar))
					WarehouseFunctions.showWithdrawWindowClan(activeChar);
			}
			else if(_bypass.startsWith("Quest "))
			{
				/*String p = _bypass.substring(6).trim();
				int idx = p.indexOf(' ');
				if(idx < 0)
					activeChar.processQuestEvent(Integer.parseInt(p.split("_")[1]), StringUtils.EMPTY, npc);
				else
					activeChar.processQuestEvent(Integer.parseInt(p.substring(0, idx).split("_")[1]), p.substring(idx).trim(), npc); */
				_log.warn("Trying to call Quest bypass: " + _bypass + ", player: " + activeChar);
			}
			else if (_bypass.startsWith("buffstore?"))
			{
				OfflineBufferManager.getInstance().processBypass(activeChar, _bypass.substring(10).trim());
			}
			else if(bp.bypass.startsWith("pvpevent_"))
			{
				String[] temp = bp.bypass.split(";");

				for(String bypass : temp)
				{
					if(bypass.startsWith("pvpevent"))
					{
						StringTokenizer st = new StringTokenizer(bypass, "_");
						st.nextToken();
						String cmd = st.nextToken();
						int val = Integer.parseInt(st.nextToken());

						if(cmd.equalsIgnoreCase("showReg"))
						{
							PvPEvent event = EventHolder.getInstance().getEvent(EventType.CUSTOM_PVP_EVENT, val);
							if(event != null && event.isRegActive())
								event.showReg();
						}
						else if(cmd.startsWith("reg"))
						{
							PvPEvent event = EventHolder.getInstance().getEvent(EventType.CUSTOM_PVP_EVENT, val);
							if(event != null && event.isRegActive())
							{
								if(cmd.contains(":"))
									event.regCustom(activeChar, cmd);
								else
									event.reg(activeChar);
							}
						}
					}
					else
					{
						IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler(bypass);
						if(handler != null)
							handler.onBypassCommand(activeChar, bypass);
					}
				}
			}
			else if (_bypass.startsWith("multiclass?"))
			{
				MulticlassUtils.onBypass(activeChar, _bypass.substring(11).trim());
			}
			else if(bp.type == BypassType.BBS)
			{
				if(!Config.BBS_ENABLED)
					activeChar.sendPacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
				else
				{
					IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler(_bypass);
					if(handler != null)
						handler.onBypassCommand(activeChar, _bypass);
				}
			}
		}
		catch(Exception e)
		{
			String st = "Error while handling bypass: " + _bypass;
			if(npc != null)
				st = st + " via NPC " + npc;

			_log.error(st, e);
		}
	}
}