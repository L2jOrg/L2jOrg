package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExShowChannelingEffect extends ServerPacket {
    private final L2Character _caster;
    private final L2Character _target;
    private final int _state;

    public ExShowChannelingEffect(L2Character caster, L2Character target, int state) {
        _caster = caster;
        _target = target;
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_CHANNELING_EFFECT);
        writeInt(_caster.getObjectId());
        writeInt(_target.getObjectId());
        writeInt(_state);
    }

}
