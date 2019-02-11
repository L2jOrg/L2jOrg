package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.c2s.RequestExRequestReceivedPostList;
import io.github.joealisson.mmocore.StaticPacket;

import java.nio.ByteBuffer;

/**
 * Уведомление о получении почты. При нажатии на него клиент отправляет {@link RequestExRequestReceivedPostList}.
 */
@StaticPacket
public class ExNoticePostArrived extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC_TRUE = new ExNoticePostArrived(1);
	public static final L2GameServerPacket STATIC_FALSE = new ExNoticePostArrived(0);

	private int _anim;

	private ExNoticePostArrived(int useAnim) {
		_anim = useAnim;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.putInt(_anim); // 0 - просто показать уведомление, 1 - с красивой анимацией
	}

	@Override
	protected int size(GameClient client) {
		return 9;
	}
}