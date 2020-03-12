package handlers.usercommandhandlers;

import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.data.ClanData;
import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.contains;
import static org.l2j.commons.util.Util.isNotEmpty;

/**
 * Clan War Start, Under Attack List, War List user commands.
 * @author Tempy
 * @author JoeAlisson
 */
public class ClanWarsList implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = { 88,  89,  90 };

    @Override
    public boolean useUserCommand(int id, Player player) {

        if (!contains(COMMAND_IDS, id)) {
            return false;
        }

        final Clan clan = player.getClan();
        if (isNull(clan)) {
            player.sendPacket(SystemMessageId.NOT_JOINED_IN_ANY_CLAN);
            return false;
        }

        switch (id) {
            case 88 -> sendAttackList(player);
            case 89 -> sendUnderAttackList(player);
            default -> sendWarList(player);
        }
        return true;
    }

    private void sendWarList(Player player) {
        player.sendPacket(SystemMessageId.CLAN_WAR_LIST);
        getDAO(ClanDAO.class).findWarList(player.getClanId()).forEach(clanData -> sendClanInfo(player, clanData));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }

    private void sendUnderAttackList(Player player) {
        player.sendPacket(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU);
        getDAO(ClanDAO.class).findUnderAttackList(player.getClanId()).forEach(clanData -> sendClanInfo(player, clanData));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }

    private void sendAttackList(Player player) {
        player.sendPacket(SystemMessageId.CLANS_YOU_VE_DECLARED_WAR_ON);
        getDAO(ClanDAO.class).findAttackList(player.getClanId()).forEach(clanData -> sendClanInfo(player, clanData));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }

    private void sendClanInfo(Player player, ClanData clanData) {
        SystemMessage message;
        if(isNotEmpty(clanData.getAllyName())) {
            message = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_ALLIANCE).addString(clanData.getName()).addString(clanData.getAllyName());
        } else {
            message =SystemMessage.getSystemMessage(SystemMessageId.S1_NO_ALLIANCE_EXISTS).addString(clanData.getName());
        }
        player.sendPacket(message);
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
