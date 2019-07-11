package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExShowChannelingEffect extends ServerPacket {
    private final Creature _caster;
    private final Creature _target;
    private final int _state;

    public ExShowChannelingEffect(Creature caster, Creature target, int state) {
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
