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

import zbb.entities.Flavor;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by zbb on 6/11/16.
 *
 */
class FlavorTableModel extends AbstractTableModel {

	private final String[] columns = {"Name", "Amount Remaining"};
	private final List<Flavor> flavors;

	FlavorTableModel(List<Flavor> flavors) {
		this.flavors = flavors;
	}

	@Override
	public int getRowCount() {
		return flavors.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		final Flavor flavor = flavors.get(row);
		switch (col) {
			case (0):
				if (!("".equals(flavor.getManufacturer()))) {
					return flavor.getName() + " (" + flavor.getManufacturer() + ")";
				} else {
					return flavor.getName();
				}
			case (1):
				return flavor.getAmountRemaining();
			default:
				return null;
		}
	}

	@Override
	public String getColumnName(int i) {
		return columns[i];
	}
}
