/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author UnAfraid, mrTJO
 * @author JoeAlisson
 */
public class ContactList implements Iterable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactList.class);

    private final Player player;
    private final Set<String> contacts = ConcurrentHashMap.newKeySet();

    public ContactList(Player player) {
        this.player = player;
    }

    public void restore() {
        getDAO(PlayerDAO.class).findContacts(player.getObjectId(), this::restore);
    }

    private void restore(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                var contactId = resultSet.getInt(1);
                String contactName = PlayerNameTable.getInstance().getNameById(contactId);
                if (isNull(contactName)) {
                    continue;
                }
                contacts.add(contactName);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public boolean add(String name) {
        if (verifyContact(name)) {
            return false;
        }

        final int contactId = PlayerNameTable.getInstance().getIdByName(name);

        if (contactId < 1) {
            player.sendPacket(getSystemMessage(SystemMessageId.THE_NAME_S1_DOESN_T_EXIST_PLEASE_TRY_ANOTHER_NAME).addString(name));
            return false;
        }

        getDAO(PlayerDAO.class).addContact(player.getObjectId(), contactId);
        player.sendPacket(getSystemMessage(SystemMessageId.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST).addString(name));
        return true;
    }

    private boolean verifyContact(String name) {
        var verified = true;

        if (contacts.contains(name)) {
            player.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
            verified = false;
        } else if (player.getName().equals(name)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOUR_OWN_NAME);
            verified = false;
        } else if (contacts.size() >= 100) {
            player.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED_YOU_CANNOT_REGISTER_ANY_MORE);
            verified = false;
        }
        return verified;
    }

    public void remove(String name) {
        if (!contacts.contains(name)) {
            player.sendPacket(SystemMessageId.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
            return;
        }

        final int contactId = PlayerNameTable.getInstance().getIdByName(name);
         if (contactId < 1) {
             player.sendPacket(getSystemMessage(SystemMessageId.THE_NAME_S1_DOESN_T_EXIST_PLEASE_TRY_ANOTHER_NAME).addString(name));
            return;
        }

        contacts.remove(name);

        getDAO(PlayerDAO.class).deleteContact(player.getObjectId(), contactId);
        player.sendPacket(getSystemMessage(SystemMessageId.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST).addString(name));
    }

    public int size() {
        return contacts.size();
    }

    @Override
    public Iterator<String> iterator() {
        return contacts.iterator();
    }
}
