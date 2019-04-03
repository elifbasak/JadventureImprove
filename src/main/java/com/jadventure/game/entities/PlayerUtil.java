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

public class PlayerUtil {  
	
	public static Player load(String name) {
//    player = new Player();
		 Player.resetPlayer();
		Player player = Player.getPlayer();
		
        JsonParser parser = new JsonParser();
        String fileName = Player.getProfileFileName(name);
        
        try {
            Reader reader = new FileReader(fileName);
            JsonObject json = parser.parse(reader).getAsJsonObject();
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
            player.setLuck(json.get("luck").getAsInt());
            player.setStealth(json.get("stealth").getAsInt());
            player.setCurrentCharacterType(json.get("type").getAsString());
            Map<String, Integer> charLevels = new Gson().fromJson(json.get("types"), new TypeToken<HashMap<String, Integer>>(){}.getType());
            player.setCharacterLevels(charLevels);
            if (json.has("equipment")) {
                Map<String, EquipmentLocation> locations = new HashMap<>();
                locations.put("head", EquipmentLocation.HEAD);
                locations.put("chest", EquipmentLocation.CHEST);
                locations.put("leftArm", EquipmentLocation.LEFT_ARM);
                locations.put("leftHand", EquipmentLocation.LEFT_HAND);
                locations.put("rightArm", EquipmentLocation.RIGHT_ARM);
                locations.put("rightHand", EquipmentLocation.RIGHT_HAND);
                locations.put("bothHands", EquipmentLocation.BOTH_HANDS);
                locations.put("bothArms", EquipmentLocation.BOTH_ARMS);
                locations.put("legs", EquipmentLocation.LEGS);
                locations.put("feet", EquipmentLocation.FEET);
                HashMap<String, String> equipment = new Gson().fromJson(json.get("equipment"), new TypeToken<HashMap<String, String>>(){}.getType());
               Map<EquipmentLocation, Item> equipmentMap = new HashMap<>();
               for(Map.Entry<String, String> entry : equipment.entrySet()) {
                   EquipmentLocation el = locations.get(entry.getKey());
                   Item i =  GameBeans.getItemRepository().getItem(entry.getValue());
                   equipmentMap.put(el, i);
               }
               player.setEquipment(equipmentMap);
            }
            if (json.has("items")) {
                Map<String, Integer> items = new Gson().fromJson(json.get("items"), new TypeToken<HashMap<String, Integer>>(){}.getType());
                List<ItemStack> itemList = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                    String itemID = entry.getKey();
                    int amount = entry.getValue();
                    Item item = GameBeans.getItemRepository().getItem(itemID);
                    ItemStack itemStack = new ItemStack(amount, item);
                    itemList.add(itemStack);
                }
                float maxWeight = (float)Math.sqrt(player.getStrength()*300);
                player.setStorage(new Storage(maxWeight, itemList));
            }
            Coordinate coordinate = new Coordinate(json.get("location").getAsString());
            
           // locationRepo = GameBeans.getLocationRepository(player.getName());
           Player.setLocRepo(GameBeans.getLocationRepository(player.getName()));
            player.setLocation(Player.getLocRepo().getLocation(coordinate));
            reader.close();
            Player.setUpCharacterLevels();
        } catch (FileNotFoundException ex) {
            QueueProvider.offer( "Unable to open file '" + fileName + "'.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return player;
    }

    public void save() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",Player.getPlayer(). getName());
        jsonObject.addProperty("healthMax", Player.getPlayer(). getHealthMax());
        jsonObject.addProperty("health",Player.getPlayer().  getHealth());
        jsonObject.addProperty("gold", Player.getPlayer(). getGold());
        jsonObject.addProperty("armour", Player.getPlayer(). getArmour());
        jsonObject.addProperty("damage", Player.getPlayer(). getDamage());
        jsonObject.addProperty("level", Player.getPlayer(). getLevel());
        jsonObject.addProperty("xp",Player.getPlayer().  getXP());
        jsonObject.addProperty("strength",Player.getPlayer().  getStrength());
        jsonObject.addProperty("intelligence", Player.getPlayer(). getIntelligence());
        jsonObject.addProperty("dexterity", Player.getPlayer(). getDexterity());
        jsonObject.addProperty("luck", Player.getPlayer(). getLuck());
        jsonObject.addProperty("stealth", Player.getPlayer(). getStealth());
        jsonObject.addProperty("weapon",Player.getPlayer().  getWeapon());
        jsonObject.addProperty("type", Player.getPlayer(). getCurrentCharacterType());
        Map<String, Integer> items = new HashMap<String, Integer>();
        for (ItemStack item :Player.getPlayer().getStorage().getItemStack()) {
            items.put(item.getItem().getId(), item.getAmount());
        }
        JsonElement itemsJsonObj = gson.toJsonTree(items);
        jsonObject.add("items", itemsJsonObj);
        Map<EquipmentLocation, String> locations = new HashMap<>();
        locations.put(EquipmentLocation.HEAD, "head");
        locations.put(EquipmentLocation.CHEST, "chest");
        locations.put(EquipmentLocation.LEFT_ARM, "leftArm");
        locations.put(EquipmentLocation.LEFT_HAND, "leftHand");
        locations.put(EquipmentLocation.RIGHT_ARM, "rightArm");
        locations.put(EquipmentLocation.RIGHT_HAND, "rightHand");
        locations.put(EquipmentLocation.BOTH_HANDS, "BothHands");
        locations.put(EquipmentLocation.BOTH_ARMS, "bothArms");
        locations.put(EquipmentLocation.LEGS, "legs");
        locations.put(EquipmentLocation.FEET, "feet");
        Map<String, String> equipment ;
        equipment = new HashMap<>();
        Item hands = GameBeans.getItemRepository().getItem("hands");
        for (Map.Entry<EquipmentLocation, Item> item :  Player.getPlayer().getEquipment().entrySet()) {
            if (item.getKey() != null && !hands.equals(item.getValue()) && item.getValue() != null) {
                equipment.put(locations.get(item.getKey()), item.getValue().getId());
            }
        }
        JsonElement equipmentJsonObj = gson.toJsonTree(equipment);
        jsonObject.add("equipment", equipmentJsonObj);
        JsonElement typesJsonObj = gson.toJsonTree( Player.getPlayer().getCharacterLevels());
        jsonObject.add("types", typesJsonObj);
        Coordinate coordinate =  Player.getPlayer().getLocation().getCoordinate();
        String coordinateLocation = coordinate.x+","+coordinate.y+","+coordinate.z;
        jsonObject.addProperty("location", coordinateLocation);

        String fileName =Player.getPlayer(). getProfileFileName(Player.getPlayer().getName());
        new File(fileName).getParentFile().mkdirs();
        try {
            Writer writer = new FileWriter(fileName);
            gson.toJson(jsonObject, writer);
            writer.close();
            //locationRepo = GameBeans.getLocationRepository(Player.getPlayer().getName());
            Player.setLocRepo(GameBeans.getLocationRepository( Player.getPlayer().getName()));
            //locationRepo.writeLocations();
            Player.getLocRepo().writeLocations();
            QueueProvider.offer("\nYour game data was saved.");
        } catch (IOException ex) {
            QueueProvider.offer("\nUnable to save to file '" + fileName + "'.");
        }
    }

}
