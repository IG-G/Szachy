package View;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class ChessViewSquare extends JButton {
    ColorOfSquare colorOfSquare; //WHITE OR BLACK
    Color displayingColorOfSquare; //for displaying purposes
    Icon pieceIcon;
    int posXInUI, posYInUI;
    int posXOnBoard, posYOnBoard;

    public ChessViewSquare() {
        setUI(new BasicButtonUI()); //to clear on-click effects
        posXInUI = 0;
        posYInUI = 0;
        setSize(50, 50);
        pieceIcon = null;
        setBorderPainted(true);
        setFocusPainted(false);
        setPieceIcon(null);
        setBorder(BorderFactory.createLineBorder(new Color(111, 111, 111), 1));
    }

    public ColorOfSquare getColorOfSquare() {
        return colorOfSquare;
    }

    public void setColorOfSquare(ColorOfSquare colorOfSquare) {
        this.colorOfSquare = colorOfSquare;
    }

    public void setPosXonBoard(int posXonBoard) {
        this.posXOnBoard = posXonBoard;
    }

    public void setPosYonBoard(int posYonBoard) {
        this.posYOnBoard = posYonBoard;
    }

    public int getPosXOnBoard() {
        return posXOnBoard;
    }

    public int getPosYOnBoard() {
        return posYOnBoard;
    }

    public Color getDisplayingColorOfSquare() {
        return displayingColorOfSquare;
    }

    public void setDisplayingColorOfSquare(Color color) {
        displayingColorOfSquare = color;
        setBackground(color);
    }

    public void setPieceIcon(Icon icon) {
        pieceIcon = icon;
        setIcon(pieceIcon);
    }

    public Icon getImageIcon() {
        return pieceIcon;
    }
}
