package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.PlayerTemplateHolder;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.templates.player.PlayerTemplate;

public class NewCharacterSuccessPacket extends L2GameServerPacket
{
	private List<ClassId> _chars = new ArrayList<ClassId>();

	public NewCharacterSuccessPacket()
	{
		for(ClassId classId : ClassId.VALUES)
		{
			if(classId.isOfLevel(ClassLevel.NONE))
				_chars.add(classId);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_chars.size());

		for(ClassId temp : _chars)
		{
			/*На оффе статты атрибутов у М и Ж одинаковы.*/
			PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(temp.getRace(), temp, Sex.MALE);
			writeInt(temp.getRace().ordinal());
			writeInt(temp.getId());
			writeInt(0x46);
			writeInt(template.getBaseSTR());
			writeInt(0x0a);
			writeInt(0x46);
			writeInt(template.getBaseDEX());
			writeInt(0x0a);
			writeInt(0x46);
			writeInt(template.getBaseCON());
			writeInt(0x0a);
			writeInt(0x46);
			writeInt(template.getBaseINT());
			writeInt(0x0a);
			writeInt(0x46);
			writeInt(template.getBaseWIT());
			writeInt(0x0a);
			writeInt(0x46);
			writeInt(template.getBaseMEN());
			writeInt(0x0a);
		}
	}
}