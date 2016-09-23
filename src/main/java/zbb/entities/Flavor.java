package zbb.entities;

import java.io.*;
import java.util.*;

import nu.xom.*;
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
		final Element root = new Element("Root");
		final Element flavor = new Element("Flavor");
		root.appendChild(flavor);
		final Element nameElem = new Element("Name");
		nameElem.appendChild(name);
		final Element manfElem = new Element("Manufacturer");
		manfElem.appendChild(manufacturer);
		final Element amtRem = new Element("AmountRemaining");
		amtRem.appendChild(Double.toString(this.amountRemaining));
		final Element categoriesElem = new Element("Categories");
		for (String s : this.categories) {
			final Element category = new Element("Category");
			category.appendChild(s);
			categoriesElem.appendChild(category);
		}
		flavor.appendChild(nameElem);
		flavor.appendChild(manfElem);
		flavor.appendChild(categoriesElem);
		flavor.appendChild(amtRem);
		return new Document(root);
	}

	public static Flavor constructFromXml(File file)
			throws IOException, ParsingException {
		final Builder builder = new Builder();
		final Document doc = builder.build(file);
		final Element root = doc.getRootElement();
		final Element flavorElem = root.getFirstChildElement("Flavor");
		final Elements flavorNameElem = flavorElem.getChildElements();
		final String flavorName = flavorNameElem.get(0).getValue();

		final Element manf = flavorElem.getFirstChildElement("Manufacturer");
		final String flavorManf = manf.getChild(0).getValue();

		final Element categoriesElem = flavorElem.getFirstChildElement("Categories");
		final Elements categoryElems = categoriesElem.getChildElements();
		final List<String> categories = new LinkedList<>();
		for (int i = 0; i < categoryElems.size(); i++) {
			categories.add(categoryElems.get(i).getValue());
		}

		final Element amtRemElem = flavorElem.getFirstChildElement("AmountRemaining");
		final double amtRem = Double.valueOf(amtRemElem.getChild(0).getValue());

		return new Flavor(flavorManf, flavorName, categories, amtRem);
	}
}
