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

package zbb;

import nu.xom.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import zbb.entities.Flavor;

import java.io.*;
import java.util.*;

/**
 * Created by zbb on 9/14/16.
 */
@RunWith(JUnit4.class)
public class FlavorXmlTest {

	private File file;

	@Before
	public void before() {
		List<String> categories = new LinkedList<>();
		categories.add("Fruit");
		categories.add("Candy");
		Flavor test = new Flavor("Lorann", "Watermelon", categories, 7.5);
		Document doc = test.toXml();
		try {
			file = new File("test.xml");
			FileOutputStream fos = new FileOutputStream(file);
			Serializer serializer = new Serializer(fos, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(64);
			serializer.write(doc);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	@Test
	public void testFlavor() {
		Builder builder = new Builder();
		Flavor flavor = null;
		try {
			Document doc = builder.build(file);
			Element root = doc.getRootElement();
			Element flavorElem = root.getFirstChildElement("Flavor");
			Elements flavorNameElem = flavorElem.getChildElements();
			String flavorName = flavorNameElem.get(0).getValue();

			Element manf = flavorElem.getFirstChildElement("Manufacturer");
			String flavorManf = manf.getChild(0).getValue();

			Element categoriesElem = flavorElem.getFirstChildElement("Categories");
			Elements categoryElems = categoriesElem.getChildElements();
			List<String> categories = new LinkedList<>();
			for (int i = 0; i < categoryElems.size(); i++) {
				categories.add(categoryElems.get(i).getValue());
			}

			Element amtRemElem = flavorElem.getFirstChildElement("AmountRemaining");
			double amtRem = Double.valueOf(amtRemElem.getChild(0).getValue());

			flavor = new Flavor(flavorManf, flavorName, categories, amtRem);
		} catch (ParsingException|IOException exc) {
			exc.printStackTrace();
		}
		List<String> expectedCategories = new LinkedList<>();
		expectedCategories.add("Fruit");
		expectedCategories.add("Candy");
		Assert.assertEquals(new Flavor("Lorann", "Watermelon", expectedCategories, 7.5), flavor);
	}
}
