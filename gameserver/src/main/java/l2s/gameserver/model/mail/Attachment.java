package l2s.gameserver.model.mail;

import l2s.gameserver.model.items.ItemInstance;

public class Attachment
{
	private int messageId;

	private ItemInstance item;
	private Mail mail;

	public int getMessageId()
	{
		return messageId;
	}

	public void setMessageId(int messageId)
	{
		this.messageId = messageId;
	}

	public ItemInstance getItem()
	{
		return item;
	}

	public void setItem(ItemInstance item)
	{
		this.item = item;
	}

	public Mail getMail()
	{
		return mail;
	}

	public void setMail(Mail mail)
	{
		this.mail = mail;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if(o == null)
			return false;
		if(o.getClass() != this.getClass())
			return false;
		return ((Attachment) o).getItem() == getItem();
	}

	@Override     
	public int hashCode()
	{
		return 12 * getItem().hashCode() + 11270;
	}
}