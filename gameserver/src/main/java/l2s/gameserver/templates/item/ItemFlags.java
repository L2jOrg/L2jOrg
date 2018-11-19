package l2s.gameserver.templates.item;

/**
 * @author VISTALL
 * @date 20:51/11.01.2011
 */
public enum ItemFlags
{
	DESTROYABLE(true), // возможность уничтожить
	DROPABLE(true), // возможность дропнуть
	FREIGHTABLE(false), // возможность передать в рамках аккаунта
	ENCHANTABLE(true), // возможность заточить
	SELLABLE(true), // возможность продать
	TRADEABLE(true), // возможность передать
	STOREABLE(true), // возможность положить в ВХ
	PRIVATESTOREABLE(true), // возможность продать в личной лавке
	ENSOULABLE(true); // возможность выставить предмет на аукцион

	public static final ItemFlags[] VALUES = values();

	private final int _mask;
	private final boolean _defaultValue;

	ItemFlags(boolean defaultValue)
	{
		_defaultValue = defaultValue;
		_mask = 1 << ordinal();
	}

	public int mask()
	{
		return _mask;
	}

	public boolean getDefaultValue()
	{
		return _defaultValue;
	}
}
