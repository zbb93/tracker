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

import groovy.swing.SwingBuilder;
import zbb.tracker.Tracker;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zbb on 7/13/16.
 */
public class GroovyApp {

	private static Tracker tracker;
	private static JFrame frame;
	private static JPanel panel;
	private static Properties properties;

	public static void main(String... args) {
		if (init()) {
			SwingUtilities.invokeLater(GroovyApp::buildMainFrameShowMainMenu);
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
				SwingUtilities.invokeLater(GroovyApp::buildMainFrameShowPrefrences);
			} else {
				SwingUtilities.invokeLater(GroovyApp::buildMainFrameShowMainMenu);
			}
			return false;
		}
		return true;
	}

	private static void buildMainFrameShowPrefrences() {

	}

	public static void buildMainFrameShowMainMenu() {
		SwingBuilder mainMenu = new SwingBuilder();
	}
}
