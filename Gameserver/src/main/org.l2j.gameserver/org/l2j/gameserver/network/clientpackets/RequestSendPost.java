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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExNoticePostSent;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 * @author JoeAlisson
 */
public final class RequestSendPost extends ClientPacket {
    private static final int BATCH_LENGTH = 12; // length of the one item

    private static final int MAX_RECV_LENGTH = 16;
    private static final int MAX_SUBJ_LENGTH = 128;
    private static final int MAX_TEXT_LENGTH = 512;
    private static final int MAX_ATTACHMENTS = 8;
    private static final int INBOX_SIZE = 240;
    private static final int OUTBOX_SIZE = 240;

    private String _receiver;
    private boolean _isCod;
    private String _subject;
    private String _text;
    private List<ItemHolder> items = Collections.emptyList();
    private long _reqAdena;

    public RequestSendPost() {
    }

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _receiver = readString();
        _isCod = readInt() != 0;
        _subject = readString();
        _text = readString();

        final int attachCount = readInt();
        if ((attachCount < 0) || (attachCount > Config.MAX_ITEM_IN_PACKET) || (((attachCount * BATCH_LENGTH) + 8) != available())) {
            throw new InvalidDataPacketException();
        }

        if (attachCount > 0) {
            items = new ArrayList<>(attachCount);
            for (int i = 0; i < attachCount; i++) {
                final int objectId = readInt();
                final long count = readLong();
                if ((objectId < 1) || (count < 0)) {
                    items = null;
                    throw new InvalidDataPacketException();
                }
                items.add(new ItemHolder(objectId, count));
            }
        }

        _reqAdena = readLong();
    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail()) {
            return;
        }

        final Player player = client.getPlayer();

        if (!Config.ALLOW_ATTACHMENTS) {
            items = Collections.emptyList();
            _isCod = false;
            _reqAdena = 0;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE) && !items.isEmpty()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
            return;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_SEND_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (_receiver.length() > MAX_RECV_LENGTH) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED);
            return;
        }

        if (_subject.length() > MAX_SUBJ_LENGTH) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }

        if (_text.length() > MAX_TEXT_LENGTH) {
            // not found message for this
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }

        if (items.size() > MAX_ATTACHMENTS) {
            player.sendPacket(SystemMessageId.ITEM_SELECTION_IS_POSSIBLE_UP_TO_8);
            return;
        }

        if ((_reqAdena < 0) || (_reqAdena > Inventory.MAX_ADENA)) {
            return;
        }

        if (_isCod) {
            if (_reqAdena == 0) {
                player.sendPacket(SystemMessageId.WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL);
                return;
            }
            if (items.isEmpty()) {
                player.sendPacket(SystemMessageId.IT_S_A_PAYMENT_REQUEST_TRANSACTION_PLEASE_ATTACH_THE_ITEM);
                return;
            }
        }

        final int receiverId = PlayerNameTable.getInstance().getIdByName(_receiver);
        if (receiverId <= 0) {
            player.sendPacket(SystemMessageId.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return;
        }

        if (receiverId == player.getObjectId()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
            return;
        }

        final int level = PlayerNameTable.getInstance().getAccessLevelById(receiverId);
        final AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(level);

        if ((accessLevel != null) && accessLevel.isGm() && !player.getAccessLevel().isGm()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_MESSAGE_TO_C1_DID_NOT_REACH_ITS_RECIPIENT_YOU_CANNOT_SEND_MAIL_TO_THE_GM_STAFF);
            sm.addString(_receiver);
            player.sendPacket(sm);
            return;
        }

        if (player.isJailed() && ((Config.JAIL_DISABLE_TRANSACTION && !items.isEmpty()) || getSettings(GeneralSettings.class).disableChatInJail())) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (BlockList.isInBlockList(receiverId, player.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1);
            sm.addString(_receiver);
            player.sendPacket(sm);
            return;
        }

        if (MailEngine.getInstance().getOutboxSize(player.getObjectId()) >= OUTBOX_SIZE) {
            player.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        if (MailEngine.getInstance().getInboxSize(receiverId) >= INBOX_SIZE) {
            player.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        if (!client.getFloodProtectors().getSendMail().tryPerformAction("sendmail")) {
            player.sendPacket(SystemMessageId.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        if (MailEngine.getInstance().sendMail(player, receiverId, _isCod, _subject, _text, _reqAdena, items)) {
            player.sendPacket(ExNoticePostSent.valueOf(true));
            player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_SENT);
        }
    }
}
