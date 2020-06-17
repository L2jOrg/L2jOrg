/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.actionshifthandlers;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionShiftHandler;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.MathUtil.calculateDistance2D;
import static org.l2j.gameserver.util.MathUtil.calculateDistance3D;

public class NpcActionShift implements IActionShiftHandler
{
	@Override
	public boolean action(Player player, WorldObject target, boolean interact)
	{

		if (player.isGM())
		{
			// Set the target of the Player activeChar
			player.setTarget(target);
			
			final Npc npc = (Npc) target;
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByNpcId(npc.getId());
			html.setFile(player, "data/html/admin/npcinfo.htm");
			
			html.replace("%objid%", String.valueOf(target.getObjectId()));
			html.replace("%class%", target.getClass().getSimpleName());
			html.replace("%race%", npc.getTemplate().getRace().toString());
			html.replace("%id%", String.valueOf(npc.getTemplate().getId()));
			html.replace("%lvl%", String.valueOf(npc.getTemplate().getLevel()));
			html.replace("%name%", npc.getTemplate().getName());
			html.replace("%tmplid%", String.valueOf(npc.getTemplate().getId()));
			html.replace("%aggro%", String.valueOf(isAttackable(target) ? ((Attackable) target).getAggroRange() : 0));
			html.replace("%hp%", String.valueOf((int) npc.getCurrentHp()));
			html.replace("%hpmax%", String.valueOf(npc.getMaxHp()));
			html.replace("%mp%", String.valueOf((int) npc.getCurrentMp()));
			html.replace("%mpmax%", String.valueOf(npc.getMaxMp()));
			
			html.replace("%patk%", String.valueOf(npc.getPAtk()));
			html.replace("%matk%", String.valueOf(npc.getMAtk()));
			html.replace("%pdef%", String.valueOf(npc.getPDef()));
			html.replace("%mdef%", String.valueOf(npc.getMDef()));
			html.replace("%accu%", String.valueOf(npc.getAccuracy()));
			html.replace("%evas%", String.valueOf(npc.getEvasionRate()));
			html.replace("%crit%", String.valueOf(npc.getCriticalHit()));
			html.replace("%rspd%", String.valueOf(npc.getRunSpeed()));
			html.replace("%aspd%", String.valueOf(npc.getPAtkSpd()));
			html.replace("%cspd%", String.valueOf(npc.getMAtkSpd()));
			html.replace("%atkType%", String.valueOf(npc.getTemplate().getBaseAttackType()));
			html.replace("%atkRng%", String.valueOf(npc.getTemplate().getBaseAttackRange()));
			html.replace("%str%", String.valueOf(npc.getSTR()));
			html.replace("%dex%", String.valueOf(npc.getDEX()));
			html.replace("%con%", String.valueOf(npc.getCON()));
			html.replace("%int%", String.valueOf(npc.getINT()));
			html.replace("%wit%", String.valueOf(npc.getWIT()));
			html.replace("%men%", String.valueOf(npc.getMEN()));
			html.replace("%loc%", target.getX() + " " + target.getY() + " " + target.getZ());
			html.replace("%heading%", String.valueOf(npc.getHeading()));
			html.replace("%collision_radius%", String.valueOf(npc.getTemplate().getfCollisionRadius()));
			html.replace("%collision_height%", String.valueOf(npc.getTemplate().getfCollisionHeight()));
			html.replace("%clanHall%", clanHall != null ? clanHall.getName() : "none");
			html.replace("%mpRewardValue%", npc.getTemplate().getMpRewardValue());
			html.replace("%mpRewardTicks%", npc.getTemplate().getMpRewardTicks());
			html.replace("%mpRewardType%", npc.getTemplate().getMpRewardType().name());
			html.replace("%mpRewardAffectType%", npc.getTemplate().getMpRewardAffectType().name());
			html.replace("%loc2d%", String.valueOf((int) calculateDistance2D(player, npc)));
			html.replace("%loc3d%", String.valueOf((int) calculateDistance3D(player, npc)));
			
			final AttributeType attackAttribute = npc.getAttackElement();
			html.replace("%ele_atk%", attackAttribute.name());
			html.replace("%ele_atk_value%", String.valueOf(npc.getAttackElementValue(attackAttribute)));
			html.replace("%ele_dfire%", String.valueOf(npc.getDefenseElementValue(AttributeType.FIRE)));
			html.replace("%ele_dwater%", String.valueOf(npc.getDefenseElementValue(AttributeType.WATER)));
			html.replace("%ele_dwind%", String.valueOf(npc.getDefenseElementValue(AttributeType.WIND)));
			html.replace("%ele_dearth%", String.valueOf(npc.getDefenseElementValue(AttributeType.EARTH)));
			html.replace("%ele_dholy%", String.valueOf(npc.getDefenseElementValue(AttributeType.HOLY)));
			html.replace("%ele_ddark%", String.valueOf(npc.getDefenseElementValue(AttributeType.DARK)));
			
			final Spawn spawn = npc.getSpawn();
			if (spawn != null)
			{
				final NpcSpawnTemplate template = spawn.getNpcSpawnTemplate();
				if (template != null)
				{
					final String fileName = template.getSpawnTemplate().getFilePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/');
					html.replace("%spawnfile%", fileName);
					html.replace("%spawnname%", String.valueOf(template.getSpawnTemplate().getName())); // used String.valueOf because it can be null
					html.replace("%spawngroup%", String.valueOf(template.getGroup().getName())); // used String.valueOf because it can be null
					if (template.getSpawnTemplate().getAI() != null)
					{
						final Quest script = QuestManager.getInstance().getQuest(template.getSpawnTemplate().getAI());
						if (script != null)
						{
							html.replace("%spawnai%", "<a action=\"bypass -h admin_quest_info " + script.getName() + "\"><font color=\"LEVEL\">" + script.getName() + "</font></a>");
						}
					}
					html.replace("%spawnai%", "<font color=FF0000>" + template.getSpawnTemplate().getAI() + "</font>");
				}
				
				html.replace("%spawn%", npc.getSpawn().getX() + " " + npc.getSpawn().getY() + " " + npc.getSpawn().getZ());
				if (npc.getSpawn().getRespawnMinDelay() == 0)
				{
					html.replace("%resp%", "None");
				}
				else if (npc.getSpawn().hasRespawnRandom())
				{
					html.replace("%resp%", (npc.getSpawn().getRespawnMinDelay() / 1000) + "-" + (npc.getSpawn().getRespawnMaxDelay() / 1000) + " sec");
				}
				else
				{
					html.replace("%resp%", (npc.getSpawn().getRespawnMinDelay() / 1000) + " sec");
				}
			}
			else
			{
				html.replace("%spawn%", "<font color=FF0000>null</font>");
				html.replace("%resp%", "<font color=FF0000>--</font>");
			}
			
			html.replace("%spawnfile%", "<font color=FF0000>--</font>");
			html.replace("%spawnname%", "<font color=FF0000>--</font>");
			html.replace("%spawngroup%", "<font color=FF0000>--</font>");
			html.replace("%spawnai%", "<font color=FF0000>--</font>");
			
			if (npc.hasAI())
			{
				final Set<String> clans = NpcData.getInstance().getClansByIds(npc.getTemplate().getClans());
				final Set<Integer> ignoreClanNpcIds = npc.getTemplate().getIgnoreClanNpcIds();
				final String clansString = !clans.isEmpty() ? CommonUtil.implode(clans, ", ") : "";
				final String ignoreClanNpcIdsString = ignoreClanNpcIds != null ? CommonUtil.implode(ignoreClanNpcIds, ", ") : "";
				
				html.replace("%ai_intention%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>Intention:</font></td><td align=right width=170>" + npc.getAI().getIntention().name() + "</td></tr></table></td></tr>");
				html.replace("%ai%", "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>AI</font></td><td align=right width=170>" + npc.getAI().getClass().getSimpleName() + "</td></tr></table></td></tr>");
				html.replace("%ai_type%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>AIType</font></td><td align=right width=170>" + npc.getAiType() + "</td></tr></table></td></tr>");
				html.replace("%ai_clan%", "<tr><td><table width=270 border=0><tr><td width=100><font color=FFAA00>Clan & Range:</font></td><td align=right width=170>" + clansString + " " + npc.getTemplate().getClanHelpRange() + "</td></tr></table></td></tr>");
				html.replace("%ai_enemy_clan%", "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=100><font color=FFAA00>Ignore & Range:</font></td><td align=right width=170>" + ignoreClanNpcIdsString + " " + npc.getTemplate().getAggroRange() + "</td></tr></table></td></tr>");
			}
			else
			{
				html.replace("%ai_intention%", "");
				html.replace("%ai%", "");
				html.replace("%ai_type%", "");
				html.replace("%ai_clan%", "");
				html.replace("%ai_enemy_clan%", "");
			}
			
			final String routeName = WalkingManager.getInstance().getRouteName(npc);
			if (!routeName.isEmpty())
			{
				html.replace("%route%", "<tr><td><table width=270 border=0><tr><td width=100><font color=LEVEL>Route:</font></td><td align=right width=170>" + routeName + "</td></tr></table></td></tr>");
			}
			else
			{
				html.replace("%route%", "");
			}
			player.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}
