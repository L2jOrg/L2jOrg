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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A mother-trees zone Basic type zone for Hp, MP regen
 *
 * @author durgus
 */
public class MotherTreeZone extends Zone {
    private SystemMessage enterMsg;
    private SystemMessage leaveMsg;
    private int mpRegen;
    private int hpRegen;

    private MotherTreeZone(int id) {
        super(id);
    }


    @Override
    protected boolean isAffected(Creature creature) {
        return super.isAffected(creature) && isPlayer(creature);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (creature instanceof Player player) {
            creature.setInsideZone(ZoneType.MOTHER_TREE, true);
            player.sendPacket(enterMsg);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (creature instanceof Player player) {
            player.setInsideZone(ZoneType.MOTHER_TREE, false);
            player.sendPacket(leaveMsg);
        }
    }

    public int getMpRegenBonus() {
        return mpRegen;
    }

    public int getHpRegenBonus() {
        return hpRegen;
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var zone = new MotherTreeZone(id);

            var attr = zoneNode.getAttributes();
            zone.enterMsg = SystemMessage.getSystemMessage(reader.parseInt(attr, "enter-message"));
            zone.leaveMsg = SystemMessage.getSystemMessage(reader.parseInt(attr, "leave-message"));
            zone.hpRegen = reader.parseInt(attr, "regen-hp");
            zone.mpRegen = reader.parseInt(attr, "regen-mp");
            return zone;
        }

        @Override
        public String type() {
            return "mother-tree";
        }
    }
}
