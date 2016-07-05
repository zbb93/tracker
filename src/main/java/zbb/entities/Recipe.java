package zbb.entities;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
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

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}


	public Map<Flavor, Double> getRecipe() {
		return recipe;
	}

	@XmlElement
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
}
