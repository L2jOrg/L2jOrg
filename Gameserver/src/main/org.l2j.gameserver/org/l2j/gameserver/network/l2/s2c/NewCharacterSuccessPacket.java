package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.PlayerTemplateHolder;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.player.PlayerTemplate;
import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class NewCharacterSuccessPacket extends L2GameServerPacket {

	public static final NewCharacterSuccessPacket STATIC = new NewCharacterSuccessPacket();

	private List<ClassId> _chars = new ArrayList<ClassId>();

	private NewCharacterSuccessPacket()
	{
		for(ClassId classId : ClassId.VALUES)
		{
			if(classId.isOfLevel(ClassLevel.NONE))
				_chars.add(classId);
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_chars.size());

		for(ClassId temp : _chars)
		{
			/*На оффе статты атрибутов у М и Ж одинаковы.*/
			PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(temp.getRace(), temp, Sex.MALE);
			buffer.putInt(temp.getRace().ordinal());
			buffer.putInt(temp.getId());
			buffer.putInt(0x46);
			buffer.putInt(template.getBaseSTR());
			buffer.putInt(0x0a);
			buffer.putInt(0x46);
			buffer.putInt(template.getBaseDEX());
			buffer.putInt(0x0a);
			buffer.putInt(0x46);
			buffer.putInt(template.getBaseCON());
			buffer.putInt(0x0a);
			buffer.putInt(0x46);
			buffer.putInt(template.getBaseINT());
			buffer.putInt(0x0a);
			buffer.putInt(0x46);
			buffer.putInt(template.getBaseWIT());
			buffer.putInt(0x0a);
			buffer.putInt(0x46);
			buffer.putInt(template.getBaseMEN());
			buffer.putInt(0x0a);
		}
	}
}