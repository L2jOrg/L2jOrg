package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;

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
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Party party = activeChar.getParty();
        if (party != null) {
            party.answerLootChangeRequest(activeChar, _answer == 1);
        }
    }
}
