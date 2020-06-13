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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.Hit;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Attack extends ServerPacket {
    private final int attackerObjId;
    private final Location attackerLoc;
    private final Location targetLoc;
    private final List<Hit> hits = new ArrayList<>();
    private final int additionalSoulshot;
    private int hitsWithShots = 0;

    public Attack(Creature attacker, Creature target) {
        attackerObjId = attacker.getObjectId();
        attackerLoc = attacker.getLocation();
        targetLoc = target.getLocation();
        additionalSoulshot = Util.zeroIfNullOrElse(attacker.getActingPlayer(), Player::getAdditionalSoulshot);
    }

    public void addHit(Hit hit) {
        hits.add(hit);
        if(hit.isShotUsed()) {
            hitsWithShots++;
        }
    }

    public List<Hit> getHits() {
        return hits;
    }

    /**
     * @return {@code true} if current attack contains at least 1 hit.
     */
    public boolean hasHits() {
        return !hits.isEmpty();
    }

    public boolean isShotUsed() {
        return hits.stream().anyMatch(Hit::isShotUsed);
    }

    public int getHitsWithSoulshotCount() {
        return hitsWithShots;
    }

    @Override
    public void writeImpl(GameClient client) {
        final Iterator<Hit> it = hits.iterator();
        final Hit firstHit = it.next();

        writeId(ServerPacketId.ATTACK);

        writeInt(attackerObjId);
        writeInt(firstHit.getTargetId());
        writeInt(additionalSoulshot);
        writeInt(firstHit.getDamage());
        writeInt(firstHit.getFlags());
        writeInt(firstHit.getGrade());
        writeInt(attackerLoc.getX());
        writeInt(attackerLoc.getY());
        writeInt(attackerLoc.getZ());

        writeShort(hits.size() - 1);
        while (it.hasNext()) {
            writeHit(it.next());
        }

        writeInt(targetLoc.getX());
        writeInt(targetLoc.getY());
        writeInt(targetLoc.getZ());
    }

    private void writeHit(Hit hit) {
        writeInt(hit.getTargetId());
        writeInt(hit.getDamage());
        writeInt(hit.getFlags());
        writeInt(hit.getGrade());
    }
}
