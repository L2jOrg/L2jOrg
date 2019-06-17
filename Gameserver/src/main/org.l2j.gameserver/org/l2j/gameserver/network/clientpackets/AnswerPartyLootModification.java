package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author JIV
 */
public class AnswerPartyLootModification extends ClientPacket {
    public int _answer;

    @Override
    public void readImpl() {
        _answer = readInt();
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
