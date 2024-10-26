import javax.swing.*;


public class Main
{
    public static void main(String[] args) {
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns; // 32*16 = 512 px
        int boardHeight = tileSize * rows; // 32*16 = 512 px

        JFrame frame = new JFrame("Space Invaders By Motete");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvadors spaceInvadors = new SpaceInvadors();
        frame.add(spaceInvadors);
        frame.pack();
        spaceInvadors.requestFocus();
        frame.setVisible(true);
    }
}