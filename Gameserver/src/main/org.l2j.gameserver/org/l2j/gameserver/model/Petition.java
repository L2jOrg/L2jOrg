package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.PetitionState;
import org.l2j.gameserver.enums.PetitionType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.PetitionVotePacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Petition
 *
 * @author xban1x
 */
public final class Petition {
    private final long _submitTime = System.currentTimeMillis();
    private final int _id;
    private final PetitionType _type;
    private final String _content;
    private final Collection<CreatureSay> _messageLog = ConcurrentHashMap.newKeySet();
    private final Player _petitioner;
    private PetitionState _state = PetitionState.PENDING;
    private Player _responder;

    public Petition(Player petitioner, String petitionText, int petitionType) {
        _id = IdFactory.getInstance().getNextId();
        _type = PetitionType.values()[--petitionType];
        _content = petitionText;
        _petitioner = petitioner;
    }

    public boolean addLogMessage(CreatureSay cs) {
        return _messageLog.add(cs);
    }

    public Collection<CreatureSay> getLogMessages() {
        return _messageLog;
    }

    public boolean endPetitionConsultation(PetitionState endState) {
        setState(endState);

        if ((_responder != null) && _responder.isOnline()) {
            if (endState == PetitionState.RESPONDER_REJECT) {
                _petitioner.sendMessage("Your petition was rejected. Please try again later.");
            } else {
                // Ending petition consultation with <Player>.
                SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PETITION_CONSULTATION_WITH_C1_HAS_ENDED);
                sm.addString(_petitioner.getName());
                _responder.sendPacket(sm);

                if (endState == PetitionState.PETITIONER_CANCEL) {
                    // Receipt No. <ID> petition cancelled.
                    sm = SystemMessage.getSystemMessage(SystemMessageId.RECEIPT_NO_S1_PETITION_CANCELLED);
                    sm.addInt(_id);
                    _responder.sendPacket(sm);
                }
            }
        }

        // End petition consultation and inform them, if they are still online. And if petitioner is online, enable Evaluation button
        if ((_petitioner != null) && _petitioner.isOnline()) {
            _petitioner.sendPacket(SystemMessageId.THIS_ENDS_THE_GM_PETITION_CONSULTATION_NPLEASE_GIVE_US_FEEDBACK_ON_THE_PETITION_SERVICE);
            _petitioner.sendPacket(PetitionVotePacket.STATIC_PACKET);
        }

        PetitionManager.getInstance().getCompletedPetitions().put(getId(), this);
        return PetitionManager.getInstance().getPendingPetitions().remove(getId()) != null;
    }

    public String getContent() {
        return _content;
    }

    public int getId() {
        return _id;
    }

    public Player getPetitioner() {
        return _petitioner;
    }

    public Player getResponder() {
        return _responder;
    }

    public void setResponder(Player respondingAdmin) {
        if (_responder != null) {
            return;
        }

        _responder = respondingAdmin;
    }

    public long getSubmitTime() {
        return _submitTime;
    }

    public PetitionState getState() {
        return _state;
    }

    public void setState(PetitionState state) {
        _state = state;
    }

    public String getTypeAsString() {
        return _type.toString().replace("_", " ");
    }

    public void sendPetitionerPacket(ServerPacket responsePacket) {
        if ((_petitioner == null) || !_petitioner.isOnline()) {
            // Allows petitioners to see the results of their petition when
            // they log back into the game.

            // endPetitionConsultation(PetitionState.Petitioner_Missing);
            return;
        }

        _petitioner.sendPacket(responsePacket);
    }

    public void sendResponderPacket(ServerPacket responsePacket) {
        if ((_responder == null) || !_responder.isOnline()) {
            endPetitionConsultation(PetitionState.RESPONDER_MISSING);
            return;
        }

        _responder.sendPacket(responsePacket);
    }
}
