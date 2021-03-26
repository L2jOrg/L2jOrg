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
package org.l2j.gameserver.util;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.ExCharInfo;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public final class Broadcast {

    /**
     * Send a packet to all Player in the _KnownPlayers of the Creature.<BR>
     * <B><U> Concept</U> :</B><BR>
     * Player in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
     * In order to inform other players of state modification on the Creature, server just need to go through _knownPlayers to send Server->Client Packet<BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
     *
     * @param character
     * @param mov
     */
    public static void toKnownPlayers(Creature character, ServerPacket mov) {
        World.getInstance().forEachVisibleObject(character, Player.class, player -> {
            player.sendPacket(mov);
            if (mov instanceof ExCharInfo && character instanceof Player broadcaster) {
                broadcaster.updateRelation(player);
            }
        });
    }

    /**
     * Send a packet to all Player in the _KnownPlayers (in the specified radius) of the Creature.<BR>
     * <B><U> Concept</U> :</B><BR>
     * Player in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
     * In order to inform other players of state modification on the Creature, server just needs to go through _knownPlayers to send Server->Client Packet and check the distance between the targets.<BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
     *
     * @param character
     * @param mov
     * @param radius
     */
    public static void toKnownPlayersInRadius(Creature character, ServerPacket mov, int radius) {
        if (radius < 0) {
            radius = 1500;
        }

        World.getInstance().forEachVisibleObjectInRange(character, Player.class, radius, mov::sendTo);
    }

    /**
     * Send a packet to all Player in the _KnownPlayers of the Creature and to the specified character.<BR>
     * <B><U> Concept</U> :</B><BR>
     * Player in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
     * In order to inform other players of state modification on the Creature, server just need to go through _knownPlayers to send Server->Client Packet<BR>
     *
     * @param character
     * @param mov
     */
    public static void toSelfAndKnownPlayers(Creature character, ServerPacket mov) {
        if (isPlayer(character)) {
            character.sendPacket(mov);
        }

        toKnownPlayers(character, mov);
    }

    public static void toSelfAndKnownPlayersInRadius(Creature character, ServerPacket mov, int radius) {
        toSelfAndKnownPlayersInRadius(character, mov, radius, o -> true);
    }

    public static void toSelfAndKnownPlayersInRadius(Creature character, ServerPacket mov, int radius, Predicate<Player> filter) {
        if (radius < 0) {
            radius = 600;
        }

        if (isPlayer(character)) {
            character.sendPacket(mov);
        }

        World.getInstance().forEachVisibleObjectInRange(character, Player.class, radius, mov::sendTo, filter);
    }

    /**
     * Send a packet to all Player present in the world.<BR>
     * <B><U> Concept</U> :</B><BR>
     * In order to inform other players of state modification on the Creature, server just need to go through _allPlayers to send Server->Client Packet<BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
     *
     * @param packets
     */
    public static void toAllOnlinePlayers(ServerPacket... packets) {
        World.getInstance().forEachPlayer(p -> p.sendPackets(packets));
    }

    public static void toAllOnlinePlayers(String text) {
        toAllOnlinePlayers(text, false);
    }

    public static void toAllOnlinePlayers(String text, boolean isCritical) {
        toAllOnlinePlayers(new CreatureSay(0, isCritical ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT, "", text));
    }

    public static void toAllOnlinePlayersOnScreen(String text) {
        toAllOnlinePlayers(new ExShowScreenMessage(text, 10000));
    }
}
