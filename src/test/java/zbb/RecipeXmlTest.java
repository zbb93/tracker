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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import zbb.entities.Flavor;
import zbb.entities.Recipe;
import zbb.tracker.Tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zbb on 9/22/16.
 */
public class RecipeXmlTest {

	private File file = new File("test/French Toast.xml");

	@Before
	public void before() {
		Map<Flavor, Double> recipeMap = new HashMap<>();
		recipeMap.put(new Flavor("TFA", "Waffle"), 10.0);
		recipeMap.put(new Flavor("TFA", "Vanilla Swirl"), 4.0);
		recipeMap.put(new Flavor("CAP", "Cinnamon Danish Swirl"), 4.0);
		Recipe frenchToast = new Recipe("French Toast", "Syrupy Waffle", recipeMap);
		Document doc = frenchToast.toXml();
		Tracker tracker = new Tracker("", "test/");
		tracker.addRecipe(frenchToast);
		tracker.save();
	}

	@Test
	public void testRecipe() {
		Map<Flavor, Double> recipeMap = new HashMap<>();
		recipeMap.put(new Flavor("TFA", "Waffle"), 10.0);
		recipeMap.put(new Flavor("TFA", "Vanilla Swirl"), 4.0);
		recipeMap.put(new Flavor("CAP", "Cinnamon Danish Swirl"), 4.0);
		Recipe frenchToast = new Recipe("French Toast", "Syrupy Waffle", recipeMap);

		Tracker tracker = new Tracker("", "test/");
		Assert.assertTrue(tracker.getRecipes().contains(frenchToast));
	}
}
