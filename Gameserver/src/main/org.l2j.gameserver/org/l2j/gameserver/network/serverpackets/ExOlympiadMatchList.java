package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.olympiad.*;
import org.l2j.gameserver.model.olympiad.*;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrTJO
 */
public class ExOlympiadMatchList extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RECEIVE_OLYMPIAD.writeId(packet);

        packet.putInt(0x00); // Type 0 = Match List, 1 = Match Result

        packet.putInt(_games.size());
        packet.putInt(0x00);

        for (OlympiadGameTask curGame : _games) {
            final AbstractOlympiadGame game = curGame.getGame();
            if (game != null) {
                packet.putInt(game.getStadiumId()); // Stadium Id (Arena 1 = 0)

                if (game instanceof OlympiadGameNonClassed) {
                    packet.putInt(1);
                } else if (game instanceof OlympiadGameClassed) {
                    packet.putInt(2);
                } else {
                    packet.putInt(0);
                }

                packet.putInt(curGame.isRunning() ? 0x02 : 0x01); // (1 = Standby, 2 = Playing)
                writeString(game.getPlayerNames()[0], packet); // Player 1 Name
                writeString(game.getPlayerNames()[1], packet); // Player 2 Name
            }
        }
    }
}
