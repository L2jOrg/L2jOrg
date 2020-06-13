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

import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestBlock extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBlock.class);
    private static final int BLOCK = 0;
    private static final int UNBLOCK = 1;
    private static final int BLOCKLIST = 2;
    private static final int ALLBLOCK = 3;
    private static final int ALLUNBLOCK = 4;

    private String _name;
    private Integer _type;

    @Override
    public void readImpl() {
        _type = readInt(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock
        if ((_type == BLOCK) || (_type == UNBLOCK)) {
            _name = readString();
        }
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        final int targetId = PlayerNameTable.getInstance().getIdByName(_name);
        final int targetAL = PlayerNameTable.getInstance().getAccessLevelById(targetId);

        if (activeChar == null) {
            return;
        }

        switch (_type) {
            case BLOCK:
            case UNBLOCK: {
                // can't use block/unblock for locating invisible characters
                if (targetId <= 0) {
                    // Incorrect player name.
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
                    return;
                }

                if (targetAL > 0) {
                    // Cannot block a GM character.
                    activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
                    return;
                }

                if (activeChar.getObjectId() == targetId) {
                    return;
                }

                if (_type == BLOCK) {
                    BlockList.addToBlockList(activeChar, targetId);
                } else {
                    BlockList.removeFromBlockList(activeChar, targetId);
                }
                break;
            }
            case BLOCKLIST: {
                BlockList.sendListToOwner(activeChar);
                break;
            }
            case ALLBLOCK: {
                activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
                BlockList.setBlockAll(activeChar, true);
                break;
            }
            case ALLUNBLOCK: {
                activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
                BlockList.setBlockAll(activeChar, false);
                break;
            }
            default: {
                LOGGER.info("Unknown 0xA9 block type: " + _type);
            }
        }
    }
}
