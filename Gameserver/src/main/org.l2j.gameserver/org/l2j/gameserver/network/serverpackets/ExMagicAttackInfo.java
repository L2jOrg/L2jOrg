package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExMagicAttackInfo extends IClientOutgoingPacket {
    // TODO: Enum
    public static final int CRITICAL = 1;
    public static final int CRITICAL_HEAL = 2;
    public static final int OVERHIT = 3;
    public static final int EVADED = 4;
    public static final int BLOCKED = 5;
    public static final int RESISTED = 6;
    public static final int IMMUNE = 7;
    public static final int IMMUNE2 = 8;

    private final int _caster;
    private final int _target;
    private final int _type;

    public ExMagicAttackInfo(int caster, int target, int type) {
        _caster = caster;
        _target = target;
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MAGIC_ATTACK_INFO.writeId(packet);

        packet.putInt(_caster);
        packet.putInt(_target);
        packet.putInt(_type);
    }
}