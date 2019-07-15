
package gameoflife;

import java.io.*;
import java.util.Scanner;
import java.awt.*; //needed for graphics
import java.awt.image.BufferedImage;
import javax.swing.*; //needed for graphics
import static javax.swing.JFrame.EXIT_ON_CLOSE; //needed for graphics

public class GameOfLife extends JFrame {

    //FIELDS
    int numGenerations = 500;
    int currGeneration = 1;
    
    Color aliveColor = Color.YELLOW;
    Color deadColor = Color.BLUE;
    
    String fileName = "Initial cells.txt";

    int width = 800; //width of the window in pixels
    int height = 800;
    int borderWidth = 50;

    int numCellsX = 30; //width of the grid (in cells)
    int numCellsY = 30;

    boolean alive[][] = new boolean[numCellsX][numCellsY]; 
    boolean aliveNext[][] = new boolean[numCellsX][numCellsY]; 
    
    int ratio = 5;
    int cellWidth = (width - 2*borderWidth) / numCellsX;
    int labelX = width / 2;
    int labelY = borderWidth;
 
    
    //METHODS
    public void plantFirstGeneration() throws IOException {
        makeEveryoneDead();
        //plantFromFile( fileName );
        plantBlock ( 10, 10, 3, 3 );
        //plantGlider(10, 5, 4);
        //plantGlider(26, 2, 1);
        plantGlider(26, 26, 2);
        //plantGlider(1, 26, 3);
    }

    
    //Sets all cells to dead
    public void makeEveryoneDead() {
        for (int i = 0; i < numCellsX; i++) {
            for (int j = 0; j < numCellsY; j++) {
                alive[i][j] = false;
            }
        }
    }

    
    //reads the first generations' alive cells from a file
    public void plantFromFile(String fileName) throws IOException {

        FileReader f = new FileReader(fileName);
        Scanner s = new Scanner(f);

        int x, y;

        while ( s.hasNext() ) {
            x = s.nextInt();
            y = s.nextInt();
            
            alive[x][y] = true;
        }
    }

    
    //Plants a solid rectangle of alive cells.  Would be used in place of plantFromFile()
    public void plantBlock(int startX, int startY, int numColumns, int numRows) {
        
        int endCol = Math.min(startX + numColumns, numCellsX);
        int endRow = Math.min(startY + numRows, numCellsY);

        for (int i = startX; i < endCol; i++) {
            for (int j = startY; j < endRow; j++) {
                alive[i][j] = true;
            }
        }
    }

    
    //Plants a "glider" group, which is a cluster of living cells that migrates across the grid from 1 generation to the next
    public void plantGlider(int startX, int startY, int direction) { //direction can be "SW", "NW", "SE", or "NE"
        int xUnit, yUnit;    
        if ( direction == 1 ){
            xUnit = -1;
            yUnit = 1;
        }
        else if ( direction == 2 ){
            xUnit = 1;
            yUnit = 1;
        }
        else if ( direction == 3 ){
            xUnit = 1;
            yUnit = -1;
        }
        else {
            xUnit = -1;
            yUnit = -1;
        }
        
        alive[startX][startY] = true;
        alive[startX + xUnit][startY] = true;
        alive[startX + 2*xUnit][startY] = true;
        alive[startX][startY + yUnit] = true;
        alive[startX + xUnit][startY + 2*yUnit] = true;
        
    }

    
    //Applies the rules of The Game of Life to set the true-false values of the aliveNext[][] array,
    //based on the current values in the alive[][] array

    
    public void computeNextGeneration() {
        for (int i = 0; i < numCellsX; i++){
            for (int j = 0; j < numCellsY; j++){
                int livingNeighbors = countLivingNeighbors(i, j);
          
                if (alive[i][j]){
                    if (livingNeighbors <= 1 || livingNeighbors >= 4){
                        aliveNext[i][j] = false;
                    }
                    else
                        aliveNext[i][j] = true;
                }
                else{
                    if (livingNeighbors == 3){
                        aliveNext[i][j] = true;
                    }
                    else
                        aliveNext[i][j] = false;
                }
            }
        }
    }

    
    //Overwrites the current generation's 2-D array with the values from the next generation's 2-D array
    public void plantNextGeneration() {
        currGeneration++;
        for (int i = 0; i < alive.length; i++){
            for (int j = 0; j < alive[i].length; j++){
                alive[i][j] = aliveNext[i][j];
            }
        }
    }

    
    //Counts the number of living cells adjacent to cell (i, j)
    public int countLivingNeighbors(int i, int j) {
        int livingNeighbors = 0;
        
        if (alive[i][j])
            livingNeighbors--;
        
        for (int a = -1; a < 2; a++){
             for (int b = -1; b < 2; b++){
                  int checkRow = i+a;
                  int checkCol = j+b;
                  
                  if (checkRow >= 0 && checkRow < alive.length && checkCol >= 0 && checkCol < alive[0].length){
                      if (alive[checkRow][checkCol]){
                          livingNeighbors++;
                      }
                  }        
             }
        }
        return livingNeighbors; //make it return the right thing
    }

    
    //Makes the pause between generations
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    }

    
    //Displays the statistics at the top of the screen
    void drawLabel(Graphics g, int state) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, borderWidth);
        g.setColor(Color.yellow);
        g.drawString("Generation: " + state, labelX, labelY);
    }
    

    
    //Draws the current generation of living cells on the screen
    public void paint( Graphics g){
        Image img = createImage();
        g.drawImage(img,8,30,this);
    }
    
    //Draws the current generation of living cells on the screen
    public Image createImage(){
        BufferedImage bufferedImage = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        int x, y, i, j;
        
        x = borderWidth;
        y = borderWidth;

        drawLabel(g, currGeneration);
        
        g.setColor(Color.black);
        g.drawRect(0, 0, width, height);
        
        for (i = 0; i < numCellsX; i++) {
            for (j = 0; j < numCellsY; j++) {
                if (alive[i][j] == true){
                    g.setColor(aliveColor);
                } else{
                    g.setColor(deadColor);
                }
                g.fillRect(x, y, cellWidth, cellWidth);
                g.setColor(Color.black);
                g.drawRect(x, y, cellWidth, cellWidth);
                
                x += cellWidth;
            }
            x = borderWidth;
            y += cellWidth;
            
            //Fill this in
        }
        return bufferedImage;
    }
   


    //Sets up the JFrame screen
    public void initializeWindow() {
        setTitle("Game of Life Simulator");
        setSize(height, width);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.black);
        setVisible(true); //calls paint() for the first time
    }
    
    
    //Main algorithm
    public static void main(String args[]) throws IOException {

        GameOfLife currGame = new GameOfLife();

        currGame.initializeWindow();
        currGame.plantFirstGeneration(); //Sets the initial generation of living cells, either by reading from a file or creating them algorithmically

        for (int i = 1; i <= currGame.numGenerations; i++) { 
           sleep(100);
           currGame.computeNextGeneration(); // fills aliveNext array
           currGame.plantNextGeneration(); // copies aliveNext into alive
           currGame.repaint();
        }
        
    } 
    
} //end of class
