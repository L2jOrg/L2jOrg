package org.l2j.gameserver.network;

/**
 * @author Nos
 */
public enum ConnectionState {
    CONNECTED,
    DISCONNECTED,
    CLOSING,
    AUTHENTICATED,
    JOINING_GAME,
    IN_GAME;

    public static final ConnectionState[] JOINING_GAME_AND_IN_GAME = new ConnectionState[]
            {
                    ConnectionState.JOINING_GAME,
                    ConnectionState.IN_GAME
            };
}


