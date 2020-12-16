package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.DonationDAO;
import org.l2j.gameserver.data.database.data.DonationData;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
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
        LOGGER.info("Using voice handler: " + command + " params: " + params);

        if(false /*Config.ENABLE_DONATION*/) {
            activeChar.sendMessage("Donation system not activated!");
            return true;
        }

        if(command.equals("donate")) {
            String html = HtmCache.getInstance().getHtm(null, "data/html/mods/Donate.html");
            if (html == null) {
                html = "<html><body><br><br><center><font color=LEVEL>404:</font> File Not Found</center></body></html>";
            }
            activeChar.sendPacket(new NpcHtmlMessage(html));
            return true;
        } else if(command.startsWith("claim")) {
            StringTokenizer args = new StringTokenizer(params == null ? "" : params);
            String email = "";

            if(args.hasMoreTokens()) {
                email = args.nextToken();
                LOGGER.info("Claiming donations for email: " + email);

                List<DonationData> donations = getDAO(DonationDAO.class).unClaimedDonations(email);

                if(donations.size() > 0) {
                    donations.forEach(donationData -> {
                        LOGGER.info("Claiming donation: " + donationData.getEmail() + " " + donationData.getPaymentId() + " " + donationData.getPayerId());
                        giveItems(activeChar, 29520, donationData.getAmount());
                        getDAO(DonationDAO.class).claimDonation(donationData.getPaymentId(), donationData.getPayerId(), donationData.getEmail());
                    });
                } else {
                    activeChar.sendMessage("There is no donations to retrieve.");
                }
                return true;
            } else {
                activeChar.sendMessage("There is no donations to retrieve.");
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }

}
