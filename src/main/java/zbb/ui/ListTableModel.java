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

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by zbb on 7/9/16.
 */
class ListTableModel extends AbstractTableModel {
	private final String[] columns = {"Name"};
	private final List<String> names;

	ListTableModel(List<String> listNames) {
		this.names = listNames;
	}
	@Override
	public int getRowCount() {
		return names.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return names.get(row);
	}

	@Override
	public String getColumnName(int i) {
		return columns[i];
	}
}

