package main;

import chess.ChessGame;
import chess.ChessPiece;
import client.ServerFacade;
import server.Server;
import ui.PreloginUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ServerFacade serverFacade = new ServerFacade("localhost", 8080);
        PreloginUI preloginUI = new PreloginUI(serverFacade);
        preloginUI.display();
    }
}
