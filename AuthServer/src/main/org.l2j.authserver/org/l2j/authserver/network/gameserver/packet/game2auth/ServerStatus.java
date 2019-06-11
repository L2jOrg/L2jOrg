package org.l2j.authserver.network.gameserver.packet.game2auth;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;

import static java.util.Objects.nonNull;

public class ServerStatus extends GameserverReadablePacket {

	private static final int SERVER_LIST_STATUS = 0x01;
	private static final int SERVER_LIST_CLOCK = 0x02;
	private static final int SERVER_LIST_SQUARE_BRACKET = 0x03;
	private static final int MAX_PLAYERS = 0x04;
	private static final int TEST_SERVER = 0x05;
	private static final int SERVER_LIST_TYPE = 0x06;
	
	public static final int STATUS_AUTO = 0x00;
	public static final int STATUS_GOOD = 0x01;
	public static final int STATUS_NORMAL = 0x02;
	public static final int STATUS_FULL = 0x03;
	public static final int STATUS_DOWN = 0x04;
	public static final int STATUS_GM_ONLY = 0x05;
	
	private static final int ON = 0x01;
    IntIntMap status;

	@Override
	protected void readImpl()  {
        int size = readInt();
		status = new HashIntIntMap(size);
        for (int i = 0; i < size; i++) {
            status.put(readInt(), readInt());
        }
	}

	@Override
	protected void runImpl()  {
		final var gameServerInfo = client.getGameServerInfo();
		if (nonNull(gameServerInfo)) {
		    status.forEach((type, value) -> {
                switch (type) {
                    case SERVER_LIST_STATUS:
                        gameServerInfo.setStatus(value);
                        break;
                    case SERVER_LIST_CLOCK:
                        gameServerInfo.setShowingClock(value == ON);
                        break;
                    case SERVER_LIST_SQUARE_BRACKET:
                        gameServerInfo.setShowingBrackets(value == ON);
                        break;
                    case TEST_SERVER:
                        gameServerInfo.setTestServer(value == ON);
                        break;
                    case MAX_PLAYERS:
                        gameServerInfo.setMaxPlayers(value);
                        break;
                    case SERVER_LIST_TYPE:
                        gameServerInfo.setServerType(value);
                }
            });
		}
	}
}