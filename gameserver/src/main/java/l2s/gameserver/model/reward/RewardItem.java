package l2s.gameserver.model.reward;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.data.xml.holder.ItemHolder;

public class RewardItem
{
	public final int itemId;
	public long count;

	public RewardItem(int itemId)
	{
		this.itemId = itemId;
		count = 1;
	}

	public boolean isHerb()
	{
		ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
		if(item == null)
			return false;
		return item.isHerb();
	}

	public boolean isAdena()
	{
		ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
		if(item == null)
			return false;
		return item.isAdena();
	}
}