package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static org.l2j.gameserver.util.GameUtils.isPet;

/**
 * This class ...
 *
 * @version $Revision: 1.5.2.3.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusUpdate extends ServerPacket {
    private final Summon _summon;
    private int _maxFed;
    private int _curFed;

    public PetStatusUpdate(Summon summon) {
        _summon = summon;
        if (isPet(_summon)) {
            final Pet pet = (Pet) _summon;
            _curFed = pet.getCurrentFed(); // how fed it is
            _maxFed = pet.getMaxFed(); // max fed it can be
        } else if (_summon.isServitor()) {
            final Servitor sum = (Servitor) _summon;
            _curFed = sum.getLifeTimeRemaining();
            _maxFed = sum.getLifeTime();
        }
    }

    @Override
    public void writeImpl(GameClient client) {
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
        writeLong(_summon.getStats().getExp());
        writeLong(_summon.getExpForThisLevel()); // 0% absolute value
        writeLong(_summon.getExpForNextLevel()); // 100% absolute value
        writeInt(0x01); // TODO: Find me!
    }

}
