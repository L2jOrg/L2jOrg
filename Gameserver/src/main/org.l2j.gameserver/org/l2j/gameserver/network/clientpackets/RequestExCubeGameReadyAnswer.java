package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Format: chddd d: Arena d: Answer
 *
 * @author mrTJO
 */
public final class RequestExCubeGameReadyAnswer extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExCubeGameReadyAnswer.class);
    private int _arena;
    private int _answer;

    @Override
    public void readImpl(ByteBuffer packet) {
        // client sends -1,0,1,2 for arena parameter
        _arena = packet.getInt() + 1;
        // client sends 1 if clicked confirm on not clicked, 0 if clicked cancel
        _answer = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
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
