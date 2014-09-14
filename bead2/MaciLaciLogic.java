package bead2;

import java.util.Random;
import bead2.MaciLaciFrame.Directions;
import java.util.HashMap;
import java.util.Map;

public class MaciLaciLogic {
    
    //a set of states that describe every tile on the gamefield
    public enum State {

        BEAR, RANGER, BASKET, TREE, EMPTY
    }
    private GameStateListener listener;
    private State[][] fields;
    private int maciX;
    private int maciY;
    private int baskets;
    private int size;
    private Map<int[], Directions> rangerStats = new HashMap<>();
    private Random rnd = new Random();

    public State[][] getFields() {
        return fields;
    }

    public int getBaskets() {
        return this.baskets;
    }

    public void setListener(GameStateListener listener) {
        this.listener = listener;
    }

    //depending on what direction was pressed, Mr.Bear gets moved into the right direction
    //besides that, this checks if Mr.Bear wants to go outside of the gamefield, or if he successfully collects a basket
    //moreover, the rangers get moved here as well, and a check if every basket has been collected
    public void changeMaciPosition(Directions d) {
        if (d == Directions.LEFT && maciY != 0 && fields[maciX][maciY - 1] != State.TREE) {
            fields[maciX][maciY] = State.EMPTY;
            maciY -= 1;
            checkForBasket();
            fields[maciX][maciY] = State.BEAR;

            changeRangerPosition();
        } else if (d == Directions.UP && maciX != 0 && fields[maciX - 1][maciY] != State.TREE) {
            fields[maciX][maciY] = State.EMPTY;
            maciX -= 1;
            checkForBasket();
            fields[maciX][maciY] = State.BEAR;

            changeRangerPosition();
        } else if (d == Directions.RIGHT && maciY != this.size - 1 && fields[maciX][maciY + 1] != State.TREE) {
            fields[maciX][maciY] = State.EMPTY;
            maciY += 1;
            checkForBasket();
            fields[maciX][maciY] = State.BEAR;

            changeRangerPosition();
        } else if (d == Directions.DOWN && maciX != this.size - 1 && fields[maciX + 1][maciY] != State.TREE) {
            fields[maciX][maciY] = State.EMPTY;
            maciX += 1;
            checkForBasket();
            fields[maciX][maciY] = State.BEAR;

            changeRangerPosition();
        }
        if (this.baskets == this.size) {
            listener.onFinished();
        }
    }
    //if stepped on a basket, the counter increments
    private void checkForBasket() {
        if (fields[maciX][maciY] == State.BASKET) {
            this.baskets++;
        }
    }
    //depending on what random direction a ranger got, it moves into that direction, if there is no obstacle
    //if it hits a wall, or tree, or another ranger, the direction gets changed into the opposite
    //this means, that one ranger can only move vertically, or horizontally
    private void changeRangerPosition() {
        loop:
        {
            for (Map.Entry<int[], Directions> it : rangerStats.entrySet()) {
                switch (it.getValue()) {
                    case LEFT:
                        if (it.getKey()[1] != 0 && fields[it.getKey()[0]][it.getKey()[1] - 1] == State.EMPTY) {
                            fields[it.getKey()[0]][it.getKey()[1]] = State.EMPTY;
                            it.getKey()[1] -= 1;
                            fields[it.getKey()[0]][it.getKey()[1]] = State.RANGER;
                        } else {
                            it.setValue(Directions.RIGHT);
                        }
                        break;
                    case UP:
                        if (it.getKey()[0] != 0 && fields[it.getKey()[0] - 1][it.getKey()[1]] == State.EMPTY) {
                            fields[it.getKey()[0]][it.getKey()[1]] = State.EMPTY;
                            it.getKey()[0] -= 1;
                            fields[it.getKey()[0]][it.getKey()[1]] = State.RANGER;
                        } else {
                            it.setValue(Directions.DOWN);
                        }
                        break;
                    case RIGHT:
                        if (it.getKey()[1] != this.size - 1 && fields[it.getKey()[0]][it.getKey()[1] + 1] == State.EMPTY) {
                            fields[it.getKey()[0]][it.getKey()[1]] = State.EMPTY;
                            it.getKey()[1] += 1;
                            fields[it.getKey()[0]][it.getKey()[1]] = State.RANGER;
                        } else {
                            it.setValue(Directions.LEFT);
                        }
                        break;
                    case DOWN:
                        if (it.getKey()[0] != this.size - 1 && fields[it.getKey()[0] + 1][it.getKey()[1]] == State.EMPTY) {
                            fields[it.getKey()[0]][it.getKey()[1]] = State.EMPTY;
                            it.getKey()[0] += 1;
                            fields[it.getKey()[0]][it.getKey()[1]] = State.RANGER;
                        } else {
                            it.setValue(Directions.UP);
                        }
                        break;
                }
                listener.onFieldChange();
                //the vicinity of the ranger is checked after it moves
                //if Mr.Bear is found in one of the 8 blocks next ot the ranger, the game is over
                for (int i = it.getKey()[0] - 1; i <= it.getKey()[0] + 1; i++) {
                    for (int j = it.getKey()[1] - 1; j <= it.getKey()[1] + 1; j++) {
                        if (i < fields.length && j < fields.length && i >= 0 && j >= 0) {
                            if (fields[i][j] == State.BEAR) {
                                listener.onFinished();
                                break loop;
                            }
                        }
                    }
                }
            }
        }
    }
    //the gamefield gets randomly populated with trees, baskets, and some rangers (number depends on size of field)
    private void populateField() {
        int num = 1;
        int x, y;
        do {
            x = rnd.nextInt(this.size);
            y = rnd.nextInt(this.size);
            if (fields[x][y] == null && (x >= 2 || y >= 2)) {
                fields[x][y] = State.TREE;
                num++;
            }
        } while (num <= this.size);

        num = 1;
        do {
            x = rnd.nextInt(this.size);
            y = rnd.nextInt(this.size);
            if (fields[x][y] == null && (x >= 2 || y >= 2)) {
                fields[x][y] = State.BASKET;
                num++;
            }
        } while (num <= this.size);

        num = 0;
        do {
            x = rnd.nextInt(this.size);
            y = rnd.nextInt(this.size);
            if (fields[x][y] == null && x >= 2 && y >= 2) {
                fields[x][y] = State.RANGER;
                rangerStats.put(new int[]{x, y}, Directions.values()[rnd.nextInt(4)]);
                num++;
            }
        } while (num + 1 <= this.size / 3);
    }
    //this resets the whole game, and populates it again
    void startNewGame(int size) {
        this.size = size;
        this.fields = new State[this.size][this.size];
        fields[0][0] = State.BEAR;
        maciX = 0;
        maciY = 0;
        baskets = 0;
        rangerStats.clear();
        populateField();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (fields[i][j] == null) {
                    fields[i][j] = State.EMPTY;
                }
            }
        }
        listener.onFieldChange();
    }
}
