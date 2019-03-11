package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Hit;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Attack extends IClientOutgoingPacket {
    private final int _attackerObjId;
    private final Location _attackerLoc;
    private final Location _targetLoc;
    private final List<Hit> _hits = new ArrayList<>();

    /**
     * @param attacker
     * @param target
     */
    public Attack(L2Character attacker, L2Character target) {
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

    /**
     * Writes current hit
     *
     * @param packet
     * @param hit
     */
    private void writeHit(ByteBuffer packet, Hit hit) {
        packet.putInt(hit.getTargetId());
        packet.putInt(hit.getDamage());
        packet.putInt(hit.getFlags());
        packet.putInt(hit.getGrade()); // GOD
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        final Iterator<Hit> it = _hits.iterator();
        final Hit firstHit = it.next();
        OutgoingPackets.ATTACK.writeId(packet);

        packet.putInt(_attackerObjId);
        packet.putInt(firstHit.getTargetId());
        packet.putInt(0x00); // Ertheia Unknown
        packet.putInt(firstHit.getDamage());
        packet.putInt(firstHit.getFlags());
        packet.putInt(firstHit.getGrade()); // GOD
        packet.putInt(_attackerLoc.getX());
        packet.putInt(_attackerLoc.getY());
        packet.putInt(_attackerLoc.getZ());

        packet.putShort((short) (_hits.size() - 1));
        while (it.hasNext()) {
            writeHit(packet, it.next());
        }

        packet.putInt(_targetLoc.getX());
        packet.putInt(_targetLoc.getY());
        packet.putInt(_targetLoc.getZ());
    }
}
