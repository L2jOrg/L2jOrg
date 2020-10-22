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
import org.l2j.gameserver.model.olympiad.OlympiadInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchResult;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
class OlympiadClasslessMatch extends OlympiadMatch {

    private Player red;
    private Player blue;
    private Location redBackLocation;
    private Location blueBackLocation;
    private Player defeated;

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
    protected boolean processPlayerDeath(Player player) {
        defeated = player;
        finishBattle();
        return false;
    }

    @Override
    protected void calculateResults() {
        if(nonNull(defeated)) {
            var winner = red == defeated ?  blue : red;
            sendPacket(getSystemMessage(CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addPcName(winner));
            sendPacket(getSystemMessage(C1_HAS_LOST_S2_POINTS_IN_THE_OLYMPIAD_GAMES).addPcName(defeated).addInt(10));
            sendPacket(new ExOlympiadMatchResult(false, winner.getOlympiadSide(), List.of(OlympiadInfo.of(winner, 10)), List.of(OlympiadInfo.of(defeated, -10))));
        } else {
            sendMessage(THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE);
            sendPacket(new ExOlympiadMatchResult(true, 1, List.of(OlympiadInfo.of(red, 0)), List.of(OlympiadInfo.of(blue, 0))));
        }
    }

    @Override
    protected void teleportBack() {
        red.teleToLocation(redBackLocation, null);
        blue.teleToLocation(blueBackLocation, null);
    }

}
