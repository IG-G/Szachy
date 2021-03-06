package Controller;

import Model.ChessModelBoard;
import Model.ChessModelSquare;
import Pieces.BlackPawn;
import Pieces.ColorOfPiece;
import Pieces.Rook;
import Pieces.WhitePawn;
import View.AppGUI;
import View.ChessViewBoard;
import View.ChessViewSquare;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class ChessGameController {
    ChessModelBoard boardModel;
    ChessViewBoard boardView;
    AppGUI frame;
    ChessModelSquare[] previousPossibleMoves = null;
    ChessModelSquare selectedForMove = null;
    ColorOfPiece colorOnMove = ColorOfPiece.WHITE;
    boolean isGameWithBot = false;
    ColorOfPiece humanColor = null;
    private ChessBot bot;

    public void setPreviousPossibleMoves(ChessModelSquare[] modelSquares) {
        previousPossibleMoves = modelSquares;
    }

    public ChessGameController(ChessModelBoard model, ChessViewBoard view, AppGUI mainFrame) {
        boardModel = model;
        boardView = view;
        frame = mainFrame;
    }

    public void initGUI() {
        NewGameListener listener = new NewGameListener(this, frame.getFrame());
        NewPieceListener listener1 = new NewPieceListener(this);
        CreateCustomGameListener listener2 = new CreateCustomGameListener(this);
        frame.initMainFrame(listener, listener1, listener2);
    }

    public void actionOccurred(ChessViewSquare source) {
        ChessModelSquare modelSquare = boardModel.getChessModelSquare(source.getPosXOnBoard(), source.getPosYOnBoard());
        if (selectedForMove == null) {
            if (modelSquare.getPiece() != null && modelSquare.getPiece().getColor() == colorOnMove) {
                selectedForMove = modelSquare;
                if (!selectSquaresToMove(modelSquare)) {
                    selectedForMove = null;
                }
            }
        } else {
            if (modelSquare == selectedForMove) {
                selectedForMove = null;
                boardView.cleanPossibleMovesSquares();
                previousPossibleMoves = null;
                return;
            }
            boolean wasMoveMadeSuccessfully = makeMove(modelSquare);
            selectedForMove = null;
            boardView.cleanPossibleMovesSquares();
            previousPossibleMoves = null;
            if (!wasMoveMadeSuccessfully) {
                if (modelSquare.getPiece() != null && modelSquare.getPiece().getColor() == colorOnMove) {
                    if (!selectSquaresToMove(modelSquare)) {
                        selectedForMove = null;
                    } else {
                        selectedForMove = modelSquare;
                    }
                }
            } else {
                if (isGameWithBot) {
                    makeBotMove();
                    colorOnMove = humanColor;
                } else {
                    colorOnMove = colorOnMove == ColorOfPiece.WHITE ? ColorOfPiece.BLACK : ColorOfPiece.WHITE;
                }
            }
        }
    }

    public boolean selectSquaresToMove(ChessModelSquare source) {
        List<ChessModelSquare> possibleMovesFromModel = boardModel.getLegalPossibleMoves(source);
        List<ChessViewSquare> viewSquaresPossibleToMove = new ArrayList<>();
        if (possibleMovesFromModel != null) {
            for (ChessModelSquare itr : possibleMovesFromModel) {
                viewSquaresPossibleToMove.add(boardView.getChessViewSquare(itr.getY(), itr.getX()));
            }
            boardView.setPossibleMovesSquares(viewSquaresPossibleToMove.toArray(new ChessViewSquare[0]));
            previousPossibleMoves = possibleMovesFromModel.toArray(new ChessModelSquare[0]);
            return true;
        }
        return false;
    }

    public boolean makeMove(ChessModelSquare destinationSquare) {
        for (ChessModelSquare square : previousPossibleMoves) {
            if (square == destinationSquare) {
                boardModel.makeMove(selectedForMove, destinationSquare, colorOnMove);
                boardView.makeMove(selectedForMove.getY(), selectedForMove.getX(),
                        destinationSquare.getY(), destinationSquare.getX());
                if (boardModel.didShortCastleHappened()) {
                    int row = destinationSquare.getY();
                    ChessModelSquare rookSquare = boardModel.getChessModelSquare(7, row);
                    ChessModelSquare squareAfterCastle = boardModel.getChessModelSquare(5, row);
                    boardView.makeMove(rookSquare.getY(), rookSquare.getX(), squareAfterCastle.getY(), squareAfterCastle.getX());
                    ((Rook) boardModel.getChessModelSquare(5, row).getPiece()).setWasMoved(true);
                    boardModel.setShortCastleHappened(false);
                }
                if (boardModel.didLongCastleHappened()) {
                    int row = destinationSquare.getY();
                    ChessModelSquare rookSquare = boardModel.getChessModelSquare(0, row);
                    ChessModelSquare squareAfterCastle = boardModel.getChessModelSquare(3, row);
                    boardView.makeMove(rookSquare.getY(), rookSquare.getX(), squareAfterCastle.getY(), squareAfterCastle.getX());
                    ((Rook) boardModel.getChessModelSquare(3, row).getPiece()).setWasMoved(true);
                    boardModel.setLongCastleHappened(false);
                }

                //segment en passant
                if (boardModel.didEnPassantHappened()) {
                    if (destinationSquare.getY() == 2) {
                        boardView.getChessViewSquare(3, destinationSquare.getX()).setPieceIcon(null);
                    } else {
                        boardView.getChessViewSquare(4, destinationSquare.getX()).setPieceIcon(null);
                    }
                    boardModel.setEnPassantHappened(false);
                }
                //promotion of pawn
                if ((destinationSquare.getY() == 0 && destinationSquare.getPiece() instanceof WhitePawn) ||
                        (destinationSquare.getY() == 7 && destinationSquare.getPiece() instanceof BlackPawn)) {
                    ChessViewSquare promotionSquare = boardView.getChessViewSquare(destinationSquare.getY(), destinationSquare.getX());
                    if(isGameWithBot && humanColor != colorOnMove){
                        if(colorOnMove == ColorOfPiece.WHITE)
                            promotionSquare.setPieceIcon(boardView.getWhitePiecesIcons()[4]);
                        else
                            promotionSquare.setPieceIcon(boardView.getBlackPiecesIcons()[4]);
                        boardModel.makePromotion(destinationSquare, 0);
                    }else {
                        int chosenOption = boardView.makePromotion(promotionSquare);
                        boardModel.makePromotion(destinationSquare, chosenOption);
                    }
                }

                //check segment
                if (boardModel.isKingUnderCheck()) {
                    boardView.cleanKingUnderCheck(); // in case of checking the other king during defence
                    ColorOfPiece color = colorOnMove == ColorOfPiece.WHITE ? ColorOfPiece.BLACK : ColorOfPiece.WHITE;
                    ChessModelSquare kingSquare = boardModel.getKingPosition(color);
                    boardView.setKingUnderCheck(kingSquare.getY(), kingSquare.getX());
                } else {
                    boardView.cleanKingUnderCheck();
                }
                if (boardModel.hasGameFinished()) {
                    endGame();
                }
                return true;
            }
        }
        return false;
    }

    private void endGame() {
        if (!boardModel.isStaleMate()) {
            boardView.showEndGameInfo(colorOnMove);
        } else {
            boardView.showTieGameInfo();
        }
        isGameWithBot = false;
    }

    public void startNewGame() {
        ((JFrame) (frame.getFrame())).getContentPane().removeAll();
        boardModel.initPieces();
        boardModel.clearAll();
        SquareButtonListener listener = new SquareButtonListener(this);
        boardView.initViewBoard(listener);
        colorOnMove = ColorOfPiece.WHITE;
        if (isGameWithBot && humanColor == ColorOfPiece.BLACK) {
            makeBotMove();
            colorOnMove = ColorOfPiece.BLACK;
        }
    }

    public void setParamsForNewGameWithBot(ChessBot bot, ColorOfPiece humanColor) {
        this.bot = bot;
        isGameWithBot = true;
        this.humanColor = humanColor;
    }

    private void makeBotMove() {
        bot.makeBotMove();
    }
}