package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExMagicAttackInfo extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_MAGIC_ATTACK_INFO);

        writeInt(_caster);
        writeInt(_target);
        writeInt(_type);
    }

}