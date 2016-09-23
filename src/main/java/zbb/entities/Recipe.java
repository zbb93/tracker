package zbb.entities;

import nu.xom.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zbb on 6/6/16.
 *
 */
public class Recipe implements Serializable{
	private String name;
	private String description;
	private Map<Flavor, Double> recipe;

	public Recipe(String name, Map<Flavor, Double> recipe) {
		this(name, null, recipe);

	}

	public Recipe(String name, String description, Map<Flavor, Double> recipe) {
		this.name = name;
		this.description = description;
		this.recipe = recipe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Map<Flavor, Double> getRecipe() {
		return recipe;
	}

	public void setRecipe(Map<Flavor, Double> recipe) {
		this.recipe = recipe;
	}

	public double getFlavorAmount(Flavor flavor) {
		return recipe.get(flavor);
	}
	
	public void setFlavorAmount(Flavor flavor, double amount) {
		recipe.put(flavor, amount);
	}

	//TODO: If the recipe name must be unique is it ok to determine equality based solely on recipe.name?
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} else if (!(other instanceof Recipe)) {
			return false;
		} else {
			return ((Recipe) other).getRecipe().equals(recipe) && ((Recipe) other).getName().equals(name);
		}
	}

	@Override
	public int hashCode() {
		return 13 * recipe.hashCode() + 11 * name.hashCode();
	}
	public String getDescription() {
		return description;
	}

	public Document toXml() {
		final Element root = new Element("Root");
		final Element recipeElem = new Element("Recipe");
		root.appendChild(recipeElem);

		final Element nameElem = new Element("Name");
		nameElem.appendChild(this.name);
		recipeElem.appendChild(nameElem);

		final Element desc = new Element("Description");
		desc.appendChild(description);
		recipeElem.appendChild(desc);

		final Element items = new Element("Items");
		for (Map.Entry<Flavor, Double> entry : this.recipe.entrySet()) {
			final Flavor flavor = entry.getKey();
			final Element item = new Element("Item");
			final Element flavorElem = new Element("Flavor");
			item.appendChild(flavorElem);

			final Element flavorName = new Element("Name");
			flavorName.appendChild(flavor.getName());
			flavorElem.appendChild(flavorName);

			final Element manf = new Element("Manufacturer");
			manf.appendChild(flavor.getManufacturer());
			flavorElem.appendChild(manf);

			final Element amount = new Element("Amount");
			amount.appendChild(Double.toString(entry.getValue()));
			item.appendChild(amount);
			items.appendChild(item);
		}
		recipeElem.appendChild(items);
		return new Document(root);
	}

	public static Recipe constructFromXml(File file)
			throws IOException, ParsingException {
		final Builder builder = new Builder();
		final Document doc = builder.build(file);
		final Element root = doc.getRootElement();

		final Element recipeElem = root.getFirstChildElement("Recipe");
		final Element nameElem = recipeElem.getFirstChildElement("Name");
		final String name = nameElem.getChild(0).getValue();

		final Element descElem = recipeElem.getFirstChildElement("Description");
		final String description = descElem != null ? descElem.getChild(0).getValue() : null;

		final Elements items = recipeElem.getFirstChildElement("Items").getChildElements();
		final Map<Flavor, Double> recipeMap = new HashMap<>();
		for (int i = 0; i < items.size(); i++) {
			final Element item = items.get(i);
			final Element flavorElem = item.getFirstChildElement("Flavor");
			final String flavorName = flavorElem.getFirstChildElement("Name").getChild(0).getValue();
			final String flavorManf = flavorElem.getFirstChildElement("Manufacturer").getChild(0).getValue();
			final Flavor flavor = new Flavor(flavorManf, flavorName);

			final double amount = Double.valueOf(item.getFirstChildElement("Amount").getChild(0).getValue());
			recipeMap.put(flavor, amount);
		}
		return new Recipe(name, description, recipeMap);
	}
}
