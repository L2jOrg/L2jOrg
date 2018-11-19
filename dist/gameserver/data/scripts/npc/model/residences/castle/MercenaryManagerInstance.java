package npc.model.residences.castle;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 17:35/13.07.2011
 */
public class MercenaryManagerInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public MercenaryManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}
}
