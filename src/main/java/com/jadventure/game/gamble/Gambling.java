package com.jadventure.game.gamble;

import com.jadventure.game.QueueProvider;

import com.jadventure.game.entities.Entity;
import com.jadventure.game.entities.Player;

import java.util.ArrayList;
import java.util.List;
import com.jadventure.game.menus.MenuItem;
import com.jadventure.game.menus.Menus;

public class Gambling {
	Player player;

	public Gambling(Player player) {
		this.player = player;
	}

	public void gambling() {
		List<MenuItem> gambleList = new ArrayList<>();
		String bac = "Bulls and Cows";
		gambleList.add(new MenuItem(bac, null));
		gambleList.add(new MenuItem("Exit", null));
		Menus gambleMenu = new Menus();
		MenuItem response = gambleMenu.displayMenu(gambleList);
        String command = response.getCommand();
        if (command.equals(bac)) {
        	bullsAndCows();
        } else if (command.equals("Exit")) {
        	return;
        }
        gambling();
	}

	public void bullsAndCows() {
		QueueProvider.offer("How much do you want to bet?");
		String input;
		int bet, bulls, cows, tries = 0;
		while (true) {
			input = QueueProvider.take();
			try {
				bet = Integer.parseInt(input);
			} catch (Exception e) {
				QueueProvider.offer("Don't blather!");
				continue;
			}
			if (bet <= 0) {
				QueueProvider.offer("Don't blather!");
			} else if (bet > player.getGold()) {
				QueueProvider.offer("Hahah! I don't see that much money :D");
			}
			break;
		}
		player.setGold(player.getGold() - bet);


        String number = "1234";
		QueueProvider.offer("I have a number with 4 distinct digits. Predict it. I will give you hints later.");
		QueueProvider.offer("If you find the number at most 8 trying, you'll get the money!");

        while (tries < 8) {
            input = QueueProvider.take();
            try {
                validateInput(input);
            } catch (Exception e) {
                continue;
            }
            bulls = getBull();
            cows = getCow();
        }

	}

    int getBull(String input, String number) {
        int bull = 0;

        for (int i = 0; i < 4; i++)
            if (number.charAt(i) == input.charAt(i))
                bull++;

        return bull;
    }

    int getCow(String input, String number) {
        int cow = 0;

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (number.charAt(j) == input.charAt(i) && i != j) {
                    cow++;
                    break;
                }

        return cow;
    }

    void validateInput(String input) {
        if (input == null)
            throw new IllegalArgumentException();
        if (input.length() != 4)
            throw new IllegalArgumentException();

        for (int i = 0; i < 4; i++)
            if (!Character.isDigit(input.charAt(i)))
                throw new IllegalArgumentException();

        if (input.charAt(0) == '0')
            throw new IllegalArgumentException();

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (input.charAt(i) == input.charAt(j) && i != j)
                    throw new IllegalArgumentException();
    }
}