package com.tantum.app.tantum.helper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.tantum.app.tantum.TantumApplication;

public class Helper {

	public static String readJson(String path) {
		StringBuilder result = new StringBuilder();

		ClassLoader classLoader = TantumApplication.class.getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());
		// File file = new File(path);

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

}
