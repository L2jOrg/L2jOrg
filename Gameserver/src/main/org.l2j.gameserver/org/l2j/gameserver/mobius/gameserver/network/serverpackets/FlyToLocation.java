package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author KenM
 */
public final class FlyToLocation implements IClientOutgoingPacket
{
    private final int _destX;
    private final int _destY;
    private final int _destZ;
    private final int _chaObjId;
    private final int _chaX;
    private final int _chaY;
    private final int _chaZ;
    private final FlyType _type;
    private int _flySpeed;
    private int _flyDelay;
    private int _animationSpeed;

    public enum FlyType
    {
        THROW_UP,
        THROW_HORIZONTAL,
        DUMMY,
        CHARGE,
        PUSH_HORIZONTAL,
        JUMP_EFFECTED,
        NOT_USED,
        PUSH_DOWN_HORIZONTAL,
        WARP_BACK,
        WARP_FORWARD
    }

    public FlyToLocation(L2Character cha, int destX, int destY, int destZ, FlyType type)
    {
        _chaObjId = cha.getObjectId();
        _chaX = cha.getX();
        _chaY = cha.getY();
        _chaZ = cha.getZ();
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _type = type;
    }

    public FlyToLocation(L2Character cha, int destX, int destY, int destZ, FlyType type, int flySpeed, int flyDelay, int animationSpeed)
    {
        _chaObjId = cha.getObjectId();
        _chaX = cha.getX();
        _chaY = cha.getY();
        _chaZ = cha.getZ();
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _type = type;
        _flySpeed = flySpeed;
        _flyDelay = flyDelay;
        _animationSpeed = animationSpeed;
    }

    public FlyToLocation(L2Character cha, ILocational dest, FlyType type)
    {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type);
    }

    public FlyToLocation(L2Character cha, ILocational dest, FlyType type, int flySpeed, int flyDelay, int animationSpeed)
    {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type, flySpeed, flyDelay, animationSpeed);
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.FLY_TO_LOCATION.writeId(packet);

        packet.writeD(_chaObjId);
        packet.writeD(_destX);
        packet.writeD(_destY);
        packet.writeD(_destZ);
        packet.writeD(_chaX);
        packet.writeD(_chaY);
        packet.writeD(_chaZ);
        packet.writeD(_type.ordinal());
        packet.writeD(_flySpeed);
        packet.writeD(_flyDelay);
        packet.writeD(_animationSpeed);
        return true;
    }
}
