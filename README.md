# Skunk
## Mitchell Hoffmann and Eyad Shesli
## SEIS635 Team Project 1

This Java application simulates a game of Skunk, a variant of the Pig dice game.

The game is played with at least two players, each of whom start with 50 chips. A match of Skunk consists of at least one game, and is played until one player holds all the chips, which are accumulated when players pay penalties to the "kitty" during each game.

The goal of each game of Skunk is to beat a target score of 100, and remain in the scoring lead for one more complete round. Players take turns as follows:

1. Roll two dice.

  a. If one dice results in a 1 (a.k.a "Skunk"), you lose your points for this round only and you put one chip in the kitty. Your turn is then considered over and you pass the dice to the next player.
 
  b. If one dice results in a 1 and the other in a 2 (a.k.a. "Deuce"), you lose your points for this round only and you put two chips in the kitty. Your turn is then considered over and you pass the dice to the next player.
 
  c. If both dice result in a 1, you lose all of your points for the game, not just the round, and you put four chips in the kitty. Your turn is then considered over and you pass the dice to the next player.
 
  d. If none of the above conditions happen, you can add the sum of the dice to your score (but not the game) and choose to keep rolling or end your turn.
 
2. If a player is over 100 points for the game when their turn ends, each player has one chance to beat their score, and then each player has one more round after them to beat their score, ad infinitum.
3. If no players can beat the leading player's score within 1 round, that player wins the game and gets to take all the chips in the kitty.
4. If a player cannot pay a chip cost, they pay what they can but do not accrue debt.
5. If a player runs out of chips during a game and does not win the game, they are excluded from future games in the match.
6. A match is played as a series of individual games until one player holds all the chips.
