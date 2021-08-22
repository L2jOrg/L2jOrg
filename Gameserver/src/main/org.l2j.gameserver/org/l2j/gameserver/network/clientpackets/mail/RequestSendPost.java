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
package org.l2j.gameserver.network.clientpackets.mail;

import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.settings.CharacterSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

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

    private String receiver;
    private boolean isPayment;
    private String subject;
    private String text;
    private List<ItemHolder> items = Collections.emptyList();
    private long reqAdena;

    public RequestSendPost() {
    }

    @Override
    public void readImpl() throws InvalidDataPacketException {
        receiver = readString();
        isPayment = readInt() != 0;
        subject = readString();
        text = readString();

        final int attachCount = readInt();
        if (attachCount < 0 || attachCount > CharacterSettings.maxItemInPacket() || attachCount * BATCH_LENGTH + 8 != available()) {
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

        reqAdena = readLong();
    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getSendMail().tryPerformAction("sendmail")) {
            client.sendPacket(SystemMessageId.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        if(receiver.length() > MAX_RECV_LENGTH) {
            client.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED);
            return;
        }

        if (subject.length() > MAX_SUBJ_LENGTH) {
            client.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }

        if (text.length() > MAX_TEXT_LENGTH) {
            // not found message for this
            client.sendPacket(getSystemMessage(SystemMessageId.S1).addString("The allowed length for content exceeded."));
            return;
        }

        if (items.size() > MAX_ATTACHMENTS) {
            client.sendPacket(SystemMessageId.ITEM_SELECTION_IS_POSSIBLE_UP_TO_8);
            return;
        }

        MailEngine.getInstance().sendMail(client.getPlayer(), receiver, isPayment, subject, text, reqAdena, items);
    }
}
