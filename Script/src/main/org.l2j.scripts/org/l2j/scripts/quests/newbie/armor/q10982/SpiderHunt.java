/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.quests.newbie.armor.q10982;

import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.armor.MoonArmorRewardQuest;

import java.util.Set;

/**
 * @author RobikBobik
 * @author JoeAlisson
 */
public class SpiderHunt extends MoonArmorRewardQuest {

	private static final int JACKSON = 30002;

	private static final int GIANT_SPIDER = 20103;
	private static final int GIANT_FANG_SPIDER = 20106;
	private static final int GIANT_BLADE_SPIDER = 20108;

	private static final String QUEST_HUNT_PROGRESS = "hunt_progress";
	
	public SpiderHunt() {
		super(10982, JACKSON, ClassId.FIGHTER, ClassId.MAGE);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon) {
		var qs = getQuestState(killer, false);
		if (qs != null && qs.isCond(1)) {
			qs.set(QUEST_HUNT_PROGRESS, qs.getInt(QUEST_HUNT_PROGRESS) +1);
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			sendNpcLogList(killer, killCountLogList(qs));
		}
		return super.onKill(npc, killer, isSummon);
	}

	private Set<NpcLogListHolder> killCountLogList(QuestState qs) {
		return Set.of(new NpcLogListHolder(NpcStringId.KILL_GIANT_SPIDERS.getId(), true, qs.getInt(QUEST_HUNT_PROGRESS)));
	}

	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player) {
		var qs = getQuestState(player, false);
		if (qs != null && qs.isCond(1)) {
			return killCountLogList(qs);
		}
		return super.getNpcLogList(player);
	}

	@Override
	protected IntCollection huntMonsters() {
		return IntSet.of(GIANT_SPIDER, GIANT_FANG_SPIDER, GIANT_BLADE_SPIDER);
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_15_20_SPIDER_HUNT;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-117409, 227185, -2896);
	}

	@Override
	protected int questHuntProgress(Player player) {
		var qs = getQuestState(player, false);
		return qs != null ? qs.getInt(QUEST_HUNT_PROGRESS) : 0;
	}
}