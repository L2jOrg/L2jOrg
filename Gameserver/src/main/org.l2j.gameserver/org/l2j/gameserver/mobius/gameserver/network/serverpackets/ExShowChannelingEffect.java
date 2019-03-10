package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExShowChannelingEffect extends AbstractItemPacket {
    private final L2Character _caster;
    private final L2Character _target;
    private final int _state;

    public ExShowChannelingEffect(L2Character caster, L2Character target, int state) {
        _caster = caster;
        _target = target;
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_CHANNELING_EFFECT.writeId(packet);
        packet.putInt(_caster.getObjectId());
        packet.putInt(_target.getObjectId());
        packet.putInt(_state);
    }
}
