package org.l2j.gameserver.mobius.gameserver.network.serverpackets;


import org.l2j.gameserver.mobius.gameserver.enums.FenceState;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2FenceInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author HoridoJoho / FBIagent
 */
public class ExColosseumFenceInfo implements IClientOutgoingPacket
{
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _width;
    private final int _length;
    private final int _clientState;

    public ExColosseumFenceInfo(L2FenceInstance fence)
    {
        this(fence.getObjectId(), fence.getX(), fence.getY(), fence.getZ(), fence.getWidth(), fence.getLength(), fence.getState());
    }

    public ExColosseumFenceInfo(int objId, double x, double y, double z, int width, int length, FenceState state)
    {
        _objId = objId;
        _x = (int) x;
        _y = (int) y;
        _z = (int) z;
        _width = width;
        _length = length;
        _clientState = state.getClientId();
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_COLOSSEUM_FENCE_INFO.writeId(packet);

        packet.writeD(_objId);
        packet.writeD(_clientState);
        packet.writeD(_x);
        packet.writeD(_y);
        packet.writeD(_z);
        packet.writeD(_width);
        packet.writeD(_length);

        return true;
    }
}