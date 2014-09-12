package bead2;

import javax.swing.JButton;

public class GameButton extends JButton {

    private int i;
    private int j;

    public GameButton(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
