package npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.instances.MerchantInstance;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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
