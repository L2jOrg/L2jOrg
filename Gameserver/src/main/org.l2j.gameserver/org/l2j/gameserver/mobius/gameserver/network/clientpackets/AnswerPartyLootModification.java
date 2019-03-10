package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class AnswerPartyLootModification extends IClientIncomingPacket {
    public int _answer;

    @Override
    public void readImpl(ByteBuffer packet) {
        _answer = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Party party = activeChar.getParty();
        if (party != null) {
            party.answerLootChangeRequest(activeChar, _answer == 1);
        }
    }
}
