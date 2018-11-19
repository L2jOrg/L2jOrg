package l2s.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.base.MultiSellEntry;

public class MultiSellListContainer
{
	public static enum MultisellType
	{
		NORMAL,
		CHANCED;
	}

	private int _listId;
	private boolean _showall = true;
	private boolean keep_enchanted = false;
	private boolean is_dutyfree = false;
	private boolean nokey = false;
	private List<MultiSellEntry> entries = new ArrayList<MultiSellEntry>();
	private MultisellType _type = MultisellType.NORMAL;

	public void setListId(int listId)
	{
		_listId = listId;
	}

	public int getListId()
	{
		return _listId;
	}

	public void setShowAll(boolean bool)
	{
		_showall = bool;
	}

	public boolean isShowAll()
	{
		return _showall;
	}

	public void setNoTax(boolean bool)
	{
		is_dutyfree = bool;
	}

	public boolean isNoTax()
	{
		return is_dutyfree;
	}

	public void setNoKey(boolean bool)
	{
		nokey = bool;
	}

	public boolean isNoKey()
	{
		return nokey;
	}

	public void setKeepEnchant(boolean bool)
	{
		keep_enchanted = bool;
	}

	public boolean isKeepEnchant()
	{
		return keep_enchanted;
	}

	public void setType(MultisellType val)
	{
		_type = val;
	}

	public MultisellType getType()
	{
		return _type;
	}

	public void addEntry(MultiSellEntry e)
	{
		entries.add(e);
	}

	public List<MultiSellEntry> getEntries()
	{
		return entries;
	}

	public boolean isEmpty()
	{
		return entries.isEmpty();
	}
}