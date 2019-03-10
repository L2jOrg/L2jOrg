package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2ServitorInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.5.2.3.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusUpdate extends IClientOutgoingPacket {
    private final L2Summon _summon;
    private int _maxFed;
    private int _curFed;

    public PetStatusUpdate(L2Summon summon) {
        _summon = summon;
        if (_summon.isPet()) {
            final L2PetInstance pet = (L2PetInstance) _summon;
            _curFed = pet.getCurrentFed(); // how fed it is
            _maxFed = pet.getMaxFed(); // max fed it can be
        } else if (_summon.isServitor()) {
            final L2ServitorInstance sum = (L2ServitorInstance) _summon;
            _curFed = sum.getLifeTimeRemaining();
            _maxFed = sum.getLifeTime();
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PET_STATUS_UPDATE.writeId(packet);

        packet.putInt(_summon.getSummonType());
        packet.putInt(_summon.getObjectId());
        packet.putInt(_summon.getX());
        packet.putInt(_summon.getY());
        packet.putInt(_summon.getZ());
        writeString(_summon.getTitle(), packet);
        packet.putInt(_curFed);
        packet.putInt(_maxFed);
        packet.putInt((int) _summon.getCurrentHp());
        packet.putInt(_summon.getMaxHp());
        packet.putInt((int) _summon.getCurrentMp());
        packet.putInt(_summon.getMaxMp());
        packet.putInt(_summon.getLevel());
        packet.putLong(_summon.getStat().getExp());
        packet.putLong(_summon.getExpForThisLevel()); // 0% absolute value
        packet.putLong(_summon.getExpForNextLevel()); // 100% absolute value
        packet.putInt(0x01); // TODO: Find me!
    }
}
