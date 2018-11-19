package l2s.commons.dao;

import java.io.Serializable;

public interface JdbcDAO<K extends Serializable, E extends JdbcEntity>
{
	public E load(K key);
	
	public void save(E e);
	
	public void update(E e);
	
	public void saveOrUpdate(E e);
	
	public void delete(E e);
	
	public JdbcEntityStats getStats();
}
