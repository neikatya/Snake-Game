import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class Game extends JPanel implements ActionListener {
    private final int scaler = 16;
    private Image dotImage;
    private Image appleImage;
    private Timer timer;
    private boolean inGame = true;
    private final GameSnakeClient gameClient;

    public Game(GameSnakeClient gameClient) {
        this.gameClient = gameClient;
        setBackground(Color.black);
        loadImages();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
        timer = new Timer(500, this);
        timer.start();
    }

    public Game(String baseUrl) {
        this.gameClient = new GameSnakeClient(baseUrl);

        setBackground(Color.black);
        loadImages();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
        timer = new Timer(500, this);
        timer.start();
    }

    public void loadImages(){
        ImageIcon iia = new ImageIcon("apple.png");
        appleImage = iia.getImage();
        ImageIcon iid = new ImageIcon("dot.png");
        dotImage = iid.getImage();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (inGame){
            try {
                List<Coordinates> appleCoordinates = gameClient.getAppleCoordinates();
                appleCoordinates.forEach(
                        coordinates -> g.drawImage(appleImage, coordinates.x() * scaler,
                                coordinates.y() * scaler, this)
                );

                List<Coordinates> snakeCoordinates = gameClient.getSnakeCoordinates();
                snakeCoordinates.forEach(
                        coordinates -> g.drawImage(dotImage, coordinates.x() * scaler,
                                coordinates.y() * scaler, this)
                );
            } catch (Exception e) {
                System.err.println("Ошибка при получении данных: " + e.getMessage());
                endGame();
            }
        } else {
            String str = "Game Over";
            g.setColor(Color.white);
            g.drawString(str, 125, gameClient.getSize() * 16 / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            try {
                gameClient.moveSnake();
            } catch (RuntimeException exception) {
                endGame();
            } catch (Exception ex) {
                System.err.println("Ошибка при движении змейки: " + ex.getMessage());
                endGame();
            }
        }
        repaint();
    }

    public void endGame() {
        inGame = false;
    }

    public boolean getStateOfGame(){
        return inGame;
    }

    public boolean isServerAvailable() {
        return gameClient.isServerAvailable();
    }

    class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_LEFT){
                gameClient.turnTo(Direction.LEFT);
            }
            if(key == KeyEvent.VK_RIGHT){
                gameClient.turnTo(Direction.RIGHT);
            }
            if(key == KeyEvent.VK_UP){
                gameClient.turnTo(Direction.UP);
            }
            if(key == KeyEvent.VK_DOWN){
                gameClient.turnTo(Direction.DOWN);
            }
        }
    }
}