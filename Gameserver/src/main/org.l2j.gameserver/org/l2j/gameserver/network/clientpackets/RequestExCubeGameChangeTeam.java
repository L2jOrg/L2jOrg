package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: chdd d: Arena d: Team
 *
 * @author mrTJO
 */
public final class RequestExCubeGameChangeTeam extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExCubeGameChangeTeam.class);
    private int _arena;
    private int _team;

    @Override
    public void readImpl() {
        // client sends -1,0,1,2 for arena parameter
        _arena = readInt() + 1;
        _team = readInt();
    }

    @Override
    public void runImpl() {
        // do not remove players after start
        if (HandysBlockCheckerManager.getInstance().arenaIsBeingUsed(_arena)) {
            return;
        }
        final Player player = client.getPlayer();

        switch (_team) {
            case 0:
            case 1: {
                // Change Player Team
                HandysBlockCheckerManager.getInstance().changePlayerToTeam(player, _arena, _team);
                break;
            }
            case -1: {
                // Remove Player (me)
            }
            {
                final int team = HandysBlockCheckerManager.getInstance().getHolder(_arena).getPlayerTeam(player);
                // client sends two times this packet if click on exit
                // client did not send this packet on restart
                if (team > -1) {
                    HandysBlockCheckerManager.getInstance().removePlayer(player, _arena, team);
                }
                break;
            }
            default: {
                LOGGER.warn("Wrong Cube Game Team ID: " + _team);
                break;
            }
        }
    }
}
