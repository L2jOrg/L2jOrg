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
package org.l2j.gameserver.util;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

/**
 * Collection of flood protectors for single player.
 *
 * @author fordfrog
 */
public final class FloodProtectors {
    /**
     * Use-item flood protector.
     */
    private final FloodProtectorAction _useItem;
    /**
     * Roll-dice flood protector.
     */
    private final FloodProtectorAction _rollDice;
    /**
     * Firework flood protector.
     */
    private final FloodProtectorAction _firework;
    /**
     * Item-pet-summon flood protector.
     */
    private final FloodProtectorAction _itemPetSummon;
    /**
     * Hero-voice flood protector.
     */
    private final FloodProtectorAction _heroVoice;
    /**
     * Global-chat flood protector.
     */
    private final FloodProtectorAction _globalChat;
    /**
     * Subclass flood protector.
     */
    private final FloodProtectorAction _subclass;
    /**
     * Drop-item flood protector.
     */
    private final FloodProtectorAction _dropItem;
    /**
     * Server-bypass flood protector.
     */
    private final FloodProtectorAction _serverBypass;
    /**
     * Multisell flood protector.
     */
    private final FloodProtectorAction _multiSell;
    /**
     * Transaction flood protector.
     */
    private final FloodProtectorAction _transaction;
    /**
     * Manufacture flood protector.
     */
    private final FloodProtectorAction _manufacture;
    /**
     * Manor flood protector.
     */
    private final FloodProtectorAction _manor;
    /**
     * Send mail flood protector.
     */
    private final FloodProtectorAction _sendMail;
    /**
     * Character Select protector
     */
    private final FloodProtectorAction _characterSelect;
    /**
     * Item Auction
     */
    private final FloodProtectorAction _itemAuction;

    /**
     * Creates new instance of FloodProtectors.
     *
     * @param client game client for which the collection of flood protectors is being created.
     */
    public FloodProtectors(GameClient client) {
        super();
        _useItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_USE_ITEM);
        _rollDice = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ROLL_DICE);
        _firework = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_FIREWORK);
        _itemPetSummon = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ITEM_PET_SUMMON);
        _heroVoice = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_HERO_VOICE);
        _globalChat = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_GLOBAL_CHAT);
        _subclass = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SUBCLASS);
        _dropItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_DROP_ITEM);
        _serverBypass = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SERVER_BYPASS);
        _multiSell = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MULTISELL);
        _transaction = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_TRANSACTION);
        _manufacture = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MANUFACTURE);
        _manor = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MANOR);
        _sendMail = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SENDMAIL);
        _characterSelect = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_CHARACTER_SELECT);
        _itemAuction = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ITEM_AUCTION);
    }

    /**
     * Returns {@link #_useItem}.
     *
     * @return {@link #_useItem}
     */
    public FloodProtectorAction getUseItem() {
        return _useItem;
    }

    /**
     * Returns {@link #_rollDice}.
     *
     * @return {@link #_rollDice}
     */
    public FloodProtectorAction getRollDice() {
        return _rollDice;
    }

    /**
     * Returns {@link #_firework}.
     *
     * @return {@link #_firework}
     */
    public FloodProtectorAction getFirework() {
        return _firework;
    }

    /**
     * Returns {@link #_itemPetSummon}.
     *
     * @return {@link #_itemPetSummon}
     */
    public FloodProtectorAction getItemPetSummon() {
        return _itemPetSummon;
    }

    /**
     * Returns {@link #_heroVoice}.
     *
     * @return {@link #_heroVoice}
     */
    public FloodProtectorAction getHeroVoice() {
        return _heroVoice;
    }

    /**
     * Returns {@link #_globalChat}.
     *
     * @return {@link #_globalChat}
     */
    public FloodProtectorAction getGlobalChat() {
        return _globalChat;
    }

    /**
     * Returns {@link #_subclass}.
     *
     * @return {@link #_subclass}
     */
    public FloodProtectorAction getSubclass() {
        return _subclass;
    }

    /**
     * Returns {@link #_dropItem}.
     *
     * @return {@link #_dropItem}
     */
    public FloodProtectorAction getDropItem() {
        return _dropItem;
    }

    /**
     * Returns {@link #_serverBypass}.
     *
     * @return {@link #_serverBypass}
     */
    public FloodProtectorAction getServerBypass() {
        return _serverBypass;
    }

    /**
     * @return {@link #_multiSell}
     */
    public FloodProtectorAction getMultiSell() {
        return _multiSell;
    }

    /**
     * Returns {@link #_transaction}.
     *
     * @return {@link #_transaction}
     */
    public FloodProtectorAction getTransaction() {
        return _transaction;
    }

    /**
     * Returns {@link #_manufacture}.
     *
     * @return {@link #_manufacture}
     */
    public FloodProtectorAction getManufacture() {
        return _manufacture;
    }

    /**
     * Returns {@link #_manor}.
     *
     * @return {@link #_manor}
     */
    public FloodProtectorAction getManor() {
        return _manor;
    }

    /**
     * Returns {@link #_sendMail}.
     *
     * @return {@link #_sendMail}
     */
    public FloodProtectorAction getSendMail() {
        return _sendMail;
    }

    /**
     * Returns {@link #_characterSelect}.
     *
     * @return {@link #_characterSelect}
     */
    public FloodProtectorAction getCharacterSelect() {
        return _characterSelect;
    }

    /**
     * Returns {@link #_itemAuction}.
     *
     * @return {@link #_itemAuction}
     */
    public FloodProtectorAction getItemAuction() {
        return _itemAuction;
    }
}
