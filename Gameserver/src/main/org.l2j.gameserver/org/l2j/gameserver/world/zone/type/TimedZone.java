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

import org.l2j.gameserver.engine.timedzone.TimedZoneEngine;
import org.l2j.gameserver.engine.timedzone.TimedZoneInfo;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public class TimedZone extends Zone {

    private static final Attributes DEFAULT_ATTRIBUTES = new Attributes(60, 60, true, 1, 999, false, false, false, ResetCycle.DAILY, Collections.emptyList());

    private final Attributes attributes;

    private TimedZone(int id, Attributes attributes) {
        super(id);
        this.attributes = attributes;
    }

    @Override
    protected void onEnter(Creature creature) {
        if(creature instanceof Player player && !canEnter(player)) {
            creature.teleToLocation(TeleportWhereType.TOWN);
            return;
        }

        creature.setInsideZone(ZoneType.TIMED, true);

        if(!attributes.allowPvP) {
            creature.setInsideZone(ZoneType.PEACE, true);
        }
    }

    private boolean canEnter(Player player) {
        if(player.getLevel() < attributes.minLevel || player.getLevel() > attributes.maxLevel) {
            return false;
        }

        return !attributes.vipOnly || player.getVipTier() >= 1;
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.TIMED, false);

        if(!attributes.allowPvP) {
            creature.setInsideZone(ZoneType.PEACE, false);
        }
    }

    public int requiredItemsAmount() {
        return attributes.items.size();
    }

    public void forEachRequiredItem(Consumer<ItemHolder> action) {
        attributes.items.forEach(action);
    }

    public int getResetCycle() {
        return attributes.resetCycle.ordinal();
    }

    public int getMinLevel() {
        return attributes.minLevel;
    }

    public int getMaxLevel() {
        return attributes.maxLevel;
    }

    public int getTime() {
        return attributes.time;
    }

    public int getRechargeTime() {
        return attributes.rechargeTime;
    }

    public boolean isUserBound() {
        return attributes.userBound;
    }

    public boolean isVipOnly() {
        return attributes.vipOnly;
    }

    public boolean worldInZone() {
        return attributes.worldInZone;
    }

    public int getMaxTime() {
        return attributes.time + attributes.rechargeTime;
    }

    public TimedZoneInfo getPlayerZoneInfo(Player player) {
        return TimedZoneEngine.getInstance().getTimedZoneInfo(player, this);
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var attributes = parseZoneAttributes(zoneNode, reader);
            return new TimedZone(id, attributes);
        }

        private Attributes parseZoneAttributes(Node zoneNode, GameXmlReader reader) {
            for(var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if(node.getNodeName().equals("attributes")) {
                    var attr = node.getAttributes();
                    var time = reader.parseInt(attr, "time") * 60;
                    var timeExtension = reader.parseInt(attr, "recharge-time") * 60;
                    var allowPvP = reader.parseBoolean(attr, "allow-pvp");
                    var minLevel = reader.parseInt(attr, "min-level");
                    var maxLevel = reader.parseInt(attr, "max-level");
                    var userBound = reader.parseBoolean(attr, "user-bound");
                    var vipOnly = reader.parseBoolean(attr, "vip-only");
                    var worldInZone = reader.parseBoolean(attr, "world-in-zone");
                    var resetCycle = reader.parseEnum(attr, ResetCycle.class, "reset-cycle");
                    var items = parseItems(node, reader);
                    return new Attributes(time, timeExtension, allowPvP, minLevel, maxLevel, userBound, vipOnly, worldInZone, resetCycle, items);
                }
            }
            return DEFAULT_ATTRIBUTES;
        }

        private List<ItemHolder> parseItems(Node attrNode, GameXmlReader reader) {
            List<ItemHolder> items = new ArrayList<>(attrNode.getChildNodes().getLength());

            for(var node = attrNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if(node.getNodeName().equals("item")) {
                    items.add(reader.parseItemHolder(node));
                }
            }
            return items;
        }

        @Override
        public String type() {
            return "timed";
        }
    }

    private record Attributes(
            int time,
            int rechargeTime,
            boolean allowPvP,
            int minLevel,
            int maxLevel,
            boolean userBound,
            boolean vipOnly,
            boolean worldInZone,
            ResetCycle resetCycle, List<ItemHolder> items) {
    }

    enum ResetCycle {
        WEEKLY,
        DAILY
    }
}
