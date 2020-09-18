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
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMode;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadSpelledInfo;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadUserInfo;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
class OlympiadClasslessMatch extends OlympiadMatch {

    private Player red;
    private Player blue;

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
    protected void teleportPlayers(Location first, Location second, Instance arena) {
        red.setIsInOlympiadMode(true);
        red.setOlympiadSide(1);
        blue.setIsInOlympiadMode(true);
        blue.setOlympiadSide(2);
        red.sendPacket(new ExOlympiadMode(1));
        blue.sendPacket(new ExOlympiadMode(2));
        red.teleToLocation(first, arena);
        blue.teleToLocation(second, arena);
    }

    @Override
    public void broadcastMessage(SystemMessageId messageId) {
        red.sendPacket(messageId);
        blue.sendPacket(messageId);
    }

    @Override
    public void broadcastPacket(ServerPacket packet) {
        red.sendPacket(packet);
        blue.sendPacket(packet);
    }

    @Override
    protected void sendOlympiadUserInfo() {
        broadcastPacket(new ExOlympiadUserInfo(red));
        broadcastPacket(new ExOlympiadUserInfo(blue));
    }

    @Override
    protected void sendOlympiadSpellInfo() {
        var spellInfo = new ExOlympiadSpelledInfo(red);
        red.getEffectList().getEffects().forEach(spellInfo::addSkill);
        broadcastPacket(spellInfo);

        spellInfo = new ExOlympiadSpelledInfo(blue);
        blue.getEffectList().getEffects().forEach(spellInfo::addSkill);
    }
}
