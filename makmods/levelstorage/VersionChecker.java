package makmods.levelstorage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import makmods.levelstorage.logic.util.LogHelper;

public class VersionChecker {

	public static enum CheckResult {
		OUTDATED, OK, ERROR, UNINITIALIZED
	}

	public static class CheckInfo {
		private final CheckResult result;

		public CheckInfo(CheckResult res) {
			this.result = res;
		}
	}

	public static final String VERSION_URL = "https://raw.github.com/mak326428/LevelStorage/master/version.xml";

	public static CheckInfo result = new CheckInfo(CheckResult.UNINITIALIZED);

	public static void checkVersion() {
		try {
			URL versionUrl = new URL(VERSION_URL);
			InputStream versionStream = versionUrl.openStream();

			String fileContents = null;
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					versionStream));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			fileContents = sb.toString();
			
			System.out.println(fileContents);

			versionStream.close();
		} catch (Exception e) {
			LogHelper
					.severe("Fatal exception happened when tried to check LevelStorage's version.");
			e.printStackTrace();
			result = new CheckInfo(CheckResult.ERROR);
		}
	}

}
