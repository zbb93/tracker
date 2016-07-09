package zbb.tracker;

import zbb.entities.Flavor;
import zbb.entities.Recipe;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import org.jetbrains.annotations.*;

/**
 * Created by zbb on 6/6/16.
 * This is where the magic happens! Keeps track of flavors and recipes etc etc.
 */
public class Tracker {
	/**
	 * Valid paths must end with a trailing slash!
	 */
	private String pathToRecipesFolder;
	private String pathToFlavorFolder;
	private ArrayList<Flavor> flavors;
	private ArrayList<Recipe> recipes;
  //TODO: There needs to be a way to input these values.
	private double vg;
	private double pg;
	private double nicotine;
	private double mwNicotineSolution;

	public static final double MW_VG = 1.260;
	public static final double MW_PG = 1.040;

	/**
	 * Constructor to use if config.properties is successfully located while application is initializing.
	 * @param pathToFlavors path to flavors folder; should be obtained from config.properties
	 * @param pathToRecipes path to recipes folder; should be obtained from config.properties
	 */
	public Tracker(String pathToFlavors, String pathToRecipes) {
		pathToFlavorFolder = pathToFlavors;
		pathToRecipesFolder = pathToRecipes;
		loadFlavorsFromFile();
		loadRecipesFromFile();
		loadPgVgNicFromFile();
	}

	/**
	 * Default constructor to use when config.properties file cannot be found.
	 * Path to flavors set to: ./flavors/
	 * Path to recipes set to: ./recipes/
	 */
	public Tracker() {
		pathToFlavorFolder = "flavors/";
		pathToRecipesFolder = "recipes/";
		loadFlavorsFromFile();
		loadRecipesFromFile();
		loadPgVgNicFromFile();
	}

	private void loadFlavorsFromFile() {
		flavors = new ArrayList<>();
		File flavorFolder = new File("flavors/");
		File[] flavorFiles = flavorFolder.listFiles();
		if (flavorFiles != null) {
			for (File flavorFile : flavorFiles) {
				if (flavorFile.getName().endsWith(".xml")) {
					Flavor flavor = readFlavor(flavorFile.getName());
					flavors.add(flavor);
				}
			}
		}
	}

	private void loadRecipesFromFile() {
		recipes = new ArrayList<>();
		File recipeFolder = new File("recipes/");
		File[] recipeFiles = recipeFolder.listFiles();
		if (recipeFiles != null) {
			for (File recipeFile : recipeFiles) {
				Recipe recipe = readRecipe(recipeFile.getName());
				recipes.add(recipe);
			}
		}
	}

	private void loadPgVgNicFromFile() {
		try {
			File file = new File("flavors/misc.txt");
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			vg = dis.readDouble();
			pg = dis.readDouble();
			nicotine = dis.readDouble();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	private void writePgVgNicToFile() {
		try {
			File file = new File("flavors/misc.txt");
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeDouble(pg);
			dos.writeDouble(vg);
			dos.writeDouble(nicotine);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}


	public void addFlavor(@NotNull Flavor flavor) {
		flavors.add(flavor);
		writeFlavorToFile(flavor);
	}

	public void addRecipe(@NotNull Recipe recipe) {
		recipes.add(recipe);
		writeRecipeToFile(recipe);
	}

	private void writeFlavorToFile(Flavor flavor) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			String fileName = pathToFlavorFolder + flavor.getName() + ".xml";
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(flavor);
		}
		catch(IOException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (NullPointerException|IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Nullable
	private Flavor readFlavor(String fileName) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Flavor flavor = null;
		try {
			fis = new FileInputStream(pathToFlavorFolder + fileName);
			ois = new ObjectInputStream(fis);
			flavor = (Flavor) ois.readObject();
		}
		catch(IOException | ClassNotFoundException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (NullPointerException|IOException e) {
				e.printStackTrace();
			}
		}
		return flavor;
	}

	private void writeRecipeToFile(Recipe recipe) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			String fileName = pathToRecipesFolder + recipe.getName() + ".xml";
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(recipe);
		}
		catch(IOException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (NullPointerException|IOException e) {
				e.printStackTrace();
			}
		}
	}



	@Nullable
	private Recipe readRecipe(String fileName) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Recipe recipe = null;
		try {
			fis = new FileInputStream(pathToRecipesFolder + fileName);
			ois = new ObjectInputStream(fis);
			recipe = (Recipe) ois.readObject();
		}
		catch(IOException|ClassNotFoundException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (NullPointerException|IOException e) {
				e.printStackTrace();
			}
		}
		return recipe;
	}


	@NotNull
	public List<Flavor> getFlavors() {
		return flavors;
	}

	@Nullable
	public Flavor findFlavor(@NotNull String manf, @NotNull String name) {
		Flavor toFind = new Flavor(manf, name);
		for (Flavor flavor : flavors) {
			if (flavor.equals(toFind)) {
				return flavor;
			}
		}
		return null;
	}

	@Nullable
	public Recipe findRecipe(@NotNull String name) {
		for (Recipe recipe : recipes) {
			if (recipe.getName().equals(name)) {
				return recipe;
			}
		}
		return null;
	}

	public ArrayList<Recipe> getRecipes() {
		return recipes;
	}

	public void useVg(double amountUsed) {
		vg -= amountUsed;
	}

	public void addVg(double amountToAdd) {
		vg += amountToAdd;
	}

	public double getVg() {
		return vg;
	}

	public void usePg(double amountUsed) {
		pg -= amountUsed;
	}

	public void addPg(double amountToAdd) {
		pg += amountToAdd;
	}

	public double getPg() {
		return pg;
	}

	public void useNicotine(double amountUsed) {
		nicotine -= amountUsed;
	}

	public void addNicotine(double amountToAdd) {
		nicotine += amountToAdd;
	}

	public double getNicotine() {
		return nicotine;
	}

	public void useFlavor(@NotNull Flavor flavorUsed, double amount) {
		for (Flavor flavor : flavors) {
			if (flavor.equals(flavorUsed)) {
				flavor.use(amount);
				writeFlavorToFile(flavor);
				break;
			}
		}
	}

	public void save() {
		flavors.stream().forEach(this::writeFlavorToFile);
		recipes.stream().forEach(this::writeRecipeToFile);
		writePgVgNicToFile();
	}

	public void calculateMWofNicotine(@Nullable String mg, @Nullable String pgRatio, @Nullable String vgRatio) {
		final double MW_NICOTINE = 1.01;

		if (mg == null || pgRatio == null || vgRatio == null) {
			return;
		}
		double concentration = Double.parseDouble(mg);
		double pg = Double.parseDouble(pgRatio);
		double vg = Double.parseDouble(vgRatio);

		double nicotinePercent = concentration / 10;
		double weightOfNicotineInSolution = nicotinePercent * MW_NICOTINE;
		double pgPercent = (100 - nicotinePercent) / (100 / pg);
		double weightOfPgInSolution = pgPercent * MW_PG;
		double vgPercent = (100 - nicotinePercent) / (100 / vg);
		double weightOfVgInSolution = vgPercent * MW_VG;
		mwNicotineSolution = (weightOfNicotineInSolution + weightOfPgInSolution + weightOfVgInSolution) / 100;
	}

	public double getMwNicotine() {
		return mwNicotineSolution;
	}

	public List<Flavor> buildAutoShoppingList(double amount) {
		return flavors.stream().filter(flavor -> flavor.getAmountRemaining() < amount).collect(Collectors.toList());
	}

	public List<Recipe> getRecipesThatUse(@NotNull Flavor flavor) {
		List<Recipe> usedIn = new LinkedList<>();
		for (Recipe recipe : recipes) {
			Map<Flavor, Double> flavors = recipe.getRecipe();
			for (Flavor flavorInRecipe : flavors.keySet()) {
				if (flavor.equals(flavorInRecipe)) {
					usedIn.add(recipe);
					break;
				}
			}
		}
		return usedIn;
	}

	public void setPathToRecipes(String path) {
		pathToRecipesFolder = path;
	}

	public void setPathToFlavors(String path) {
		pathToFlavorFolder = path;
	}

	public void writeListToFile(String name, List<Flavor> shoppingList) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		LinkedList<Flavor> list = new LinkedList<>(shoppingList);
		try {
			String fileName = "lists/" + name + ".xml";
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
		}
		catch(IOException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (NullPointerException|IOException e) {
				e.printStackTrace();
			}
		}
	}

	@NotNull
	public List<Flavor> readListFromFile(String name) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		List<Flavor> shoppingList = new LinkedList<>();
		try {
			String fileName = "lists/" + name + ".xml";
			fis = new FileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			shoppingList = (List<Flavor>) ois.readObject();
		}
		catch(ClassNotFoundException | IOException exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return shoppingList;
	}
}
