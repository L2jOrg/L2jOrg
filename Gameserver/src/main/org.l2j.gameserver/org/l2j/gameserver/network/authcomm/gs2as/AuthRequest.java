package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

public class AuthRequest extends SendablePacket {

	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		var serverSettings = Configurator.getSettings(ServerSettings.class);
		buffer.put((byte) 0x00);
		buffer.put((byte) serverSettings.serverId());
		buffer.put((byte) (serverSettings.acceptAlternativeId() ? 0x01 : 0x00));
		buffer.putInt(serverSettings.type());
		buffer.putInt(serverSettings.maximumOnlineUsers());
		buffer.put(serverSettings.ageLimit());

		buffer.put((byte) (serverSettings.isShowingBrackets() ? 0x01 : 0x00));
		buffer.put((byte) (serverSettings.isPvP() ? 0x01 : 0x00));

		var hosts = Config.GAME_SERVER_HOSTS.size();
		buffer.putShort((short) hosts);

		for (int i = 0; i < Config.GAME_SERVER_HOSTS.size(); i++) {
			writeString(Config.GAME_SERVER_HOSTS.get(i), buffer);
			writeString(Config.GAME_SERVER_SUBNETS.get(i), buffer);
		}
		buffer.putShort(serverSettings.port());
	}
}