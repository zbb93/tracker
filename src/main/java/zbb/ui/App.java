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

package zbb.ui;

import zbb.entities.*;
import zbb.tracker.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

import org.jetbrains.annotations.*;

public class App {

	private static Tracker tracker;
	private static JFrame frame;
	private static JPanel panel;
	private static Properties properties;

	public static void main(String[] args) {
		init();
		javax.swing.SwingUtilities.invokeLater(App::createAndShowGUI);
	}

	private static void init() {
		tracker = new Tracker();
		properties = new Properties();
		try {
			String filename = "config.properties";
			FileInputStream fis = new FileInputStream(filename);
			if (!fis.getFD().valid()) {
				throw new IOException("Sorry, unable to find " + filename);
			}
			properties.load(fis);
		} catch (IOException e) {
			//TODO: error message cannot locate properties file. cleanup and close input stream.
		}
	}

	private static void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Eliquid Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Preferences");
		menuItem.addActionListener(a -> showPreferencesView());
		menu.add(menuItem);
		menuBar.add(menu);
		menu = new JMenu("View");
		menuItem = new JMenuItem("Recipes");
		menuItem.addActionListener(a -> showRecipeView());
		menu.add(menuItem);
		menuItem = new JMenuItem("Flavors");
		menuItem.addActionListener(a -> showFlavorView());
		menu.add(menuItem);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);

		showMainMenu();

		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static void showFlavorView() {
		panel.removeAll();
		JPanel buttonPanel = new JPanel();
		JButton createFlavor = new JButton("Make a new flavor");
		createFlavor.addActionListener(a -> showNewFlavorView());
		JButton showFlavors = new JButton("View Flavors");
		showFlavors.addActionListener(a -> showExistingFlavorsView());
		buttonPanel.add(createFlavor);
		buttonPanel.add(showFlavors);
		panel.add(buttonPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void showRecipeView() {
		panel.removeAll();
		JPanel buttonPanel = new JPanel();
		JButton createRecipe = new JButton("Make a new recipe");
		createRecipe.addActionListener(a -> showNewRecipeView());
		buttonPanel.add(createRecipe);
		JButton viewAllRecipes = new JButton("View recipes");
		viewAllRecipes.addActionListener(a -> showExistingRecipesView());
		buttonPanel.add(viewAllRecipes);
		JButton makeARecipe = new JButton("Make a recipe");
		makeARecipe.addActionListener(a -> showChooseRecipeToMakeView());
		buttonPanel.add(makeARecipe);
		panel.add(buttonPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void showNewRecipeView() {
		JFrame newRecipeFrame = new JFrame("Create a new recipe");
		newRecipeFrame.setLayout(new BoxLayout(newRecipeFrame.getContentPane(), BoxLayout.Y_AXIS));
		newRecipeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel newRecipePanel = new JPanel(new SpringLayout());
		int numFlavors = Integer.parseInt(JOptionPane.showInputDialog(frame, "How many flavors are in this recipe?"));

		JLabel recipeName = new JLabel("Name");
		JTextField recipeNameField = new JTextField(5);
		recipeName.setLabelFor(recipeNameField);
		newRecipePanel.add(recipeName);
		newRecipePanel.add(recipeNameField);

		JLabel description = new JLabel("Description");
		JTextField descriptionField = new JTextField(25);
		description.setLabelFor(descriptionField);
		newRecipePanel.add(description);
		newRecipePanel.add(descriptionField);

		SpringUtilities.makeCompactGrid(newRecipePanel, 2, 2, 6, 6, 6, 6);

		JPanel newRecipeFlavors = new JPanel(new SpringLayout());
		JLabel flavorName;
		JLabel flavorManf;
		JLabel flavorAmount;
		JTextField flavorNameField;
		JTextField flavorManfField;
		JTextField flavorAmountField;
		List<JTextField> textFields = new ArrayList<>();
		for (int i = 1; i <= numFlavors; i++) {
			flavorName = new JLabel("Flavor" + i + ": Name");
			flavorNameField = new JTextField(10);
			flavorName.setLabelFor(flavorNameField);
			newRecipeFlavors.add(flavorName);
			newRecipeFlavors.add(flavorNameField);
			textFields.add(flavorNameField);

			flavorManf = new JLabel("Manufacturer");
			flavorManfField = new JTextField(10);
			flavorManf.setLabelFor(flavorManfField);
			newRecipeFlavors.add(flavorManf);
			newRecipeFlavors.add(flavorManfField);
			textFields.add(flavorManfField);

			flavorAmount = new JLabel("Amount (%)");
			flavorAmountField = new JTextField(2);
			flavorAmount.setLabelFor(flavorAmountField);
			newRecipeFlavors.add(flavorAmount);
			newRecipeFlavors.add(flavorAmountField);
			textFields.add(flavorAmountField);
		}
		SpringUtilities.makeCompactGrid(newRecipeFlavors, numFlavors, 6, 6, 6, 6, 6);

		JPanel buttonPanel = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(a -> addRecipe(recipeNameField.getText(), descriptionField.getText(), textFields));
		ok.addActionListener(a -> newRecipeFrame.dispose());
		buttonPanel.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(a -> newRecipeFrame.dispose());
		buttonPanel.add(cancel);
		newRecipeFrame.getContentPane().add(newRecipePanel);
		newRecipeFrame.getContentPane().add(newRecipeFlavors);
		newRecipeFrame.getContentPane().add(buttonPanel);
		newRecipeFrame.pack();
		newRecipeFrame.setLocationRelativeTo(frame);
		newRecipeFrame.setVisible(true);
	}

	private static void showNewFlavorView() {
		JFrame newFlavorFrame = new JFrame("Create a new flavor");
		newFlavorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel newFlavorPanel = new JPanel(new SpringLayout());

		JLabel name = new JLabel("Name");
		JTextField nameField = new JTextField(10);
		name.setLabelFor(nameField);
		newFlavorPanel.add(name);
		newFlavorPanel.add(nameField);


		JLabel manufacturer = new JLabel("Manufacturer");
		JTextField manfField = new JTextField(10);
		manufacturer.setLabelFor(manfField);
		newFlavorPanel.add(manufacturer);
		newFlavorPanel.add(manfField);

		JLabel amountRemaining = new JLabel("Amount Remaining");
		JTextField amtRemField = new JTextField(3);
		amountRemaining.setLabelFor(amtRemField);
		newFlavorPanel.add(amountRemaining);
		newFlavorPanel.add(amtRemField);

		JButton ok = new JButton("Ok");
		ok.addActionListener(a -> addFlavor(nameField.getText(), manfField.getText(), Integer.parseInt(amtRemField.getText())));
		ok.addActionListener(a -> newFlavorFrame.dispose());
		newFlavorPanel.add(ok);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(a -> newFlavorFrame.dispose());
		newFlavorPanel.add(cancel);

		SpringUtilities.makeCompactGrid(newFlavorPanel, 4, 2, 6, 6, 6, 6);
		newFlavorFrame.add(newFlavorPanel);
		newFlavorFrame.pack();
		newFlavorFrame.setLocationRelativeTo(frame);
		newFlavorFrame.setVisible(true);
	}

	private static void showExistingFlavorsView() {
		//TODO: display remaining amount of PG/VG/Nicotine somewhere in this window
		panel.removeAll();
		JTable flavorTable = new JTable(new FlavorTableModel(tracker.getFlavors()));
		flavorTable.getSelectionModel().addListSelectionListener(a -> {
				if (flavorTable.getSelectedRow() > -1 && !a.getValueIsAdjusting()) {
					String manf = (String) flavorTable.getValueAt(flavorTable.getSelectedRow(), 1);
					String name = (String) flavorTable.getValueAt(flavorTable.getSelectedRow(), 0);
					Flavor flavor = tracker.findFlavor(manf, name);
					showIndividualFlavorView(flavor);
				}
		});
		flavorTable.setPreferredSize(new Dimension(500, 200));
		panel.add(flavorTable);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void showExistingRecipesView() {
		panel.removeAll();
		JTable recipeTable = new JTable(new AllRecipesTableModel(tracker.getRecipes()));
		recipeTable.getSelectionModel().addListSelectionListener(a -> {
				if (recipeTable.getSelectedRow() > -1 && !a.getValueIsAdjusting()) {
					String name = (String) recipeTable.getValueAt(recipeTable.getSelectedRow(), 0);
					Recipe recipe = tracker.findRecipe(name);
					showIndividualRecipeView(recipe);
				}
			});
		recipeTable.setPreferredSize(new Dimension(500, 200));
		panel.add(recipeTable);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void showChooseRecipeToMakeView() {
		panel.removeAll();
		JTable recipeTable = new JTable(new AllRecipesTableModel(tracker.getRecipes()));
		recipeTable.getSelectionModel().addListSelectionListener(a -> {
				if (recipeTable.getSelectedRow() > -1 && !a.getValueIsAdjusting()) {
					String name = (String) recipeTable.getValueAt(recipeTable.getSelectedRow(), 0);
					Recipe recipe = tracker.findRecipe(name);
					showMakeRecipeView(recipe);
				}
			});
		recipeTable.setPreferredSize(new Dimension(500, 200));
		panel.add(recipeTable);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void addFlavor(@NotNull String name, @NotNull String manf, int amtRem) {
		Flavor flavor = new Flavor(manf, name, amtRem);
		tracker.addFlavor(flavor);
	}

	private static void showIndividualFlavorView(Flavor flavor) {
		JFrame flavorFrame = new JFrame(flavor.getName());
		flavorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel flavorPanel = new JPanel();

		JPanel labelPanel = new JPanel();
		JPanel contentPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		JLabel nameLabel = new JLabel("Name: ");
		JLabel name = new JLabel(flavor.getName());
		labelPanel.add(nameLabel);
		contentPanel.add(name);

		JLabel manfLabel = new JLabel("Manufacturer: ");
		JLabel manf = new JLabel(flavor.getManufacturer());
		labelPanel.add(manfLabel);
		contentPanel.add(manf);

		JLabel amountRemainingLabel = new JLabel("Amount Remaining (ml): ");
		JLabel amountRemaining = new JLabel(Double.toString(flavor.getAmountRemaining()));
		labelPanel.add(amountRemainingLabel);
		contentPanel.add(amountRemaining);

		flavorPanel.add(labelPanel);
		flavorPanel.add(contentPanel);
		flavorFrame.add(flavorPanel);
		flavorFrame.pack();
		flavorFrame.setLocationRelativeTo(frame);
		flavorFrame.setVisible(true);
	}

	private static void showPreferencesView() {
		panel.removeAll();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel nicotineLabel = new JLabel("Nicotine");
		panel.add(nicotineLabel);
		JPanel nicotineSettings = new JPanel(new SpringLayout());

		JLabel nicotineStrength = new JLabel("Strength (mg/ml):");
		JTextField nicotineStrengthField = new JTextField(properties.getProperty("nicotine.mg") != null ?
				properties.getProperty("nicotine.mg") : "");

		nicotineSettings.add(nicotineStrength);
		nicotineSettings.add(nicotineStrengthField);

		JLabel nicotinePg = new JLabel("Percent PG:");
		JTextField nicotinePgField = new JTextField(properties.getProperty("nicotine.pg") != null ?
			properties.getProperty("nicotine.pg") : "");

		nicotineSettings.add(nicotinePg);
		nicotineSettings.add(nicotinePgField);

		JLabel nicotineVg = new JLabel("Percent VG:");
		JTextField nicotineVgField = new JTextField(properties.getProperty("nicotine.vg") != null ?
			properties.getProperty("nicotine.vg") : "");

		nicotineSettings.add(nicotineVg);
		nicotineSettings.add(nicotineVgField);

		JButton ok = new JButton("Ok");
		ok.addActionListener(a -> {
			properties.setProperty("nicotine.mg", nicotineStrengthField.getText());
			properties.setProperty("nicotine.pg", nicotinePgField.getText());
			properties.setProperty("nicotine.vg", nicotineVgField.getText());
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(a -> showMainMenu());

		nicotineSettings.add(ok);
		nicotineSettings.add(cancel);

		SpringUtilities.makeCompactGrid(nicotineSettings, 4, 2, 6, 6, 6, 6);

		panel.add(nicotineSettings);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void showMainMenu() {
		panel.removeAll();
		JPanel menuButtons = new JPanel();
		menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.X_AXIS));

		JButton flavor = new JButton("Flavor");
		flavor.addActionListener(a -> showFlavorView());
		menuButtons.add(flavor);
		JButton recipe = new JButton("Recipe");
		recipe.addActionListener(a -> showRecipeView());
		menuButtons.add(recipe);
		JButton report = new JButton("Reports");
		report.addActionListener(a -> showReportView());
		menuButtons.add(report);

		panel.add(menuButtons);
		frame.pack();
	}

	private static void showReportView() {
		panel.removeAll();

		JLabel todo = new JLabel("TODO");

		panel.add(todo);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void addRecipe(String name, String description, List<JTextField> textFields) {
		Map<Flavor, Double> recipeMap = getRecipeFromTextFields(textFields);
		Recipe recipe = new Recipe(name, description, recipeMap);
		tracker.addRecipe(recipe);
	}

	private static Map<Flavor, Double> getRecipeFromTextFields(List<JTextField> textFields) {
		Map<Flavor, Double> recipe = new HashMap<>();
		for (Iterator<JTextField> i = textFields.iterator(); i.hasNext();) {
			JTextField nameField = i.next();
			JTextField manfField = i.next();
			JTextField amountField = i.next();
			String name = nameField.getText();
			String manf = manfField.getText();
			double amount = Double.parseDouble(amountField.getText());
			Flavor flavor = tracker.findFlavor(manf, name);
			recipe.put(flavor, amount);
		}
		return recipe;
	}

	private static void showIndividualRecipeView(Recipe recipe) {
		JFrame recipeFrame = new JFrame(recipe.getName());
		recipeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel recipePanel = new JPanel();
		recipePanel.setLayout(new BoxLayout(recipePanel, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();
		JPanel descriptionPanel = new JPanel();
//		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
//		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));

		JLabel nameLabel = new JLabel("Name: ");
		JLabel name = new JLabel(recipe.getName());
		namePanel.add(nameLabel);
		namePanel.add(name);

		JLabel descriptionLabel = new JLabel("Description: ");
		JLabel description = new JLabel(recipe.getDescription());
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(description);

		recipePanel.add(namePanel);
		recipePanel.add(descriptionPanel);

		JLabel flavorName;
		JLabel flavorAmount;
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			JPanel flavorPanel = new JPanel();
			flavorName = new JLabel(entry.getKey().getName() + " (" + entry.getKey().getManufacturer() + ")");
			flavorAmount = new JLabel(entry.getValue() + "%");
			flavorPanel.add(flavorName);
			flavorPanel.add(flavorAmount);
			recipePanel.add(flavorPanel);
		}

		recipeFrame.add(recipePanel);
		recipeFrame.pack();
		recipeFrame.setLocationRelativeTo(frame);
		recipeFrame.setVisible(true);
	}

	private static void showMakeRecipeView(Recipe recipe) {
		int amountToMake = Integer.parseInt(JOptionPane.showInputDialog(
				frame, "How many ml to make?"));
		String pgVgRatio =JOptionPane.showInputDialog(frame, "Enter your PG/VG ratio (PG/VG)");
		String[] pgVgSplit = pgVgRatio.split("/");
		double pg = Double.parseDouble(pgVgSplit[0]);
		double vg = Double.parseDouble(pgVgSplit[1]);



		//TODO: Maybe center the labelPanel better?
		JFrame recipeFrame = new JFrame(recipe.getName());
		recipeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel recipePanel = new JPanel();
		recipePanel.setLayout(new BoxLayout(recipePanel, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();
		JPanel descriptionPanel = new JPanel();

		JLabel nameLabel = new JLabel("Name: ");
		JLabel name = new JLabel(recipe.getName());
		namePanel.add(nameLabel);
		namePanel.add(name);

		JLabel descriptionLabel = new JLabel("Description: ");
		JLabel description = new JLabel(recipe.getDescription());
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(description);

		recipePanel.add(namePanel);
		recipePanel.add(descriptionPanel);

		JLabel nicotineLabel = new JLabel("Nicotine: ");
		//TODO: MW of nicotine should be determined through user input
		JLabel nicotine = new JLabel(Double.toString(calculateMlNicotineToAdd(amountToMake) * Tracker.MW_NICOTINE_100VG));

		recipePanel.add(nicotineLabel);
		recipePanel.add(nicotine);

		//TODO: limit significant digits of output
		JLabel pgLabel = new JLabel("Propylene Glycol: ");
		JLabel pgAmountLabel = new JLabel(Double.toString(calculateMlPgToAdd(recipe, amountToMake, pg) * Tracker.MW_PG) + " grams");

		recipePanel.add(pgLabel);
		recipePanel.add(pgAmountLabel);

		JLabel vgLabel = new JLabel("Vegetable Glycerin: ");
		JLabel vgAmountLabel = new JLabel(Double.toString(calculateMlVgToAdd(amountToMake, vg) * Tracker.MW_VG) + " grams");

		recipePanel.add(vgLabel);
		recipePanel.add(vgAmountLabel);


		JLabel flavorName;
		JLabel flavorAmount;
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			JPanel flavorPanel = new JPanel();
			flavorName = new JLabel(entry.getKey().getName() + " (" + entry.getKey().getManufacturer() + ")");
			flavorAmount = new JLabel(Double.toString(calculateMlFlavorToAdd(entry.getKey(),
					entry.getValue(), amountToMake)) + " grams");
			flavorPanel.add(flavorName);
			flavorPanel.add(flavorAmount);
			recipePanel.add(flavorPanel);
		}

		JPanel buttonPanel = new JPanel();
		JButton makeThisRecipe = new JButton("Make this recipe");
		makeThisRecipe.addActionListener(a -> makeRecipe(recipe, amountToMake, pg, vg));
		buttonPanel.add(makeThisRecipe);
		recipePanel.add(buttonPanel);

		recipeFrame.add(recipePanel);
		recipeFrame.pack();
		recipeFrame.setVisible(true);


	}

	private static void makeRecipe(Recipe recipe, double amount, double pg, double vg) {
		tracker.usePg(calculateMlPgToAdd(recipe, amount, pg));
		tracker.useVg(calculateMlVgToAdd(amount, vg));
		tracker.useNicotine(calculateMlNicotineToAdd(amount));
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			tracker.useFlavor(entry.getKey(), calculateMlFlavorToAdd(entry.getKey(), entry.getValue(), amount));
		}
		tracker.save();
	}

	private static double calculateMlFlavorToAdd(Flavor flavor, double percentToAdd, double amountToMake) {
		return (percentToAdd/100) * amountToMake * flavor.getMW();
	}

	private static double calculateMlPgToAdd(Recipe recipe, double amountToMake, double pgProportion) {
		//double pgProportion = Double.parseDouble(properties.getProperty("pg.proportion"));
		double nicotinePg = Double.parseDouble(properties.getProperty("nicotine.pg"));
		double pgToAdd = (amountToMake * (pgProportion/100));
		return pgToAdd - (calculateMlNicotineToAdd(amountToMake) * (nicotinePg/100) + sumFlavorAmounts(recipe, amountToMake));
	}

	private static double calculateMlNicotineToAdd(double amountToMake) {
		double nicotineMg = Double.parseDouble(properties.getProperty("nicotine.mg"));
		double nicotineStrength = Double.parseDouble(properties.getProperty("nicotine.strength"));
		return (nicotineStrength/nicotineMg) * amountToMake;
	}

	private static double calculateMlVgToAdd(double amountToMake, double vgProportion) {
		double nicotineVg = Double.parseDouble(properties.getProperty("nicotine.vg"));
		//double vgProportion = Double.parseDouble(properties.getProperty("vg.proportion"));
		double vgToAdd = (amountToMake * (vgProportion/100));
		return vgToAdd - (calculateMlNicotineToAdd(amountToMake) * (nicotineVg/100));
	}

	private static double sumFlavorAmounts(Recipe recipe, double amount) {
		double sum = 0.0;
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			sum += (entry.getValue()/100) * amount;
		}
		return sum;
	}
}