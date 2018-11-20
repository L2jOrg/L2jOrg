package org.l2j.commons.dao;

public interface JdbcEntityStats
{
	public long getLoadCount();
	public long getInsertCount();
	public long getUpdateCount();
	public long getDeleteCount();
}
