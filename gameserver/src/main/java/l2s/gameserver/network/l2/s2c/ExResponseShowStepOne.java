package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.PetitionGroupHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.petition.PetitionMainGroup;
import l2s.gameserver.utils.Language;

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
	protected void writeImpl()
	{
		Collection<PetitionMainGroup> petitionGroups = PetitionGroupHolder.getInstance().getPetitionGroups();
		writeD(petitionGroups.size());
		for(PetitionMainGroup group : petitionGroups)
		{
			writeC(group.getId());
			writeS(group.getName(_language));
		}
	}
}