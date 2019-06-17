package org.l2j.gameserver.network.serverpackets.appearance;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExCuriousHouseMemberUpdate extends ServerPacket {
    public final int _objId;
    public final int _maxHp;
    public final int _maxCp;
    public final int _currentHp;
    public final int _currentCp;

    public ExCuriousHouseMemberUpdate(CeremonyOfChaosMember member) {
        _objId = member.getObjectId();
        final L2PcInstance player = member.getPlayer();
        if (player != null) {
            _maxHp = player.getMaxHp();
            _maxCp = player.getMaxCp();
            _currentHp = (int) player.getCurrentHp();
            _currentCp = (int) player.getCurrentCp();
        } else {
            _maxHp = 0;
            _maxCp = 0;
            _currentHp = 0;
            _currentCp = 0;
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CURIOUS_HOUSE_MEMBER_UPDATE);

        writeInt(_objId);
        writeInt(_maxHp);
        writeInt(_maxCp);
        writeInt(_currentHp);
        writeInt(_currentCp);
    }

}
