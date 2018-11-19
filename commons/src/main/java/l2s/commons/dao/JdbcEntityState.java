package l2s.commons.dao;

/**
 * @author VISTALL
 * @date 8:28/31.01.2011
 */
public enum JdbcEntityState
{
	CREATED(true, false, false, false),
	STORED(false, true, false, true),
	UPDATED(false, true, true, true),
	DELETED(false, false, false, false);

	private final boolean savable;
	private final boolean deletable;
	private final boolean updatable;
	private final boolean persisted;
	
	JdbcEntityState(boolean savable, boolean deletable, boolean updatable, boolean persisted)
	{
		this.savable = savable;
		this.deletable = deletable;
		this.updatable = updatable;
		this.persisted = persisted;
	}

	/**
	 * @return true, если сущность может быть сохранена в хранилище
	 */
	public boolean isSavable()
	{
		return savable;
	}

	/**
	 * @return true, если сущность может быть удалена из хранилища
	 */
	public boolean isDeletable()
	{
		return deletable;
	}

	/**
	 * @return true, если сущность может быть обновлена в хранилище
	 */
	public boolean isUpdatable()
	{
		return updatable;
	}
	
	/**
	 * @return true, если сущность в хранилище
	 */
	public boolean isPersisted()
	{
		return persisted;
	}
}
