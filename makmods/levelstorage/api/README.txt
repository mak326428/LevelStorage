API for LevelStorage.

Some tips & tricks:
ALWAYS use after LevelStorage passed it init phase.
The later the better (post-init is pretty good).
Always check for Loader.isModLoaded("LevelStorage"), then load
you compatibility features.

For item usage, refer to ItemAPI.
Sample:
ItemStack freqCard = ItemAPI.getItem("itemFreqCard", 0);