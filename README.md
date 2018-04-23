# Tetris

This is my version of Tetris made in Java.

You can download it and modify it as you want. If you want to add more pieces to the game, there are instructions at the top of the Tetromino.java

You can also download the .jar file ready to play.

By the time I'm uploading the code the game is NOT complete.

**Please**, if you distribute the code or the code modified by you, **give credit** linking this original source.

## Getting Started

- [x] Add key to drop directly a key to the bottom
- [x] Add key to change the tetromino and store it for later
- [x] Make the player loose when he/she reaches the top of the board with a piece
- [ ] Make the tetromino shadow to see where it is going to fall
- [ ] Add sounds
- [ ] Add option to change the background
- [x] Main menu
- [x] Game screen
- [ ] Top scores screen
- [ ] Options screen

## HOW IT WORKS

This is the Board matrix. Black squares will represent non block values and other colors will represent that value of the matrix is a block.

![board_matrix](https://user-images.githubusercontent.com/36163709/38576955-e4fdeda4-3cff-11e8-839f-e5d8f16bb63c.png)

This is the Tetromino matrix. White squares represent no block and green squares represent blocks. Same matrix, for rotations for each Tetromino (except for the square).

![tetromino_matrix](https://user-images.githubusercontent.com/36163709/38577716-02cff6b8-3d02-11e8-8664-cb78f2169a0e.png) ![tetromino_matrix1](https://user-images.githubusercontent.com/36163709/38577887-7feab746-3d02-11e8-9a94-a0c9d9cc6b59.png) ![tetromino_matrix2](https://user-images.githubusercontent.com/36163709/38577888-802c4cc4-3d02-11e8-80c7-de3bc6b177a0.png) ![tetromino_matrix3](https://user-images.githubusercontent.com/36163709/38577885-7fc28c1c-3d02-11e8-9bf3-74bb17369b70.png)

The Tetromino matrix will fall over the Board matrix. When the Board detects if the falling Tetromino collides or not with a block inside it's matrix. If the collision is on the X axis, the board won't let the Tetromino move in that direction, but will continue falling. If the collision is on the Y axis, the Tetromino won't be able to move any more, as it happens in Tetris.

So we will have this situation:

![board_tetro_1](https://user-images.githubusercontent.com/36163709/38578298-d2e3a7e0-3d03-11e8-8119-5392a0d8ffbe.png) ![board_tetro_2](https://user-images.githubusercontent.com/36163709/38578299-d3057550-3d03-11e8-80fc-0bccd7044d2f.png) ![board_tetro_3](https://user-images.githubusercontent.com/36163709/38578300-d3272d26-3d03-11e8-9027-e73e080ebbcd.png)

The moment it reaches the bottom or it collides on Y axis with other block, the Tetromino matrix values get attached to the board matrix

![board_tetro_4](https://user-images.githubusercontent.com/36163709/38578297-d2a86b1c-3d03-11e8-8c8b-dcf0f3f5029a.png)

## REMOVE

When a row is full (no black squares, in out matrix will be no zeros) it is replaced by all black squares and the all blocks above will fall like if they have gravity, to the lowest block they can fall.

![board_tetro_5](https://user-images.githubusercontent.com/36163709/38578672-df0c651a-3d04-11e8-9182-4d43e34b624c.png) ![board_tetro_6](https://user-images.githubusercontent.com/36163709/38578790-46ec79ea-3d05-11e8-8d9f-500f32f51620.png) ![board_tetro_7](https://user-images.githubusercontent.com/36163709/38578813-541e5d90-3d05-11e8-8477-d2eb7c3cb2db.png)

## CONTROLS

![controls](https://user-images.githubusercontent.com/36163709/38580260-c652477e-3d09-11e8-846a-a07d16660570.png)

- C: Change Tetromino
- Up: Drop Tetromino to bottom
- Down: Drop Tetromino faster
- Right: Move Tetromino to the right
- Left: Move Tertomino to the left
- Space: Rotate Tetromino
- P: pause / resume
