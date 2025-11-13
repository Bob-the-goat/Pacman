import java.awt.*;
import java.awt.event.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

public class Pacman extends JPanel implements ActionListener, KeyListener {
  class Block {
    int x;
    int y;
    int width;
    int height;
    Image image;

    int startX;
    int StartY;
    char direction = 'U';
    int velocityX = 0;
    int velocityY = 0;

    Block(Image image, int x, int y, int width, int height) {
      this.image = image;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.startX = x;
      this.StartY = y;
    }

    void updateDirection(char direction) {
      this.direction = direction;
      UpdateVelocity();
    }

    void UpdateVelocity() {
      if (this.direction == 'U') {
        this.velocityX = 0;
        this.velocityY = -tileSize / 4;
      } else if (this.direction == 'D') {
        this.velocityX = 0;
        this.velocityY = tileSize / 4;
      } else if (this.direction == 'L') {
        this.velocityX = -tileSize / 4;
        this.velocityY = 0;
      } else if (this.direction == 'R') {
        this.velocityX = tileSize / 4;
        this.velocityY = 0;
      }
    }

    void reset() {
      this.x = this.startX;
      this.y = this.StartY;
      this.direction = 'U';
      this.velocityX = 0;
      this.velocityY = 0;
    }

  }

  private int rowCount = 19;
  private int columnCount = 19;
  private int tileSize = 32;
  private int boardWidth = columnCount * tileSize;
  private int boardHeight = rowCount * tileSize;

  private Image wallImage;
  private Image meteorImage;
  private Image earthImage;


  private String[] tileMap = {
      "XXXXXXXXXXXXXXXXXXX",
      "X        X        X",
      "X XX XXX X XXX XX X",
      "X                 X",
      "X XX X XXXXX X XX X",
      "X    X       X    X",
      "XXXX XXXX XXXX XXXX",
      "OOOX X       X XOOO",
      "XXXX X X m X X XXXX",
      "        mmm        ",
      "XXXX X XXXXX X XXXX",
      "OOOX X       X XOOO",
      "XXXX X XXXXX X XXXX",
      "X        X        X",
      "X XX XXX X XXX XX X",
      "X        R        X",
      "XX X X XXXXX X X XX",
      "X    X   X   X    X",
      "XXXXXXXXXXXXXXXXXXX",
  };

  HashSet<Block> walls;
  HashSet<Block> foods;
  HashSet<Block> ghosts;
  HashSet<Block> meteors;
  Block pacman;
  private Random random;
  int score = 0;
  int lives = 3;
  boolean gameOver = false;
  static int highScore = 0;
  boolean isNewHigh = false;

  Timer gameLoop;
  char[] directions = { 'U', 'D', 'L', 'R' };

  char nextDirection = 'R';

  public Pacman() {
    random = new Random();
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setBackground(Color.BLACK);
    addKeyListener(this);
    setFocusable(true);

    wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
    meteorImage = new ImageIcon(getClass().getResource("/meteor.png")).getImage();
    earthImage = new ImageIcon(getClass().getResource("/earth.png")).getImage();


    loadMap();
    for (Block ghost : ghosts) {
      char newDirection = directions[random.nextInt(4)];
      ghost.updateDirection(newDirection);
    }
    gameLoop = new Timer(90, this);
    gameLoop.start();
  }

  public void loadMap() {
    walls = new HashSet<Block>();
    foods = new HashSet<Block>();
    ghosts = new HashSet<Block>();
    meteors = new HashSet<Block>();

    for (int r = 0; r < rowCount; r++) {
      for (int c = 0; c < columnCount; c++) {
        String row = tileMap[r];
        char tileMapchar = row.charAt(c);

        int x = c * tileSize;
        int y = r * tileSize;
        if (tileMapchar == 'X') {
          Block wall = new Block(wallImage, x, y, tileSize, tileSize);
          walls.add(wall);
        } else if (tileMapchar == 'm') {
          Block meteor = new Block(meteorImage, x, y, tileSize, tileSize);
          // Kept ghost coz too much too change as I did normal pacman before this lol
          ghosts.add(meteor);
        } else if (tileMapchar == 'R') {
          // Kept pacman coz too much too change as I did normal pacman before this lol.
          pacman = new Block(earthImage, x, y, tileSize, tileSize);
        } else if (tileMapchar == ' ') {
          Block food = new Block(null, x + 14, y + 14, 4, 4);
          foods.add(food);
        }
      }
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {
    g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
    for (Block ghost : ghosts) {
      g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
    }
    for (Block wall : walls) {
      g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
    }
    Font statusFont = new Font("Arial", Font.BOLD, 23);
    FontMetrics fm = g.getFontMetrics(statusFont);
    g.setFont(statusFont);
    g.setColor(Color.WHITE);
    int statusY = tileSize - 4;
    for (Block food : foods) {
      g.fillRect(food.x, food.y, food.width, food.height);
    }
    fm = g.getFontMetrics();
    String highScoreText = "High Score: " + highScore;
    int highScoreX = (boardWidth - fm.stringWidth(highScoreText)) / 2;
    g.drawString(highScoreText, highScoreX, statusY);
    g.drawString("Score: " + score, 10, statusY);
    g.drawString("Lives: " + lives, boardWidth - fm.stringWidth("Lives" + lives) - 33, statusY);

    if (gameOver) {
      g.setFont(new Font("Monospaced", Font.BOLD, 80));
      fm = g.getFontMetrics(g.getFont());
      String gameOverText = "GAME OVER";
      int x = (boardWidth - fm.stringWidth(gameOverText)) / 2;
      int textHeight = fm.getHeight();
      int targetYCenter = boardHeight / 3;
      int y = targetYCenter + textHeight / 2;

      g.setFont(new Font("Monospaced", Font.BOLD, 50));
      g.setColor(Color.RED);
      if (isNewHigh == true) {
        fm = g.getFontMetrics(g.getFont());
        String Highscorebeaten = ("NEW HIGH SCORE: " + score);
        x = (boardWidth - fm.stringWidth(Highscorebeaten)) / 2;
        g.drawString(Highscorebeaten, x, y + 100);

      }
      fm = g.getFontMetrics();
      int yTitle = boardHeight / 3;
      int xCenterGameOver = (boardWidth - fm.stringWidth(gameOverText)) / 2;
      g.drawString(gameOverText, xCenterGameOver, yTitle);
      g.setFont(new Font("Monospaced", Font.PLAIN, 22));
      g.setColor(Color.WHITE);
      String restartText = "Final Score: " + score + " | Press any key to restart";
      fm = g.getFontMetrics();
      x = (boardWidth - fm.stringWidth(restartText)) / 2;
      g.drawString(restartText, x, y + 50);
    }
  }

  private char getReverseDirection(char direction) {
    if (direction == 'U')
      return 'D';
    if (direction == 'D')
      return 'U';
    if (direction == 'L')
      return 'R';
    if (direction == 'R')
      return 'L';
    return ' ';
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (gameOver) {
      gameLoop.stop();
      return;
    }
    move();

    Block foodEaten = null;
    for (Block food : foods) {
      if (collision(pacman, food)) {
        foodEaten = food;
        score += 10;
      }
    }
    if (pacman.x < -pacman.width) {
      pacman.x = boardWidth;
    } else if (pacman.x > boardWidth) {
      pacman.x = -pacman.width;
    }

    foods.remove(foodEaten);
    if (foods.isEmpty()) {
      loadMap();
      resetPositions();
    }
    repaint();
  }

  public void move() {
    if (pacman == null)
      return;

    if (nextDirection != pacman.direction) {

      Block tempPacman = new Block(null, pacman.x, pacman.y, pacman.width, pacman.height);
      tempPacman.updateDirection(nextDirection);
      if (nextDirection == 'U' || nextDirection == 'R'){

      }
      else if (nextDirection == 'L' || nextDirection == 'R'){

      } 
     
     
      int nextX = pacman.x + tempPacman.velocityX;
      int nextY = pacman.y + tempPacman.velocityY;

      boolean canSwitchDirection = true;
      for (Block wall : walls) {

        if (collision(new Block(null, nextX, nextY, tempPacman.width, tempPacman.height), wall)) {
          canSwitchDirection = false;
          break;
        }
      }
      if (canSwitchDirection) {
        pacman.updateDirection(nextDirection);

      }
    }
    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;

    if (pacman.x < 0) {
      pacman.x = boardWidth;
    } else if (pacman.x > boardWidth) {
      pacman.x = 0;
    }

    for (Block wall : walls) {
      if (collision(pacman, wall)) {
        pacman.x -= pacman.velocityX;
        pacman.y -= pacman.velocityY;
        break;
      }
    }

    for (Block ghost : ghosts) {
      if (collision(ghost, pacman)) {
        lives -= 1;
        if (score > highScore) {
          highScore = score;
          isNewHigh = true;
        }
        resetPositions();
        if (lives == 0) {
          gameOver = true;
          return;
        }
      }

      if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
        ghost.updateDirection('U');
      }
      ghost.x += ghost.velocityX;
      ghost.y += ghost.velocityY;
      for (Block wall : walls) {
        if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
          ghost.x -= ghost.velocityX;
          ghost.y -= ghost.velocityY;
          char newDirection = directions[random.nextInt(4)];
          ghost.updateDirection(newDirection);

        }
      }
    }

    if (score == highScore) {
      highScore = highScore;
    }

  }

  public boolean collision(Block a, Block b) {
    return a.x < b.x + b.width &&
        a.x + a.width > b.x &&
        a.y < b.y + b.height &&
        a.y + a.height > b.y;
  }

  public void resetPositions() {
    pacman.reset();
    pacman.velocityX = 0;
    pacman.velocityY = 0;
    nextDirection = 'R';
    for (Block ghost : ghosts) {
      ghost.reset();
      char newDirection = directions[random.nextInt(4)];
      ghost.updateDirection(newDirection);
      if (lives == 0) {
        if (score > highScore) {
          highScore = score;
        }
        gameOver = true;
        return;
      }
    }
    for (Block ghost : ghosts) {
      ghost.reset();
      char newDirection = directions[random.nextInt(4)];
      ghost.updateDirection(newDirection);
    }

  }

  public void keyReleased(KeyEvent e) {
    if (gameOver) {
      if (score > highScore) {
        highScore = score;
      }
      loadMap();
      resetPositions();
      lives = 3;
      score = 0;
      gameOver = false;
      isNewHigh = false;
      gameLoop.start();
      for (Block ghost : ghosts) {
        char newDirection = directions[random.nextInt(4)];
        ghost.updateDirection(newDirection);
      }

      return;
    }
    if (e.getKeyCode() == KeyEvent.VK_UP) {
      nextDirection = 'U';
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      nextDirection = 'D';
    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      nextDirection = 'L';
    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
      nextDirection = 'R';
    }
  }


  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
  }
}
