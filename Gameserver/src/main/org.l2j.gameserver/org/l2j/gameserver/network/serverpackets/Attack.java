package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Hit;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Attack extends ServerPacket {
    private final int _attackerObjId;
    private final Location _attackerLoc;
    private final Location _targetLoc;
    private final List<Hit> _hits = new ArrayList<>();
    private int hitsWithShots = 0;

    public Attack(Creature attacker, Creature target) {
        _attackerObjId = attacker.getObjectId();
        _attackerLoc = new Location(attacker);
        _targetLoc = new Location(target);
    }

    /**
     * Adds hit to the attack (Attacks such as dual dagger/sword/fist has two hits)
     *
     * @param hit
     */
    public void addHit(Hit hit) {
        _hits.add(hit);
        if(hit.isShotUsed()) {
            hitsWithShots++;
        }
    }

    public List<Hit> getHits() {
        return _hits;
    }

    /**
     * @return {@code true} if current attack contains at least 1 hit.
     */
    public boolean hasHits() {
        return !_hits.isEmpty();
    }

    public boolean isShotUsed() {
        return _hits.stream().anyMatch(Hit::isShotUsed);
    }

    public int getHitsWithSoulshotCount() {
        return hitsWithShots;
    }

    @Override
    public void writeImpl(GameClient client) {
        final Iterator<Hit> it = _hits.iterator();
        final Hit firstHit = it.next();

        writeId(ServerPacketId.ATTACK);

        writeInt(_attackerObjId);
        writeInt(firstHit.getTargetId());
        writeInt(0x00); // Soulshot substitute
        writeInt(firstHit.getDamage());
        writeInt(firstHit.getFlags());
        writeInt(firstHit.getGrade());
        writeInt(_attackerLoc.getX());
        writeInt(_attackerLoc.getY());
        writeInt(_attackerLoc.getZ());

        writeShort((short) (_hits.size() - 1));
        while (it.hasNext()) {
            writeHit(it.next());
        }

        writeInt(_targetLoc.getX());
        writeInt(_targetLoc.getY());
        writeInt(_targetLoc.getZ());
    }

    private void writeHit(Hit hit) {
        writeInt(hit.getTargetId());
        writeInt(hit.getDamage());
        writeInt(hit.getFlags());
        writeInt(hit.getGrade()); // GOD
    }
}
