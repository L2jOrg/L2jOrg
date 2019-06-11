package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public final class FlyToLocation extends IClientOutgoingPacket {
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

    public FlyToLocation(L2Character cha, int destX, int destY, int destZ, FlyType type) {
        _chaObjId = cha.getObjectId();
        _chaX = cha.getX();
        _chaY = cha.getY();
        _chaZ = cha.getZ();
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _type = type;
    }

    public FlyToLocation(L2Character cha, int destX, int destY, int destZ, FlyType type, int flySpeed, int flyDelay, int animationSpeed) {
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

    public FlyToLocation(L2Character cha, ILocational dest, FlyType type) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type);
    }

    public FlyToLocation(L2Character cha, ILocational dest, FlyType type, int flySpeed, int flyDelay, int animationSpeed) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type, flySpeed, flyDelay, animationSpeed);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.FLY_TO_LOCATION);

        writeInt(_chaObjId);
        writeInt(_destX);
        writeInt(_destY);
        writeInt(_destZ);
        writeInt(_chaX);
        writeInt(_chaY);
        writeInt(_chaZ);
        writeInt(_type.ordinal());
        writeInt(_flySpeed);
        writeInt(_flyDelay);
        writeInt(_animationSpeed);
    }


    public enum FlyType {
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
}
