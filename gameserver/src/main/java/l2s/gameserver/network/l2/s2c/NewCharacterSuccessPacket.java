package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.PlayerTemplateHolder;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.templates.player.PlayerTemplate;

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
		writeD(_chars.size());

		for(ClassId temp : _chars)
		{
			/*На оффе статты атрибутов у М и Ж одинаковы.*/
			PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(temp.getRace(), temp, Sex.MALE);
			writeD(temp.getRace().ordinal());
			writeD(temp.getId());
			writeD(0x46);
			writeD(template.getBaseSTR());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseDEX());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseCON());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseINT());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseWIT());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseMEN());
			writeD(0x0a);
		}
	}
}