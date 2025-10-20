import java.awt.*;
import java.awt.event.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener,KeyListener {
  class Block {
     int x;
     int y;
     int width;
     int height;
     Image image;

     int startX;
     int StartY;
     char direction ='U';
     int velocityX = 0;
     int velocityY = 0;
     
     
     Block(Image image,int x,int y,int width, int height) {
      this.image=image;
      this.x=x;
      this.y=y;
      this.width=width;
      this.height=height;
      this.startX=x;
      this.StartY=y;
     }
      void updateDirection(char direction) {
        char prevDirection=this.direction;
        this.direction = direction;
         UpdateVelocity();
         this.x += this.velocityX;
         this.y += this.velocityY;
        for (Block wall : walls){
          if (collision(this, wall)){
           this.x -= this.velocityX;
           this.y-= this.velocityY;
           this.direction = prevDirection;
           UpdateVelocity();
          }
         }
      
  }

      void UpdateVelocity() {
       if(this.direction=='U'){
          this.velocityX=0;
          this.velocityY=-tileSize/4;

          }
       else if (this.direction == 'D') {
            this.velocityX = 0;
            this.velocityY = tileSize/4;
       }
       else if (this.direction == 'L')  {
        this.velocityX = -tileSize/4;
        this.velocityY = 0;
       }   
       else if  (this.direction =='R'){
        this.velocityX = tileSize/4;
        this.velocityY = 0;
      }
       }
       void reset() {
        this.x=this.startX;
        this.y=this.StartY;
       }
}
      
  
 
  private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image redGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
  

   
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "X       bpo       X",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX",     
      };
   
 
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    private Random random;
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    Timer gameLoop;
    char[] directions = {'U','D','L','R'};

    Pacman(){
      random= new Random();
      setPreferredSize(new Dimension(boardWidth,boardHeight));
      setBackground(Color.BLACK);
      addKeyListener(this);
      setFocusable(true);
      
      wallImage=new ImageIcon(getClass().getResource("./wall.png")).getImage();
      blueGhostImage=new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
      orangeGhostImage=new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
      pinkGhostImage=new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
      redGhostImage=new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

      pacmanUpImage=new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
      pacmanDownImage=new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
      pacmanLeftImage=new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
      pacmanRightImage=new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
    
      loadMap();
      for(Block ghost :ghosts){
        char newDirection = directions[random.nextInt(4)];
        ghost.updateDirection(newDirection);
      }
      gameLoop = new Timer(50,this);
      gameLoop.start();
    }  
    
    
     public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r =0;r < rowCount;r++ ) {
           for(int c=0;c<columnCount;c++){
           String row = tileMap[r];
           char tileMapchar = row.charAt(c);

           int x = c*tileSize;
           int y = r*tileSize;
           if (tileMapchar == 'X') {
             Block wall= new Block(wallImage ,x ,y, tileSize, tileSize);
             walls.add(wall);
           }
             else if (tileMapchar =='b') {
              Block ghost = new Block(blueGhostImage ,x ,y , tileSize , tileSize);
              ghosts.add(ghost);
           }
             else if (tileMapchar == 'o') {
               Block ghost = new Block(orangeGhostImage , x , y , tileSize ,tileSize);
               ghosts.add(ghost);
           }
             else if (tileMapchar == 'r') {
               Block ghost = new Block(redGhostImage , x , y , tileSize , tileSize);
               ghosts.add(ghost);
           }
              else if (tileMapchar == 'p') {
               Block ghost = new Block(pinkGhostImage , x , y , tileSize , tileSize);
               ghosts.add(ghost);
           }
              else if (tileMapchar =='P') {
               pacman = new Block(pacmanRightImage , x , y , tileSize , tileSize);
           }
              else if (tileMapchar == ' ') {
               Block food = new Block(null , x+14 , y+14 ,  4 , 4);
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
      g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width ,pacman.height,null);
      for( Block ghost:ghosts) {
         g.drawImage(ghost.image, ghost.x,ghost.y,ghost.width,ghost.height,null);
       }    
     for(Block wall : walls){
       g.drawImage(wall.image,wall.x,wall.y,wall.width,wall.height,null);
     } 
     g.setColor(Color.WHITE);

     for(Block food:foods) {
       g.fillRect(food.x, food.y, food.width,food.height);
      }
       g.setFont(new Font("Arial",Font.PLAIN,30));
      if (gameOver){
        g.drawString("GAME OVER :" + String.valueOf(score),tileSize/2, tileSize/2);
      }
      else {
        g.drawString("x" + String.valueOf(lives) + "Score:" + String.valueOf(score),tileSize/2,tileSize/2);
      }
    
  
  Block foodEaten = null ;
  for (Block food:foods) {
   if (collision(pacman,food)) {
      foodEaten=food;
      score+=10;
   }
    }
    foods.remove(foodEaten) ;
   if (foods.isEmpty()){
    loadMap();
    resetPositions();
   }
  
  } 
    

  

  @Override
  public void actionPerformed(ActionEvent e) {
    move();
    repaint();
    if(gameOver){
      g.drawString("GAME OVER");
      gameLoop.stop();
    }
  }
  
   public void move() {
    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;
   

    for (Block wall:walls){
       if(collision(pacman, wall)) {
        pacman.x-=pacman.velocityX;
        pacman.y-=pacman.velocityY;
        break;
       } 
      } 
   
    for (Block ghost : ghosts){
      if (collision(ghost, pacman)){
        lives-=1;
        resetPositions();
        if(lives==0){
        gameOver = true;
        return;
        }
      }
      
      if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D'){
         ghost.updateDirection('U');
      }
      ghost.x += ghost.velocityX;
      ghost.y += ghost.velocityY;
      for (Block wall :walls){
         if (collision(ghost, wall)|| ghost.x<= 0 || ghost.x+ghost.width >= boardWidth) {
           ghost.x -= ghost.velocityX;
           ghost.y-=ghost.velocityY;
           char newDirection = directions[random.nextInt(4)];
           ghost.updateDirection(newDirection);

         }
      }
    }    
  } 


  

  public boolean collision(Block a,Block b){
    return  a.x<b.x +b.width &&
            a.x+a.width>b.x &&
            a.y<b.y +b.height &&
            a.y+a.height>b.y;  

    
  }
   
  public void resetPositions(){
  pacman.reset();
  pacman.velocityX = 0;
  pacman.velocityY = 0;
  for(Block ghost : ghosts){
    ghost.reset();
    char newDirection = directions[random.nextInt(4)];
    ghost.updateDirection(newDirection);

  } 
  }
    

  @Override
  public void keyTyped(KeyEvent e) {}
 @Override
  public void keyPressed(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {    
    if (gameOver){
      loadMap();
      resetPositions();
      lives=3;
      score=0;
      gameOver = false;
      gameLoop.start();
    }
    //System.out.println("KeyEvent: "+ e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_UP) {
          pacman.updateDirection('U');
    }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
          pacman.updateDirection('D');
    }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
          pacman.updateDirection('L');
    }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
          pacman.updateDirection('R');
    }
     if (pacman.direction=='U'){
      pacman.image=pacmanUpImage;
     }
     else if (pacman.direction=='D'){
      pacman.image=pacmanDownImage;
     }
     else if (pacman.direction=='L'){
      pacman.image=pacmanLeftImage;
     }
     else if (pacman.direction=='R'){
      pacman.image=pacmanRightImage;
     }
  
    }
  }

    

 



