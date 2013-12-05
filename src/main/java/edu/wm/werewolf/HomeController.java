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

import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.GPSLocation;
import edu.wm.werewolf.model.JsonResponse;
import edu.wm.werewolf.model.Player;
import edu.wm.werewolf.model.PlayerListResponse;
import edu.wm.werewolf.service.GameService;
import edu.wm.werewolf.service.UserServiceImpl;

/**
 * Handles requests for the application home page.
 * Responsible for handling the different Views for the url paths.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired private GameService gameService;
	@Autowired private UserServiceImpl userService; 
	
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
	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public @ResponseBody JsonResponse createUser(Principal principal) {
		JsonResponse response = new JsonResponse();
		//userService.createUser()
		response.setStatus("success");
		return response;
	}
	
	// Not complete
	// Should get return AdminConsole jsp (I think)
	@RequestMapping(value = "/admin", method=RequestMethod.GET)
	public @ResponseBody JsonResponse adminConsole(Principal principal) {
		JsonResponse response = new JsonResponse();
		response.setStatus("success");
		return response;
	}
	
	
	@RequestMapping(value = "/admin/restartGame", method=RequestMethod.POST)
	public @ResponseBody JsonResponse restartGame(Principal principal) {
		JsonResponse response = new JsonResponse();
		logger.info(principal.getName() + " is restarting the game");
		gameService.restartGame();
		response.setStatus("success");
		return response;
	}
	

	@RequestMapping(value = "/players/all", method=RequestMethod.GET)
	public @ResponseBody PlayerListResponse getAllPlayers() {
		List<Player> players = new ArrayList<>();
		PlayerListResponse response = new PlayerListResponse(players);
		
		players = gameService.getAllPlayers();
		response.setPlayerList(players);
		
		if (!(players.isEmpty())) 
			response.setStatus = "success";
		else
			response.setStatus = "no players";
		
		return response;
	}
	@RequestMapping(value = "/players/alive", method=RequestMethod.GET)
	public @ResponseBody PlayerListResponse getAllAlive() {
		List<Player> players = new ArrayList<>();
		PlayerListResponse response = new PlayerListResponse(players);
		
		players = gameService.getAllAlive();
		response.setPlayerList(players);
		
		if (!(players.isEmpty())) 
			response.setStatus = "success";
		else
			response.setStatus = "no players";
		
		return response;
	}

	
	@RequestMapping(value = "/players/nearby", method=RequestMethod.GET)
	public @ResponseBody PlayerListResponse getAllNearby(Principal principal) {
		List<Player> playersNearby = new ArrayList<>();
		PlayerListResponse response = new PlayerListResponse(playersNearby);

		try {
			playersNearby = gameService.getAllNearby(principal.getName(), playersNearby);
			response.setPlayerList(playersNearby);
		} catch (NoPlayerFoundException e) {
			e.printStackTrace();
			response.setStatus = "failure";
		}
		
		return response;
	}
	

	
	@RequestMapping(value = "/players/votable", method=RequestMethod.GET)
	public @ResponseBody PlayerListResponse getAllVotable() {
		List<Player> votables = new ArrayList<>();
		PlayerListResponse response = new PlayerListResponse(votables);
		
		votables = gameService.getAllVotable();
		response.setPlayerList(votables);
		
		if (!(votables.isEmpty())) 
			response.setStatus = "success";
		else
			response.setStatus = "no votables";
		
		return response;
	}
	
	
	@RequestMapping(value = "/players/votes/{id}", method=RequestMethod.POST)
	public @ResponseBody JsonResponse castVote(@PathVariable String id, Principal principal) 
												throws NoPlayerFoundException {
		JsonResponse response = new JsonResponse();
		logger.info(principal.getName() + "votes for" + id);
		gameService.votePlayer(principal.getName(), id);
		response.setStatus("success");
		return response;
	}
	

	@RequestMapping(value = "/players/kill/{id}", method=RequestMethod.POST)
	public @ResponseBody JsonResponse killPlayer(@PathVariable String id, Principal principal) 
												  throws NoPlayerFoundException {
		JsonResponse response = new JsonResponse();
		logger.info( id + "has been killed");
		gameService.killPlayer(principal.getName(), id);
		response.setStatus("success");
		return response;
	}
	
	
	@RequestMapping(value ="/location/{id}", method=RequestMethod.POST)
	public @ResponseBody JsonResponse setLocation(@ModelAttribute GPSLocation location, Principal principal) 
													throws NoPlayerFoundException {
		JsonResponse response = new JsonResponse();
		logger.info("Setting" + principal.getName() + "s location to:" + location);
//		principal.getName() / "ben"
		GPSLocation location1 = new GPSLocation(1.01, 10.1);
		gameService.updatePosition("ben", location1);
		response.setStatus("success");
		return response;
	}
	
	
	@RequestMapping(value=".welcome", method=RequestMethod.GET)
	public String home(ModelMap model){
		return "home";
	}
	
}
