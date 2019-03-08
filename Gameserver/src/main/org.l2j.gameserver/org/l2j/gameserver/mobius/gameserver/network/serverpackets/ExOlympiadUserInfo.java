package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author godson
 */
public class ExOlympiadUserInfo implements IClientOutgoingPacket
{
    private final L2PcInstance _player;
    private Participant _par = null;
    private int _curHp;
    private int _maxHp;
    private int _curCp;
    private int _maxCp;

    public ExOlympiadUserInfo(L2PcInstance player)
    {
        _player = player;
        if (_player != null)
        {
            _curHp = (int) _player.getCurrentHp();
            _maxHp = _player.getMaxHp();
            _curCp = (int) _player.getCurrentCp();
            _maxCp = _player.getMaxCp();
        }
        else
        {
            _curHp = 0;
            _maxHp = 100;
            _curCp = 0;
            _maxCp = 100;
        }
    }

    public ExOlympiadUserInfo(Participant par)
    {
        _par = par;
        _player = par.getPlayer();
        if (_player != null)
        {
            _curHp = (int) _player.getCurrentHp();
            _maxHp = _player.getMaxHp();
            _curCp = (int) _player.getCurrentCp();
            _maxCp = _player.getMaxCp();
        }
        else
        {
            _curHp = 0;
            _maxHp = 100;
            _curCp = 0;
            _maxCp = 100;
        }
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_OLYMPIAD_USER_INFO.writeId(packet);

        if (_player != null)
        {
            packet.writeC(_player.getOlympiadSide());
            packet.writeD(_player.getObjectId());
            packet.writeS(_player.getName());
            packet.writeD(_player.getClassId().getId());
        }
        else
        {
            packet.writeC(_par.getSide());
            packet.writeD(_par.getObjectId());
            packet.writeS(_par.getName());
            packet.writeD(_par.getBaseClass());
        }

        packet.writeD(_curHp);
        packet.writeD(_maxHp);
        packet.writeD(_curCp);
        packet.writeD(_maxCp);
        return true;
    }
}
