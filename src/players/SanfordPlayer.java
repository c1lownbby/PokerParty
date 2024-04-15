package players;

import game.HandRanks;
import game.Player;

public class SanfordPlayer extends Player {
    // play safe but try to call people's bluff
    public SanfordPlayer(String name) {
        super(name);
    }

    @Override
    protected void takePlayerTurn() {
        // hand evaluation
        HandRanks handRanks = evaluatePlayerHand();
        System.out.println(handRanks);
        System.out.println(" : " + getGameState().getNumRoundStage());

        // calculate a random number between 0 and 1
        double randomValue = Math.random();

        if (shouldCheck()) {
            // mostly check to play safe, but randomly raise instead of checking to throw people off
            if (randomValue < 0.45) { // 45% chance of raising
                int betAmount = (int) (getBank() * getRaisePercentage(handRanks));
                raise(betAmount);
            } else {
                check();
            }
        } else if (shouldCall()) {
            call();
        } else if (shouldRaise()) {
            int betAmount = (int) (getBank() * getRaisePercentage(handRanks));
            raise(betAmount);
        } else if (shouldAllIn()) {
            allIn();
        } else if (shouldFold()) {
            fold();
        }
        // fold only when nothing else can be done
    }



    @Override
    protected boolean shouldCheck() {
        // always check if possible (check when there is no bet on the table)
        return !getGameState().isActiveBet();
    }

    @Override
    protected boolean shouldFold() {
        // only fold with bad hand at high bets
        return evaluatePlayerHand().getValue() < HandRanks.PAIR.getValue() && getGameState().getTableBet() > getBank() * 0.1;
    }

    @Override
    protected boolean shouldCall() {
        // hand evaluation
        HandRanks handRanks = evaluatePlayerHand();
        double callPercentage = 0;

        switch (handRanks) {
            case HIGH_CARD:
            case PAIR:
                callPercentage = .10; // call if the bet is 10% or less of the player's bank
                break;
            case TWO_PAIR:
                callPercentage = 0.25; // call if the bet is 25% or less of the player's bank
                break;
            case THREE_OF_A_KIND:
            case STRAIGHT:
            case FLUSH:
                callPercentage = 0.5; // call if the bet is 50% or less of the player's bank
                break;
            case FULL_HOUSE:
            case FOUR_OF_A_KIND:
            case STRAIGHT_FLUSH:
                callPercentage = 0.8; // call if the bet is 80% or less of the player's bank
                break;
            case ROYAL_FLUSH:
                return true; // always call for a royal flush
        }

        double maxBetAllowed = getBank() * callPercentage;
        double currentBet = getGameState().getTableBet();
        double remainingBet = maxBetAllowed - currentBet;

        // check if the remaining bet is pos
        return remainingBet >= 0;
    }

    @Override
    protected boolean shouldRaise() {
        HandRanks handRanks = evaluatePlayerHand();
        double betPercentage = 0;

        switch (handRanks) {
            case HIGH_CARD:
            case PAIR:
                betPercentage = 0.10; // 10% raise for high card and pair
                break;
            case TWO_PAIR:
                betPercentage = 0.15; // 15% raise for two pairs
                break;
            case THREE_OF_A_KIND:
            case STRAIGHT:
                betPercentage = 0.25; // 25% raise for three of a kind and straight
                break;
            case FLUSH:
                betPercentage = 0.30; // 30% raise for flush
                break;
            case FULL_HOUSE:
                betPercentage = 0.35; // 35% raise for full house
                break;
            case FOUR_OF_A_KIND:
                betPercentage = 0.40; // 40% raise for four of a kind
                break;
            case STRAIGHT_FLUSH:
                betPercentage = 0.75; // 75% raise for straight flush
                break;
            case ROYAL_FLUSH:
                return true; // go all-in for royal flush
        }

        // Calculate the bet amount as a percentage of SanfordPlayer's bankroll
        int betAmount = (int) (getBank() * betPercentage);

        // Ensure that the bet amount is at least the minimum bet required
        if (betAmount >= getGameState().getTableMinBet()) {
            // Raise by the rounded amount
            raise(betAmount);
            return true;
        } else {
            // If the calculated bet amount is less than the minimum bet, do not raise
            return false;
        }
    }

    private double getRaisePercentage(HandRanks handRanks) {
        // define raise percentages for different hand ranks
        switch (handRanks) {
            case HIGH_CARD:
            case PAIR:
                return 0.10; // 10% raise for high card and pair
            case TWO_PAIR:
                return 0.15; // 15% raise for two pairs
            case THREE_OF_A_KIND:
            case STRAIGHT:
                return 0.25; // 25% raise for three of a kind and straight
            case FLUSH:
                return 0.30; // 30% raise for flush
            case FULL_HOUSE:
                return 0.35; // 35% raise for full house
            case FOUR_OF_A_KIND:
                return 0.40; // 40% raise for four of a kind
            case STRAIGHT_FLUSH:
                return 0.75; // 75% raise for straight flush
            case ROYAL_FLUSH:
                return 1.0; // go all-in for royal flush
            default:
                return 0.0; // default to no raise
        }
    }





    @Override
    protected boolean shouldAllIn() {
        // all in only with royal flush only if there is no check
        return evaluatePlayerHand().getValue() >= HandRanks.ROYAL_FLUSH.getValue();
    }
}
