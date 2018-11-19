package l2s.gameserver.model.mail;

import java.util.HashSet;
import java.util.Set;

import l2s.commons.dao.JdbcEntity;
import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public class Mail implements JdbcEntity, Comparable<Mail>
{
	public static enum SenderType
	{
		NORMAL, // Письма, отправленные от персонажа персонажу.
		NEWS_INFORMER, // Новости
		NONE, //
		BIRTHDAY, // Письма, отправленные в честь дня рождения персонажа.
		UNKNOWN, //
		SYSTEM, // Системные письма (комиссионка).
		MENTOR, // Письма, отправленные при вознаграждении наставника.
		PRESENT; // Письма, отправленные при подарке из итем-молла.

		public static SenderType[] VALUES = values();
	}

	private static final long serialVersionUID = -8704970972611917153L;

	private static final int MAX_SYSTEM_PARAMS_COUNT = 8;

	public static final int DELETED = 0;
	public static final int READED = 1;
	public static final int REJECTED = 2;

	public static final String COMMISSION_BUY_TOPIC = "CommissionBuyTitle";

	//Mail types (0 - Normal, ... - unk, 5 - commission buy)
	public static final int NORMAL_POST = 0;
	public static final int COMMISSION_POST = 5;

	private static final MailDAO _mailDAO = MailDAO.getInstance();

	private int _postType;
	private int messageId;
	private int senderId;
	private String senderName;
	private int receiverId;
	private String receiverName;
	private int expireTime;
	private String topic;
	private String body;
	private long price;
	private SenderType _type = SenderType.NORMAL;
	private boolean _isUnread;
	private boolean _isReturned = false;
	private Set<ItemInstance> attachments = new HashSet<ItemInstance>();
	private int _systemTopic;
	private int _systemBody;
	private int[] _systemParams = new int[MAX_SYSTEM_PARAMS_COUNT];

	private JdbcEntityState _state = JdbcEntityState.CREATED;

	public int getPostType()
	{
		return _postType;
	}

	public void setPostType(int val)
	{
		_postType = val;
	}

	public int getMessageId()
	{
		return messageId;
	}

	public void setMessageId(int messageId)
	{
		this.messageId = messageId;
	}

	public int getSenderId()
	{
		return senderId;
	}

	public void setSenderId(int senderId)
	{
		this.senderId = senderId;
	}

	public String getSenderName()
	{
		return senderName;
	}

	public void setSenderName(String senderName)
	{
		this.senderName = senderName;
	}

	public int getReceiverId()
	{
		return receiverId;
	}

	public void setReceiverId(int receiverId)
	{
		this.receiverId = receiverId;
	}

	public String getReceiverName()
	{
		return receiverName;
	}

	public void setReceiverName(String receiverName)
	{
		this.receiverName = receiverName;
	}

	public int getExpireTime()
	{
		return expireTime;
	}

	public void setExpireTime(int expireTime)
	{
		this.expireTime = expireTime;
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body == null ? "" : body;
	}

	public boolean isPayOnDelivery()
	{
		return price > 0L;
	}

	public long getPrice()
	{
		return price;
	}

	public void setPrice(long price)
	{
		this.price = price;
	}

	public boolean isUnread()
	{
		return _isUnread;
	}

	public void setUnread(boolean isUnread)
	{
		_isUnread = isUnread;
	}

	public boolean isReturned()
	{
		return _isReturned;
	}

	public void setReturned(boolean value)
	{
		_isReturned = value;
	}

	public Set<ItemInstance> getAttachments()
	{
		return attachments;
	}

	public void addAttachment(ItemInstance item)
	{
		attachments.add(item);
	}

	public boolean isReturnable()
	{
		return _type == SenderType.NORMAL && attachments.size() > 0 && !_isReturned;
	}

	public int getSystemTopic()
	{
		return _systemTopic;
	}

	public void setSystemTopic(int systemTopic)
	{
		_systemTopic = systemTopic;
	}

	public void setSystemTopic(SystemMsg systemTopic)
	{
		_systemTopic = systemTopic.getId();
	}

	public int getSystemBody()
	{
		return _systemBody;
	}

	public void setSystemBody(int systemBody)
	{
		_systemBody = systemBody;
	}

	public void setSystemBody(SystemMsg systemBody)
	{
		_systemBody = systemBody.getId();
	}

	public int[] getSystemParams()
	{
		return _systemParams;
	}

	public String getSystemParamsToString()
	{
		String result = "";
		for(int param : _systemParams)
			result += param + ";";
		return result;
	}

	public void setSystemParam(int i, int val)
	{
		_systemParams[i] = val;
	}

	public void setSystemParams(String val)
	{
		if(val == null || val.isEmpty())
			return;

		String[] params = val.split(";");
		int length = Math.min(params.length, MAX_SYSTEM_PARAMS_COUNT);
		for(int i = 0; i < length; i++)
		{
			String param = params[i];
			if(param == null || param.isEmpty())
				continue;

			setSystemParam(i, Integer.parseInt(param));
		}
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
		return ((Mail) o).getMessageId() == getMessageId();
	}

	@Override     
	public int hashCode()
	{
		return 13 * getMessageId() + 11700;
	}

	@Override
	public void setJdbcState(JdbcEntityState state)
	{
		_state = state;
	}

	@Override
	public JdbcEntityState getJdbcState()
	{
		return _state;
	}

	public void save()
	{
		_mailDAO.save(this);
	}

	public void update()
	{
		_mailDAO.update(this);
	}

	public void delete()
	{
		_mailDAO.delete(this);
	}

	public Mail reject()
	{
		Mail mail = new Mail();
		mail.setSenderId(1);
		mail.setSenderName("System");
		mail.setReceiverId(getSenderId());
		mail.setReceiverName(getSenderName());
		mail.setTopic(getTopic());
		mail.setBody(getBody());
		synchronized (getAttachments())
		{
			for(ItemInstance item : getAttachments())
				mail.addAttachment(item);
			getAttachments().clear();
		}
		mail.setType(SenderType.NEWS_INFORMER);
		mail.setUnread(true);
		mail.setReturned(true);
		mail.setPrice(getPrice());
		return mail;
	}

	@Override
	public int compareTo(Mail o)
	{
		return o.getMessageId() - this.getMessageId();
	}

	public SenderType getType()
	{
		return _type;
	}

	public void setType(SenderType type)
	{
		_type = type;
	}
}
