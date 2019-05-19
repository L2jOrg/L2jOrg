package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.settings.GeneralSettings;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

public class ExRequestBlockListForAD extends IClientIncomingPacket {

    private static final String ADENA_ADS = "ADENA_ADS";
    private String name;
    private String message;

    @Override
    protected void readImpl(ByteBuffer packet) throws Exception {
        name = readString(packet);
        message = readString(packet);
        // next is Always Adena Sale ADS text
    }

    @Override
    protected void runImpl() {
        // simple check if has adena on message. Should be some others checks or not check at all?!
        if(!message.toLowerCase().contains("adena")) {
            return;
        }

        final int reportedId = CharNameTable.getInstance().getIdByName(name);
        BlockList.addToBlockList(client.getActiveChar(), reportedId);
        var reported = L2World.getInstance().getPlayer(reportedId);
        if(nonNull(reported)) {
            var variables = reported.getVariables();
            var reportedCount = variables.getInt("ADENA_ADS", 0) + 1;
            variables.set(ADENA_ADS,  reportedCount);
            variables.storeMe();

            if(reportedCount >= getSettings(GeneralSettings.class).banChatAdenaAdsReportCount()) {
                var manager = PunishmentManager.getInstance();
                if(manager.hasPunishment(reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN)) {
                    manager.stopPunishment(reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
                }
                manager.startPunishment(new PunishmentTask(0, reportedId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, Instant.now().plus(14, ChronoUnit.HOURS).toEpochMilli(), "Chat banned bot report", "system", false));
            }
        }
    }
}
