package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author godson
 */
public class ExOlympiadUserInfo extends ServerPacket {
    private final L2PcInstance _player;
    private Participant _par = null;
    private int _curHp;
    private int _maxHp;
    private int _curCp;
    private int _maxCp;

    public ExOlympiadUserInfo(L2PcInstance player) {
        _player = player;
        if (_player != null) {
            _curHp = (int) _player.getCurrentHp();
            _maxHp = _player.getMaxHp();
            _curCp = (int) _player.getCurrentCp();
            _maxCp = _player.getMaxCp();
        } else {
            _curHp = 0;
            _maxHp = 100;
            _curCp = 0;
            _maxCp = 100;
        }
    }

    public ExOlympiadUserInfo(Participant par) {
        _par = par;
        _player = par.getPlayer();
        if (_player != null) {
            _curHp = (int) _player.getCurrentHp();
            _maxHp = _player.getMaxHp();
            _curCp = (int) _player.getCurrentCp();
            _maxCp = _player.getMaxCp();
        } else {
            _curHp = 0;
            _maxHp = 100;
            _curCp = 0;
            _maxCp = 100;
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_OLYMPIAD_USER_INFO);

        if (_player != null) {
            writeByte((byte) _player.getOlympiadSide());
            writeInt(_player.getObjectId());
            writeString(_player.getName());
            writeInt(_player.getClassId().getId());
        } else {
            writeByte((byte) _par.getSide());
            writeInt(_par.getObjectId());
            writeString(_par.getName());
            writeInt(_par.getBaseClass());
        }

        writeInt(_curHp);
        writeInt(_maxHp);
        writeInt(_curCp);
        writeInt(_maxCp);
    }

}
