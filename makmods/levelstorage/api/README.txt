API for LevelStorage.

Some tips & tricks:
ALWAYS use after LevelStorage passed its init phase.
The later the better (post-init is pretty good).
Always check for Loader.isModLoaded("LevelStorage"), then load
you compatibility features.

For item usage, refer to ItemAPI.
Sample:
ItemStack freqCard = ItemAPI.getItem("itemFreqCard", 0).copy();

FMLInterModComms:
WARNING: use IMCs exactly in the pattern below
Metadata(s) IS REQUIRED
Currently available IMCs:
1) Adds an item to the XP Registry:
   Key: add-xp
   Value: id:metadata,value
   Example: 1:0,1 (this example will add XP value of 1 to item/block with id 1 (stone) and metadata 0)