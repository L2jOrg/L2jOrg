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
package org.l2j.scripts.ai.areas.dungeonofabyss.tracker;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

import java.util.Collections;
import java.util.Map;

/**
 * @author JoeAlisson
 */
public class SoulTracker extends AbstractNpcAI {

    private static final String EXPLORING_WEST_WING_QUEST = "Q00933_ExploringTheWestWingOfTheDungeonOfAbyss";

    private static final IntMap<Map<String, Location>> NPC_TELEPORT_LOCATIONS = new HashIntMap<>(4);
    private static final IntIntMap NPC_KEYS = new HashIntIntMap(4);

    static {
        NPC_TELEPORT_LOCATIONS.put(31774, Map.of(
            "1", new Location(-119440, -182464, -6752), // Join Room from Magrit
            "2", new Location(-120394, -179651, -6751), // Move to West Wing 2nd
            "3", new Location(-116963, -181492, -6575), // Go to the Condemned of Abyss Prison
            "4", new Location(146945, 26764, -2200) // Return to Aden
        ));

        NPC_TELEPORT_LOCATIONS.put(31775, Map.of(
            "1", new Location(-119533, -179641, -6751), // Join Room from Ingrit
            "2", new Location(-120325, -182444, -6752), // Move to West Wing 1nd
            "3", new Location(-116975, -178699, -6751), // Go to the Condemned of Abyss Prison
            "4", new Location(146945, 26764, -2200) // Return to Aden
        ));

        NPC_TELEPORT_LOCATIONS.put(31776, Map.of(
             "1", new Location(-110038, -180560, -6754), // Join Room from Iris
            "2", new Location(-109234, -177737, -6751), // Move to East Wing 2nd
            "3", new Location(-112648, -181517, -6751), // Go to the Condemned of Abyss Prison
            "4", new Location(146945, 26764, -2200) // Return to Aden
        ));

        NPC_TELEPORT_LOCATIONS.put(31777, Map.of(
                "1", new Location(-110067, -177733, -6751), // Join Room from Rosammy
                "2", new Location(-120318, -179626, -6752), // Move to East Wing 1nd
                "3", new Location(-112632, -178671, -6751), // Go to the Condemned of Abyss Prison
                "4", new Location(146945, 26764, -2200) // Return to Aden
        ));

        NPC_KEYS.put(31774, 90010);
        NPC_KEYS.put(31775, 90010);
        NPC_KEYS.put(31776, 90011);
        NPC_KEYS.put(31777, 90011);
    }

    private SoulTracker() {
        addStartNpc(31774, 31775, 31776, 31777);
        addTalkId(31774, 31775, 31776, 31777);
        addFirstTalkId(31774, 31775, 31776, 31777);
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        return npc.getId() + ".htm";
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        var questState = player.getQuestState(EXPLORING_WEST_WING_QUEST);
        if(questState == null || !questState.isStarted()) {
            return "no_enter.htm";
        }

        var result = switch (event) {
            case "1", "2", "4" -> teleportToEventLocation(player, npc, event);
            case "3" -> teleportWithKey(player, npc, event);
            default -> null;
        };

        return result == null ? super.onAdvEvent(event, npc, player) : result;
    }

    private String teleportWithKey(Player player, Npc npc, String event) {
        if(!hasQuestItems(player, NPC_KEYS.getOrDefault(npc.getId(), 0))) {
            return "no_key.htm";
        }
        return teleportToEventLocation(player, npc, event);

    }

    private String teleportToEventLocation(Player player, Npc npc, String event) {
        var location = NPC_TELEPORT_LOCATIONS.getOrDefault(npc.getId(), Collections.emptyMap()).get(event);
        if(location != null) {
            player.teleToLocation(location, false);
        }
        return null;
    }

    public static AbstractNpcAI provider() {
        return new SoulTracker();
    }
}
