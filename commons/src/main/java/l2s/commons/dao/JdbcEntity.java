package l2s.commons.dao;

import java.io.Serializable;

public interface JdbcEntity extends Serializable
{
	public void setJdbcState(JdbcEntityState state);

	public JdbcEntityState getJdbcState();

	public void save();
	
	public void update();
	
	public void delete();
}
