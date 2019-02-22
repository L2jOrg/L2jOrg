package org.l2j.scripts.npc.model.residences.clanhall;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 13:19/31.03.2011
 */
public class DoormanInstance extends org.l2j.scripts.npc.model.residences.DoormanInstance
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
