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
import java.text.*;
import java.util.*;
import java.io.*;
import java.util.List;

import org.jetbrains.annotations.*;

public class App {

	private static Tracker tracker;
	private static JFrame frame;
	private static JPanel panel;
	private static Properties properties;

	public static void main(String... args) {
		if (init()) {
			SwingUtilities.invokeLater(App::buildMainFrameShowMainMenu);
		}
	}

	private static boolean init() {
		properties = new Properties();
		try (FileInputStream fis = new FileInputStream("config.properties")) {
			if (!fis.getFD().valid()) {
				throw new IOException("Sorry, unable to find file: " + fis.getFD().toString());
			}
			properties.load(fis);
			tracker = new Tracker(properties.getProperty("flavor.path"), properties.getProperty("recipe.path"));
			tracker.calculateMWofNicotine(properties.getProperty("nicotine.mg"),
					properties.getProperty("nicotine.pg"),
					properties.getProperty("nicotine.vg"));
		} catch (IOException e) {
			tracker = new Tracker();
			int response = JOptionPane.showConfirmDialog(frame,
					"You will not be able to make recipes without setting nicotine properties. Would you like to do this now?",
					"Unable to Locate Properties File", JOptionPane.YES_NO_OPTION);
			if (response == 0) {
				SwingUtilities.invokeLater(App::buildMainFrameShowPrefrences);
			} else {
				SwingUtilities.invokeLater(App::buildMainFrameShowMainMenu);
			}
			return false;
		}
		return true;
	}

	private static void buildMainFrameShowPrefrences() {
		//Create and set up the window.
		frame = new JFrame("Eliquid Manager");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem mainMenu = new JMenuItem("Main Menu");
		mainMenu.addActionListener(a -> showMainMenu());
		menu.add(mainMenu);
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
		showPreferencesView();
	}

	private static void buildMainFrameShowMainMenu() {
		//Create and set up the window.
		frame = new JFrame("Eliquid Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem mainMenu = new JMenuItem("Main Menu");
		mainMenu.addActionListener(a -> showMainMenu());
		menu.add(mainMenu);
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
		panel.removeAll();
		List<Flavor> flavors = tracker.getFlavors();
		//Add dummy flavors for pg/vg/nicotine.
		flavors.add(new Flavor("", "Propylene Glycol", tracker.getPg()));
		flavors.add(new Flavor("", "Vegetable Glycerin", tracker.getVg()));
		flavors.add(new Flavor("", "Nicotine", tracker.getNicotine()));
		JTable flavorTable = new JTable(new FlavorTableModel(flavors));
		flavorTable.getSelectionModel().addListSelectionListener(a -> {
			if (flavorTable.getSelectedRow() > -1 && !a.getValueIsAdjusting()) {
				String name = (String) flavorTable.getValueAt(flavorTable.getSelectedRow(), 0);
				String[] nameAndManf = name.split("\\(");
				if (nameAndManf.length == 2) {
					nameAndManf[1] = nameAndManf[1].replace(")", "");
					nameAndManf[0] = nameAndManf[0].trim();
					nameAndManf[1] = nameAndManf[1].trim();
					Flavor flavor = tracker.findFlavor(nameAndManf[1], nameAndManf[0]);
					if (flavor != null) {
						showIndividualFlavorView(flavor);
					} else {
						JOptionPane.showMessageDialog(flavorTable, "Unable to load information for flavor: " + name,
								"Flavor Does Not Exist", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		flavorTable.setAutoCreateRowSorter(true);
		JScrollPane tablePane = new JScrollPane(flavorTable);
		tablePane.setPreferredSize(new Dimension(700, 200));
		panel.add(tablePane);
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
					if (recipe != null) {
						showIndividualRecipeView(recipe);
					} else {
						JOptionPane.showMessageDialog(recipeTable, "Unable to load information for recipe: " + name,
								"Recipe Does Not Exist", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		recipeTable.setAutoCreateRowSorter(true);
		JScrollPane tablePane = new JScrollPane(recipeTable);
		tablePane.setPreferredSize(new Dimension(700, 200));
		panel.add(tablePane);
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
					if (recipe != null) {
						showMakeRecipeView(recipe);
					} else {
						JOptionPane.showMessageDialog(recipeTable, "Unable to load information for flavor: " + name,
								"Flavor Does Not Exist", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		recipeTable.setAutoCreateRowSorter(true);
		JScrollPane tablePane = new JScrollPane(recipeTable);
		tablePane.setPreferredSize(new Dimension(700, 200));
		panel.add(tablePane);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void addFlavor(@NotNull String name, @NotNull String manf, int amtRem) {
		Flavor flavor = new Flavor(manf, name, amtRem);
		tracker.addFlavor(flavor);
	}

	private static void showIndividualFlavorView(@NotNull Flavor flavor) {
		JFrame flavorFrame = new JFrame(flavor.getName());
		flavorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel flavorPanel = new JPanel();
		flavorPanel.setLayout(new BoxLayout(flavorPanel, BoxLayout.Y_AXIS));

		JPanel labelPanel = new JPanel(new SpringLayout());

		JLabel nameLabel = new JLabel("Name: ");
		JLabel name = new JLabel(flavor.getName());
		labelPanel.add(nameLabel);
		labelPanel.add(name);

		JLabel manfLabel = new JLabel("Manufacturer: ");
		JLabel manf = new JLabel(flavor.getManufacturer());
		labelPanel.add(manfLabel);
		labelPanel.add(manf);

		JLabel amountRemainingLabel = new JLabel("Amount Remaining (ml): ");
		JLabel amountRemaining = new JLabel(Double.toString(flavor.getAmountRemaining()));
		labelPanel.add(amountRemainingLabel);
		labelPanel.add(amountRemaining);
		SpringUtilities.makeCompactGrid(labelPanel, 3, 2, 6, 6, 6, 6);
		flavorPanel.add(labelPanel);
		flavorPanel.add(new JSeparator());

		JPanel lowerPanel = new JPanel();
		JPanel recipePanel = new JPanel(new BorderLayout());
		JLabel recipesUsedIn = new JLabel("Recipes used in:");
		recipePanel.add(recipesUsedIn, BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		List<Recipe> recipes = tracker.getRecipesThatUse(flavor);
		for (Recipe recipe : recipes) {
			JButton recipeName = new JButton(recipe.getName());
			recipeName.addActionListener(a -> showIndividualRecipeView(recipe));
			recipeName.setBorderPainted(false);
			recipeName.setBackground(Color.WHITE);
			recipeName.setFocusPainted(false);
			buttonPanel.add(recipeName);
		}
		lowerPanel.add(recipePanel);
		lowerPanel.add(buttonPanel);
		flavorPanel.add(lowerPanel);

		flavorFrame.add(flavorPanel);
		flavorFrame.pack();
		flavorFrame.setLocationRelativeTo(frame);
		flavorFrame.setVisible(true);
	}

	private static void showPreferencesView() {
		panel.removeAll();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel nicotineLabel = new JLabel("Nicotine Settings");
		panel.add(nicotineLabel);
		JPanel nicotineSettings = new JPanel(new SpringLayout());

		JLabel nicotineStrength = new JLabel("Strength (mg/ml):");
		JTextField nicotineStrengthField = new JTextField(properties.getProperty("nicotine.mg") != null ?
				properties.getProperty("nicotine.mg") : " ");

		nicotineSettings.add(nicotineStrength);
		nicotineSettings.add(nicotineStrengthField);

		JLabel nicotinePg = new JLabel("Percent PG:");
		JTextField nicotinePgField = new JTextField(properties.getProperty("nicotine.pg") != null ?
			properties.getProperty("nicotine.pg") : " ");

		nicotineSettings.add(nicotinePg);
		nicotineSettings.add(nicotinePgField);

		JLabel nicotineVg = new JLabel("Percent VG:");
		JTextField nicotineVgField = new JTextField(properties.getProperty("nicotine.vg") != null ?
			properties.getProperty("nicotine.vg") : " ");

		nicotineSettings.add(nicotineVg);
		nicotineSettings.add(nicotineVgField);
		SpringUtilities.makeCompactGrid(nicotineSettings, 3, 2, 6, 6, 6, 6);
		panel.add(nicotineSettings);
		panel.add(new JSeparator());

		panel.add(new JLabel("Flavor Settings"));
		JPanel flavorSettingPanel = new JPanel(new SpringLayout());
		JLabel amountRemLabel = new JLabel("Amount remaining before adding to shopping list (ml):");
		JTextField amountRemField = new JTextField(properties.getProperty("flavor.amtrem") != null ?
			properties.getProperty("flavor.amtrem") : " ");
		flavorSettingPanel.add(amountRemLabel);
		flavorSettingPanel.add(amountRemField);

		JLabel pathLabel = new JLabel("Path to flavors folder:");
		JTextField pathField = new JTextField(properties.getProperty("flavor.path") != null ?
			properties.getProperty("flavor.path") : " ");
		flavorSettingPanel.add(pathLabel);
		flavorSettingPanel.add(pathField);
		SpringUtilities.makeCompactGrid(flavorSettingPanel, 2, 2, 6, 6, 6, 6);
		panel.add(flavorSettingPanel);
		panel.add(new JSeparator());

		JLabel recipe = new JLabel("Recipe Settings");
		panel.add(recipe);
		JPanel recipePanel = new JPanel(new SpringLayout());
		JLabel recipePathLabel = new JLabel("Path to recipes folder:");
		JTextField recipePathField = new JTextField(properties.getProperty("recipe.path"));
		recipePanel.add(recipePathLabel);
		recipePanel.add(recipePathField);
		SpringUtilities.makeCompactGrid(recipePanel, 1, 2, 6, 6, 6, 6);
		panel.add(recipePanel);

		JPanel buttons = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(a -> {
			properties.setProperty("nicotine.mg", nicotineStrengthField.getText());
			properties.setProperty("nicotine.pg", nicotinePgField.getText());
			properties.setProperty("nicotine.vg", nicotineVgField.getText());
			properties.setProperty("flavor.amtrem", amountRemField.getText());
			properties.setProperty("flavor.path", pathField.getText());
			properties.setProperty("recipe.path", recipePathField.getText());
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(a -> showMainMenu());

		buttons.add(ok);
		buttons.add(cancel);

		panel.add(buttons);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static void showReportView() {
		//TODO: Add an option for autogenerated reports based on more filters than just amount remaining.
		//TODO: Add an option for a shopping list constructed manually.
		panel.removeAll();

		JPanel buttons = new JPanel();
		JButton generateShoppingList = new JButton("Generate a shopping list");
		generateShoppingList.addActionListener(a -> buildAndShowAutoShoppingList());
		buttons.add(generateShoppingList);

		JButton viewExistingList = new JButton("View Existing List");
		viewExistingList.addActionListener(a -> selectExistingListView());
		buttons.add(viewExistingList);


		frame.add(buttons);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	private static void addRecipe(String name, String description, List<JTextField> textFields) {
		Map<Flavor, Double> recipeMap = getRecipeFromTextFields(textFields);
		Recipe recipe = new Recipe(name, description, recipeMap);
		tracker.addRecipe(recipe);
	}

	private static Map<Flavor, Double> getRecipeFromTextFields(@NotNull List<JTextField> textFields) {
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

	private static void showIndividualRecipeView(@NotNull Recipe recipe) {
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

	private static void showMakeRecipeView(@NotNull Recipe recipe) {
		int amountToMake = Integer.parseInt(JOptionPane.showInputDialog(
				frame, "How many ml to make?"));
		String pgVgRatio =JOptionPane.showInputDialog(frame, "Enter your PG/VG ratio (PG/VG)");
		String[] pgVgSplit = pgVgRatio.split("/");
		double pg = Double.parseDouble(pgVgSplit[0]);
		double vg = Double.parseDouble(pgVgSplit[1]);

		JFrame recipeFrame = new JFrame(recipe.getName());
		recipeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel recipePanel = new JPanel();
		recipePanel.setLayout(new BoxLayout(recipePanel, BoxLayout.Y_AXIS));

		JPanel detailPanel = new JPanel(new SpringLayout());
		JLabel nameLabel = new JLabel("Name: ");
		JLabel name = new JLabel(recipe.getName());
		detailPanel.add(nameLabel);
		detailPanel.add(name);

		JLabel descriptionLabel = new JLabel("Description: ");
		JLabel description = new JLabel(recipe.getDescription());
		detailPanel.add(descriptionLabel);
		detailPanel.add(description);

		SpringUtilities.makeCompactGrid(detailPanel, 2, 2, 6, 6, 6, 6);
		recipePanel.add(detailPanel);
		recipePanel.add(new JSeparator());

		//TODO: DecimalFormat behavior depends on locale, determine if any checks are necessary to ensure problems don't occur.
		DecimalFormat df = new DecimalFormat("0.0##");
		JPanel pgVgNicPanel = new JPanel(new SpringLayout());
		JLabel nicotineLabel = new JLabel("Nicotine: ");
		JLabel nicotine = new JLabel(df.format(calculateMlNicotineToAdd(amountToMake) * tracker.getMwNicotine()));

		pgVgNicPanel.add(nicotineLabel);
		pgVgNicPanel.add(nicotine);

		JLabel pgLabel = new JLabel("Propylene Glycol: ");
		JLabel pgAmountLabel = new JLabel(df.format(calculateMlPgToAdd(recipe, amountToMake, pg) * Tracker.MW_PG) + " grams");

		pgVgNicPanel.add(pgLabel);
		pgVgNicPanel.add(pgAmountLabel);

		JLabel vgLabel = new JLabel("Vegetable Glycerin: ");
		JLabel vgAmountLabel = new JLabel(df.format(calculateMlVgToAdd(amountToMake, vg) * Tracker.MW_VG) + " grams");

		pgVgNicPanel.add(vgLabel);
		pgVgNicPanel.add(vgAmountLabel);
		SpringUtilities.makeCompactGrid(pgVgNicPanel, 3, 2, 6, 6, 6, 6);
		recipePanel.add(pgVgNicPanel);
		recipePanel.add(new JSeparator());

		JLabel flavorName;
		JLabel flavorAmount;
		JPanel flavorPanel = new JPanel(new SpringLayout());
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			flavorName = new JLabel(entry.getKey().getName() + " (" + entry.getKey().getManufacturer() + "):");
			flavorAmount = new JLabel(df.format(calculateMlFlavorToAdd(entry.getKey(),
					entry.getValue(), amountToMake)) + " grams");
			flavorPanel.add(flavorName);
			flavorPanel.add(flavorAmount);
		}
		SpringUtilities.makeCompactGrid(flavorPanel, recipe.getRecipe().size(), 2, 6, 6, 6, 6);
		recipePanel.add(flavorPanel);

		JPanel buttonPanel = new JPanel();
		JButton makeThisRecipe = new JButton("Make this recipe");
		makeThisRecipe.addActionListener(a -> makeRecipe(recipe, amountToMake, pg, vg));
		buttonPanel.add(makeThisRecipe);
		recipePanel.add(buttonPanel);

		recipeFrame.add(recipePanel);
		recipeFrame.pack();
		recipeFrame.setLocationRelativeTo(null);
		recipeFrame.setVisible(true);
	}

	private static void makeRecipe(@NotNull Recipe recipe, double amount, double pg, double vg) {
		tracker.usePg(calculateMlPgToAdd(recipe, amount, pg));
		tracker.useVg(calculateMlVgToAdd(amount, vg));
		tracker.useNicotine(calculateMlNicotineToAdd(amount));
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			tracker.useFlavor(entry.getKey(), calculateMlFlavorToAdd(entry.getKey(), entry.getValue(), amount));
		}
		tracker.save();
	}

	private static double calculateMlFlavorToAdd(@NotNull Flavor flavor, double percentToAdd, double amountToMake) {
		return (percentToAdd/100) * amountToMake * flavor.getMW();
	}

	private static double calculateMlPgToAdd(@NotNull Recipe recipe, double amountToMake, double pgProportion) {
		if (pgProportion == 0) {
			return 0;
		}
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
		double vgToAdd = (amountToMake * (vgProportion/100));
		return vgToAdd - (calculateMlNicotineToAdd(amountToMake) * (nicotineVg/100));
	}

	private static double sumFlavorAmounts(@NotNull Recipe recipe, double amount) {
		double sum = 0.0;
		for (Map.Entry<Flavor, Double> entry : recipe.getRecipe().entrySet()) {
			sum += (entry.getValue()/100) * amount;
		}
		return sum;
	}

	private static void buildAndShowAutoShoppingList() {
		double amount = Double.parseDouble(properties.getProperty("flavor.amtrem") != null ?
			properties.getProperty("flavor.amtrem") : promptUserForAmtRem());
		List<Flavor> shoppingList = tracker.buildAutoShoppingList(amount);
		showExistingListView(shoppingList);
	}

	private static String promptUserForAmtRem() {
		return JOptionPane.showInputDialog(frame, "Add flavors below how many ml's to shopping list?");
	}

	private static void selectExistingListView() {
		JFrame listFrame = new JFrame("Select a list to open");
		listFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		File listDir = new File("lists/");
		File[] lists = listDir.listFiles();
		LinkedList<String> listNames = new LinkedList<>();
		if (lists != null) {
			for (File file : lists) {
				listNames.add(file.getName().replaceAll(".xml", ""));
			}
		}
		JTable listTable = new JTable(new ListTableModel(listNames));
		listTable.getSelectionModel().addListSelectionListener(a -> {
			if (listTable.getSelectedRow() > -1 && !a.getValueIsAdjusting()) {
				String name = (String) listTable.getValueAt(listTable.getSelectedRow(), 0);
				List<Flavor> flavors = tracker.readListFromFile(name);
				showExistingListView(flavors);
				listFrame.dispose();
			}
		});
		listTable.setAutoCreateRowSorter(true);
		JScrollPane tablePane = new JScrollPane(listTable);
		tablePane.setPreferredSize(new Dimension(200, 200));
		listFrame.add(tablePane);
		listFrame.pack();
		listFrame.setLocationRelativeTo(frame);
		listFrame.setVisible(true);
	}

	private static void showExistingListView(List<Flavor> shoppingList) {
		JFrame shoppingListFrame = new JFrame("Shopping List");
		shoppingListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(a -> tracker.writeListToFile(JOptionPane.showInputDialog(shoppingListFrame, "Name your list"),
				shoppingList));
		file.add(save);
		menuBar.add(file);
		shoppingListFrame.setJMenuBar(menuBar);

		JPanel shoppingListPanel = new JPanel();
		JTable shoppingListTable = new JTable(new FlavorTableModel(shoppingList));
		shoppingListTable.getSelectionModel().addListSelectionListener(a -> {
			if (shoppingListTable.getSelectedRow() > -1 && !a.getValueIsAdjusting()) {
				String name = (String) shoppingListTable.getValueAt(shoppingListTable.getSelectedRow(), 0);
				String[] nameAndManf = name.split("\\(");
				if (nameAndManf.length == 2) {
					nameAndManf[1] = nameAndManf[1].replace(")", "");
					nameAndManf[0] = nameAndManf[0].trim();
					nameAndManf[1] = nameAndManf[1].trim();
					Flavor flavor = tracker.findFlavor(nameAndManf[1], nameAndManf[0]);
					if (flavor != null) {
						showIndividualFlavorView(flavor);
					} else {
						JOptionPane.showMessageDialog(shoppingListTable, "Unable to load information for flavor: " + name,
								"Flavor Does Not Exist", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		shoppingListTable.setAutoCreateRowSorter(true);
		JScrollPane tablePane = new JScrollPane(shoppingListTable);
		tablePane.setPreferredSize(new Dimension(700, 200));
		shoppingListPanel.add(tablePane);
		shoppingListFrame.add(shoppingListPanel);
		shoppingListFrame.pack();
		shoppingListFrame.setLocationRelativeTo(frame);
		shoppingListFrame.setVisible(true);
	}
}
