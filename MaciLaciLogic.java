package bead2;

import java.util.Random;
import bead2.MaciLaciFrame.Directions;
import java.util.HashMap;
import java.util.Map;

public class MaciLaciLogic {

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

    private void checkForBasket() {
        if (fields[maciX][maciY] == State.BASKET) {
            this.baskets++;
        }
    }

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
