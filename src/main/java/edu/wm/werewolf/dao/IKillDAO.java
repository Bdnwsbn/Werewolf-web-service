package edu.wm.werewolf.dao;

import java.util.List;

import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.Kill;

public interface IKillDAO {

	Kill getKillByKillerId(String killerId);

	void createKill(Kill kill);

	List<Kill> getAllKills();

	void dropAllKills();

}
