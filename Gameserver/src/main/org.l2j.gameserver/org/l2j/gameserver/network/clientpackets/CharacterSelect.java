package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.data.xml.impl.SecondaryAuthData;
import org.l2j.gameserver.data.xml.impl.VipData;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSelect;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.CharSelected;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 *
 * @version $Revision: 1.5.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterSelect extends ClientPacket {
    protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    // cd
    private int _charSlot;

    @SuppressWarnings("unused")
    private int _unk1; // new in C4
    @SuppressWarnings("unused")
    private int _unk2; // new in C4
    @SuppressWarnings("unused")
    private int _unk3; // new in C4
    @SuppressWarnings("unused")
    private int _unk4; // new in C4

    @Override
    public void readImpl() {
        _charSlot = readInt();
        _unk1 = readShort();
        _unk2 = readInt();
        _unk3 = readInt();
        _unk4 = readInt();

    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterSelect")) {
            return;
        }

        if (SecondaryAuthData.getInstance().isEnabled() && !client.getSecondaryAuth().isAuthed()) {
            client.getSecondaryAuth().openDialog();
            return;
        }

        // We should always be able to acquire the lock
        // But if we can't lock then nothing should be done (i.e. repeated packet)
        if (client.getActiveCharLock().tryLock()) {
            try {
                // should always be null
                // but if not then this is repeated packet and nothing should be done here
                if (client.getActiveChar() == null) {
                    final CharSelectInfoPackage info = client.getCharSelection(_charSlot);
                    if (info == null) {
                        return;
                    }

                    // Banned?
                    if (PunishmentManager.getInstance().hasPunishment(info.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN) || PunishmentManager.getInstance().hasPunishment(client.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.BAN) || PunishmentManager.getInstance().hasPunishment(client.getHostAddress(), PunishmentAffect.IP, PunishmentType.BAN)) {
                        client.close(ServerClose.STATIC_PACKET);
                        return;
                    }

                    // Selected character is banned (compatibility with previous versions).
                    if (info.getAccessLevel() < 0) {
                        client.close(ServerClose.STATIC_PACKET);
                        return;
                    }

                    if ((Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddClient(AntiFeedManager.GAME_ID, client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)) {
                        final NpcHtmlMessage msg = new NpcHtmlMessage();
                        msg.setFile(null, "data/html/mods/IPRestriction.htm");
                        msg.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)));
                        client.sendPacket(msg);
                        return;
                    }

                    // load up character from disk
                    final Player cha = client.load(_charSlot);
                    if (cha == null) {
                        return; // handled in L2GameClient
                    }

                    CharNameTable.getInstance().addName(cha);

                    cha.setClient(client);
                    client.setActiveChar(cha);
                    cha.setOnlineStatus(true, true);
                    cha.setVipTier(VipData.getInstance().getVipTier(cha));

                    final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerSelect(cha, cha.getObjectId(), cha.getName(), client), Containers.Players(), TerminateReturn.class);
                    if ((terminate != null) && terminate.terminate()) {
                        Disconnection.of(cha).defaultSequence(false);
                        return;
                    }

                    client.setConnectionState(ConnectionState.JOINING_GAME);
                    client.sendPacket(new CharSelected(cha, client.getSessionId().getGameServerSessionId()));
                }
            } finally {
                client.getActiveCharLock().unlock();
            }

            LOGGER_ACCOUNTING.info("Logged in, " + client);
        }
    }
}
