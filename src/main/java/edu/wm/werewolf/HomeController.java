package edu.wm.werewolf;

import java.security.Principal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.wm.werewolf.dao.IPlayerDAO;
import edu.wm.werewolf.dao.MongoUserDAO;
import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.GPSLocation;
import edu.wm.werewolf.model.Game;
import edu.wm.werewolf.model.JsonResponse;
import edu.wm.werewolf.model.Kill;
import edu.wm.werewolf.model.Player;
import edu.wm.werewolf.model.PlayerListResponse;
import edu.wm.werewolf.model.Vote;
import edu.wm.werewolf.service.GameService;

/**
 * Handles requests for the application home page.
 * Responsible for handling the different Views for the url paths.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	// Autowired - finds a satisfied bean that wires reference to existing bean (link to this DAO)
	@Autowired private GameService gameService;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	// Not complete
	// Should get return AdminConsole jsp (I think)
	@RequestMapping(value = "/admin", method=RequestMethod.GET)
	public @ResponseBody JsonResponse adminConsole(Principal principal) {
		JsonResponse response = new JsonResponse();
		
		return response;
	}
	

	@RequestMapping(value = "/admin/restartGame", method=RequestMethod.POST)
	public @ResponseBody Game restartGame(@ModelAttribute Game game, Principal principal) {
		logger.info(principal.getName() + " is restarting the game");
		gameService.restartGame();
		return game;
	}
	
	
	// Should this be PlayerListResponse??
	@RequestMapping(value = "/players/alive", method=RequestMethod.GET)
	public @ResponseBody List<Player> getAllAlive() {
		List<Player> players = gameService.getAllAlive();
		return players;
	}
	

	@RequestMapping(value = "/players/nearby", method=RequestMethod.GET)
	public @ResponseBody PlayerListResponse getAllNearby(Player werewolf) {
		List<Player> playersNearby = new ArrayList<>();
		PlayerListResponse response = new PlayerListResponse(playersNearby);

		try {
			playersNearby = gameService.getAllNearby(werewolf, playersNearby);
			response.setPlayerList(playersNearby);
		} catch (NoPlayerFoundException e) {
			e.printStackTrace();
			response.setStatus = "failure";
		}
		
		return response;
	}
	

	// Should I change to playerListResponse??
	@RequestMapping(value = "/players/votable", method=RequestMethod.GET)
	public @ResponseBody List<Player> getAllVotable() {
		List<Player> votables = gameService.getAllVotable();
		return votables;
	}
	
	
	@RequestMapping(value = "/players/{id}", method=RequestMethod.POST)
	public @ResponseBody JsonResponse castVote(@ModelAttribute Vote vote, @PathVariable String id, 
												Principal principal) throws NoPlayerFoundException {
		JsonResponse response = new JsonResponse();
		logger.info(principal.getName() + "votes for" + id);
		gameService.votePlayer(principal.getName(), id);
		response.setStatus("success");
		return response;
	}
	

	@RequestMapping(value = "/players/{id}", method=RequestMethod.POST)
	public @ResponseBody JsonResponse killPlayer(@ModelAttribute Kill kill, @PathVariable String id,
												Principal principal) throws NoPlayerFoundException {
		JsonResponse response = new JsonResponse();
		logger.info( id + "has been killed");
		gameService.killPlayer(principal.getName(), id);
		response.setStatus("success");
		return response;
	}
	
	
	@RequestMapping(value ="/location", method=RequestMethod.POST)
	public @ResponseBody JsonResponse setLocation(@ModelAttribute GPSLocation location, Principal principal) 
													throws NoPlayerFoundException {
		JsonResponse response = new JsonResponse();
		logger.info("Setting" + principal.getName() + "s location to:" + location);
		gameService.updatePosition(principal.getName(), location);
		response.setStatus("success");
		return response;
	}
	
	@RequestMapping(value=".welcome", method=RequestMethod.GET)
	public String home(ModelMap model){
		return "home";
	}
	
}
