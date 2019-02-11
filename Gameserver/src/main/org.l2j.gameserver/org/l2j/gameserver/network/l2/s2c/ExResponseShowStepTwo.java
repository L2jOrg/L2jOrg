package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import org.l2j.gameserver.model.petition.PetitionSubGroup;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Language;

/**
 * @author VISTALL
 */
public class ExResponseShowStepTwo extends L2GameServerPacket
{
	private Language _language;
	private PetitionMainGroup _petitionMainGroup;

	public ExResponseShowStepTwo(Player player, PetitionMainGroup gr)
	{
		_language = player.getLanguage();
		_petitionMainGroup = gr;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		Collection<PetitionSubGroup> subGroups = _petitionMainGroup.getSubGroups();
		buffer.putInt(subGroups.size());
		writeString(_petitionMainGroup.getDescription(_language), buffer);
		for(PetitionSubGroup g : subGroups)
		{
			buffer.put((byte)g.getId());
			writeString(g.getName(_language), buffer);
		}
	}
}