package zbb.entities;

import java.io.*;
import java.util.*;

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
	private final double MW = 1.0;

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
		return name + '_' + manufacturer;
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
		return this.MW;
	}

	public List<String> getCategories() {
		return categories;
	}
}
