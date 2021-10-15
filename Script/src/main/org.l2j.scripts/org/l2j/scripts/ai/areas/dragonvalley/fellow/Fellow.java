/*
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
package org.l2j.scripts.ai.areas.dragonvalley.fellow;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.scripts.ai.AbstractNpcAI;

import java.util.Objects;

/**
 * @author RobikBobik
 */
public class Fellow extends AbstractNpcAI
{

    private static final int FELLOW_ID = 31713;
    private static final Location TELEPORT_LOC = new Location(154376, 121290, -3807);
    private static final NpcStringId[] TEXT = {
        NpcStringId.LET_S_JOIN_OUR_FORCES_AND_FACE_THIS_TOGETHER,
        NpcStringId.BALTHUS_KNIGHTS_ARE_LOOKING_FOR_MERCENARIES
    };
    public static final String CHAT_TIMER = "CHAT_TIMER";

    private Fellow() {
        addFirstTalkId(FELLOW_ID);
        addTalkId(FELLOW_ID);
        addSpawnId(FELLOW_ID);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if ("teleToAntharas".equals(event)) {
            if ((player.getCommandChannel() == null) || (player.getCommandChannel().getLeader() != player) || (player.getCommandChannel().getMemberCount() < 27) || (player.getCommandChannel().getMemberCount() > 300)) {
                return "31713-01.html";
            }
            for (var member : player.getCommandChannel().getMembers()) {
                if ((member != null) && (member.getLevel() > 70)) {
                    member.teleToLocation(TELEPORT_LOC);
                }
            }
        } else if (CHAT_TIMER.equals(event)) {
            npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, Objects.requireNonNull(Rnd.get(TEXT))));
            startQuestTimer(CHAT_TIMER, 30000, npc, null);
        }
        return null;
    }

    @Override
    public String onFirstTalk(Npc npc, Player player)
    {
        return "31713.html";
    }

    @Override
    public String onSpawn(Npc npc)
    {
        startQuestTimer(CHAT_TIMER, 5000, npc, null);
        return super.onSpawn(npc);
    }

    public static AbstractNpcAI provider()
    {
        return new Fellow();
    }
}
