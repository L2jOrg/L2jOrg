package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

public class AuthRequest extends SendablePacket {

	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		var serverSettings = Configurator.getSettings(ServerSettings.class);
		buffer.put((byte) 0x00);
		buffer.put((byte) serverSettings.serverId());
		buffer.put((byte) 0x00); // ACCEPT ALTERNATE ID
		writeString(serverSettings.internalAddress(), buffer);
		writeString(serverSettings.externalAddress(), buffer);
		buffer.putShort(serverSettings.port());
		buffer.putInt(serverSettings.type());
		buffer.put(serverSettings.ageLimit());
		buffer.put((byte) (serverSettings.isGMOnly() ? 0x01 : 0x00));
		buffer.put((byte) (serverSettings.isShowingBrackets() ? 0x01 : 0x00));
		buffer.put((byte) (serverSettings.isPvP() ? 0x01 : 0x00));
		buffer.putInt(serverSettings.maximumOnlineUsers());
	}
}