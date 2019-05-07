package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author godson
 */
public class ExOlympiadUserInfo extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_OLYMPIAD_USER_INFO.writeId(packet);

        if (_player != null) {
            packet.put((byte) _player.getOlympiadSide());
            packet.putInt(_player.getObjectId());
            writeString(_player.getName(), packet);
            packet.putInt(_player.getClassId().getId());
        } else {
            packet.put((byte) _par.getSide());
            packet.putInt(_par.getObjectId());
            writeString(_par.getName(), packet);
            packet.putInt(_par.getBaseClass());
        }

        packet.putInt(_curHp);
        packet.putInt(_maxHp);
        packet.putInt(_curCp);
        packet.putInt(_maxCp);
    }

    @Override
    protected int size(L2GameClient client) {
        return 80;
    }
}
