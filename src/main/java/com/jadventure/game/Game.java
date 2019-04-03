package com.jadventure.game;

import com.jadventure.game.entities.Player;
import com.jadventure.game.entities.PlayerUtil;
import com.jadventure.game.monsters.Monster;
import com.jadventure.game.monsters.MonsterFactory;
import com.jadventure.game.repository.LocationRepository;
import com.jadventure.game.prompts.CommandParser;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the main loop that takes the input and
 * does the according actions.
 */
public class Game {
    public List<Monster> monsterList = new ArrayList<Monster>();
    public MonsterFactory monsterFactory = new MonsterFactory(); 
    public CommandParser parser;
    public Monster monster;
    Player player = null;

    public Game(Player player, String playerType) throws DeathException {
          this.parser = new CommandParser(player);
          this.player = player;
          switch (playerType) {
              case "new":
                  newGameStart(player);
                  break;
              case "old":
                  QueueProvider.offer("Welcome back, " + player.getName() + "!");
                  QueueProvider.offer("");
                  player.getLocation().print();
                  gamePrompt(player);
                  break;
              default:
                  QueueProvider.offer("Invalid player type");
                  break;
          }
    }
   
    /**
     * Starts a new game.
     * It prints the introduction text first and asks for the name of the player's
     * character and welcomes him / her. After that, it goes to the normal game prompt.
     */
    public void newGameStart(Player player) throws DeathException {
    	String input="";
    	
        QueueProvider.offer(player.getIntro());
        String userInput = QueueProvider.take();
        if(player.profileExists(userInput)) {
        	QueueProvider.offer("Hmm.. I can recognize you, this is not your first time in Silliya " + userInput +".\nDo you want to remove existing adventure and create another one?[y]");
        	input=QueueProvider.take();

        if(!input.equals("y"))
        	return;
        }
	        player.setName(userInput);
	        LocationRepository locationRepo = GameBeans.getLocationRepository(player.getName());
	        this.player.setLocation(locationRepo.getInitialLocation());
	        new PlayerUtil().save();
	        QueueProvider.offer("Welcome to Silliya, " + player.getName() + ".");
	        player.getLocation().print();
	        gamePrompt(player);

        }

    /**
     * This is the main loop for the player-game interaction. It gets input from the
     * command line and checks if it is a recognised command.
     *
     * This keeps looping as long as the player didn't type an exit command.
     */
    public void gamePrompt(Player player) throws DeathException {
        boolean continuePrompt = true;
        try {
            while (continuePrompt) {
                QueueProvider.offer("\nPrompt:");
                String command = QueueProvider.take().toLowerCase();
                continuePrompt = parser.parse(player, command);
            }
        } catch (DeathException e) {
            if (e.getLocalisedMessage().equals("replay")) {
                return;
            } else {
                throw e;
            }
        }
    }
}
