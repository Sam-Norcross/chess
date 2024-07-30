package model;

import chess.ChessGame;

public record JoinRequest(String authToken, int gameID, ChessGame.TeamColor playerColor) {

}
