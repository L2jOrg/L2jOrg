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
package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE;

/**
 * @author JoeAlisson
 */
class OlympiadClasslessMatch extends OlympiadMatch {

    private Player red;
    private Player blue;
    private Location redBackLocation;
    private Location blueBackLocation;

    OlympiadClasslessMatch() {
    }

    @Override
    public void addParticipant(Player player)  {
        if(isNull(red)) {
            red = player;
        } else {
            blue = player;
        }
    }

    @Override
    public String getPlayerRedName() {
        return red.getAppearance().getVisibleName();
    }

    @Override
    public String getPlayerBlueName() {
        return blue.getAppearance().getVisibleName();
    }

    @Override
    public OlympiadRuleType getType() {
        return OlympiadRuleType.CLASSLESS;
    }

    @Override
    protected void teleportPlayers(Location redLocation, Location blueLocation, Instance arena) {
        redBackLocation = red.getLocation();
        red.teleToLocation(redLocation, arena);

        blueBackLocation = blue.getLocation();
        blue.teleToLocation(blueLocation, arena);
    }

    @Override
    protected void forEachParticipant(Consumer<Player> action) {
        action.accept(red);
        action.accept(blue);
    }

    @Override
    protected void forBluePlayers(Consumer<Player> action) {
        action.accept(blue);
    }

    @Override
    protected void forRedPlayers(Consumer<Player> action) {
        action.accept(red);
    }

    @Override
    public void sendMessage(SystemMessageId messageId) {
        red.sendPacket(messageId);
        blue.sendPacket(messageId);
    }

    @Override
    public void sendPacket(ServerPacket packet) {
        red.sendPacket(packet);
        blue.sendPacket(packet);
    }

    @Override
    protected void calculateResults() {
        sendMessage(THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE);
        //broadcastMessage(CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
        //broadcastMessage(C1_HAS_LOST_S2_POINTS_IN_THE_OLYMPIAD_GAMES);
    }

    @Override
    protected void teleportBack() {
        red.teleToLocation(redBackLocation, null);
        blue.teleToLocation(blueBackLocation, null);
    }

}
