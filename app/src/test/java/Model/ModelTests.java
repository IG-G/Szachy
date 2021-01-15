/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Model;
import Pieces.ChessPiece;
import Pieces.ColorOfPiece;
import Pieces.WhitePawn;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;

public class ModelTests {
    ChessModelBoard board;

    @BeforeEach
    public void init(){
        board = new ChessModelBoard();
        board.initPieces();
    }

    @Test
    public void testFirstPawnMovePossibilities(){
        List<ChessModelSquare> expectedMoves = new ArrayList<>();
        expectedMoves.add(board.getChessModelSquare(0, 5));
        expectedMoves.add(board.getChessModelSquare(0, 4));
        List<ChessModelSquare> moves = board.getLegalPossibleMoves(board.getChessModelSquare(0, 6));
        assertEquals(2, moves.size());
        assertTrue(moves.contains(expectedMoves.get(0)), "Nie ma ruchu");
        assertTrue(moves.contains(expectedMoves.get(1)));
    }
    @Test
    public void testEnPassantMove(){
        board.makeMove(board.getChessModelSquare(0, 6), board.getChessModelSquare(0, 4), ColorOfPiece.WHITE);
        board.makeMove(board.getChessModelSquare(6, 1), board.getChessModelSquare(6, 3), ColorOfPiece.BLACK);
        board.makeMove(board.getChessModelSquare(0, 4), board.getChessModelSquare(0, 3), ColorOfPiece.WHITE);
        board.makeMove(board.getChessModelSquare(1, 1), board.getChessModelSquare(1, 3), ColorOfPiece.BLACK);
        List<ChessModelSquare> moves = board.getLegalPossibleMoves(board.getChessModelSquare(0,3));
        List<ChessModelSquare> expectedMoves = new ArrayList<>();
        expectedMoves.add(board.getChessModelSquare(0 ,2));
        expectedMoves.add(board.getChessModelSquare(1, 2));
        assertEquals(2, moves.size());
        assertTrue(moves.contains(expectedMoves.get(0)));
        assertTrue(moves.contains(expectedMoves.get(1)));
        board.makeMove(board.getChessModelSquare(0, 3), board.getChessModelSquare(1, 2), ColorOfPiece.WHITE);
        ChessPiece piece = board.getChessModelSquare(1, 3).getPiece();
        assertNull(piece);
        piece = board.getChessModelSquare(1, 2).getPiece();
        assertTrue(piece instanceof WhitePawn);
    }
    @Test
    public void testQuickestCheckMate(){
        board.makeMove(board.getChessModelSquare(5,6), board.getChessModelSquare(5, 4), ColorOfPiece.WHITE);
        board.makeMove(board.getChessModelSquare(4, 1), board.getChessModelSquare(4, 3), ColorOfPiece.BLACK);
        board.makeMove(board.getChessModelSquare(6, 6), board.getChessModelSquare(6, 4), ColorOfPiece.WHITE);
        board.makeMove(board.getChessModelSquare(3, 0), board.getChessModelSquare(7, 4), ColorOfPiece.BLACK);
        assertTrue(board.isKingUnderCheck());
        assertTrue(board.hasGameFinished());
    }
}