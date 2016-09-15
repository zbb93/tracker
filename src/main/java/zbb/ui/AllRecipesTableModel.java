/*
 *     Copyright 2016 Zachary Bowen
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zbb.ui;

import zbb.entities.Recipe;
import zbb.tracker.Tracker;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by zbb on 6/21/16.
 *
 */
class AllRecipesTableModel extends AbstractTableModel{
	private final String[] columns = {"Name", "Description", "Possible Amount (ml)"};
	private final List<Recipe> recipes;
	private final Tracker tracker;

	AllRecipesTableModel(List<Recipe> recipes, Tracker tracker) {
		this.recipes = recipes;
		this.tracker = tracker;
	}
	@Override
	public int getRowCount() {
		return recipes.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		Recipe recipe = recipes.get(row);
		switch (col) {
			case (0) :
				return recipe.getName();
			case (1) :
				return recipe.getDescription();
			case (2) :
				return tracker.calculatePossibleAmountToMake(recipe);
		}
		return null;
	}

	@Override
	public String getColumnName(int i) {
		return columns[i];
	}
}
