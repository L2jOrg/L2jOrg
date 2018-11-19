package npc.model.residences.clanhall;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 13:19/31.03.2011
 */
public class DoormanInstance extends npc.model.residences.DoormanInstance
{
	private static final long serialVersionUID = 1L;

	public DoormanInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public int getOpenPriv()
	{
		return Clan.CP_CH_ENTRY_EXIT;
	}

	@Override
	public Residence getResidence()
	{
		return getClanHall();
	}
}
