import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.io.*; 
import java.util.Timer;
import java.util.TimerTask;
public class Minesweeper extends JFrame implements MouseListener,ActionListener {
    JToggleButton[][] buttons;
    JPanel buttonPanel; 
    int numMines = 10; 
    boolean firstClick = true;
    boolean gameOver; 
    int dimension; 
    ImageIcon[] numbers = new ImageIcon[8]; 
    ImageIcon flag, mine, smile, win, wait, dead; 
    int selectedCount = 0; 
    int butSize = 40; 
    Timer timer;
    int timePassed = 0;
    JTextField timefield;
    GraphicsEnvironment ge; 
    Font clockFont; 

    //menu stuff
    JMenuBar menuBar;
    JMenu menu; 
    JMenuItem beg, inter, exp; 
    JButton reset; 

    public Minesweeper() {
        setGrid(9, 9); 

        for(int x=0; x<8; x++) {
        
            numbers[x] = new ImageIcon("/Users/Mahati/Data/Code/Data Structures 22-23/Minesweeper Images/"+(x+1)+".png");
            numbers[x] = new ImageIcon(numbers[x].getImage().getScaledInstance(butSize, butSize, Image.SCALE_SMOOTH)); 
        }

        flag = new ImageIcon("/Users/Mahati/Data/Code/Data Structures 22-23/Minesweeper Images/flag.png");
        flag = new ImageIcon(flag.getImage().getScaledInstance(butSize, butSize, Image.SCALE_SMOOTH)); 

        mine = new ImageIcon("/Users/Mahati/Data/Code/Data Structures 22-23/Minesweeper Images/mine0.png");
        mine = new ImageIcon(mine.getImage().getScaledInstance(butSize, butSize, Image.SCALE_SMOOTH));

        try {
            String st = "/Users/Mahati/Data/Code/Data Structures 22-23/"; 
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            clockFont=Font.createFont(Font.TRUETYPE_FONT,
                       new File(st+"digital-7.ttf"));
            ge.registerFont(clockFont);
       } catch (IOException|FontFormatException e) {

       }


        timefield = new JTextField("   "+timePassed);
        timefield.setFont(clockFont.deriveFont(18f));
        timefield.setBackground(Color.BLACK);
	    timefield.setForeground(Color.GREEN);
        menuBar = new JMenuBar(); 
        menuBar.add(timefield);

        

        menu = new JMenu("difficulty level"); 
        beg=new JMenuItem("Beginner");
        beg.addActionListener(this);

        inter=new JMenuItem("Intermmediate");
        inter.addActionListener(this);

        exp=new JMenuItem("Expert");
        exp.addActionListener(this);

        menu.add(beg);
        menu.add(inter);
        menu.add(exp);

        menuBar.add(menu);

        reset = new JButton("reset"); 
        menuBar.add(reset); 

        this.add(menuBar, BorderLayout.NORTH); 

        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void setGrid(int rows, int cols) {
        
        if(buttonPanel != null)
            this.remove(buttonPanel); 
        
        gameOver = false; 
        
        buttonPanel = new JPanel(); 
        buttonPanel.setLayout(new GridLayout(rows, cols)); 
        buttons = new JToggleButton[rows][cols];
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                buttons[r][c] = new JToggleButton();
                buttons[r][c].putClientProperty("row", r);
                buttons[r][c].putClientProperty("col", c);
                buttons[r][c].putClientProperty("state", 0);
                buttons[r][c].putClientProperty("flag", false);
                buttons[r][c].setPreferredSize(new Dimension(butSize, butSize));
                buttons[r][c].addMouseListener(this);
                buttonPanel.add(buttons[r][c]); 
            }
        }
        this.add(buttonPanel, BorderLayout.CENTER); 
        this.setSize(cols*butSize, rows*butSize); //width, height
        this.revalidate(); 
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        int rowClicked = (int)((JToggleButton)e.getComponent()).getClientProperty("row"); 
        int colClicked = (int)((JToggleButton)e.getComponent()).getClientProperty("col"); 

        if(!gameOver) {
            if(e.getButton() == MouseEvent.BUTTON1 && buttons[rowClicked][colClicked].isEnabled() ) { //left click
                if(firstClick) {
                    dropMines(rowClicked, colClicked);
                    firstClick = false; 
                    timer=new Timer();
                    timer.schedule(new UpdateTimer(),0,1000); 
                }
                int state = (int)(buttons[rowClicked][colClicked].getClientProperty("state"));
                if(state == -1) {
                    gameOver = true;
                    buttons[rowClicked][colClicked].setIcon(mine); 
                    disableButons();
                    timer.cancel();
                    JOptionPane.showMessageDialog(null, "You are a loser!");

                    //disable all the buttons
                    
                }
                else {
                    buttons[rowClicked][colClicked].setEnabled(false);

                    selectedCount++; 
                    expand(rowClicked, colClicked); 

                    if(selectedCount == buttons.length*buttons[0].length-numMines) {
                        disableButons();
                        gameOver = true;
                        timer.cancel();
                        JOptionPane.showMessageDialog(null, "You are a winner!");

                        //disable all tge buttons
                         
                    }
                }
            }
            if(!firstClick && e.getButton() == MouseEvent.BUTTON3) {
                try {
                    if(buttons[rowClicked][colClicked].isEnabled() && buttons[rowClicked][colClicked].getIcon() == null) {
                        buttons[rowClicked][colClicked].putClientProperty("flag", true);
                        //buttons[rowClicked][colClicked].setSelected(false);
                        buttons[rowClicked][colClicked].setIcon(flag); 
                        buttons[rowClicked][colClicked].setDisabledIcon(flag);
                        buttons[rowClicked][colClicked].setEnabled(false);
                    }
                    else if(buttons[rowClicked][colClicked].getIcon().equals(flag)) {
                        buttons[rowClicked][colClicked].putClientProperty("flag", false);
                        buttons[rowClicked][colClicked].setIcon(null); 
                        buttons[rowClicked][colClicked].setDisabledIcon(null);
                        buttons[rowClicked][colClicked].setEnabled(true);
                    }
                } catch(NullPointerException ee) {}
                
            }
            
        }
        
        
    }

    public void expand(int row, int col) {
        if(!(boolean)(buttons[row][col].getClientProperty("flag")))
        {
            if(!buttons[row][col].isSelected()) {
                buttons[row][col].setSelected(true);
                buttons[row][col].setEnabled(false);
                selectedCount++;
            }
                
            
            int state = (int)(buttons[row][col].getClientProperty("state"));
            if(state > 0)
            {
                buttons[row][col].setIcon(numbers[state-1]);  
                buttons[row][col].setDisabledIcon(numbers[state-1]);  
            }     
            else {
                for(int r=row-1; r<=row+1; r++) {
                    for(int c=col-1; c<=col+1; c++) {
                        try {
                            if(!buttons[r][c].isSelected()) 
                                expand(r, c); 
                        } catch(ArrayIndexOutOfBoundsException e) {}
                    }
                }
            }
        } 
        
    }

    public void dropMines(int row, int col) {
        int count = numMines; 
        while(count > 0) {
            int r = (int)(Math.random()*buttons.length); 
            int c = (int)(Math.random()*buttons[0].length); 
            int state = (int)buttons[r][c].getClientProperty("state");
            if((Math.abs(r-row)>1 || Math.abs(c-col)>1) && state == 0) {
                buttons[r][c].putClientProperty("state", -1); 
                count--; 
            }
        }

        for(int r=0; r<buttons.length; r++) {
            for(int c=0; c<buttons[0].length; c++) {
                int state = (int)(buttons[r][c].getClientProperty("state"));
                if(state != -1) {
                    count = 0; 
                    for(int a = r-1; a<= r+1; a++) {
                        for(int b = c-1; b<=c+1; b++) {
                            try {
                                state = (int)(buttons[a][b].getClientProperty("state")); 
                                if(state == -1) {
                                    count++; 
                                }
                            } catch(ArrayIndexOutOfBoundsException e) {
                                
                            }
                            
                        }
                    }
                    buttons[r][c].putClientProperty("state", count);
                }
            }
        }
/* 
        for(int r=0; r<buttons.length; r++) {
            for(int c=0; c<buttons[0].length; c++) {
                int state = (int)(buttons[r][c].getClientProperty("state"));
                //buttons[r][c].setText(""+state); 
                buttons[r][c].setIcon(mine);
            }
        }
        */
    }

    public static void main(String [] args) {
        Minesweeper app = new Minesweeper(); 
    }

    public void disableButons() {
        for(int r=0; r<buttons.length; r++) {
            for(int c=0; c<buttons[0].length; c++) {
                buttons[r][c].setEnabled(false);   
                if((int)buttons[r][c].getClientProperty("state") == -1) {
                    buttons[r][c].setIcon(mine); 
                    buttons[r][c].setDisabledIcon(mine); 
                }      
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub       
    }
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub      
    }    
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub       
    }
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub      
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == beg) {
            numMines = 10; 
            setGrid(9, 9); 
        }
        if(e.getSource() == inter) {
            numMines = 40; 
            setGrid(16, 16); 
        }
        if(e.getSource() == exp) {
            numMines = 99; 
            setGrid(16, 40); 
        }

        if(timer!=null) {
            timer.cancel();
            timePassed=0;
	        timefield.setText("   "+timePassed);
        }
		    
        
    }

    class UpdateTimer extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(!gameOver){
				timePassed++;
				timefield.setText("  "+timePassed);
			}

        }
    
    }

    
    
}


