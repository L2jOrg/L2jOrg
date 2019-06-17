package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.actor.instance.L2ServitorInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * This class ...
 *
 * @version $Revision: 1.5.2.3.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusUpdate extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PET_STATUS_UPDATE);

        writeInt(_summon.getSummonType());
        writeInt(_summon.getObjectId());
        writeInt(_summon.getX());
        writeInt(_summon.getY());
        writeInt(_summon.getZ());
        writeString(_summon.getTitle());
        writeInt(_curFed);
        writeInt(_maxFed);
        writeInt((int) _summon.getCurrentHp());
        writeInt(_summon.getMaxHp());
        writeInt((int) _summon.getCurrentMp());
        writeInt(_summon.getMaxMp());
        writeInt(_summon.getLevel());
        writeLong(_summon.getStat().getExp());
        writeLong(_summon.getExpForThisLevel()); // 0% absolute value
        writeLong(_summon.getExpForNextLevel()); // 100% absolute value
        writeInt(0x01); // TODO: Find me!
    }

}
