/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_random_craft")
public class RandomCraftDAOData {
    private int charId;
    private int random_craft_full_points;
    private int random_craft_points;
    private boolean sayha_roll;
    private int item_1_id;
    private long item_1_count;
    private boolean item_1_locked;
    private int item_1_lock_left;
    private int item_2_id;
    private long item_2_count;
    private boolean item_2_locked;
    private int item_2_lock_left;
    private int item_3_id;
    private long item_3_count;
    private boolean item_3_locked;
    private int item_3_lock_left;
    private int item_4_id;
    private long item_4_count;
    private boolean item_4_locked;
    private int item_4_lock_left;
    private int item_5_id;
    private long item_5_count;
    private boolean item_5_locked;
    private int item_5_lock_left;

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public int getRandomCraftFullPoints() {
        return random_craft_full_points;
    }

    public void setRandomCraftFullPoints(int random_craft_full_points) {
        this.random_craft_full_points = random_craft_full_points;
    }

    public int getRandomCraftPoints() {
        return random_craft_points;
    }

    public void setRandomCraftPoints(int random_craft_points) {
        this.random_craft_points = random_craft_points;
    }

    public boolean getSayhaRoll() {
        return sayha_roll;
    }

    public void setSayhaRoll(boolean sayha_roll) {
        this.sayha_roll = sayha_roll;
    }

    public int getItem1Id() {
        return item_1_id;
    }

    public void setItem1Id(int item_1_id) {
        this.item_1_id = item_1_id;
    }

    public long getItem1Count() {
        return item_1_count;
    }

    public void setItem1Count(long item_1_count) {
        this.item_1_count = item_1_count;
    }

    public boolean getItem1Locked() {
        return item_1_locked;
    }

    public void setItem1Locked(boolean item_1_locked) {
        this.item_1_locked = item_1_locked;
    }

    public int getItem1LockLeft() {
        return item_1_lock_left;
    }

    public void setItem1LockLeft(int item_1_lock_left) {
        this.item_1_lock_left = item_1_lock_left;
    }

    public int getItem2Id() {
        return item_2_id;
    }

    public void setItem2Id(int item_2_id) {
        this.item_2_id = item_2_id;
    }

    public long getItem2Count() {
        return item_2_count;
    }

    public void setItem2Count(long item_2_count) {
        this.item_2_count = item_2_count;
    }

    public boolean getItem2Locked() {
        return item_2_locked;
    }

    public void setItem2Locked(boolean item_2_locked) {
        this.item_2_locked = item_2_locked;
    }

    public int getItem2LockLeft() {
        return item_2_lock_left;
    }

    public void setItem2LockLeft(int item_2_lock_left) {
        this.item_2_lock_left = item_2_lock_left;
    }

    public int getItem3Id() {
        return item_3_id;
    }

    public void setItem3Id(int item_3_id) {
        this.item_3_id = item_3_id;
    }

    public long getItem3Count() {
        return item_3_count;
    }

    public void setItem3Count(long item_3_count) {
        this.item_3_count = item_3_count;
    }

    public boolean getItem3Locked() {
        return item_3_locked;
    }

    public void setItem3Locked(boolean item_3_locked) {
        this.item_3_locked = item_3_locked;
    }

    public int getItem3LockLeft() {
        return item_3_lock_left;
    }

    public void setItem3LockLeft(int item_3_lock_left) {
        this.item_3_lock_left = item_3_lock_left;
    }

    public int getItem4Id() {
        return item_4_id;
    }

    public void setItem4Id(int item_4_id) {
        this.item_4_id = item_4_id;
    }

    public long getItem4Count() {
        return item_4_count;
    }

    public void setItem4Count(long item_4_count) {
        this.item_4_count = item_4_count;
    }

    public boolean getItem4Locked() {
        return item_4_locked;
    }

    public void setItem4Locked(boolean item_4_locked) {
        this.item_4_locked = item_4_locked;
    }

    public int getItem4LockLeft() {
        return item_4_lock_left;
    }

    public void setItem4LockLeft(int item_4_lock_left) {
        this.item_4_lock_left = item_4_lock_left;
    }

    public int getItem5Id() {
        return item_5_id;
    }

    public void setItem5Id(int item_5_id) {
        this.item_5_id = item_5_id;
    }

    public long getItem5Count() {
        return item_5_count;
    }

    public void setItem5Count(long item_5_count) {
        this.item_5_count = item_5_count;
    }

    public boolean getItem5Locked() {
        return item_5_locked;
    }

    public void setItem5Locked(boolean item_5_locked) {
        this.item_5_locked = item_5_locked;
    }

    public int getItem5LockLeft() {
        return item_5_lock_left;
    }

    public void setItem5LockLeft(int item_5_lock_left) {
        this.item_5_lock_left = item_5_lock_left;
    }
}
