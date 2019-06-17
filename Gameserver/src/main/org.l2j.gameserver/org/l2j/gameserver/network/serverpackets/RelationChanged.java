package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Luca Baldi
 */
public final class RelationChanged extends ServerPacket {
    // TODO: Enum
    public static final int RELATION_PARTY1 = 0x00001; // party member
    public static final int RELATION_PARTY2 = 0x00002; // party member
    public static final int RELATION_PARTY3 = 0x00004; // party member
    public static final int RELATION_PARTY4 = 0x00008; // party member (for information, see L2PcInstance.getRelation())
    public static final int RELATION_PARTYLEADER = 0x00010; // true if is party leader
    public static final int RELATION_HAS_PARTY = 0x00020; // true if is in party
    public static final int RELATION_CLAN_MEMBER = 0x00040; // true if is in clan
    public static final int RELATION_LEADER = 0x00080; // true if is clan leader
    public static final int RELATION_CLAN_MATE = 0x00100; // true if is in same clan
    public static final int RELATION_INSIEGE = 0x00200; // true if in siege
    public static final int RELATION_ATTACKER = 0x00400; // true when attacker
    public static final int RELATION_ALLY = 0x00800; // blue siege icon, cannot have if red
    public static final int RELATION_ENEMY = 0x01000; // true when red icon, doesn't matter with blue
    public static final int RELATION_DECLARED_WAR = 0x04000; // single sword
    public static final int RELATION_MUTUAL_WAR = 0x08000; // double swords
    public static final int RELATION_ALLY_MEMBER = 0x10000; // clan is in alliance
    public static final int RELATION_TERRITORY_WAR = 0x80000; // show Territory War icon

    // Masks
    public static final byte SEND_ONE = 0x00;
    public static final byte SEND_DEFAULT = 0x01;
    public static final byte SEND_MULTI = 0x04;
    private final List<Relation> _multi;
    private Relation _singled;
    private byte _mask = 0x00;
    public RelationChanged(L2Playable activeChar, int relation, boolean autoattackable) {
        _mask |= SEND_ONE;

        _singled = new Relation();
        _singled._objId = activeChar.getObjectId();
        _singled._relation = relation;
        _singled._autoAttackable = autoattackable ? 1 : 0;
        _singled._reputation = activeChar.getReputation();
        _singled._pvpFlag = activeChar.getPvpFlag();
        _multi = null;
    }

    public RelationChanged() {
        _mask |= SEND_MULTI;
        _multi = new LinkedList<>();
    }

    public void addRelation(L2Playable activeChar, int relation, boolean autoattackable) {
        if (activeChar.isInvisible()) {
            // throw new IllegalArgumentException("Cannot add invisible character to multi relation packet");
            return;
        }
        final Relation r = new Relation();
        r._objId = activeChar.getObjectId();
        r._relation = relation;
        r._autoAttackable = autoattackable ? 1 : 0;
        r._reputation = activeChar.getReputation();
        r._pvpFlag = activeChar.getPvpFlag();
        _multi.add(r);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RELATION_CHANGED);

        writeByte(_mask);
        if (_multi == null) {
            writeRelation(_singled);
        } else {
            writeShort((short) _multi.size());
            for (Relation r : _multi) {
                writeRelation(r);
            }
        }
    }


    private void writeRelation(Relation relation) {
        writeInt(relation._objId);

        if ((_mask & SEND_DEFAULT) == 0) {
            writeInt(relation._relation);
            writeByte((byte) relation._autoAttackable);
            writeInt(relation._reputation);
            writeByte((byte) relation._pvpFlag);
        }
    }


    protected static class Relation {
        int _objId;
        int _relation;
        int _autoAttackable;
        int _reputation;
        int _pvpFlag;
    }
}
