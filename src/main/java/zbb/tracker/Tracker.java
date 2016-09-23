package zbb.tracker;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import zbb.entities.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;
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
	private Logger logger;

	public static final double MW_VG = 1.260;
	public static final double MW_PG = 1.040;

	/**
	 * Constructor to use if config.properties is successfully located while application is initializing.
	 * @param pathToFlavors path to flavors folder; should be obtained from config.properties
	 * @param pathToRecipes path to recipes folder; should be obtained from config.properties
	 */
	public Tracker(String pathToFlavors, String pathToRecipes) {
		logger = Logger.getLogger(Tracker.class.getName());
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
		File flavorFolder = new File(pathToFlavorFolder);
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
		File recipeFolder = new File(pathToRecipesFolder);
		File[] recipeFiles = recipeFolder.listFiles();
		if (recipeFiles != null) {
			for (File recipeFile : recipeFiles) {
				Recipe recipe = readRecipe(recipeFile.getName());
				recipes.add(recipe);
			}
		}
	}

	private void loadPgVgNicFromFile() {
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			File file = new File(pathToFlavorFolder + "misc.txt");
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			vg = dis.readDouble();
			pg = dis.readDouble();
			nicotine = dis.readDouble();
		} catch (IOException exc) {
			logger.log(Level.SEVERE, "Error occurred while attempting to load PG/VG/Nicotine information", exc);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (dis != null) {
					dis.close();
				}
			} catch (IOException|NullPointerException e) {
				logger.log(Level.WARNING, "Error occurred while attempting to close resources after loading PG/VG/Nicotine", e);
			}
		}
	}

	private void writePgVgNicToFile() {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			File file = new File(pathToFlavorFolder + "misc.txt");
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			dos.writeDouble(pg);
			dos.writeDouble(vg);
			dos.writeDouble(nicotine);
		} catch (IOException exc) {
			logger.log(Level.SEVERE, "An error occurred while trying to persist PG/VG/Nicotine information", exc);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (dos != null) {
					dos.close();
				}
			} catch (IOException|NullPointerException e) {
				logger.log(Level.WARNING, "Error occurred while attempting to close resources after saving PG/VG/Nicotine", e);
			}
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
		String fileName = pathToFlavorFolder + flavor.getName() + ".xml";
		try {
			fos = new FileOutputStream(fileName);
			Serializer serializer = new Serializer(fos, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(64);
			Document doc = flavor.toXml();
			serializer.write(doc);
		}
		catch(IOException exc) {
			String errorMessage = "An error occurred while trying to save a Flavor: \n" +
					"\tFlavor: " + flavor.toString() +
					"\tFile Path: " + fileName +
					"Document:\n\t" + flavor.toXml().toString();
			logger.log(Level.SEVERE, errorMessage, exc);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (NullPointerException|IOException e) {
				logger.log(Level.WARNING, "An error occurred while trying to close resources after saving a flavor.", e);
			}
		}
	}

	@Nullable
	private Flavor readFlavor(String fileName) {
		String path = pathToFlavorFolder + fileName;
		Flavor flavor = null;
		try {
			flavor = Flavor.constructFromXml(new File(path));
		} catch (ParsingException |IOException exc) {
			String error = "An error occurred while trying to read a flavor from a file." +
					"\n\tPath: " + fileName;
			logger.log(Level.SEVERE, error, exc);
		}
		return flavor;
	}

	private void writeRecipeToFile(Recipe recipe) {
		FileOutputStream fos = null;

		String fileName = pathToRecipesFolder + recipe.getName() + ".xml";
		try {
			fos = new FileOutputStream(fileName);
			Serializer serializer = new Serializer(fos, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(64);
			Document doc = recipe.toXml();
			serializer.write(doc);
		}
		catch(IOException exc) {
			logger.log(Level.SEVERE, "An error occurred while trying to save a recipe:\n" +
					"\tRecipe: " + recipe.toString() +
					"\tFile Path: " + fileName, exc);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "An error occurred while trying to close resources after saving a recipe", e);
			}
		}
	}



	@Nullable
	private Recipe readRecipe(String fileName) {
		String path = pathToRecipesFolder + fileName;
		Recipe recipe = null;
		try {
			recipe = Recipe.constructFromXml(new File(path));
		}
		catch(IOException|ParsingException exc) {
			logger.log(Level.SEVERE, "An error occurred while attempting to load a recipe:\n" +
					"\tPath: " + fileName, exc);
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
		flavors.forEach(this::writeFlavorToFile);
		recipes.forEach(this::writeRecipeToFile);
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

	@NotNull
	public List<Flavor> buildAutoShoppingList(double amount) {
		return flavors.stream().filter(flavor -> flavor.getAmountRemaining() < amount).collect(Collectors.toList());
	}

	@NotNull
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
		String fileName = "lists/" + name + ".xml";
		try {
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
		}
		catch(IOException exc) {
			StringBuilder sb = new StringBuilder("An error occurred while attempting to save a list:\n" +
					"\tContents:\n");
			for (Flavor flavor : shoppingList) {
				sb.append("\t\t");
				sb.append(flavor.toString());
			}
			sb.append("\tPath: ");
			sb.append(fileName);
			String errorMsg = sb.toString();
			logger.log(Level.SEVERE, errorMsg);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "An error occurred attempting to close resources after saving a list", e);
			}
		}
	}

	@NotNull
	public List<Flavor> readListFromFile(String name) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		List<Flavor> shoppingList = new LinkedList<>();
		String fileName = "lists/" + name + ".xml";
		try {
			fis = new FileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			List<?> tempList = (List<?>) ois.readObject();
			shoppingList = tempList.stream().filter(f -> f instanceof Flavor).map(Flavor.class::cast).collect(Collectors.toList());
		}
		catch(ClassNotFoundException | IOException exc) {
			logger.log(Level.SEVERE, "An error occurred while trying to load a list\n\tPath: " + fileName, exc);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "An error occurred attempting to close resources after loading a list", e);
			}
		}
		return shoppingList;
	}

	public double calculatePossibleAmountToMake(Recipe recipe) {
		Flavor limitingFlavor = findLimitingFlavor(recipe);
		double amountPerBottle = recipe.getFlavorAmount(limitingFlavor) * (.01);
		return limitingFlavor.getAmountRemaining() / amountPerBottle;
	}

	@NotNull
	public Flavor findLimitingFlavor(Recipe recipe) {
		Flavor limitingFlavor = new Flavor("", "");
		double lowestSoFar = -1;
		for (Flavor flavor : recipe.getRecipe().keySet()) {
			double amountPerBottle = recipe.getFlavorAmount(flavor) * (.01);
			double possibleAmt = flavor.getAmountRemaining() / amountPerBottle;
			if (possibleAmt < lowestSoFar || Math.abs(lowestSoFar - (-1)) < 0.001) {
				lowestSoFar = possibleAmt;
				limitingFlavor = flavor;
			}
		}
		return  limitingFlavor;
	}
}
