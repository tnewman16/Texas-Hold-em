package Poker;

import java.util.*;

/**
 * Created by ty on 10/13/15.
 *
 * A class that is used by the Table to obtain the winner of a single round of poker.
 * Is initially given the list of players.
 * Is later given the number of folded players in a round along with what cards are on the table.
 *
 * @author Tyler Newman
 */
public class HandChecker {

    private ArrayList<Player> players;      // keeps track of the players throughout the game


    public HandChecker(ArrayList<Player> players) {
        this.players = players;
    }


    /**
     * Goes through each non-folded players hand and determines its value when compared to the pot.
     * Returns the position of the winner.
     *
     * @param numOfFoldedPlayers - the number of players in the current round that are folded
     * @param cardsOnTable - an ArrayList of the cards that are on the table in the current round
     * @return - an array of integers that contains the position(s) of the winner(s) in a round (multiple for a tie)
     */
    public int[] determineWinner(int numOfFoldedPlayers, ArrayList<Card> cardsOnTable) {


        int winningPosition = 0;    // if only one player is left, they win

        if ((players.size() - numOfFoldedPlayers) == 1) {
            // if there is only one player left, they win
            return new int[] {winningPosition};

        } else if (numOfFoldedPlayers > players.size()) {
            // if there are more folded players than there are players... something messed up, so throw out of bounds exception
            throw new IndexOutOfBoundsException();
        }

        // an ArrayList of the values for each player (which are each stored as ArrayList<double[]>)
        ArrayList<ArrayList<int[]>> valuesForEachPlayer = new ArrayList<>();
        ArrayList<Integer> weightsForEachPlayer =new ArrayList<>();

        for (Player player : players) {
            if (player.isFolded()) {
                valuesForEachPlayer.add(new ArrayList<>());     // if the player is folded, add an empty arrayList
                weightsForEachPlayer.add(0);    // also add a 0 for their hand's weight

            } else {   // only check the hands of the players that aren't folded

                // add the values of the cards from the table to each player's hand
                for (Card card : cardsOnTable) {
                    player.giveCard(card);
                }
                // check the value of the player's hand
                ArrayList<int[]> values = getValueOfHand(player.getHand().getCardValues());
                valuesForEachPlayer.add(values);
                weightsForEachPlayer.add(values.get(0)[0]);
            }
        }

        ArrayList<Integer> tiePositions = new ArrayList<>();

        int highestValue = Collections.max(weightsForEachPlayer);

        // Check if there are multiple of the same weight (a tie is present)
        ArrayList<Integer> duplicates = new ArrayList<>();      // holds the duplicate weights
        for (int i=0; i<weightsForEachPlayer.size()-1; i++) {
            int weightOne = weightsForEachPlayer.get(i);

            for (int j=i+1; j<weightsForEachPlayer.size(); j++) {
                int weightTwo = weightsForEachPlayer.get(j);

                // add the duplicate value to the duplicates list if it isn't already there
                if (weightOne == weightTwo && !duplicates.contains(weightTwo)) {
                    duplicates.add(weightTwo);
                }
            }
        }

        int duplicateMax;
        try {
            duplicateMax = Collections.max(duplicates);
        } catch (NoSuchElementException e) {
            duplicateMax = 0;   // if there are no duplicates, set the max to 0
        }

            // if there is a duplicate and the duplicate is on the highest weight value
        boolean tieWins = false;
        if ((duplicates.size() > 0) && (duplicateMax == highestValue)) {
            winningPosition = weightsForEachPlayer.indexOf(highestValue);   // starting position
            weightsForEachPlayer.set(winningPosition, 0);   // set the starting position to 0 to obtain the next position

            boolean done = false;
            while (!done) {
                int nextPosition = weightsForEachPlayer.indexOf(highestValue);

                int winner = compareHands(valuesForEachPlayer.get(winningPosition),
                        valuesForEachPlayer.get(nextPosition));

                // check to see if finished for later (do this before setting any positions)
                if (nextPosition == weightsForEachPlayer.lastIndexOf(highestValue)) {
                    done = true;
                }

                if (winner == 0) {  // if the first wins
                    weightsForEachPlayer.set(nextPosition, 0);  // set the second position to 0 to obtain the next position later
                    weightsForEachPlayer.set(winningPosition, 0);

                } else if (winner == 1) {   // if the second wins
                    weightsForEachPlayer.set(winningPosition, 0);   // set the first position to 0 to obtain the next position later
                    winningPosition = nextPosition;     // the new winning position is the second position
                    tieWins = false;

                } else {    // if it is a tie, add the positions to the tie list if they are not already there
                    if (!tiePositions.contains(winningPosition)) {
                        tiePositions.add(winningPosition);
                    }
                    if (!tiePositions.contains(nextPosition)) {
                        tiePositions.add(nextPosition);
                    }
                    weightsForEachPlayer.set(winningPosition, 0);   // set the first position to 0 to obtain the next position late
                    winningPosition = nextPosition;
                    tieWins = true;
                }
            }

        } else {
            winningPosition = weightsForEachPlayer.indexOf(highestValue);
        }

            // if there IS NO TIE
        if (!tieWins) {
            return new int[] {winningPosition};

            // if there IS A TIE
        } else {
            int[] positions = new int[tiePositions.size()];     // an array to contain each tied position

            for (int i=0; i<tiePositions.size(); i++) {
                positions[i] = tiePositions.get(i);     // add each tied position to the array
            }

            return positions;
        }

    }

    /**
     * Compares two hands to each other to see which one is the winner.
     * Is used by the determineWinner() method.
     * Returns 0 for first hand and 1 for second hand (-1 if tie).
     *
     * @param hand1Values - the values of the cards in hand one
     * @param hand2Values -  the values of the cards in hand two
     * @return - an integer that indicates which hand won between the two
     */
    public int compareHands(ArrayList<int[]> hand1Values, ArrayList<int[]> hand2Values) {

            // if the first hand is better
        if (hand1Values.get(0)[0] > hand2Values.get(0)[0]) {
            return 0;   // winner is the first hand

            // if the second hand is better
        } else if (hand2Values.get(0)[0] > hand1Values.get(0)[0]) {
            return 1;   // winner is the second hand

            // if they are equal
        } else {
            int hand1Val = hand1Values.get(0)[0];

            int hand1RankValue = hand1Values.get(1)[0];
            int hand2RankValue = hand2Values.get(1)[0];

            int hand1HighestPair = hand1Values.get(2)[0];
            int hand2HighestPair = hand2Values.get(2)[0];

            int hand1NextHighestPair = hand1Values.get(3)[0];
            int hand2NextHighestPair = hand2Values.get(3)[0];

            int[] hand1Kickers = hand1Values.get(4);
            int[] hand2Kickers = hand2Values.get(4);

            if (hand1Val == 9) {  // if they share the same STRAIGHT FLUSH
                // check high card
                if (hand1RankValue > hand2RankValue) {
                    return 0;   // one wins

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;   // two wins

                } else {
                    return -1;  // tie
                }

            } else if (hand1Val == 8) {   // if they share the same FOUR OF A KIND
                // check highest 4-set
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else {
                    // if they are the same, check the first kickers
                    if (hand1Kickers[0] > hand2Kickers[0]) {
                        return 0;

                    } else if (hand2Kickers[0] > hand1Kickers[0]) {
                        return 1;

                    } else {
                        return -1;
                    }
                }

            } else if (hand1Val == 7) {   // if they share the same FULL HOUSE
                // highest 3-set wins when there is only one community deck
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else if (hand1Kickers[0] > hand2Kickers[0]) {
                    return 0;

                } else if (hand2Kickers[0] > hand1Kickers[0]) {
                    return 1;

                } else {
                    return -1;
                }

            } else if (hand1Val == 6) {   // if they share the same FLUSH
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else if (hand1Kickers[0] > hand2Kickers[0]) {
                    return 0;

                } else if (hand2Kickers[0] > hand1Kickers[0]) {
                    return 1;

                } else if (hand1Kickers[1] > hand2Kickers[1]) {
                    return 0;

                } else if (hand2Kickers[1] > hand1Kickers[1]) {
                    return 1;

                } else if (hand1Kickers[2] > hand2Kickers[2]) {
                    return 0;

                } else if (hand2Kickers[2] > hand1Kickers[2]) {
                    return 1;

                } else {
                    // if they share the same flush, they tie
                    return -1;
                }

            } else if (hand1Val == 5) {   // if they share the same STRAIGHT
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else {
                    // if they share the same high straight card, they tie
                    return -1;
                }

            } else if (hand1Val == 4) {   // if they share the same THREE OF A KIND
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else {
                    // if they share the same 3-set, check next two kickers
                    int hand1Kicker1 = hand1Kickers[0];
                    int hand2Kicker1 = hand2Kickers[0];

                    int hand1Kicker2 = hand1Kickers[1];
                    int hand2Kicker2 = hand2Kickers[1];

                    // check first kickers
                    if (hand1Kicker1 > hand2Kicker1) {
                        return 0;

                    } else if (hand2Kicker1 > hand1Kicker1) {
                        return 1;

                    } else {
                        // if they share the same first kicker, check second kickers
                        if (hand1Kicker2 > hand2Kicker2) {
                            return 0;

                        } else if (hand2Kicker2 > hand1Kicker2) {
                            return 1;

                        } else {
                            // if they share the same second kicker, they tie
                            return -1;
                        }
                    }
                }

            } else if (hand1Val == 3) {   // if they share the same TWO PAIR
                if (hand1HighestPair > hand2HighestPair) {
                    return 0;

                } else if (hand2HighestPair > hand1HighestPair) {
                    return 1;

                } else {
                    // if they share the same first pair, check second pair
                    if (hand1NextHighestPair > hand2NextHighestPair) {
                        return 0;

                    } else if (hand2NextHighestPair > hand1NextHighestPair) {
                        return 1;

                    } else {
                        // if they share the same two pairs, check the first kickers
                        if (hand1Kickers[0] > hand2Kickers[0]) {
                            return 0;

                        } else if (hand2Kickers[0] > hand1Kickers[0]) {
                            return 1;

                        } else {
                            // if they also share the same kicker, they tie
                            return -1;
                        }
                    }
                }

            } else if (hand1Val == 2) {   // if they share the same ONE PAIR
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else {
                    // if they share the same pair, check next three kickers
                    int hand1Kicker1 = hand1Kickers[0];
                    int hand2Kicker1 = hand2Kickers[0];

                    int hand1Kicker2 = hand1Kickers[1];
                    int hand2Kicker2 = hand2Kickers[1];

                    int hand1Kicker3 = hand1Kickers[2];
                    int hand2Kicker3 = hand2Kickers[2];

                    // check first kickers
                    if (hand1Kicker1 > hand2Kicker1) {
                        return 0;

                    } else if (hand2Kicker1 > hand1Kicker1) {
                        return 1;

                    } else {
                        // if they share the same first kicker, check second kickers
                        if (hand1Kicker2 > hand2Kicker2) {
                            return 0;

                        } else if (hand2Kicker2 > hand1Kicker1) {
                            return 1;

                        } else {
                            // if they share the same second kicker, check third kickers
                            if (hand1Kicker3 > hand2Kicker3) {
                                return 0;

                            } else if (hand2Kicker3 > hand1Kicker3) {
                                return 1;

                            } else {
                                // if they share the same third kicker, they tie
                                return -1;
                            }
                        }
                    }
                }

            } else {    // if they both have just a HIGH CARD
                if (hand1RankValue > hand2RankValue) {
                    return 0;

                } else if (hand2RankValue > hand1RankValue) {
                    return 1;

                } else {
                    // if they share the same high card, check next four kickers
                    int hand1Kicker1 = hand1Kickers[0];
                    int hand2Kicker1 = hand2Kickers[0];

                    int hand1Kicker2 = hand1Kickers[1];
                    int hand2Kicker2 = hand2Kickers[1];

                    int hand1Kicker3 = hand1Kickers[2];
                    int hand2Kicker3 = hand2Kickers[2];

                    // check first kickers
                    if (hand1Kicker1 > hand2Kicker1) {
                        return 0;

                    } else if (hand2Kicker1 > hand1Kicker1) {
                        return 1;

                    } else {
                        // if they share the same first kicker, check second kickers
                        if (hand1Kicker2 > hand2Kicker2) {
                            return 0;

                        } else if (hand2Kicker2 > hand1Kicker1) {
                            return 1;

                        } else {
                            // if they share the same second kicker, check third kickers
                            if (hand1Kicker3 > hand2Kicker3) {
                                return 0;

                            } else if (hand2Kicker3 > hand1Kicker3) {
                                return 1;

                            } else {
                                // if they share the same third kicker, check fourth kickers
                                if (hand1HighestPair > hand2HighestPair) {
                                    return 0;

                                } else if (hand2HighestPair > hand1HighestPair) {
                                    return 1;

                                } else {
                                    // if they share the same fourth kicekr, they tie
                                    return -1;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the value of one Hand containing 7 Cards (includes the Cards from the Table).
     * Checks for the following (ranked from worst to best):
     *     (decimals are used for the backup high card value)
     *     1.0)     High Card
     *     2.0)     One Pair
     *     3.0)     Two Pair
     *     4.0)     Three of a Kind
     *     5.0)     Straight
     *     6.0)     Flush
     *     7.0)     Full House
     *     8.0)     Four of a Kind
     *     9.0)     Straight Flush
     *     10.0)    Royal Flush
     *
     * Is used by the compareHands() method.
     *
     * @param hand - the card values in a specific hand as strings
     * @return - an array of doubles indicating the overall value of the hand
     */
    public ArrayList<int[]> getValueOfHand(ArrayList<String> hand) {

        ArrayList<String> handValues = new ArrayList<>();   // list of all Card values
        ArrayList<String> handSuits = new ArrayList<>();    // list of all Card suits

        for (String card : hand) {
            String[] split = card.split("-");
            // parallel lists (one with values and one with suits)
            handValues.add(split[0]);       // add the value of each Card to handValues
            handSuits.add(split[1]);        // add the suit of each Card to handSuits
        }

        ArrayList<Integer> handIntValues = new ArrayList<>();   // holds all the values as integers

        for (String val : handValues) {
            int integerVal;
            try {
                integerVal = (Integer.parseInt(val));
            } catch (Exception ex) {
                integerVal = 11;
            }

            if (!(integerVal <= 10)) {     // if the value is greater than 10, add a corresponding integer
                if (val.equals("J")) {
                    handIntValues.add(11);
                } else if (val.equals("Q")) {
                    handIntValues.add(12);
                } else if (val.equals("K")) {
                    handIntValues.add(13);
                } else {
                    handIntValues.add(14);
                }
            } else {
                handIntValues.add(integerVal);      // if the value is 10 or less, add the integerValue
            }
        }

        // returns an ArrayList<Double[]> of format:    {handRank, rankValue, highestPair, nextHighestPair, kickers}
        ArrayList<int[]> flushAndStraightValues = checkFlushAndStraight(handValues, handIntValues, handSuits);

        // returns an ArrayList<Double[]> of format:    {handRank, rankValue, highestPair, nextHighestPair, kickers}
        ArrayList<int[]> houseKindsAndPairsValues = checkHouseKindsAndPairs(handIntValues);

        // if the overall hand rank from houseKindsAndPairsValue is highest, return it
        if (houseKindsAndPairsValues.get(0)[0] > flushAndStraightValues.get(0)[0]) {
            return houseKindsAndPairsValues;
        } else {
            return flushAndStraightValues;
        }
    }

    /**
     * Checks to see whether the given list of handIntValues contains either a Four of a Kind,
     * Full House, Three of a Kind, Two Pair, or One Pair.
     * Is used by the getValueOfHand() method.
     *
     * @param handIntValues - the number values in a specific hand as integers
     * @return - an array of doubles indicating the overall value of the hand
     */
    public ArrayList<int[]> checkHouseKindsAndPairs(ArrayList<Integer> handIntValues) {
        // returns a double
        int handRank;
        int rankValue = 0;
        int kicker1 = 0;
        int kicker2 = 0;
        int kicker3 = 0;

        int highestPair = 0;
        int nextHighestPair = 0;

        ArrayList<int[]> returnValue = new ArrayList<>();

        // an array to hold number of times a specific value occurs within a hand
                        // FORMAT:    {2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14}
        Integer[] numberOfEachValue = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int value : handIntValues) {
            numberOfEachValue[value - 2]++; // 13 possible values (2 - 14) so indexes = 0 - 12 (value - 2)
        }

        List<Integer> eachValueAsList = Arrays.asList(numberOfEachValue);

        // CHECK FOUR OF A KIND
        if (eachValueAsList.contains(4)) {   // if it contains four of the same value
            handRank = 8;     // Four of a Kind
            rankValue = eachValueAsList.indexOf(4) + 2;

            returnValue.add(new int[] {handRank});
            returnValue.add(new int[] {rankValue});
            returnValue.add(new int[] {highestPair});            // highestPair (0)
            returnValue.add(new int[] {nextHighestPair});        // nextHighestPair (0)
            returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

            return returnValue;
        }

        // CHECK FULL HOUSE
        boolean fullHouse = false;
        if ((eachValueAsList.contains(3) &&
                (eachValueAsList.indexOf(3) != eachValueAsList.lastIndexOf(3)))) {
            // if there are two different 3's then fullHouse is true
            fullHouse = true;
            if (eachValueAsList.indexOf(3) > eachValueAsList.lastIndexOf(3)) {
                // if first 3 is greater than last 3, set rankValue equal to it
                rankValue = eachValueAsList.indexOf(3) + 2;
                kicker1 = eachValueAsList.lastIndexOf(3);   // kicker is the lower 3-Set

            } else {
                rankValue = eachValueAsList.lastIndexOf(3) + 2;
                kicker1 = eachValueAsList.indexOf(3);   // kicker is the lower 3-Set
            }

        } else if (eachValueAsList.contains(2) && eachValueAsList.contains(3)) {
            // if it has both a 2 and a 3 then fullHouse is true
            fullHouse = true;
            rankValue = eachValueAsList.indexOf(3) + 2;
            if (eachValueAsList.indexOf(2) != eachValueAsList.lastIndexOf(2)) {
                // if there are multiple pairs of two
                ArrayList<Integer> values = new ArrayList<>();
                values.add(eachValueAsList.indexOf(2) + 2);
                values.add(eachValueAsList.lastIndexOf(2) + 2);
                kicker1 = Collections.max(values);      // sets kicker to the largest other two pair
            } else {
                kicker1 = eachValueAsList.indexOf(2) + 2;   // otherwise sets it to the only two pair
            }
        }

        // FULL HOUSE CONTINUED
        if (fullHouse) {     // if it contains either a 2 or 3 AND also another 3
            handRank = 7;     // FULL HOUSE - has one kicker a.k.a. the value of the 2-pair

            returnValue.add(new int[] {handRank});
            returnValue.add(new int[] {rankValue});
            returnValue.add(new int[] {highestPair});            // highestPair
            returnValue.add(new int[] {nextHighestPair});        // nextHighestPair
            returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

            return returnValue;
        }

        // CHECK THREE OF A KIND
        if ((eachValueAsList.contains(3))) {
            handRank = 4;     // THREE OF A KIND - has two kickers
            rankValue = eachValueAsList.indexOf(3) + 2;
            for (int i=0; i<3; i++) {
                handIntValues.remove(handIntValues.indexOf(rankValue));    // remove all of the cards in the 3 of a Kind from the hand
            }
            kicker1 = Collections.max(handIntValues);   // find the first kicker in remaining values
            handIntValues.remove(handIntValues.indexOf(kicker1));          // remove kicker1 from the hand
            kicker2 = Collections.max(handIntValues);   // find the second kicker in what's left

            returnValue.add(new int[] {handRank});
            returnValue.add(new int[] {rankValue});
            returnValue.add(new int[] {highestPair});            // highestPair
            returnValue.add(new int[]{nextHighestPair});        // nextHighestPair
            returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

            return returnValue;
        }

        // CHECK TWO PAIR
        if (eachValueAsList.contains(2)) {
            List<Integer> splitList = eachValueAsList.subList(eachValueAsList.indexOf(2)+1, eachValueAsList.size());
            // if multiple pairs are present
            if (eachValueAsList.indexOf(2) != eachValueAsList.lastIndexOf(2)) {
                // if there are three different pairs
                if (splitList.indexOf(2) != splitList.lastIndexOf(2)) {

                    highestPair = splitList.lastIndexOf(2) + 2;     // highest will be farthest on the right
                    nextHighestPair = splitList.indexOf(2) + 2;     // next highest will be the next on the left

                    // find the kicker if three pairs of two are present
                    ArrayList<Integer> newList = new ArrayList<>();
                    for (int value : handIntValues) {
                        if (value != highestPair && value != nextHighestPair) {
                            newList.add(value);
                        }
                    }
                    kicker1 = Collections.max(newList);

                    handRank = 3;     // TWO PAIR - has one kicker

                    returnValue.add(new int[] {handRank});
                    returnValue.add(new int[] {rankValue});
                    returnValue.add(new int[] {highestPair});            // highestPair
                    returnValue.add(new int[] {nextHighestPair});        // nextHighestPair
                    returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

                    return returnValue;
                } else {
                    // if there are only two different pairs

                    int first = eachValueAsList.indexOf(2) + 2;
                    int second = eachValueAsList.lastIndexOf(2) + 2;
                    if (first > second) {
                        highestPair = first;
                        nextHighestPair = second;
                    } else {
                        highestPair = second;
                        nextHighestPair = first;
                    }

                    // find the kicker if there are only two pairs of two present
                    ArrayList<Integer> newList = new ArrayList<>();
                    for (int value : handIntValues) {
                        if (value != highestPair && value != nextHighestPair) {
                            newList.add(value);
                        }
                    }
                    kicker1 = Collections.max(newList);

                    handRank = 3;     // TWO PAIR - has one kicker

                    returnValue.add(new int[] {handRank});
                    returnValue.add(new int[] {rankValue});
                    returnValue.add(new int[] {highestPair});            // highestPair
                    returnValue.add(new int[] {nextHighestPair});        // nextHighestPair
                    returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

                    return returnValue;
                }
            }
        }

        // CHECK ONE PAIR
        if (eachValueAsList.contains(2)) {
            handRank = 2;     // ONE PAIR - needs three kickers
            rankValue = eachValueAsList.indexOf(2) + 2;
            for (int i=0; i<2; i++) {
                handIntValues.remove(handIntValues.indexOf(rankValue));
            }

            kicker1 = Collections.max(handIntValues);
            handIntValues.remove(handIntValues.indexOf(kicker1));
            kicker2 = Collections.max(handIntValues);
            handIntValues.remove(handIntValues.indexOf(kicker2));
            kicker3 = Collections.max(handIntValues);
            handIntValues.remove(handIntValues.indexOf(kicker3));

            returnValue.add(new int[] {handRank});
            returnValue.add(new int[] {rankValue});
            returnValue.add(new int[] {highestPair});            // highestPair
            returnValue.add(new int[] {nextHighestPair});        // nextHighestPair
            returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

            return returnValue;
        }

        // IF STILL HERE, HAND VALUE IS HIGH CARD
        handRank = 1;
        rankValue = Collections.max(handIntValues); // get value of the high card
        handIntValues.remove(handIntValues.indexOf(rankValue));
        // get each kicker
        kicker1 = Collections.max(handIntValues);
        handIntValues.remove(handIntValues.indexOf(kicker1));
        kicker2 = Collections.max(handIntValues);
        handIntValues.remove(handIntValues.indexOf(kicker2));
        kicker3 = Collections.max(handIntValues);
        handIntValues.remove(handIntValues.indexOf(kicker3));

        highestPair = Collections.max(handIntValues);   // store the final kicker in highestPair
        handIntValues.remove(handIntValues.indexOf(highestPair));

        returnValue.add(new int[] {handRank});
        returnValue.add(new int[] {rankValue});
        returnValue.add(new int[] {highestPair});            // highestPair
        returnValue.add(new int[] {nextHighestPair});        // nextHighestPair
        returnValue.add(new int[] {kicker1, kicker2, kicker3});      // kickers

        assert (returnValue.size() == 5);   // make sure that the size of the return ArrayList is 5

        return returnValue;
    }

    /**
     * Checks to see if given parallel lists of handValues, handIntValues, and handSuits contain
     * either a Royal Flush, Straight Flush, Regular Flush, or Straight.
     * Is used by the getValueOfHand() method.
     *
     * @param handValues - the values in a specific hand as strings
     * @param handIntValues - the number values in a specific hand as integers
     * @param handSuits - the suit values in a specific hand as strings
     * @return - an array of doubles indicating the overall value of the hand
     */
    public ArrayList<int[]> checkFlushAndStraight(ArrayList<String> handValues,
                                                     ArrayList<Integer> handIntValues,
                                                     ArrayList<String> handSuits) {
        // returns a double (0.0: none, 10.0: Royal Flush, 9.0: Straight Flush, 6.0: Flush, 5.0: Straight)
        int handRank = 0;
        int rankValue = 0;

        ArrayList<int[]> returnValue = new ArrayList<>();

        String[] royalties = {"10","J","Q","K", "A"};
        // an array to check if 5 of the suits are the same (if so, then royal flush is true)
                                 // FORMAT:     {S, D, H, C}
        Integer[] numberOfEachSuitRoyal    =    {0, 0, 0, 0};   // keep track of only royals
        Integer[] numberOfEachSuitRegular  =    {0, 0, 0, 0};   // keep track of everything

        // a list to store lists containing all the values of a specific suit
        ArrayList<ArrayList<Integer>> allValues = new ArrayList<>();  // FORMAT: {S, D, H, C}
        allValues.add(new ArrayList<>());
        allValues.add(new ArrayList<>());
        allValues.add(new ArrayList<>());
        allValues.add(new ArrayList<>());

        for (int i=0; i<handSuits.size(); i++) {
            String suit = handSuits.get(i);

            // if the value is of royalty, add its suit to the array
            if (Arrays.asList(royalties).contains(handValues.get(i))){

                if (suit.equals("S")) {
                    numberOfEachSuitRoyal[0]++;
                    numberOfEachSuitRegular[0]++;
                    allValues.get(0).add(handIntValues.get(i));     // also add each value to the allValues list

                } else if (suit.equals("D")) {
                    numberOfEachSuitRoyal[1]++;
                    numberOfEachSuitRegular[1]++;
                    allValues.get(1).add(handIntValues.get(i));

                } else if (suit.equals("H")) {
                    numberOfEachSuitRoyal[2]++;
                    numberOfEachSuitRegular[2]++;
                    allValues.get(2).add(handIntValues.get(i));

                } else {
                    numberOfEachSuitRoyal[3]++;
                    numberOfEachSuitRegular[3]++;
                    allValues.get(3).add(handIntValues.get(i));
                }
            } else {
                if (suit.equals("S")) {
                    numberOfEachSuitRegular[0]++;
                    allValues.get(0).add(handIntValues.get(i));

                } else if (suit.equals("D")) {
                    numberOfEachSuitRegular[1]++;
                    allValues.get(1).add(handIntValues.get(i));

                } else if (suit.equals("H")) {
                    numberOfEachSuitRegular[2]++;
                    allValues.get(2).add(handIntValues.get(i));

                } else {
                    numberOfEachSuitRegular[3]++;
                    allValues.get(3).add(handIntValues.get(i));
                }
            }
        }

        // CHECK ROYAL FLUSH
        if (Arrays.asList(numberOfEachSuitRoyal).contains(5)) {
            handRank = 10;  // ROYAL FLUSH - no kickers a.k.a. automatic win

            returnValue.add(new int[] {handRank});
            returnValue.add(new int[] {rankValue});
            returnValue.add(new int[] {0});        // highestPair
            returnValue.add(new int[] {0});        // nextHighestPair
            returnValue.add(new int[] {0, 0, 0});      // kickers

            return (returnValue);
        }


        List<Integer> eachSuitAsList = Arrays.asList(numberOfEachSuitRegular);  // make it a list for parsing

        // CHECK FLUSHES (STRAIGHT AND REGULAR)
        if (eachSuitAsList.contains(5)
                || eachSuitAsList.contains(6)
                || eachSuitAsList.contains(7)){   // if there is a flush, check for a straight flush

            // get the list of values that have the same suit as the flush
            // index depends on if it is 5, 6, or 7
            ArrayList<Integer> straightSuitValues;
            if (eachSuitAsList.contains(5)) {
                straightSuitValues = allValues.get(eachSuitAsList.indexOf(5));
            } else if (eachSuitAsList.contains(6)) {
                straightSuitValues = allValues.get(eachSuitAsList.indexOf(6));
            } else {
                straightSuitValues = allValues.get(eachSuitAsList.indexOf(7));
            }

            Collections.sort(straightSuitValues);

            boolean finished = false;
            while (!finished) {
                // first check for straights with Aces as 14
                for (int i = 0; i < (straightSuitValues.size() - 4); i++) {   // go through once if there are 5, twice if 6, thrice if 7

                    int place = straightSuitValues.size() - 1 - i;  // start at the end and decrement one each time
                    int first = straightSuitValues.get(place);
                    int second = straightSuitValues.get(place - 1);
                    int third = straightSuitValues.get(place - 2);
                    int fourth = straightSuitValues.get(place - 3);
                    int fifth = straightSuitValues.get(place - 4);

                    if (first - 1 == second && second - 1 == third
                            && third - 1 == fourth && fourth - 1 == fifth) {

                        rankValue = first;

                        // Straight Flush is true
                        handRank = 9;

                        returnValue.add(new int[]{handRank});
                        returnValue.add(new int[]{rankValue});
                        returnValue.add(new int[]{0});        // highestPair
                        returnValue.add(new int[]{0});        // nextHighestPair
                        returnValue.add(new int[]{0, 0, 0});      // kickers

                        return (returnValue);
                    }
                }

                // then if there is an Ace, set it equal to one and recheck
                if (straightSuitValues.contains(14)) {
                    straightSuitValues.set(straightSuitValues.indexOf(14), 1);
                    Collections.sort(straightSuitValues);

                } else {    // if there is no Ace or it is a 1, exit the loop
                    finished = true;
                }
            }

            if (straightSuitValues.contains(1)) {   // if the Ace was changed to a 1, change it back
                straightSuitValues.set(straightSuitValues.indexOf(1), 14);
                Collections.sort(straightSuitValues);
            }

            // if there is no straight flush, get the rankValue for the regular flush

            handRank = 6;   // FLUSH - no kickers a.k.a. decided by rankValue
            rankValue = straightSuitValues.get(straightSuitValues.size()-1);    // highest is last, then go down the array


            returnValue.add(new int[] {handRank});
            returnValue.add(new int[] {rankValue});
            returnValue.add(new int[] {0});        // highestPair
            returnValue.add(new int[] {0});        // nextHighestPair
            returnValue.add(new int[] {straightSuitValues.get(straightSuitValues.size()-2),
                                        straightSuitValues.get(straightSuitValues.size()-3),
                                        straightSuitValues.get(straightSuitValues.size()-4)});  // kickers

            return (returnValue);

        }

        // CHECK STRAIGHT
        else {    // if there is no flush, check if there is a straight

            List<Integer> newHandIntValues = new ArrayList<>(handIntValues);    // copy handIntValues so that original doesn't change

            // sort the new list
            Collections.sort(newHandIntValues);

            // get rid of any duplicates
            Set<Integer> hs = new HashSet<>();
            hs.addAll(newHandIntValues);
            newHandIntValues.clear();
            newHandIntValues.addAll(hs);

            if (newHandIntValues.size() > 4) {     // only check straight if it is possible (5 different integers)

                boolean finished = false;
                while (!finished) {
                    // first check for straights with Aces as 14
                    for (int i = 0; i < (newHandIntValues.size() - 4); i++) {   // go through once if there are 5, twice if 6, thrice if 7

                        int place = newHandIntValues.size() - 1 - i;  // start at the end and decrement one each time
                        int first = newHandIntValues.get(place);
                        int second = newHandIntValues.get(place - 1);
                        int third = newHandIntValues.get(place - 2);
                        int fourth = newHandIntValues.get(place - 3);
                        int fifth = newHandIntValues.get(place - 4);

                        if (first - 1 == second && second - 1 == third
                                && third - 1 == fourth && fourth - 1 == fifth) {

                            rankValue = first;

                            // Straight Flush is true
                            handRank = 5;

                            returnValue.add(new int[]{handRank});
                            returnValue.add(new int[]{rankValue});
                            returnValue.add(new int[]{0});        // highestPair
                            returnValue.add(new int[]{0});        // nextHighestPair
                            returnValue.add(new int[]{0, 0, 0});      // kickers

                            return (returnValue);
                        }
                    }

                    // then if there is an Ace, set it equal to one and recheck
                    if (newHandIntValues.contains(14)) {
                        newHandIntValues.set(newHandIntValues.indexOf(14), 1);
                        Collections.sort(newHandIntValues);

                    } else {    // if there is no Ace or it is a 1, exit the loop
                        finished = true;
                    }
                }

            }
        }

        // return everything in an array (don't need kickers or pairs from this specific method)
        returnValue.add(new int[] {handRank});
        returnValue.add(new int[] {rankValue});
        returnValue.add(new int[] {0});        // highestPair
        returnValue.add(new int[] {0});        // nextHighestPair
        returnValue.add(new int[] {0, 0, 0});      // kickers

        return (returnValue);
    }

}
