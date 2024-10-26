import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpaceInvadors extends JPanel implements ActionListener, KeyListener
{
    class Block
    {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; //used for aliens
        boolean used = false; //used for bullets

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    //SHIP
    int shipWidth= tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns /2 - tileSize;
    int shipY= tileSize * rows - tileSize * 2;
    int shipVelocityX = tileSize;

    Block ship;

    //ALIENS
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;  //number of aliens to defeat
    int alienVelocityX = 5;  //alien moving speed

    //BULLETS
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;  //bullet moving speed

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    SpaceInvadors()  {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground( Color.black);
        setFocusable(true);
        addKeyListener(this);

        //All images of the aliens and ship
        shipImg = new ImageIcon(getClass().getResource("./img/ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./img/alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./img/alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./img/alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./img/alien-yellow.png")).getImage();


        //ArryaList of Aliens
        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        //Game TImer
        gameLoop = new Timer(1000/60, this);//1000/60 = 16.7
        createAliens();
        gameLoop.start();


    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g)
    {
        //SHIP
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        //ALIENS
        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        //BULLETS
        g.setColor(Color.white);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        //SCORE
        g.setColor(Color.yellow);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("GAME OVER: " + score, 10, 35);
            g.drawString("los moteticos te agarraron el guevo", 10, 70);
            g.drawString("buen gay", 10, 100);
        }
        else {
            g.drawString(String.valueOf(score), 10, 35);
        }

    }

    public void move()
    {
        //ALIENS
        for (int i = 0; i < alienArray.size(); i++)
        {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;

                //if alien touches the borders
                if (alien.x + alien.width >= boardWidth || alien.x <= 0)
                {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;

                    //move all aliens down by one row
                    for (Block block : alienArray) {
                        block.y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        //BULLETS
        for (int i = 0; i < bulletArray.size(); i++)
        {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            //bullet collision with aliens
            for (int j = 0; j < bulletArray.size(); j++)
            {
                Block alien  = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien))
                {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }

        //CLEAR BULLETS
        while (!bulletArray.isEmpty() && (bulletArray.getFirst().used || bulletArray.getFirst().y < 0))
        {
            bulletArray.removeFirst();
        }

        //NEXT LEVEL
        if (alienCount == 0 )
        {
            //increase the number of aliens in columns and rows by 1
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns / 2-2);
            alienRows = Math.min(alienRows + 1, rows -6 );
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX++;
            createAliens();
        }
    }

    public void createAliens()
    {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++)
        {
            for (int r = 0; r < alienRows; r++)
            {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienX + c * alienWidth,
                        alienY + r * alienHeight,
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b)
    {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        move();
        repaint();
        if (gameOver)
        {
            File file = new File("src/audio/gay-audio.wav");
            AudioInputStream audioStream = null;
            try {
                audioStream = AudioSystem.getAudioInputStream(file);
            } catch (UnsupportedAudioFileException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Clip clip = null;
            try {
                clip = AudioSystem.getClip();
            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            }
            try {
                clip.open(audioStream);
            } catch (LineUnavailableException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            clip.start();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (gameOver)
        {
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            gameOver = false;
            score = 0;
            alienColumns = 3;
            alienRows = 2;
            alienVelocityX = 2;
            createAliens();
            gameLoop.start();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX; //move left one tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityX <= boardWidth) {
            ship.x += shipVelocityX; //move right one tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(ship.x + shipWidth * 15/32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }

    }
}
