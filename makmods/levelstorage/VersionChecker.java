package makmods.levelstorage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import makmods.levelstorage.logic.util.LogHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;

public class VersionChecker {

	public static enum CheckResult {
		OUTDATED, OK, ERROR, UNINITIALIZED
	}

	public static class CheckInfo {
		private final CheckResult result;
		
		private final String newVersion;

		public CheckInfo(CheckResult res) {
			this.result = res;
			this.newVersion = null;
		}
		
		public CheckInfo(CheckResult res, String newVersion) {
			this.result = res;
			this.newVersion = newVersion;
		}
	}

	public static final String VERSION_URL = "https://raw.github.com/mak326428/LevelStorage/master/version.xml";

	public static CheckInfo result = new CheckInfo(CheckResult.UNINITIALIZED);

	public static void checkVersion() {
		try {
			URL versionUrl = new URL(VERSION_URL);
			InputStream versionStream = versionUrl.openStream();

			List<String> fileLines = Lists.newArrayList();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					versionStream));
			String line = br.readLine();
			while (line != null) {
				if (!Strings.isNullOrEmpty(line))
					fileLines.add(line);
				line = br.readLine();
			}
			
			String mcVersion = Loader.instance().getMCVersionString().replace("Minecraft ", "");
			
			List<String> candidatesForCurrentVersion = Lists.newArrayList();
			
			for (String fileLine : fileLines) {
				String[] split = fileLine.split("|");
				String mcVCurrLine = split[0];
				String lsVersion = split[1];
				if (mcVCurrLine.equals(mcVersion))
					candidatesForCurrentVersion.add(lsVersion);
			}
			
			for (String s : candidatesForCurrentVersion) {
				System.out.println(s);
			}
			
			versionStream.close();
		} catch (Exception e) {
			LogHelper
					.severe("Fatal exception happened when tried to check LevelStorage's version.");
			e.printStackTrace();
			result = new CheckInfo(CheckResult.ERROR);
		}
	}

}
