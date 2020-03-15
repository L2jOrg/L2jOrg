package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.model.olympiad.*;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
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
        writeId(ServerPacketId.EX_RECEIVE_OLYMPIAD);

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
