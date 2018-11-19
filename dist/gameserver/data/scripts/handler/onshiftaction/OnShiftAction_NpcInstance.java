package handler.onshiftaction;

import handler.onshiftaction.commons.RewardListInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.text.StrBuilder;
import l2s.gameserver.Config;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.PositionUtils;
import l2s.gameserver.utils.Util;

/**
 * @author VISTALL
 * @date 2:43/19.08.2011
 */
public class OnShiftAction_NpcInstance extends ScriptOnShiftActionHandler<NpcInstance>
{
	@Override
	public Class<NpcInstance> getClazz()
	{
		return NpcInstance.class;
	}

	@Override
	public boolean call(NpcInstance npc, Player player)
	{
		return showMain(player, npc, player.isGM());
	}

	@Bypass("actions.OnActionShift:showShort")
	public void showShort(Player player, NpcInstance npc, String[] par)
	{
		showMain(player, npc, false);
	}

	private boolean showMain(Player player, NpcInstance npc, boolean full)
	{
		if(npc == null)
			return false;

		// Для мертвых мобов не показываем табличку, иначе спойлеры плачут
		if((npc.noShiftClick() || npc.isDead()) && !player.isGM())
			return false;

		if(!Config.ALLOW_NPC_SHIFTCLICK && !player.isGM())
		{
			if(Config.ALT_GAME_SHOW_DROPLIST)
			{
				droplist(player, npc, null);
				return true;
			}
			return false;
		}

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setFile("scripts/actions/player.L2NpcInstance.onActionShift." + (full ? "full.htm" : "htm"));

		if(full)
		{
			msg.replace("%class%", String.valueOf(npc.getClass().getSimpleName()));
			msg.replace("%id%", String.valueOf(npc.getNpcId()));
			msg.replace("%respawn%", String.valueOf(npc.getSpawn() != null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : "0"));
			msg.replace("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
			msg.replace("%pevs%", String.valueOf(npc.getPEvasionRate(null)));
			msg.replace("%pacc%", String.valueOf(npc.getPAccuracy()));
			msg.replace("%mevs%", String.valueOf(npc.getMEvasionRate(null)));
			msg.replace("%macc%", String.valueOf(npc.getMAccuracy()));
			msg.replace("%pcrt%", String.valueOf(npc.getPCriticalHit(null)));
			msg.replace("%mcrt%", String.valueOf(npc.getMCriticalHit(null, null)));
			msg.replace("%aspd%", String.valueOf(npc.getPAtkSpd()));
			msg.replace("%cspd%", String.valueOf(npc.getMAtkSpd()));
			msg.replace("%currentMP%", String.valueOf(npc.getCurrentMp()));
			msg.replace("%currentHP%", String.valueOf(npc.getCurrentHp()));
			msg.replace("%loc%", npc.getSpawn() == null ? "" : npc.getSpawn().getName());
			msg.replace("%dist%", String.valueOf((int) npc.getDistance3D(player)));
			msg.replace("%killed%", String.valueOf(0));//TODO [G1ta0] убрать
			msg.replace("%spReward%", String.valueOf(npc.getSpReward()));
			msg.replace("%xyz%", npc.getLoc().x + " " + npc.getLoc().y + " " + npc.getLoc().z);
			msg.replace("%ai_type%", npc.getAI().getClass().getSimpleName());
			msg.replace("%direction%", PositionUtils.getDirectionTo(npc, player).toString().toLowerCase());
			msg.replace("%respawn%", String.valueOf(npc.getSpawn() != null ? (npc.getSpawn().getRespawnPattern() == null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : npc.getSpawn().getRespawnPattern().toString()) : "0"));
			msg.replace("%factionId%", String.valueOf(npc.getFaction()));
			msg.replace("%aggro%", String.valueOf(npc.getAggroRange()));
			msg.replace("%pDef%", String.valueOf(npc.getPDef(null)));
			msg.replace("%mDef%", String.valueOf(npc.getMDef(null, null)));
			msg.replace("%pAtk%", String.valueOf(npc.getPAtk(null)));
			msg.replace("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
			msg.replace("%runSpeed%", String.valueOf(npc.getRunSpeed()));
			msg.replace("%hp_regen%", String.valueOf(npc.getHpRegen()));
			msg.replace("%mp_regen%", String.valueOf(npc.getMpRegen()));

			// Дополнительная инфа для ГМов
			if(player.isGM())
				msg.replace("%AI%", String.valueOf(npc.getAI()) + ",<br1>active: " + npc.getAI().isActive() + ",<br1>intention: " + npc.getAI().getIntention());
			else
				msg.replace("%AI%", "");

			StrBuilder b = new StrBuilder("");
			for(Event e : npc.getEvents())
				b.append(e.toString()).append(";");

			msg.replace("%event%", b.toString());
		}

		msg.replace("<?npc_name?>", nameNpc(npc));
		msg.replace("<?id?>", String.valueOf(npc.getNpcId()));
		msg.replace("<?level?>", String.valueOf(npc.getLevel()));
		msg.replace("<?max_hp?>", String.valueOf(npc.getMaxHp()));
		msg.replace("<?max_mp?>", String.valueOf(npc.getMaxMp()));
		msg.replace("<?xp_reward?>", String.valueOf(npc.getExpReward()));
		msg.replace("<?sp_reward?>", String.valueOf(npc.getSpReward()));
		msg.replace("<?aggresive?>", new CustomMessage(npc.getAggroRange() > 0 ? "YES" : "NO").toString(player));

		player.sendPacket(msg);
		return true;
	}

	@Bypass("actions.OnActionShift:droplist")
	public void droplist(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		if(Config.ALT_GAME_SHOW_DROPLIST || player.isGM())
		{
			if(par == null || par.length == 0)
				RewardListInfo.showInfo(player, npc, null, 1);
			else if(par.length == 1)
				RewardListInfo.showInfo(player, npc, RewardType.valueOf(par[0]), 1);
			else if(par.length > 1)
				RewardListInfo.showInfo(player, npc, RewardType.valueOf(par[0]), Integer.parseInt(par[1]));
		}
	}

	@Bypass("actions.OnActionShift:stats")
	public void stats(Player player, NpcInstance npc, String[] par)
	{
		if(npc == null)
			return;

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setFile("scripts/actions/player.L2NpcInstance.stats.htm");

		msg.replace("%name%", nameNpc(npc));
		msg.replace("%level%", String.valueOf(npc.getLevel()));
		msg.replace("%factionId%", String.valueOf(npc.getFaction()));
		msg.replace("%aggro%", String.valueOf(npc.getAggroRange()));
		msg.replace("%race%", getNpcRaceById(npc.getTemplate().getRace()));
		msg.replace("%maxHp%", String.valueOf(npc.getMaxHp()));
		msg.replace("%maxMp%", String.valueOf(npc.getMaxMp()));
		msg.replace("%pDef%", String.valueOf(npc.getPDef(null)));
		msg.replace("%mDef%", String.valueOf(npc.getMDef(null, null)));
		msg.replace("%pAtk%", String.valueOf(npc.getPAtk(null)));
		msg.replace("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
		msg.replace("%paccuracy%", String.valueOf(npc.getPAccuracy()));
		msg.replace("%pevasionRate%", String.valueOf(npc.getPEvasionRate(null)));
		msg.replace("%pcriticalHit%", String.valueOf(npc.getPCriticalHit(null)));
		msg.replace("%maccuracy%", String.valueOf(npc.getMAccuracy()));
		msg.replace("%mevasionRate%", String.valueOf(npc.getMEvasionRate(null)));
		msg.replace("%mcriticalHit%", String.valueOf(npc.getMCriticalHit(null, null)));
		msg.replace("%runSpeed%", String.valueOf(npc.getRunSpeed()));
		msg.replace("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
		msg.replace("%pAtkSpd%", String.valueOf(npc.getPAtkSpd()));
		msg.replace("%mAtkSpd%", String.valueOf(npc.getMAtkSpd()));

		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:quests")
	public void quests(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><br>");

		Map<QuestEventType, Set<Quest>> list = npc.getTemplate().getQuestEvents();
		for(Map.Entry<QuestEventType, Set<Quest>> entry : list.entrySet())
		{
			for(Quest q : entry.getValue())
				dialog.append(entry.getKey()).append(" ").append(q.getClass().getSimpleName()).append("<br1>");
		}

		dialog.append("</body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:skills")
	public void skills(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center>");

		Collection<SkillEntry> list = npc.getAllSkills();
		if(list != null && !list.isEmpty())
		{
			dialog.append("<br><font color=\"LEVEL\">Active:</font><br>");
			for(SkillEntry s : list)
			{
				if(s.getTemplate().isActive())
					dialog.append(s.getName(player)).append(" <font color=\"LEVEL\">Id: ").append(s.getId()).append(" Level: ").append(s.getLevel()).append("</font><br1>");
			}

			dialog.append("<br><font color=\"LEVEL\">Passive:</font><br>");
			for(SkillEntry s : list)
			{
				if(!s.getTemplate().isActive())
					dialog.append(s.getName(player)).append(" <font color=\"LEVEL\">Id: ").append(s.getId()).append(" Level: ").append(s.getLevel()).append("</font><br1>");
			}
		}

		dialog.append("</body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:effects")
	public void effects(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><br>");

		for(Abnormal e : npc.getAbnormalList())
			dialog.append(e.getSkill().getName(player)).append("<br1>");

		dialog.append("<br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h htmbypass_actions.OnActionShift:effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:resists")
	public void resists(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><table width=\"80%\">");

		boolean hasResist;

		hasResist = addResist(dialog, "Fire", npc.calcStat(Stats.DEFENCE_FIRE, 0, null, null));
		hasResist |= addResist(dialog, "Wind", npc.calcStat(Stats.DEFENCE_WIND, 0, null, null));
		hasResist |= addResist(dialog, "Water", npc.calcStat(Stats.DEFENCE_WATER, 0, null, null));
		hasResist |= addResist(dialog, "Earth", npc.calcStat(Stats.DEFENCE_EARTH, 0, null, null));
		hasResist |= addResist(dialog, "Light", npc.calcStat(Stats.DEFENCE_HOLY, 0, null, null));
		hasResist |= addResist(dialog, "Darkness", npc.calcStat(Stats.DEFENCE_UNHOLY, 0, null, null));
		hasResist |= addResist(dialog, "Bleed", npc.calcStat(Stats.DEFENCE_TRAIT_BLEED));
		hasResist |= addResist(dialog, "Poison", npc.calcStat(Stats.DEFENCE_TRAIT_POISON));
		hasResist |= addResist(dialog, "Stun", npc.calcStat(Stats.DEFENCE_TRAIT_SHOCK));
		hasResist |= addResist(dialog, "Root", npc.calcStat(Stats.DEFENCE_TRAIT_HOLD));
		hasResist |= addResist(dialog, "Sleep", npc.calcStat(Stats.DEFENCE_TRAIT_SLEEP));
		hasResist |= addResist(dialog, "Paralyze", npc.calcStat(Stats.DEFENCE_TRAIT_PARALYZE));
		hasResist |= addResist(dialog, "Mental", npc.calcStat(Stats.DEFENCE_TRAIT_DERANGEMENT));
		hasResist |= addResist(dialog, "Debuff", npc.calcStat(Stats.RESIST_ABNORMAL_DEBUFF, 0, null, null));
		hasResist |= addResist(dialog, "Cancel", npc.calcStat(Stats.CANCEL_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Sword", npc.calcStat(Stats.DEFENCE_TRAIT_SWORD));
		hasResist |= addResist(dialog, "Dual Sword", npc.calcStat(Stats.DEFENCE_TRAIT_DUAL));
		hasResist |= addResist(dialog, "Blunt", npc.calcStat(Stats.DEFENCE_TRAIT_BLUNT));
		hasResist |= addResist(dialog, "Dagger", npc.calcStat(Stats.DEFENCE_TRAIT_DAGGER));
		hasResist |= addResist(dialog, "Bow", npc.calcStat(Stats.DEFENCE_TRAIT_BOW));
		hasResist |= addResist(dialog, "Crossbow", npc.calcStat(Stats.DEFENCE_TRAIT_CROSSBOW));
		hasResist |= addResist(dialog, "2H Crossbow", npc.calcStat(Stats.DEFENCE_TRAIT_TWOHANDCROSSBOW));
		hasResist |= addResist(dialog, "Polearm", npc.calcStat(Stats.DEFENCE_TRAIT_POLE));
		hasResist |= addResist(dialog, "Fist", npc.calcStat(Stats.DEFENCE_TRAIT_FIST));

		if(!hasResist)
			dialog.append("</table>No resists</body></html>");
		else
			dialog.append("</table></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:aggro")
	public void aggro(Player player, NpcInstance npc, String[] par)
	{
		if(player == null || npc == null)
			return;

		StrBuilder dialog = new StrBuilder("<html><body><table width=\"80%\"><tr><td>Attacker</td><td>Damage</td><td>Hate</td></tr>");

		Set<AggroList.HateInfo> set = new TreeSet<AggroList.HateInfo>(AggroList.HateComparator.getInstance());
		set.addAll(npc.getAggroList().getCharMap().values());
		for(AggroList.HateInfo aggroInfo : set)
			dialog.append("<tr><td>").append(aggroInfo.attacker.getName()).append("</td><td>").append(aggroInfo.damage).append("</td><td>").append(aggroInfo.hate).append("</td></tr>");

		dialog.append("</table><br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h htmbypass_actions.OnActionShift:aggro\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	private static boolean addResist(StrBuilder dialog, String name, double val)
	{
		if (val == 0)
			return false;

		dialog.append("<tr><td>").append(name).append("</td><td>");
		if (val == Double.POSITIVE_INFINITY)
			dialog.append("MAX");
		else if (val == Double.NEGATIVE_INFINITY)
			dialog.append("MIN");
		else
		{
			dialog.append(String.valueOf((int)val));
			dialog.append("</td></tr>");
			return true;
		}

		dialog.append("</td></tr>");
		return true;
	}

	private static String getNpcRaceById(int raceId)
	{
		switch(raceId)
		{
			case 1:
				return "Undead";
			case 2:
				return "Magic Creatures";
			case 3:
				return "Beasts";
			case 4:
				return "Animals";
			case 5:
				return "Plants";
			case 6:
				return "Humanoids";
			case 7:
				return "Spirits";
			case 8:
				return "Angels";
			case 9:
				return "Demons";
			case 10:
				return "Dragons";
			case 11:
				return "Giants";
			case 12:
				return "Bugs";
			case 13:
				return "Fairies";
			case 14:
				return "Humans";
			case 15:
				return "Elves";
			case 16:
				return "Dark Elves";
			case 17:
				return "Orcs";
			case 18:
				return "Dwarves";
			case 19:
				return "Others";
			case 20:
				return "Non-living Beings";
			case 21:
				return "Siege Weapons";
			case 22:
				return "Defending Army";
			case 23:
				return "Mercenaries";
			case 24:
				return "Unknown Creature";
			case 25:
				return "Kamael";
			default:
				return "Not defined";
		}
	}

	private static String nameNpc(NpcInstance npc)
	{
		if(npc.getNameNpcString() == NpcString.NONE)
			return HtmlUtils.htmlNpcName(npc.getNpcId());
		else
			return HtmlUtils.htmlNpcString(npc.getNameNpcString().getId(), npc.getName());
	}
}
