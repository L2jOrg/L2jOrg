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
package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.model.olympiad.*;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrTJO
 */
public class ExOlympiadMatchList extends ServerPacket {
    private final List<OlympiadGameTask> _games = new ArrayList<>();

    public ExOlympiadMatchList() {
        OlympiadGameTask task;
        for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); i++) {
            task = OlympiadGameManager.getInstance().getOlympiadTask(i);
            if (task != null) {
                if (!task.isGameStarted() || task.isBattleFinished()) {
                    continue; // initial or finished state not shown
                }
                _games.add(task);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_GFX_OLYMPIAD);

        writeInt(0x00); // Type 0 = Match List, 1 = Match Result

        writeInt(_games.size());
        writeInt(0x00);

        for (OlympiadGameTask curGame : _games) {
            final AbstractOlympiadGame game = curGame.getGame();
            if (game != null) {
                writeInt(game.getStadiumId()); // Stadium Id (Arena 1 = 0)

                if (game instanceof OlympiadGameNonClassed) {
                    writeInt(1);
                } else if (game instanceof OlympiadGameClassed) {
                    writeInt(2);
                } else {
                    writeInt(0);
                }

                writeInt(curGame.isRunning() ? 0x02 : 0x01); // (1 = Standby, 2 = Playing)
                writeString(game.getPlayerNames()[0]); // Player 1 Name
                writeString(game.getPlayerNames()[1]); // Player 2 Name
            }
        }
    }

}
