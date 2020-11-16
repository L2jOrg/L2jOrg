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
import org.l2j.gameserver.model.olympiad.OlympiadResultInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
class OlympiadClasslessMatch extends OlympiadMatch {

    private Player red;
    private Player blue;
    private double redDamage;
    private double blueDamage;
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
        red.teleToLocation(redLocation, arena);
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
    protected boolean processPlayerDeath(Player player) {
        defeated = player;
        finishBattle();
        return false;
    }

    @Override
    protected OlympiadResult calcResult() {
        OlympiadResult result = OlympiadResult.TIE;
        if(nonNull(defeated)) {
            result =  defeated == blue ? OlympiadResult.RED_WIN : OlympiadResult.BLUE_WIN;
        } else if(redDamage != blueDamage) {
            result =  blueDamage < redDamage ? OlympiadResult.RED_WIN : OlympiadResult.BLUE_WIN;
            defeated = result == OlympiadResult.RED_WIN ? blue : red;
        }
        return result;
    }

    @Override
    protected List<OlympiadResultInfo> getRedTeamResultInfo() {
        return List.of(OlympiadResultInfo.of(red, redDamage));
    }

    @Override
    protected List<OlympiadResultInfo> getBlueTeamResultInfo() {
        return List.of(OlympiadResultInfo.of(blue, blueDamage));
    }

    @Override
    protected void onDamage(Player attacker, Player target, double damage) {
        if(attacker == red && target == blue) {
            redDamage += damage;
        } else if(attacker == blue && target == red) {
            blueDamage += damage;
        }
    }

}
