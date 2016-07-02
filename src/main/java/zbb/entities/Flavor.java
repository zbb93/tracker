package zbb.entities;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by zbb on 6/6/16.
 * This class represents a basic flavor.
 */
public class Flavor implements Serializable{

	private String manufacturer;
	private String name;
	private double amountRemaining;
	private final double MW = 1.0;

	public Flavor(@NotNull String manf, @NotNull String name) {
		this(manf, name, -1);
	}

	public Flavor(@NotNull String manf, @NotNull String name, int amount) {
		this.manufacturer = manf;
		this.name = name;
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
			return this.name.equals(((Flavor) other).name) && this.manufacturer.equals(((Flavor) other).manufacturer);
		}
	}

	@Override
	public int hashCode() {
		return 13*name.hashCode() + 11* manufacturer.hashCode();
	}

	public double getMW() {
		return this.MW;
	}

}
