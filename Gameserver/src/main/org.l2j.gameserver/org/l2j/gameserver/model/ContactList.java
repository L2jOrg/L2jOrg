/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * TODO: System messages:<br>
 * ADD: 3223: The previous name is being registered. Please try again later.<br>
 * DEL 3219: $s1 was successfully deleted from your Contact List.<br>
 * DEL 3217: The name is not currently registered.
 *
 * @author UnAfraid, mrTJO
 */
public class ContactList {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactList.class);
    private static final String QUERY_ADD = "INSERT INTO character_contacts (charId, contactId) VALUES (?, ?)";
    private static final String QUERY_REMOVE = "DELETE FROM character_contacts WHERE charId = ? and contactId = ?";
    private static final String QUERY_LOAD = "SELECT contactId FROM character_contacts WHERE charId = ?";
    private final Player activeChar;
    private final Set<String> _contacts = ConcurrentHashMap.newKeySet();

    public ContactList(Player player) {
        activeChar = player;
        restore();
    }

    public void restore() {
        _contacts.clear();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(QUERY_LOAD)) {
            statement.setInt(1, activeChar.getObjectId());
            try (ResultSet rset = statement.executeQuery()) {
                int contactId;
                String contactName;
                while (rset.next()) {
                    contactId = rset.getInt(1);
                    contactName = PlayerNameTable.getInstance().getNameById(contactId);
                    if ((contactName == null) || contactName.equals(activeChar.getName()) || (contactId == activeChar.getObjectId())) {
                        continue;
                    }

                    _contacts.add(contactName);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error found in " + activeChar.getName() + "'s ContactsList: " + e.getMessage(), e);
        }
    }

    public boolean add(String name) {
        SystemMessage sm;

        final int contactId = PlayerNameTable.getInstance().getIdByName(name);
        if (_contacts.contains(name)) {
            activeChar.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
            return false;
        } else if (activeChar.getName().equals(name)) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOUR_OWN_NAME);
            return false;
        } else if (_contacts.size() >= 100) {
            activeChar.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED_YOU_CANNOT_REGISTER_ANY_MORE);
            return false;
        } else if (contactId < 1) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_NAME_S1_DOESN_T_EXIST_PLEASE_TRY_ANOTHER_NAME);
            sm.addString(name);
            activeChar.sendPacket(sm);
            return false;
        } else {
            for (String contactName : _contacts) {
                if (contactName.equalsIgnoreCase(name)) {
                    activeChar.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
                    return false;
                }
            }
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(QUERY_ADD)) {
            statement.setInt(1, activeChar.getObjectId());
            statement.setInt(2, contactId);
            statement.execute();

            _contacts.add(name);

            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST);
            sm.addString(name);
            activeChar.sendPacket(sm);
        } catch (Exception e) {
            LOGGER.warn("Error found in " + activeChar.getName() + "'s ContactsList: " + e.getMessage(), e);
        }
        return true;
    }

    public void remove(String name) {
        final int contactId = PlayerNameTable.getInstance().getIdByName(name);

        if (!_contacts.contains(name)) {
            activeChar.sendPacket(SystemMessageId.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
            return;
        } else if (contactId < 1) {
            // TODO: Message?
            return;
        }

        _contacts.remove(name);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(QUERY_REMOVE)) {
            statement.setInt(1, activeChar.getObjectId());
            statement.setInt(2, contactId);
            statement.execute();

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST);
            sm.addString(name);
            activeChar.sendPacket(sm);
        } catch (Exception e) {
            LOGGER.warn("Error found in " + activeChar.getName() + "'s ContactsList: " + e.getMessage(), e);
        }
    }

    public Set<String> getAllContacts() {
        return _contacts;
    }
}
