package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

public class RequestGiveNickName extends IClientIncomingPacket {
    private String _target;
    private String _title;

    @Override
    public void readImpl() {
        _target = readString();
        _title = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // Noblesse can bestow a title to themselves
        if (activeChar.isNoble() && _target.equalsIgnoreCase(activeChar.getName())) {
            activeChar.setTitle(_title);
            client.sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
            activeChar.broadcastTitleInfo();
        } else {
            // Can the player change/give a title?
            if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_GIVE_TITLE)) {
                client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }

            if (activeChar.getClan().getLevel() < 3) {
                client.sendPacket(SystemMessageId.A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE);
                return;
            }

            final L2ClanMember member1 = activeChar.getClan().getClanMember(_target);
            if (member1 != null) {
                final L2PcInstance member = member1.getPlayerInstance();
                if (member != null) {
                    // is target from the same clan?
                    member.setTitle(_title);
                    member.sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
                    member.broadcastTitleInfo();
                } else {
                    client.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
                }
            } else {
                client.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            }
        }
    }
}
