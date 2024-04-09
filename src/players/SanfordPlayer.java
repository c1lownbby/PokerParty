package players;

import game.HandRanks;
import game.Player;
import java.lang.System;

public class SanfordPlayer extends Player {
    public SanfordPlayer(String name) {
        super(name);
    }

    @Override
    protected void takePlayerTurn() {
        HandRanks handRanks = evaluatePlayerHand();
        System.out.println(handRanks);
        System.out.println(" : " + getGameState().getNumRoundStage());


        if (shouldFold()) {
            fold();
        } else if (shouldCheck()) {
            check();
        } else if (shouldCall()) {
            call();
        } else if (shouldRaise()) {
            raise();
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
        // Always check if possible (check when there is no bet on the table)
        if (!getGameState().isActiveBet()) {
            return false;
        }

        // Only fold with bad hand at high bets
        return evaluatePlayerHand().getValue() < HandRanks.PAIR.getValue() && getGameState().getTableBet() > getBank() * 0.1;
    }

    @Override
    protected boolean shouldCall() {
        // Always check if possible (check when there is no bet on the table)
        if (!getGameState().isActiveBet()) {
            return true;
        }

        // Hand evaluation
        HandRanks handRanks = evaluatePlayerHand();
        double callPercentage = 0;

        switch (handRanks) {
            case HIGH_CARD:
            case PAIR:
            case TWO_PAIR:
                callPercentage = 0.25; // Call if the bet is 25% or less of the bankroll
                break;
            case THREE_OF_A_KIND:
            case STRAIGHT:
            case FLUSH:
                callPercentage = 0.5; // Call if the bet is 50% or less of the bankroll
                break;
            case FULL_HOUSE:
            case FOUR_OF_A_KIND:
            case STRAIGHT_FLUSH:
                callPercentage = 0.8; // Call if the bet is 80% or less of the bankroll
                break;
            case ROYAL_FLUSH:
                return true; // Always call for a Royal Flush
        }

        double maxBetAllowed = getBank() * callPercentage;
        double currentBet = getGameState().getTableBet();
        double remainingBet = maxBetAllowed - currentBet;

        // Check if the remaining bet is non-negative
        return remainingBet >= 0;
    }

    @Override
    protected boolean shouldRaise() {
        // Always check if possible (check when there is no bet on the table)
        if (!getGameState().isActiveBet()) {
            return false;
        }

        // Hand evaluation
        HandRanks handRanks = evaluatePlayerHand();

        boolean hasPair = handRanks.getValue() >= HandRanks.PAIR.getValue();
        boolean hasTwoPair = handRanks.getValue() >= HandRanks.TWO_PAIR.getValue();
        boolean hasThreeOfAKind = handRanks.getValue() >= HandRanks.THREE_OF_A_KIND.getValue();
        boolean straight = handRanks.getValue() >= HandRanks.STRAIGHT.getValue();
        boolean flush = handRanks.getValue() >= HandRanks.FLUSH.getValue();
        boolean fourOfAKind = handRanks.getValue() >= HandRanks.FOUR_OF_A_KIND.getValue();
        boolean straightFlush = handRanks.getValue() >= HandRanks.STRAIGHT_FLUSH.getValue();

        boolean betIsSmallPercentageOfBank = getGameState().getTableBet() < getBank() * 0.05;
        return betIsSmallPercentageOfBank;
    }

    @Override
    protected boolean shouldAllIn() {
        // Always check if possible (check when there is no bet on the table)
        if (!getGameState().isActiveBet()) {
            return false;
        }

        // All in only with royal flush
        return evaluatePlayerHand().getValue() >= HandRanks.FULL_HOUSE.getValue();
    }
}
