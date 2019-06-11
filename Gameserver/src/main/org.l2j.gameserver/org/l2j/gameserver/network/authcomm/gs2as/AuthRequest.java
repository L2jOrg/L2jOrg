package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

public class AuthRequest extends SendablePacket {

	protected void writeImpl(AuthServerClient client) {
		var serverSettings = Configurator.getSettings(ServerSettings.class);
		writeByte((byte) 0x00);
		writeByte((byte) serverSettings.serverId());
		writeByte((byte) (serverSettings.acceptAlternativeId() ? 0x01 : 0x00));
		writeInt(serverSettings.type());
		writeInt(serverSettings.maximumOnlineUsers());
		writeByte(serverSettings.ageLimit());

		writeByte((byte) (serverSettings.isShowingBrackets() ? 0x01 : 0x00));
		writeByte((byte) (serverSettings.isPvP() ? 0x01 : 0x00));

		var hosts = Config.GAME_SERVER_HOSTS.size();
		writeShort((short) hosts);

		for (int i = 0; i < Config.GAME_SERVER_HOSTS.size(); i++) {
			writeString(Config.GAME_SERVER_HOSTS.get(i));
			writeString(Config.GAME_SERVER_SUBNETS.get(i));
		}
		writeShort(serverSettings.port());
	}
}