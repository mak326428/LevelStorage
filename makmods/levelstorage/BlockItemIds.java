package makmods.levelstorage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.minecraftforge.common.Configuration;

public class BlockItemIds {
	public static final BlockItemIds instance = new BlockItemIds();
	public final Map<String, Integer> ids = new HashMap<String, Integer>();
	private Configuration config;

	public void addId(String forWhat, int id) {
		this.ids.put(forWhat, id);
	}

	public int getIdFor(String forWhat) {
		if (!ids.containsKey(forWhat)) {
			return 0;
		}
		return this.ids.get(forWhat);
	}

	// TODO: better name
	public void messWithConfig(Configuration config) {
		this.config = config;
		/*
		 * String itemXPBookId = "itemXPBookId"; String itemAdvScannerId =
		 * "itemAdvScannerId"; String blockXpGenId = "blockXpGenId"; String
		 * blockXpChargerId = "blockXpChargerId"; String itemFreqCard =
		 * "itemFrequencyCard"; String blockWirelessConductor =
		 * "blockWirelessConductor"; String blockWlessPowerSync =
		 * "blockWlessPowerSync"; BlockItemIds.instance.addId(itemXPBookId,
		 * config.getItem(itemXPBookId, 2085).getInt());
		 * BlockItemIds.instance.addId(itemFreqCard,
		 * config.getItem(itemFreqCard, 2086).getInt());
		 * BlockItemIds.instance.addId(itemAdvScannerId,
		 * config.getItem(itemAdvScannerId, 2087).getInt());
		 * BlockItemIds.instance.addId(blockXpGenId,
		 * config.getBlock(blockXpGenId, 237).getInt());
		 * BlockItemIds.instance.addId(blockXpChargerId,
		 * config.getBlock(blockXpChargerId, 238).getInt());
		 * BlockItemIds.instance.addId(blockWirelessConductor,
		 * config.getBlock(blockWirelessConductor, 239).getInt());
		 * BlockItemIds.instance.addId(blockWlessPowerSync,
		 * config.getBlock(blockWlessPowerSync, 240).getInt());
		 */
		// it's very dangerous to use reflection in obfuscated environment, but
		// we're using srg
		/*int currBlockId = 250;
		Field[] declaredBlocks = ModBlocks.class.getDeclaredFields();
		Object modBlocksInstance = null;
		try {
			modBlocksInstance = ModBlocks.class.getField("instance").get(null);
		} catch (Exception e1) {
			FMLLog.warning(Reference.MOD_NAME
					+ ": failed to get instance for ModBlocks");
		}*/
		int currBlockId = 250;
		ArrayList<Class<?>> blockClasses = getClassesForPackage(Package.getPackage("makmods.levelstorage.block"));
		for (Class c : blockClasses) {
			try {
				int id = currBlockId;
				Constructor cons = c.getConstructor(int.class);
				Object instance = cons.newInstance(id);
				Method getUnlocName = c.getMethod("getUnlocalizedName");
				String name = (String)getUnlocName.invoke(instance, null);
				this.addId(name, currBlockId);
				currBlockId++;
				System.out.println("name: " + name + ", id: " + currBlockId);
			} catch (Exception e) {
			}
		}
		/*for (Field f : declaredBlocks) {
			try {
				Object o = f.get(modBlocksInstance);
				if (o instanceof Block) {
					BlockItemIds.instance.addId(
							((Block) o).getUnlocalizedName(),
							config.getBlock(((Block) o).getUnlocalizedName(),
									currBlockId).getInt());
					currBlockId++;
				}
			} catch (Exception e) {
				continue;
			}
		}*/

		LevelStorage.itemLevelStorageBookSpace = config.get(
				Configuration.CATEGORY_GENERAL, "XPBookCapacity", 16384)
				.getInt();
	}

	private static ArrayList<Class<?>> getClassesForPackage(Package pkg) {
		String pkgname = pkg.getName();
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		// Get a File object for the package
		File directory = null;
		String fullPath;
		String relPath = pkgname.replace('.', '/');
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		if (resource == null) {
			throw new RuntimeException("No resource for " + relPath);
		}
		fullPath = resource.getFile();

		try {
			directory = new File(resource.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(
					pkgname
							+ " ("
							+ resource
							+ ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...",
					e);
		} catch (IllegalArgumentException e) {
			directory = null;
		}

		if (directory != null && directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					String className = pkgname + '.'
							+ files[i].substring(0, files[i].length() - 6);
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(
								"ClassNotFoundException loading " + className);
					}
				}
			}
		} else {
			try {
				String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar")
						.replaceFirst("file:", "");
				JarFile jarFile = new JarFile(jarPath);
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();
					if (entryName.startsWith(relPath)
							&& entryName.length() > (relPath.length() + "/"
									.length())) {
						String className = entryName.replace('/', '.')
								.replace('\\', '.').replace(".class", "");
						try {
							classes.add(Class.forName(className));
						} catch (ClassNotFoundException e) {
							throw new RuntimeException(
									"ClassNotFoundException loading "
											+ className);
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(pkgname + " (" + directory
						+ ") does not appear to be a valid package", e);
			}
		}
		return classes;
	}

	private BlockItemIds() {
	}
}
