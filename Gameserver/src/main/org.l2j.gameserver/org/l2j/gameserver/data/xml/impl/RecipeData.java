/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.Recipe;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.RecipeStat;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * The Class RecipeData.
 *
 * @author Zoey76
 */
public class RecipeData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeData.class);

    private final Map<Integer, RecipeList> _recipes = new HashMap<>();

    private RecipeData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/Recipes.xsd");
    }

    @Override
    public void load() {
        _recipes.clear();
        parseDatapackFile("data/Recipes.xml");
        LOGGER.info("Loaded {} recipes.", _recipes.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        // TODO: Cleanup checks enforced by XSD.
        final List<Recipe> recipePartList = new ArrayList<>();
        final List<RecipeStat> recipeStatUseList = new ArrayList<>();
        final List<RecipeStat> recipeAltStatChangeList = new ArrayList<>();
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                RECIPES_FILE:
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("item".equalsIgnoreCase(d.getNodeName())) {
                        recipePartList.clear();
                        recipeStatUseList.clear();
                        recipeAltStatChangeList.clear();
                        final NamedNodeMap attrs = d.getAttributes();
                        Node att;
                        int id = -1;
                        boolean haveRare = false;
                        final StatsSet set = new StatsSet();

                        att = attrs.getNamedItem("id");
                        if (att == null) {
                            LOGGER.error(": Missing id for recipe item, skipping");
                            continue;
                        }
                        id = Integer.parseInt(att.getNodeValue());
                        set.set("id", id);

                        att = attrs.getNamedItem("recipeId");
                        if (att == null) {
                            LOGGER.error(": Missing recipeId for recipe item id: " + id + ", skipping");
                            continue;
                        }
                        set.set("recipeId", Integer.parseInt(att.getNodeValue()));

                        att = attrs.getNamedItem("name");
                        if (att == null) {
                            LOGGER.error(": Missing name for recipe item id: " + id + ", skipping");
                            continue;
                        }
                        set.set("recipeName", att.getNodeValue());

                        att = attrs.getNamedItem("craftLevel");
                        if (att == null) {
                            LOGGER.error(": Missing level for recipe item id: " + id + ", skipping");
                            continue;
                        }
                        set.set("craftLevel", Integer.parseInt(att.getNodeValue()));

                        att = attrs.getNamedItem("type");
                        if (att == null) {
                            LOGGER.error(": Missing type for recipe item id: " + id + ", skipping");
                            continue;
                        }
                        set.set("isDwarvenRecipe", att.getNodeValue().equalsIgnoreCase("dwarven"));

                        att = attrs.getNamedItem("successRate");
                        if (att == null) {
                            LOGGER.error(": Missing successRate for recipe item id: " + id + ", skipping");
                            continue;
                        }
                        set.set("successRate", Integer.parseInt(att.getNodeValue()));

                        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                            if ("statUse".equalsIgnoreCase(c.getNodeName())) {
                                final String statName = c.getAttributes().getNamedItem("name").getNodeValue();
                                final int value = Integer.parseInt(c.getAttributes().getNamedItem("value").getNodeValue());
                                try {
                                    recipeStatUseList.add(new RecipeStat(statName, value));
                                } catch (Exception e) {
                                    LOGGER.error(": Error in StatUse parameter for recipe item id: " + id + ", skipping");
                                    continue RECIPES_FILE;
                                }
                            } else if ("altStatChange".equalsIgnoreCase(c.getNodeName())) {
                                final String statName = c.getAttributes().getNamedItem("name").getNodeValue();
                                final int value = Integer.parseInt(c.getAttributes().getNamedItem("value").getNodeValue());
                                try {
                                    recipeAltStatChangeList.add(new RecipeStat(statName, value));
                                } catch (Exception e) {
                                    LOGGER.error(": Error in AltStatChange parameter for recipe item id: " + id + ", skipping");
                                    continue RECIPES_FILE;
                                }
                            } else if ("ingredient".equalsIgnoreCase(c.getNodeName())) {
                                final int ingId = Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue());
                                final int ingCount = Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue());
                                recipePartList.add(new Recipe(ingId, ingCount));
                            } else if ("production".equalsIgnoreCase(c.getNodeName())) {
                                set.set("itemId", Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue()));
                                set.set("count", Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue()));
                            } else if ("productionRare".equalsIgnoreCase(c.getNodeName())) {
                                set.set("rareItemId", Integer.parseInt(c.getAttributes().getNamedItem("id").getNodeValue()));
                                set.set("rareCount", Integer.parseInt(c.getAttributes().getNamedItem("count").getNodeValue()));
                                set.set("rarity", Integer.parseInt(c.getAttributes().getNamedItem("rarity").getNodeValue()));
                                haveRare = true;
                            }
                        }

                        final RecipeList recipeList = new RecipeList(set, haveRare);
                        for (Recipe recipePart : recipePartList) {
                            recipeList.addRecipe(recipePart);
                        }
                        for (RecipeStat recipeStatUse : recipeStatUseList) {
                            recipeList.addStatUse(recipeStatUse);
                        }
                        for (RecipeStat recipeAltStatChange : recipeAltStatChangeList) {
                            recipeList.addAltStatChange(recipeAltStatChange);
                        }

                        _recipes.put(id, recipeList);
                    }
                }
            }
        }
    }

    /**
     * Gets the recipe list.
     *
     * @param listId the list id
     * @return the recipe list
     */
    public RecipeList getRecipeList(int listId) {
        return _recipes.get(listId);
    }

    /**
     * Gets the recipe by item id.
     *
     * @param itemId the item id
     * @return the recipe by item id
     */
    public RecipeList getRecipeByItemId(int itemId) {
        for (RecipeList find : _recipes.values()) {
            if (find.getRecipeId() == itemId) {
                return find;
            }
        }
        return null;
    }

    /**
     * Gets the all item ids.
     *
     * @return the all item ids
     */
    public int[] getAllItemIds() {
        final int[] idList = new int[_recipes.size()];
        int i = 0;
        for (RecipeList rec : _recipes.values()) {
            idList[i++] = rec.getRecipeId();
        }
        return idList;
    }

    /**
     * Gets the valid recipe list.
     *
     * @param player the player
     * @param id     the recipe list id
     * @return the valid recipe list
     */
    public RecipeList getValidRecipeList(Player player, int id) {
        final RecipeList recipeList = _recipes.get(id);
        if ((recipeList == null) || (recipeList.getRecipes().length == 0)) {
            player.sendMessage("No recipe for: " + id);
            player.setIsCrafting(false);
            return null;
        }
        return recipeList;
    }

    public static RecipeData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final RecipeData INSTANCE = new RecipeData();
    }
}
