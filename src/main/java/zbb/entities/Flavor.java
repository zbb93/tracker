package zbb.entities;

import java.io.*;
import java.util.*;

import nu.xom.Document;
import nu.xom.Element;
import org.jetbrains.annotations.*;

/**
 * Created by zbb on 6/6/16.
 * This class represents a basic flavor.
 */
public class Flavor implements Serializable{

	private String manufacturer;
	private String name;
	private double amountRemaining;
	private List<String> categories;
	private final double molecularWeight = 1.0;

	public Flavor(@NotNull String manf, @NotNull String name) {
		this(manf, name, null, -1);
	}

	public Flavor(@NotNull String manf, @NotNull String name, @Nullable List<String> categories, double amount) {
		this.manufacturer = manf;
		this.name = name;
		if (categories != null) {
			this.categories = categories;
		} else {
			this.categories = new LinkedList<>();
		}
		if (amount > 0) {
			this.amountRemaining = amount;
		}
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getName() {
		return name;
	}

	public double getAmountRemaining() {
		return amountRemaining;
	}

	public void use(double amountUsed) {
		amountRemaining -= amountUsed;
	}

	@Override
	public String toString() {
		return name + " (" + manufacturer + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} else if (!(other instanceof Flavor)) {
			return false;
		} else {
			return this.name.equals(((Flavor) other).getName()) &&
					this.manufacturer.equals(((Flavor) other).getManufacturer());
		}
	}

	@Override
	public int hashCode() {
		return 13*name.hashCode() + 11* manufacturer.hashCode();
	}

	public double getMW() {
		return this.molecularWeight;
	}

	public List<String> getCategories() {
		return categories;
	}

	public Document toXml() {
		Element root = new Element("Root");
		Element flavor = new Element("Flavor");
		root.appendChild(flavor);
		Element name = new Element("Name");
		name.appendChild(this.name);
		Element manf = new Element("Manufacturer");
		manf.appendChild(this.manufacturer);
		Element amtRem = new Element("AmountRemaining");
		amtRem.appendChild(Double.toString(this.amountRemaining));
		Element categories = new Element("Categories");
		for (String s : this.categories) {
			Element category = new Element("Category");
			category.appendChild(s);
			categories.appendChild(category);
		}
		flavor.appendChild(name);
		flavor.appendChild(manf);
		flavor.appendChild(categories);
		flavor.appendChild(amtRem);
		return new Document(root);
	}
}
