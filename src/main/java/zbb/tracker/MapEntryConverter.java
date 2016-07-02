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

package zbb.tracker;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;
import zbb.entities.Flavor;

import java.util.*;

/**
 * Created by zbb on 6/20/16.
 */
public class MapEntryConverter implements Converter {
	public boolean canConvert(Class c) {
		return AbstractMap.class.isAssignableFrom(c);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		AbstractMap<Flavor, Double> map = (AbstractMap<Flavor, Double>) value;
		for (Map.Entry<Flavor, Double> entry : map.entrySet()) {
			writer.startNode(entry.getKey().toString());
			writer.setValue(entry.getValue().toString());
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<Flavor, Double> map = new HashMap<>();

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String[] nameAndManf = reader.getNodeName().split("_");
			Flavor flavor = new Flavor(nameAndManf[1], nameAndManf[0]);
			map.put(flavor, Double.parseDouble(reader.getValue()));
			reader.moveUp();
		}
		return map;
	}
}
