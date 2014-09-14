package bead2;

import static bead2.MaciLaciLogic.State.BEAR;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class MaciLaciFrame extends JFrame implements GameStateListener {

    private MaciLaciLogic game;
    private GameButton[][] fields = new GameButton[0][0];
    private JButton basketCount = new JButton();
    private JPanel jp;
    private JPanel jp2 = new JPanel();
    private JLabel label1 = new JLabel();
    private Integer[] mapSizes = {8, 12, 16};

    //could have used just numbers, but this is more elegant, and clearer
    public enum Directions {

        LEFT, RIGHT, UP, DOWN
    };

    MaciLaciFrame(MaciLaciLogic game) {
        this.game = game;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        // this is needed, so that the frame sees changes of gameLogic
        this.game.setListener(this);
        this.setTitle("Maci Laci Game");
        this.setLocationRelativeTo(null);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/tree.png")));
        createGameArea();
        createNewGameBtn();
    }
    KeyListener listener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
            //Not supported
        }

        @Override
        public void keyPressed(KeyEvent e) {
            //Not supported
        }
        //upon releasing a cursor key, Mr. Bear moves into the right direction (or left, or up, or down)
        @Override
        public void keyReleased(KeyEvent e) {
            /*
             * Code 37 -> Left
             * Code 38 -> Up
             * Code 39 -> Right
             * Code 40 -> Down
             */

            switch (e.getKeyCode()) {
                case 37:
                    game.changeMaciPosition(Directions.LEFT);
                    break;
                case 38:
                    game.changeMaciPosition(Directions.UP);
                    break;
                case 39:
                    game.changeMaciPosition(Directions.RIGHT);
                    break;
                case 40:
                    game.changeMaciPosition(Directions.DOWN);
                    break;
            }
        }
    };
    //This just makes the field of the game, with given size (n)
    private void createGameArea() {
        int n = newGamePopUp();
        fields = new GameButton[n][n];
        jp = new JPanel(new GridLayout(n, n, 1, 1));
        for (int i = 0; i < fields.length; ++i) {
            for (int j = 0; j < fields[i].length; ++j) {
                fields[i][j] = new GameButton(i, j);
                jp.add(fields[i][j]);
                fields[i][j].setBackground(Color.lightGray);
                fields[i][j].setFocusable(false);
            }
        }
        jp.setFocusable(true);
        jp.addKeyListener(listener);
        
        //setting Mr.Bear in top-right corner
        fields[0][0].setIcon(new ImageIcon(getClass().getResource("images/yogibear.png")));
        add(jp, BorderLayout.CENTER);


        basketCount.setFocusable(false);
        basketCount.setContentAreaFilled(false);
        basketCount.setPreferredSize(new Dimension(50, 50));
        basketCount.setIcon(new ImageIcon(getClass().getResource("images/basket.png")));
        jp2.add(basketCount);

        jp2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        label1.setText(": " + 0);
        jp2.add(label1, BorderLayout.CENTER);

        add(jp2, BorderLayout.NORTH);

        this.game.startNewGame(n);
    }
    
    //adding the new game button, which has an obvious purpose
    private void createNewGameBtn() {
        JButton newGame = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = newGamePopUp();
                fields = new GameButton[n][n];
                getContentPane().remove(jp);
                jp = new JPanel(new GridLayout(n, n, 1, 1));
                for (int i = 0; i < fields.length; ++i) {
                    for (int j = 0; j < fields[i].length; ++j) {
                        fields[i][j] = new GameButton(i, j);
                        jp.add(fields[i][j]);
                        fields[i][j].setBackground(Color.lightGray);
                        fields[i][j].setFocusable(false);
                    }
                }
                fields[0][0].setIcon(new ImageIcon(getClass().getResource("images/yogibear.png")));
                jp.setFocusable(true);
                jp.addKeyListener(listener);
                add(jp, BorderLayout.CENTER);
                getContentPane().invalidate();
                getContentPane().validate();
                game.startNewGame(n);
            }
        });
        newGame.setText("New Game");
        newGame.setFocusable(false);
        add(newGame, BorderLayout.SOUTH);
    }
    
    //popup before every new game: set the size of the gamefield
    private int newGamePopUp() {
        int n = fields.length == 0 ? 8 : fields.length;
        int num = (Integer) JOptionPane.showInputDialog(this, "NxN game area\nn=", "New game", JOptionPane.QUESTION_MESSAGE, new ImageIcon(getClass().getResource("images/basket.png")), mapSizes, n);
        return num;
    }
    
    
    //going through the entire gamefield, checking the gamelogic, that at given field, what it's attribute is
    //according to the attribute, set the icon for it
    @Override
    public void onFieldChange() {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields.length; j++) {
                fields[i][j].setBackground(Color.lightGray);
                switch (game.getFields()[i][j]) {
                    case BEAR:
                        fields[i][j].setIcon(new ImageIcon(getClass().getResource("images/yogibear.png")));
                        break;
                    case RANGER:
                        fields[i][j].setIcon(new ImageIcon(getClass().getResource("images/ranger.png")));
                        fields[i][j].setBackground(Color.DARK_GRAY);
                        break;
                    case TREE:
                        fields[i][j].setIcon(new ImageIcon(getClass().getResource("images/tree.png")));
                        break;
                    case BASKET:
                        fields[i][j].setIcon(new ImageIcon(getClass().getResource("images/basket.png")));
                        break;
                    case EMPTY:
                        fields[i][j].setIcon(null);
                }
            }
        }
        label1.setText(": " + game.getBaskets());
    }
    
    //end game message, if you lsot, or won the game
    @Override
    public void onFinished() {
        if (game.getBaskets() == fields.length) {
            JOptionPane.showMessageDialog(null, "You are winner!", null, JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("images/yogibear.png")));
            game.startNewGame(fields.length);
        } else {
            JOptionPane.showMessageDialog(null, "You lose!", null, JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("images/ranger.png")));
            game.startNewGame(fields.length);
        }
    }
}
