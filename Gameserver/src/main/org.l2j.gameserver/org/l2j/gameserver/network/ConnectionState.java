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
    IN_GAME
}
