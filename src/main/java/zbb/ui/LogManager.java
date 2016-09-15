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

import java.io.*;
import java.util.logging.*;

/**
 * Created by zbb on 9/5/16.
 */
public final class LogManager {

	private static FileHandler fileHandler;
	private static SimpleFormatter simpleFormatter;

	private LogManager() {}

	static void setup() {
		final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.INFO);
		try {
			fileHandler = new FileHandler("log.txt");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error creating log file", e);
		}
	}

	static void shutdown() {
		fileHandler.close();
	}
}
