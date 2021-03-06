package com.jadventure.game.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.jadventure.game.DeathException;
import com.jadventure.game.GameBeans;
import com.jadventure.game.QueueProvider;
import com.jadventure.game.items.Item;
import com.jadventure.game.items.ItemStack;
import com.jadventure.game.items.Storage;
import com.jadventure.game.menus.BattleMenu;
import com.jadventure.game.monsters.Monster;
import com.jadventure.game.navigation.Coordinate;
import com.jadventure.game.navigation.ILocation;
import com.jadventure.game.navigation.LocationType;
import com.jadventure.game.repository.ItemRepository;
import com.jadventure.game.repository.LocationRepository;

/**
 * This class deals with the player and all of its properties.
 * Any method that changes a character or interacts with it should
 * be placed within this class. If a method deals with entities in general or
 * with variables not unique to the player, place it in the entity class.
 */
public class Player extends Entity {
    // @Resource
    protected static ItemRepository itemRepo = GameBeans.getItemRepository();
    protected static LocationRepository locationRepo = GameBeans.getLocationRepository();
    private ILocation location;
    private int xp;
    /** Player type */
    private String type;
    private static Map<String, Integer>characterLevels = new HashMap<String, Integer>();

    public Player() {
    }

    protected static void setUpCharacterLevels() {
        characterLevels.put("Sewer Rat", 5);
        characterLevels.put("Recruit", 3);
        characterLevels.put("Syndicate Member", 4);
        characterLevels.put("Brotherhood Member", 4);
    }
    public LocationRepository getRepo(){
    	return locationRepo ;
    }
    
    public  static void setLocRepo(LocationRepository repo){
    	locationRepo =repo;
    }
    public static LocationRepository getLocRepo(){
    	return locationRepo;
    }
    

    public Map<String, Integer> getCharacterLevels() {
        return characterLevels;
    }

    public void setCharacterLevels(Map<String, Integer> newCharacterLevels) {
        this.characterLevels = newCharacterLevels;
    }

    public String getCurrentCharacterType() {
        return this.type;
    }
    
    public void setCurrentCharacterType(String newCharacterType) {
        this.type = newCharacterType;
    }

    public void setCharacterLevel(String characterType, int level) {
        this.characterLevels.put(characterType, level);
    }

    public int getCharacterLevel(String characterType) {
        int characterLevel = this.characterLevels.get(characterType);
        return characterLevel;
    }

    protected static String getProfileFileName(String name) {
        return "json/profiles/" + name + "/" + name + "_profile.json";
    }

    public static boolean profileExists(String name) {
        File file = new File(getProfileFileName(name));
        return file.exists();
    }

    

    // This is known as the singleton pattern. It allows for only 1 instance of a player.
    private static Player player;
    public static void resetPlayer(){
    	player = new Player();
    }
    public static Player  getPlayer(){
    	return player;
    }
    
    public static Player getInstance(String playerClass){
        player = new Player();
        JsonParser parser = new JsonParser();
        String fileName = "json/original_data/npcs.json";
        try {
            Reader reader = new FileReader(fileName);
            JsonObject npcs = parser.parse(reader).getAsJsonObject().get("npcs").getAsJsonObject();
            JsonObject json = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : npcs.entrySet()) {
                if (entry.getKey().equals(playerClass)) {
                    json = entry.getValue().getAsJsonObject();
                }
            }

            player.setName(json.get("name").getAsString());
            player.setHealthMax(json.get("healthMax").getAsInt());
            player.setHealth(json.get("health").getAsInt());
            player.setGold(json.get("gold").getAsInt());
            player.setArmour(json.get("armour").getAsInt());
            player.setDamage(json.get("damage").getAsInt());
            player.setLevel(json.get("level").getAsInt());
            player.setXP(json.get("xp").getAsInt());
            player.setStrength(json.get("strength").getAsInt());
            player.setIntelligence(json.get("intelligence").getAsInt());
            player.setDexterity(json.get("dexterity").getAsInt());
            setUpVariables(player);
            JsonArray items = json.get("items").getAsJsonArray();
            for (JsonElement item : items) {
                player.addItemToStorage(itemRepo.getItem(item.getAsString()));
            }
            Random rand = new Random();
            int luck = rand.nextInt(3) + 1;
            player.setLuck(luck);
            player.setStealth(json.get("stealth").getAsInt());
            player.setIntro(json.get("intro").getAsString());
            if (player.getName().equals("Recruit")) {
                player.type = "Recruit";
            } else if (player.getName().equals("Sewer Rat")) {
                player.type = "Sewer Rat";
            } else {
                QueueProvider.offer("Not a valid class");
            }
            reader.close();
            setUpCharacterLevels();
        } catch (FileNotFoundException ex) {
            QueueProvider.offer( "Unable to open file '" + fileName + "'.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return player;
    } 

    public int getXP() {
        return xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public static void setUpVariables(Player player) {
        float maxWeight = (float)Math.sqrt(player.getStrength()*300);
        player.setStorage(new Storage(maxWeight));
    }

    public void getStats(){
        Item weapon = itemRepo.getItem(getWeapon());
        String weaponName = weapon.getName();
        if (weaponName.equals(null)) {
            weaponName = "hands";
        } 
        StringBuilder msg = new StringBuilder();
        			msg.append( "\nPlayer name: " + getName()
        					+ "\nType: " + type 
        				+  "\nCurrent weapon: " + weaponName
        				+ "\nGold: " + getGold()
        				+ "\nHealth/Max: " + getHealth() + "/" + getHealthMax() 
        				+ "\nDamage/Armour: " + getDamage() + "/" + getArmour()
        				+ "\nStrength: " + getStrength()
        				+ "\nIntelligence: " + getIntelligence()
        				+ "\nDexterity: " + getDexterity()
        				+ "\nLuck: " + getLuck()
        				+ "\nStealth: " + getStealth()
        				+ "\nXP: " + getXP()	
        				+ "\n" + getName() + "'s level: " + getLevel()	
        					);
        			
        QueueProvider.offer(msg.toString());
    }

    public void printBackPack() {
        storage.display();
    }


    public List<Item> searchItem(String itemName, List<Item> itemList) {
        List<Item> items = new ArrayList<>();
        for (Item item : itemList) {
            String testItemName = item.getName();
            if (testItemName.equalsIgnoreCase(itemName)) {
                items.add(item);
            }
        }
        return items;
    }

    public List<Item> searchItem(String itemName, Storage storage) {
        return storage.search(itemName);
    }
    
    
    public List<Item> searchEquipment(String itemName, Map<EquipmentLocation, Item> equipment) {
        List<Item> items = new ArrayList<>();
        for (Item item : equipment.values()) {
            if (item != null && item.getName().equals(itemName)) {
                items.add(item);
            }
        }
        return items;
    }

    public void pickUpItem(String itemName) {	
        if (itemName.equals("*")) {
            for (Item item : getLocation().getItems()) {
                addItemToStorage(item);
                location.removeItem(item);
                QueueProvider.offer(item.getName() + " picked up");
            }
            return;
        }

        List <Item>items = searchItem(itemName, getLocation().getItems());
        if (! items.isEmpty()) {
            Item item = items.get(0);
            addItemToStorage(item);
            location.removeItem(item);
            QueueProvider.offer(item.getName()+ " picked up");
            
        }
        
        items=getLocation().getItems();
        try {
        	int number=Integer.parseInt(itemName);
        	if(items.size()>=number) {
        		Item item=items.get(number-1);
        		addItemToStorage(item);
                location.removeItem(item);
                QueueProvider.offer(item.getName()+ " picked up");
        	}
        }catch(NumberFormatException e){
        	System.out.println("");
        }

    }

    public void dropItem(String itemName) {
        List<Item> itemMap = searchItem(itemName, getStorage());
        if (itemMap.isEmpty()) {
            itemMap = searchEquipment(itemName, getEquipment());
        }
        if (!itemMap.isEmpty()) {
            Item item = itemMap.get(0);
            Item itemToDrop = itemRepo.getItem(item.getId());
            Item weapon = itemRepo.getItem(getWeapon());
            String wName = weapon.getName();

            if (itemName.equals(wName)) {
                dequipItem(wName);
            }
            removeItemFromStorage(itemToDrop);
            location.addItem(itemToDrop);
            QueueProvider.offer(item.getName() + " dropped");
        }
    }

    public void equipItem(String itemName) {
        List<Item> items = searchItem(itemName, getStorage());
        if (!items.isEmpty()) {
            Item item = items.get(0);
            if (getLevel() >= item.getLevel()) {
                Map<String, String> change = equipItem(item.getPosition(), item);
                QueueProvider.offer(item.getName()+ " equipped");
                printStatChange(change);
            } else {
                QueueProvider.offer("You do not have the required level to use this item");
            }
        } else {
            QueueProvider.offer("You do not have that item");
        }
    }

    public void dequipItem(String itemName) {
         List<Item> items = searchEquipment(itemName, getEquipment());
         if (!items.isEmpty()) {
            Item item = items.get(0);
            Map<String, String> change = unequipItem(item);
            QueueProvider.offer(item.getName()+" unequipped");
	        printStatChange(change);
         }
    }

    private void printStatChange(Map<String, String> stats) {
         Set<Entry<String, String>> set = stats.entrySet();
         Iterator<Entry<String, String>> iter = set.iterator();
         while (iter.hasNext()) {
              Entry<String, String> me = iter.next();
              double value = Double.parseDouble((String) me.getValue());
              switch ((String) me.getKey()) {
                  case "damage": {
                          if (value >= 0.0) {
                              QueueProvider.offer(me.getKey() + ": " + this.getDamage() + " (+" + me.getValue() + ")");
                          } else {
                              QueueProvider.offer(me.getKey() + ": " + this.getDamage() + " (" + me.getValue() + ")");
                          }
                          break;
                    }
                    case "health": {
                          if (value >= 0) {
                              QueueProvider.offer(me.getKey() + ": " + this.getHealth() + " (+" + me.getValue() + ")");
                          } else {
                              QueueProvider.offer(me.getKey() + ": " + this.getHealth() + " (" + me.getValue() + ")");
                          }
                          break;
                    }
                    case "armour": {
                          if (value >= 0) {
                              QueueProvider.offer(me.getKey() + ": " + this.getArmour() + " (+" + me.getValue() + ")");
                          } else {
                              QueueProvider.offer(me.getKey() + ": " + this.getArmour() + " (" + me.getValue() + ")");
                          }
                          break;
                    }
                    case "maxHealth": {
                          if (value  >= 0) {
                              QueueProvider.offer(me.getKey() + ": " + this.getHealthMax() + " (+" + me.getValue() + ")");
                          } else {
                              QueueProvider.offer(me.getKey() + ": " + this.getHealthMax() + " (" + me.getValue() + ")");
                          }
                          break;
                    }
              }
         }
    }

    public void inspectItem(String itemName) {
        List<Item> itemMap = searchItem(itemName, getStorage());
        if (itemMap.isEmpty()) {
            itemMap = searchItem(itemName, getLocation().getItems());
        }
        if (!itemMap.isEmpty()) {
            Item item = itemMap.get(0);
            item.display();
        } else {
            QueueProvider.offer("Item doesn't exist within your view.");
        }
    }

    public ILocation getLocation() {
        return location;
    }

    public void setLocation(ILocation location) {
        this.location = location;
    }

    public LocationType getLocationType() {
    	return getLocation().getLocationType();
    }

    public void attack(String opponentName) throws DeathException {
    	
        Monster monsterOpponent = null;
        NPC npcOpponent = null;
        List<Monster> monsters = getLocation().getMonsters();
        List<NPC> npcs = getLocation().getNpcs();
                
       for (Monster single : monsters) {
    	   if(single.monsterType.equalsIgnoreCase(opponentName))
    		   monsterOpponent = single;
		
	}    
       
        for (NPC npc : npcs) {
        	if(npc.getName().equalsIgnoreCase(opponentName))
        		npcOpponent = npc;
        }
        
        if (monsterOpponent != null) {
            monsterOpponent.setName(monsterOpponent.monsterType);
            new BattleMenu(monsterOpponent, this);
        } else if (npcOpponent != null) {
            new BattleMenu(npcOpponent, this);
        } else {
             QueueProvider.offer("Opponent not found");
        }
    }

    public boolean hasItem(Item item) {
        List<Item> searchEquipment = searchEquipment(item.getName(), getEquipment());
        List<Item> searchStorage = searchItem(item.getName(), getStorage());
        return !(searchEquipment.size() == 0 && searchStorage.size() == 0);
    }
}
