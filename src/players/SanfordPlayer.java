package players;

import game.HandRanks;
import game.Player;
import java.lang.System;

public class SanfordPlayer extends Player {
    //play safe but try to call people's bluff
    public SanfordPlayer(String name) {
        super(name);
    }

    @Override
    protected void takePlayerTurn() {
        HandRanks handRanks = evaluatePlayerHand();
        System.out.println(handRanks);
        System.out.println(" : " + getGameState().getNumRoundStage());


        if (shouldCheck()) {
            check();
        } else if (shouldFold()) {
            fold();
        } else if (shouldCall()) {
            call();
        } else if (shouldRaise()) {
            raise(getGameState().getTableMinBet());
        } else if (shouldAllIn()) {
            allIn();
        }
    }

    @Override
    protected boolean shouldCheck() {
        // Always check if possible (check when there is no bet on the table)
        return !getGameState().isActiveBet();
    }

    @Override
    protected boolean shouldFold() {
        // always check if possible (check when there is no bet on the table)
        if (!getGameState().isActiveBet()) {
            return false;
        }

        // Only fold with bad hand at high bets
        return evaluatePlayerHand().getValue() < HandRanks.PAIR.getValue() && getGameState().getTableBet() > getBank() * 0.1;
    }

    @Override
    protected boolean shouldCall() {
        // always check if possible (check when there is no bet on the table)
        if (!getGameState().isActiveBet()) {
            return true;
        }

        // Hand evaluation
        HandRanks handRanks = evaluatePlayerHand();
        double callPercentage = 0;

        switch (handRanks) {
            case HIGH_CARD:
            case PAIR:
                callPercentage = .10; // Call if the bet is 25% or less of the player's bank
                break;
            case TWO_PAIR:
                callPercentage = 0.25; // Call if the bet is 25% or less of the player's bank
                break;
            case THREE_OF_A_KIND:
            case STRAIGHT:
            case FLUSH:
                callPercentage = 0.5; // Call if the bet is 50% or less of the player's bank
                break;
            case FULL_HOUSE:
            case FOUR_OF_A_KIND:
            case STRAIGHT_FLUSH:
                callPercentage = 0.8; // Call if the bet is 80% or less of the player's bank
                break;
            case ROYAL_FLUSH:
                return true; // Always call for a Royal Flush
        }

        double maxBetAllowed = getBank() * callPercentage;
        double currentBet = getGameState().getTableBet();
        double remainingBet = maxBetAllowed - currentBet;

        // Check if the remaining bet is pos
        return remainingBet >= 0;
    }

    @Override
    protected boolean shouldRaise() {
        // always check if possible (check when there is no bet on the table)



        // Hand evaluation
        HandRanks handRanks = evaluatePlayerHand();

        switch (handRanks) {
            case HIGH_CARD:
            case PAIR:
                //raise by 10% of current bank

                break;
            case TWO_PAIR:
                //raise by 15% of current bank

                break;
            case THREE_OF_A_KIND:
            case STRAIGHT:
                //raise by 25% of current bank

                break;
            case FLUSH:
                //raise by 30% of current bank

                break;
            case FULL_HOUSE:
                //raise by 35% of current bank

                break;
            case FOUR_OF_A_KIND:
                //raise by 40% of current bank

                break;
            case STRAIGHT_FLUSH:
                //raise by 60% of current bank

                break;
            case ROYAL_FLUSH:
                return true; // Always call for a Royal Flush
        }

        boolean betIsSmallPercentageOfBank = getGameState().getTableBet() < getBank() * 0.05;
        return betIsSmallPercentageOfBank;
    }

    @Override
    protected boolean shouldAllIn() {
        // Check when there is no active bet
        if (!getGameState().isActiveBet()) {
            return false;
        }

        // All in only with royal flush only if there is no check
        return evaluatePlayerHand().getValue() >= HandRanks.FULL_HOUSE.getValue();
    }
}
