package l2s.gameserver.model.quest;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.instances.NpcInstance;

public class QuestTimer extends RunnableImpl
{
	private String _name;
	private NpcInstance _npc;
	private long _time;
	private QuestState _qs;
	private ScheduledFuture<?> _schedule;

	public QuestTimer(String name, long time, NpcInstance npc)
	{
		_name = name;
		_time = time;
		_npc = npc;
	}

	void setQuestState(QuestState qs)
	{
		_qs = qs;
	}

	QuestState getQuestState()
	{
		return _qs;
	}

	void start()
	{
		_schedule = ThreadPoolManager.getInstance().schedule(this, _time);
	}

	@Override
	public void runImpl() throws Exception
	{
		QuestState qs = getQuestState();
		if(qs != null)
		{
			qs.removeQuestTimer(getName());
			qs.getQuest().notifyEvent(getName(), qs, getNpc());
		}
	}

	void pause()
	{
		// Запоминаем оставшееся время, для возможности возобновления таска
		if(_schedule != null)
		{
			_time = _schedule.getDelay(TimeUnit.SECONDS);
			_schedule.cancel(false);
		}
	}

	void stop()
	{
		if(_schedule != null)
			_schedule.cancel(false);
	}

	public boolean isActive()
	{
		return _schedule != null && !_schedule.isDone();
	}

	public String getName()
	{
		return _name;
	}

	public long getTime()
	{
		return _time;
	}

	public NpcInstance getNpc()
	{
		return _npc;
	}

	@Override
	public final String toString()
	{
		return _name;
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
		return ((QuestTimer) o).getName().equals(this.getName());
	}

	@Override     
	public int hashCode()
	{
		return 3 * getName().hashCode() + 17570;
	}
}