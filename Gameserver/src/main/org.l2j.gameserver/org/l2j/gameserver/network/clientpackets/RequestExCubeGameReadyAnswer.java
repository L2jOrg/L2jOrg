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

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: chddd d: Arena d: Answer
 *
 * @author mrTJO
 */
public final class RequestExCubeGameReadyAnswer extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExCubeGameReadyAnswer.class);
    private int _arena;
    private int _answer;

    @Override
    public void readImpl() {
        // client sends -1,0,1,2 for arena parameter
        _arena = readInt() + 1;
        // client sends 1 if clicked confirm on not clicked, 0 if clicked cancel
        _answer = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        switch (_answer) {
            case 0: {
                // Cancel - Answer No
                break;
            }
            case 1: {
                // OK or Time Over
                HandysBlockCheckerManager.getInstance().increaseArenaVotes(_arena);
                break;
            }
            default: {
                LOGGER.warn("Unknown Cube Game Answer ID: " + _answer);
                break;
            }
        }
    }
}
