/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.taskmanager;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.commons.database.DatabaseFactory;
import com.l2jmobius.gameserver.taskmanager.tasks.TaskBirthday;
import com.l2jmobius.gameserver.taskmanager.tasks.TaskCleanUp;
import com.l2jmobius.gameserver.taskmanager.tasks.TaskRestart;
import com.l2jmobius.gameserver.taskmanager.tasks.TaskShutdown;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.l2jmobius.gameserver.taskmanager.TaskTypes.*;

/**
 * @author Layane
 */
public final class TaskManager
{
	static final Logger LOGGER = Logger.getLogger(TaskManager.class.getName());
	
	private final Map<Integer, Task> _tasks = new ConcurrentHashMap<>();
	final List<ExecutedTask> _currentTasks = new CopyOnWriteArrayList<>();
	
	static final String[] SQL_STATEMENTS =
	{
		"SELECT id,task,type,last_activation,param1,param2,param3 FROM global_tasks",
		"UPDATE global_tasks SET last_activation=? WHERE id=?",
		"SELECT id FROM global_tasks WHERE task=?",
		"INSERT INTO global_tasks (task,type,last_activation,param1,param2,param3) VALUES(?,?,?,?,?,?)"
	};
	
	protected TaskManager()
	{
		initializate();
		startAllTasks();
		LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _tasks.size() + " Tasks.");
	}
	
	public class ExecutedTask implements Runnable
	{
		int id;
		private long lastActivation;
		private final Task task;
		private final TaskTypes type;
		private final String[] params;
		ScheduledFuture<?> scheduled;
		
		ExecutedTask(Task ptask, TaskTypes ptype, ResultSet rset) throws SQLException
		{
			task = ptask;
			type = ptype;
			id = rset.getInt("id");
			lastActivation = rset.getLong("last_activation");
			params = new String[]
			{
				rset.getString("param1"),
				rset.getString("param2"),
				rset.getString("param3")
			};
		}
		
		@Override
		public void run()
		{
			task.onTimeElapsed(this);
			lastActivation = System.currentTimeMillis();
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[1]))
			{
				statement.setLong(1, lastActivation);
				statement.setInt(2, id);
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Cannot updated the Global Task " + id + ": " + e.getMessage(), e);
			}
			
			if ((type == TYPE_SHEDULED) || (type == TYPE_TIME))
			{
				stopTask();
			}
		}
		
		@Override
		public boolean equals(Object object)
		{
			return (this == object) || ((object instanceof ExecutedTask) && (id == ((ExecutedTask) object).id));
		}
		
		@Override
		public int hashCode()
		{
			return id;
		}
		
		public Task getTask()
		{
			return task;
		}
		
		public TaskTypes getType()
		{
			return type;
		}
		
		public int getId()
		{
			return id;
		}
		
		public String[] getParams()
		{
			return params;
		}
		
		public long getLastActivation()
		{
			return lastActivation;
		}
		
		private void stopTask()
		{
			task.onDestroy();
			
			if (scheduled != null)
			{
				scheduled.cancel(true);
			}
			
			_currentTasks.remove(this);
		}
	}
	
	private void initializate()
	{
		registerTask(new TaskBirthday());
		registerTask(new TaskCleanUp());
		registerTask(new TaskRestart());
		registerTask(new TaskShutdown());
	}
	
	private void registerTask(Task task)
	{
		_tasks.computeIfAbsent(task.getName().hashCode(), k ->
		{
			task.initializate();
			return task;
		});
	}
	
	private void startAllTasks()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[0]);
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final Task task = _tasks.get(rset.getString("task").trim().toLowerCase().hashCode());
				if (task == null)
				{
					continue;
				}
				
				final TaskTypes type = TaskTypes.valueOf(rset.getString("type"));
				if (type != TYPE_NONE)
				{
					final ExecutedTask current = new ExecutedTask(task, type, rset);
					if (launchTask(current))
					{
						_currentTasks.add(current);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error while loading Global Task table: " + e.getMessage(), e);
		}
	}
	
	private boolean launchTask(ExecutedTask task)
	{
		final TaskTypes type = task.getType();
		long delay;
		long interval;
		switch (type)
		{
			case TYPE_STARTUP:
			{
				task.run();
				return false;
			}
			case TYPE_SHEDULED:
			{
				delay = Long.valueOf(task.getParams()[0]);
				task.scheduled = ThreadPool.schedule(task, delay);
				return true;
			}
			case TYPE_FIXED_SHEDULED:
			{
				delay = Long.valueOf(task.getParams()[0]);
				interval = Long.valueOf(task.getParams()[1]);
				task.scheduled = ThreadPool.scheduleAtFixedRate(task, delay, interval);
				return true;
			}
			case TYPE_TIME:
			{
				try
				{
					final Date desired = DateFormat.getInstance().parse(task.getParams()[0]);
					final long diff = desired.getTime() - System.currentTimeMillis();
					if (diff >= 0)
					{
						task.scheduled = ThreadPool.schedule(task, diff);
						return true;
					}
					LOGGER.info(getClass().getSimpleName() + ": Task " + task.getId() + " is obsoleted.");
				}
				catch (Exception e)
				{
				}
				break;
			}
			case TYPE_SPECIAL:
			{
				final ScheduledFuture<?> result = task.getTask().launchSpecial(task);
				if (result != null)
				{
					task.scheduled = result;
					return true;
				}
				break;
			}
			case TYPE_GLOBAL_TASK:
			{
				interval = Long.valueOf(task.getParams()[0]) * 86400000;
				final String[] hour = task.getParams()[1].split(":");
				
				if (hour.length != 3)
				{
					LOGGER.warning(getClass().getSimpleName() + ": Task " + task.getId() + " has incorrect parameters");
					return false;
				}
				
				final Calendar check = Calendar.getInstance();
				check.setTimeInMillis(task.getLastActivation() + interval);
				
				final Calendar min = Calendar.getInstance();
				try
				{
					min.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour[0]));
					min.set(Calendar.MINUTE, Integer.parseInt(hour[1]));
					min.set(Calendar.SECOND, Integer.parseInt(hour[2]));
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Bad parameter on task " + task.getId() + ": " + e.getMessage(), e);
					return false;
				}
				
				delay = min.getTimeInMillis() - System.currentTimeMillis();
				
				if (check.after(min) || (delay < 0))
				{
					delay += interval;
				}
				task.scheduled = ThreadPool.scheduleAtFixedRate(task, delay, interval);
				return true;
			}
			default:
			{
				return false;
			}
		}
		return false;
	}
	
	public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3)
	{
		return addUniqueTask(task, type, param1, param2, param3, 0);
	}
	
	private static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps1 = con.prepareStatement(SQL_STATEMENTS[2]))
		{
			ps1.setString(1, task);
			try (ResultSet rs = ps1.executeQuery())
			{
				if (!rs.next())
				{
					try (PreparedStatement ps2 = con.prepareStatement(SQL_STATEMENTS[3]))
					{
						ps2.setString(1, task);
						ps2.setString(2, type.toString());
						ps2.setLong(3, lastActivation);
						ps2.setString(4, param1);
						ps2.setString(5, param2);
						ps2.setString(6, param3);
						ps2.execute();
					}
				}
			}
			return true;
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, TaskManager.class.getSimpleName() + ": Cannot add the unique task: " + e.getMessage(), e);
		}
		return false;
	}
	
	public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3)
	{
		return addTask(task, type, param1, param2, param3, 0);
	}
	
	private static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[3]))
		{
			statement.setString(1, task);
			statement.setString(2, type.toString());
			statement.setLong(3, lastActivation);
			statement.setString(4, param1);
			statement.setString(5, param2);
			statement.setString(6, param3);
			statement.execute();
			return true;
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, TaskManager.class.getSimpleName() + ": Cannot add the task:  " + e.getMessage(), e);
		}
		return false;
	}
	
	public static TaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TaskManager _instance = new TaskManager();
	}
}
