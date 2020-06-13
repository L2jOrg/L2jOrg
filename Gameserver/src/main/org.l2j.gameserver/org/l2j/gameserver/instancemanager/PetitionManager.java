/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.PetitionState;
import org.l2j.gameserver.model.Petition;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Petition Manager
 *
 * @author Tempy
 */
public final class PetitionManager {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PetitionManager.class);

    private final Map<Integer, Petition> _pendingPetitions;
    private final Map<Integer, Petition> _completedPetitions;

    private PetitionManager() {
        _pendingPetitions = new HashMap<>();
        _completedPetitions = new HashMap<>();
    }

    public void clearCompletedPetitions() {
        final int numPetitions = _pendingPetitions.size();

        _completedPetitions.clear();
        LOGGER.info(getClass().getSimpleName() + ": Completed petition data cleared. " + numPetitions + " petition(s) removed.");
    }

    public void clearPendingPetitions() {
        final int numPetitions = _pendingPetitions.size();

        _pendingPetitions.clear();
        LOGGER.info(getClass().getSimpleName() + ": Pending petition queue cleared. " + numPetitions + " petition(s) removed.");
    }

    public boolean acceptPetition(Player respondingAdmin, int petitionId) {
        if (!isValidPetition(petitionId)) {
            return false;
        }

        final Petition currPetition = _pendingPetitions.get(petitionId);

        if (currPetition.getResponder() != null) {
            return false;
        }

        currPetition.setResponder(respondingAdmin);
        currPetition.setState(PetitionState.IN_PROCESS);

        // Petition application accepted. (Send to Petitioner)
        currPetition.sendPetitionerPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_APPLICATION_ACCEPTED));

        // Petition application accepted. Reciept No. is <ID>
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED_NRECEIPT_NO_IS_S1);
        sm.addInt(currPetition.getId());
        currPetition.sendResponderPacket(sm);

        // Petition consultation with <Player> underway.
        sm = SystemMessage.getSystemMessage(SystemMessageId.STARTING_PETITION_CONSULTATION_WITH_C1);
        sm.addString(currPetition.getPetitioner().getName());
        currPetition.sendResponderPacket(sm);

        // Set responder name on petitioner instance
        currPetition.getPetitioner().setLastPetitionGmName(currPetition.getResponder().getName());
        return true;
    }

    public boolean cancelActivePetition(Player player) {
        for (Petition currPetition : _pendingPetitions.values()) {
            if ((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == player.getObjectId())) {
                return (currPetition.endPetitionConsultation(PetitionState.PETITIONER_CANCEL));
            }

            if ((currPetition.getResponder() != null) && (currPetition.getResponder().getObjectId() == player.getObjectId())) {
                return (currPetition.endPetitionConsultation(PetitionState.RESPONDER_CANCEL));
            }
        }

        return false;
    }

    public void checkPetitionMessages(Player petitioner) {
        if (petitioner != null) {
            for (Petition currPetition : _pendingPetitions.values()) {
                if (currPetition == null) {
                    continue;
                }

                if ((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == petitioner.getObjectId())) {
                    for (CreatureSay logMessage : currPetition.getLogMessages()) {
                        petitioner.sendPacket(logMessage);
                    }

                    return;
                }
            }
        }
    }

    public boolean endActivePetition(Player player) {
        if (!player.isGM()) {
            return false;
        }

        for (Petition currPetition : _pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }

            if ((currPetition.getResponder() != null) && (currPetition.getResponder().getObjectId() == player.getObjectId())) {
                return (currPetition.endPetitionConsultation(PetitionState.COMPLETED));
            }
        }

        return false;
    }

    public Map<Integer, Petition> getCompletedPetitions() {
        return _completedPetitions;
    }

    public Map<Integer, Petition> getPendingPetitions() {
        return _pendingPetitions;
    }

    public int getPendingPetitionCount() {
        return _pendingPetitions.size();
    }

    public int getPlayerTotalPetitionCount(Player player) {
        if (player == null) {
            return 0;
        }

        int petitionCount = 0;

        for (Petition currPetition : _pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }

            if ((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == player.getObjectId())) {
                petitionCount++;
            }
        }

        for (Petition currPetition : _completedPetitions.values()) {
            if (currPetition == null) {
                continue;
            }

            if ((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == player.getObjectId())) {
                petitionCount++;
            }
        }

        return petitionCount;
    }

    public boolean isPetitionInProcess() {
        for (Petition currPetition : _pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }

            if (currPetition.getState() == PetitionState.IN_PROCESS) {
                return true;
            }
        }

        return false;
    }

    public boolean isPetitionInProcess(int petitionId) {
        if (!isValidPetition(petitionId)) {
            return false;
        }

        final Petition currPetition = _pendingPetitions.get(petitionId);
        return (currPetition.getState() == PetitionState.IN_PROCESS);
    }

    public boolean isPlayerInConsultation(Player player) {
        if (player != null) {
            for (Petition currPetition : _pendingPetitions.values()) {
                if (currPetition == null) {
                    continue;
                }

                if (currPetition.getState() != PetitionState.IN_PROCESS) {
                    continue;
                }

                if (((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == player.getObjectId())) || ((currPetition.getResponder() != null) && (currPetition.getResponder().getObjectId() == player.getObjectId()))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isPetitioningAllowed() {
        return Config.PETITIONING_ALLOWED;
    }

    public boolean isPlayerPetitionPending(Player petitioner) {
        if (petitioner != null) {
            for (Petition currPetition : _pendingPetitions.values()) {
                if (currPetition == null) {
                    continue;
                }

                if ((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == petitioner.getObjectId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValidPetition(int petitionId) {
        return _pendingPetitions.containsKey(petitionId);
    }

    public boolean rejectPetition(Player respondingAdmin, int petitionId) {
        if (!isValidPetition(petitionId)) {
            return false;
        }

        final Petition currPetition = _pendingPetitions.get(petitionId);

        if (currPetition.getResponder() != null) {
            return false;
        }

        currPetition.setResponder(respondingAdmin);
        return (currPetition.endPetitionConsultation(PetitionState.RESPONDER_REJECT));
    }

    public boolean sendActivePetitionMessage(Player player, String messageText) {
        // if (!isPlayerInConsultation(player))
        // return false;

        CreatureSay cs;

        for (Petition currPetition : _pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }

            if ((currPetition.getPetitioner() != null) && (currPetition.getPetitioner().getObjectId() == player.getObjectId())) {
                cs = new CreatureSay(player, ChatType.PETITION_PLAYER,messageText);
                currPetition.addLogMessage(cs);

                currPetition.sendResponderPacket(cs);
                currPetition.sendPetitionerPacket(cs);
                return true;
            }

            if ((currPetition.getResponder() != null) && (currPetition.getResponder().getObjectId() == player.getObjectId())) {
                cs = new CreatureSay(player, ChatType.PETITION_GM, messageText);
                currPetition.addLogMessage(cs);

                currPetition.sendResponderPacket(cs);
                currPetition.sendPetitionerPacket(cs);
                return true;
            }
        }

        return false;
    }

    public void sendPendingPetitionList(Player activeChar) {
        final StringBuilder htmlContent = new StringBuilder(600 + (_pendingPetitions.size() * 300));
        htmlContent.append("<html><body><center><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center>Petition Menu</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_admin7\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br><table width=\"270\"><tr><td><table width=\"270\"><tr><td><button value=\"Reset\" action=\"bypass -h admin_reset_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td align=right><button value=\"Refresh\" action=\"bypass -h admin_view_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br></td></tr>");

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (_pendingPetitions.isEmpty()) {
            htmlContent.append("<tr><td>There are no currently pending petitions.</td></tr>");
        } else {
            htmlContent.append("<tr><td><font color=\"LEVEL\">Current Petitions:</font><br></td></tr>");
        }

        boolean color = true;
        int petcount = 0;
        for (Petition currPetition : _pendingPetitions.values()) {
            if (currPetition == null) {
                continue;
            }

            htmlContent.append("<tr><td width=\"270\"><table width=\"270\" cellpadding=\"2\" bgcolor=" + (color ? "131210" : "444444") + "><tr><td width=\"130\">" + dateFormat.format(new Date(currPetition.getSubmitTime())));
            htmlContent.append("</td><td width=\"140\" align=right><font color=\"" + (currPetition.getPetitioner().isOnline() ? "00FF00" : "999999") + "\">" + currPetition.getPetitioner().getName() + "</font></td></tr>");
            htmlContent.append("<tr><td width=\"130\">");
            if (currPetition.getState() != PetitionState.IN_PROCESS) {
                htmlContent.append("<table width=\"130\" cellpadding=\"2\"><tr><td><button value=\"View\" action=\"bypass -h admin_view_petition " + currPetition.getId() + "\" width=\"50\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Reject\" action=\"bypass -h admin_reject_petition " + currPetition.getId() + "\" width=\"50\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
            } else {
                htmlContent.append("<font color=\"" + (currPetition.getResponder().isOnline() ? "00FF00" : "999999") + "\">" + currPetition.getResponder().getName() + "</font>");
            }
            htmlContent.append("</td>" + currPetition.getTypeAsString() + "<td width=\"140\" align=right>" + currPetition.getTypeAsString() + "</td></tr></table></td></tr>");
            color = !color;
            petcount++;
            if (petcount > 10) {
                htmlContent.append("<tr><td><font color=\"LEVEL\">There is more pending petition...</font><br></td></tr>");
                break;
            }
        }

        htmlContent.append("</table></center></body></html>");

        final NpcHtmlMessage htmlMsg = new NpcHtmlMessage();
        htmlMsg.setHtml(htmlContent.toString());
        activeChar.sendPacket(htmlMsg);
    }

    public int submitPetition(Player petitioner, String petitionText, int petitionType) {
        // Create a new petition instance and add it to the list of pending petitions.
        final Petition newPetition = new Petition(petitioner, petitionText, petitionType);
        final int newPetitionId = newPetition.getId();
        _pendingPetitions.put(newPetitionId, newPetition);

        // Notify all GMs that a new petition has been submitted.
        final String msgContent = petitioner.getName() + " has submitted a new petition."; // (ID: " + newPetitionId + ").";
        AdminData.getInstance().broadcastToGMs(new CreatureSay(petitioner.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent));

        return newPetitionId;
    }

    public void viewPetition(Player activeChar, int petitionId) {
        if (!activeChar.isGM()) {
            return;
        }

        if (!isValidPetition(petitionId)) {
            return;
        }

        final Petition currPetition = _pendingPetitions.get(petitionId);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/admin/petition.htm");
        html.replace("%petition%", String.valueOf(currPetition.getId()));
        html.replace("%time%", dateFormat.format(new Date(currPetition.getSubmitTime())));
        html.replace("%type%", currPetition.getTypeAsString());
        html.replace("%petitioner%", currPetition.getPetitioner().getName());
        html.replace("%online%", (currPetition.getPetitioner().isOnline() ? "00FF00" : "999999"));
        html.replace("%text%", currPetition.getContent());

        activeChar.sendPacket(html);
    }

    public static PetitionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PetitionManager INSTANCE = new PetitionManager();
    }
}
