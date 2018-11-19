package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.RecipeTemplate;
import l2s.gameserver.templates.item.data.ChancedItemData;
import l2s.gameserver.templates.item.data.ItemData;

public final class RecipeParser extends AbstractParser<RecipeHolder>
{
	private static final RecipeParser _instance = new RecipeParser();

	public static RecipeParser getInstance()
	{
		return _instance;
	}

	private RecipeParser()
	{
		super(RecipeHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/recipes.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "recipes.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			final int id = Integer.parseInt(element.attributeValue("id"));
			final int level = Integer.parseInt(element.attributeValue("level"));
			final int mpConsume = element.attributeValue("mp_consume") == null ? 0 : Integer.parseInt(element.attributeValue("mp_consume"));
			final int successRate = Integer.parseInt(element.attributeValue("success_rate"));
			final int itemId = Integer.parseInt(element.attributeValue("item_id"));
			final boolean isCommon = element.attributeValue("is_common") == null ? false : Boolean.parseBoolean(element.attributeValue("is_common"));

			RecipeTemplate recipe = new RecipeTemplate(id, level, mpConsume, successRate, itemId, isCommon);
			for(Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext();)
			{
				Element subElement = subIterator.next();

				if("materials".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("item".equalsIgnoreCase(e.getName()))
						{
							int item_id = Integer.parseInt(e.attributeValue("id"));
							long count = Long.parseLong(e.attributeValue("count"));
							if(Config.ALT_EASY_RECIPES && !checkComponent(item_id))
								continue;
							recipe.addMaterial(new ItemData(item_id, count));
						}
					}
				}
				else if("products".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("item".equalsIgnoreCase(e.getName()))
						{
							int item_id = Integer.parseInt(e.attributeValue("id"));
							long count = Long.parseLong(e.attributeValue("count"));
							int chance = Integer.parseInt(e.attributeValue("chance"));
							recipe.addProduct(new ChancedItemData(item_id, count, chance));
							if(Config.ALT_EASY_RECIPES)
							{
								int book_id = checkAndAddBook(item_id);
								if(book_id != 0)
									recipe.addMaterial(new ItemData(item_id, 1));
							}
						}
					}
				}
				else if("npc_fee".equalsIgnoreCase(subElement.getName()))
				{
					for(Element e : subElement.elements())
					{
						if("item".equalsIgnoreCase(e.getName()))
						{
							if(Config.ALT_EASY_RECIPES)
								continue;
							int item_id = Integer.parseInt(e.attributeValue("id"));
							long count = Long.parseLong(e.attributeValue("count"));
							recipe.addNpcFee(new ItemData(item_id, count));
						}
					}
				}
			}
			getHolder().addRecipe(recipe);
		}
	}
	
	public static boolean checkComponent(int itemId)
	{
		ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if(template.isRecipe())
			return true;
		if(template.isCrystall())
			return true;
		return false;	
	}
	
	public static int checkAndAddBook(int itemId)
	{
		ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if(template == null)
			return 0;
		if(template.getGrade() == ItemGrade.NONE)
			return 0;
		if(!template.isEquipable())	
			return 0;
			
		return getBookId(template.getGrade(), template.isWeapon());
	}
	
	public static int getBookId(ItemGrade grade, boolean isWpn)
	{
		switch(grade)
		{
			case D:
				if(isWpn)
					return 40000;
				else
					return 40001;
			case C:
				if(isWpn)
					return 40002;
				else
					return 40003;		
			case B:
				if(isWpn)
					return 40004;
				else
					return 40005;		
			case A:
				if(isWpn)
					return 40006;
				else
					return 40007;			
			case S:
				if(isWpn)
					return 40008;
				else
					return 40009;	
			case S80:
				if(isWpn)
					return 40010;
				else
					return 40011;
			case R:
				if(isWpn)
					return 40012;
				else
					return 40013;
			case R95:
				if(isWpn)
					return 40014;
				else
					return 40015;	
			case R99:
				if(isWpn)
					return 40016;
				else
					return 40017;		
			default:
				return 40000;
		}
	}
}