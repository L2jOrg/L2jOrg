package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.enums.SubclassType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sdw
 */
public class ExSubjobInfo extends IClientOutgoingPacket {
    private final int _currClassId;
    private final int _currRace;
    private final int _type;
    private final List<SubInfo> _subs;

    public ExSubjobInfo(L2PcInstance player, SubclassInfoType type) {
        _currClassId = player.getClassId().getId();
        _currRace = player.getRace().ordinal();
        _type = type.ordinal();

        _subs = new ArrayList<>();
        _subs.add(0, new SubInfo(player));

        for (SubClass sub : player.getSubClasses().values()) {
            _subs.add(new SubInfo(sub));
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SUBJOB_INFO.writeId(packet);

        packet.put((byte) _type);
        packet.putInt(_currClassId);
        packet.putInt(_currRace);
        packet.putInt(_subs.size());
        for (SubInfo sub : _subs) {
            packet.putInt(sub.getIndex());
            packet.putInt(sub.getClassId());
            packet.putInt(sub.getLevel());
            packet.put((byte) sub.getType());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 18 + _subs.size() * 13;
    }

    private final class SubInfo {
        private final int _index;
        private final int _classId;
        private final int _level;
        private final int _type;

        public SubInfo(SubClass sub) {
            _index = sub.getClassIndex();
            _classId = sub.getClassId();
            _level = sub.getLevel();
            _type = sub.isDualClass() ? SubclassType.DUALCLASS.ordinal() : SubclassType.SUBCLASS.ordinal();
        }

        public SubInfo(L2PcInstance player) {
            _index = 0;
            _classId = player.getBaseClass();
            _level = player.getStat().getBaseLevel();
            _type = SubclassType.BASECLASS.ordinal();
        }

        public int getIndex() {
            return _index;
        }

        public int getClassId() {
            return _classId;
        }

        public int getLevel() {
            return _level;
        }

        public int getType() {
            return _type;
        }
    }
}