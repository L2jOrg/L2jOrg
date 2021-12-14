/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.DonationDAO;
import org.l2j.gameserver.data.database.data.DonationData;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.CustomFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringTokenizer;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.model.events.AbstractScript.giveItems;

public class Donate implements IVoicedCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Donate.class);

    private static final String[] _voicedCommands = {
            "donate",
            "claim"
    };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String params) {
        if(!CustomFeatures.donateEnabled()) {
            activeChar.sendMessage("Donation system not activated!");
            return true;
        }

        if(command.equals("donate")) {
            donate(activeChar);
            return true;
        } else if(command.startsWith("claim")) {
            return claim(activeChar, params);
        }
        return false;
    }

    private boolean claim(Player activeChar, String params) {
        StringTokenizer args = new StringTokenizer(params == null ? "" : params);
        String email;

        if(args.hasMoreTokens()) {
            email = args.nextToken();
            List<DonationData> donations = getDAO(DonationDAO.class).unClaimedDonations(email);

            if(donations.isEmpty()) {
                activeChar.sendMessage("There is no donations to retrieve.");
                return false;
            }

            for (var donationData : donations) {
                LOGGER.info("Claiming donation: {} {} {}", donationData.getEmail(), donationData.getPaymentId(), donationData.getPayerId());
                giveItems(activeChar, 29520, donationData.getAmount());
                getDAO(DonationDAO.class).claimDonation(donationData.getPaymentId(), donationData.getPayerId(), donationData.getEmail());
            }
        } else {
            activeChar.sendMessage("There is no donations to retrieve.");
            return false;
        }
        return true;
    }

    private void donate(Player activeChar) {
        String html = HtmCache.getInstance().getHtm(null, "data/html/mods/Donate.html");
        if (html == null) {
            html = "<html><body><br><br><center><font color=LEVEL>404:</font> File Not Found</center></body></html>";
        }
        activeChar.sendPacket(new NpcHtmlMessage(html));
    }

    @Override
    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }

}
