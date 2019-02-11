package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.data.xml.holder.PetitionGroupHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Language;

/**
 * @author VISTALL
 */
public class ExResponseShowStepOne extends L2GameServerPacket
{
	private Language _language;

	public ExResponseShowStepOne(Player player)
	{
		_language = player.getLanguage();
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		Collection<PetitionMainGroup> petitionGroups = PetitionGroupHolder.getInstance().getPetitionGroups();
		buffer.putInt(petitionGroups.size());
		for(PetitionMainGroup group : petitionGroups)
		{
			buffer.put((byte)group.getId());
			writeString(group.getName(_language), buffer);
		}
	}
}