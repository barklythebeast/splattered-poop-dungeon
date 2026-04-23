/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages multiplayer hotseat action selection and synchronization.
 * Players select actions sequentially, then all actions execute together.
 */
public class MultiplayerManager {
	
	private static int numPlayers = 1;
	private static int currentPlayerIndex = 0;
	private static Map<Hero, HeroAction> selectedActions = new HashMap<>();
	private static boolean allActionsSelected = false;
	
	public static void init(int playerCount) {
		numPlayers = Math.max(1, Math.min(3, playerCount));
		currentPlayerIndex = 0;
		selectedActions.clear();
		allActionsSelected = false;
	}
	
	public static int getNumPlayers() {
		return numPlayers;
	}
	
	public static int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}
	
	public static Hero getCurrentPlayer() {
		if (numPlayers == 1) {
			return Dungeon.hero;
		}
		if (currentPlayerIndex >= 0 && currentPlayerIndex < Dungeon.heroes.length) {
			return Dungeon.heroes[currentPlayerIndex];
		}
		return null;
	}
	
	public static void selectAction(Hero hero, HeroAction action) {
		if (action != null) {
			selectedActions.put(hero, action);
		}
		
		// Move to next player
		currentPlayerIndex++;
		
		// All players have selected
		if (currentPlayerIndex >= numPlayers) {
			allActionsSelected = true;
			applyAllActions();
			resetForNextRound();
		}
	}
	
	public static boolean isMultiplayer() {
		return numPlayers > 1;
	}
	
	public static boolean isSingleplayer() {
		return numPlayers == 1;
	}
	
	private static void applyAllActions() {
		// Apply all selected actions to their respective heroes
		if (Dungeon.hero != null && selectedActions.containsKey(Dungeon.hero)) {
			Dungeon.hero.curAction = selectedActions.get(Dungeon.hero);
		}
		
		if (Dungeon.heroes != null) {
			for (Hero hero : Dungeon.heroes) {
				if (hero != null && selectedActions.containsKey(hero)) {
					hero.curAction = selectedActions.get(hero);
				}
			}
		}
	}
	
	private static void resetForNextRound() {
		currentPlayerIndex = 0;
		selectedActions.clear();
		allActionsSelected = false;
	}
	
	public static void reset() {
		numPlayers = 1;
		currentPlayerIndex = 0;
		selectedActions.clear();
		allActionsSelected = false;
	}
}
