package edu.wm.werewolf.dao;

import java.util.Date;
import java.util.List;

import edu.wm.werewolf.model.Game;

public interface IGameDAO {


	void createGame(Game game);

	void updateGame(Game game);

	void setGameFinished(Game game);
	
	Game getCurrentGame();
	
	Game getGameByDate(Date date);

	List<Game> getAllGames();
	
	void dropAllGames();


}
