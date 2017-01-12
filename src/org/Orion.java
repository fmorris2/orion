package org;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.location.LocationSelector;
import org.manager.StatusChecks;
import org.manager.breaking.BreakManager;
import org.mission.OrionFisher;
import org.mission.OrionRJ;
import org.mission.OrionWoodcutter;
import org.missions.OrionMiner;
import org.missions.OrionRuneMys;
import org.missions.OrionSS;
import org.osbot.rs07.script.ScriptManifest;
import org.socket.OccClient;

import viking.api.skills.fishing.enums.FishType;
import viking.api.skills.mining.enums.RockType;
import viking.api.skills.woodcutting.enums.TreeType;
import viking.framework.command.CommandReceiver;
import viking.framework.mission.Mission;
import viking.framework.paint.VikingPaint;
import viking.framework.script.VikingScript;

@ScriptManifest(author = "The Viking", name = "Orion", info = "WE ROLLIN'", version = 0, logo = "")
public class Orion extends VikingScript implements CommandReceiver
{
	public final BreakManager BREAK_MANAGER = new BreakManager();
	
	public OccClient occClient;
	
	private StatusChecks statusChecks;
	private LocationSelector locSelector;
	
	@Override
	public Queue<Mission> generateMissions()
	{
		LinkedList<Mission> generated = new LinkedList<>();
		
		//tutorial - must always be first
		generated.addFirst(new OrionTutorial(this));
				
		//quests
		List<Mission> quests = new ArrayList<>(Arrays.asList(new OrionRJ(this), new OrionSS(this), new OrionRuneMys(this)));
		Collections.shuffle(quests);
		generated.addAll(quests);
		
		//variety?
		
		//task / spec
		generated.add(getTaskMission());
		
		return generated;
	}
	
	@Override
	public int onLoop()
	{
		//check account status
		statusChecks.perform();
		
		//if the client is not logged in, do not proceed to execute the missions
		//TODO should change this.... Missions should handle what should happen if the account is not logged in
		if(!client.isLoggedIn())
			return 600;
		
		int primary = super.onLoop();
		
		return primary == 0 ? 100 : primary;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		occClient = new OccClient(this, PARAMS.get("instanceId"));
		locSelector = new LocationSelector(this);
		statusChecks = new StatusChecks(this);
		
		if(!occClient.init())
			stop();
	}

	@Override
	public VikingPaint<?> getVikingPaint()
	{
		return null;//new OrionPaint(this);
	}

	@Override
	public boolean isDevBuild()
	{
		return true;
	}

	@Override
	public void receiveCommand(String command)
	{
		String[] parts = command.split(":");
		
		if(parts[0].equals("getLoc"))
		{
			CommandReceiver sendTo = (CommandReceiver)getMissionHandler().getCurrent();
			String bestLoc = locSelector.getBestLoc(command);
			occClient.set("location", bestLoc, false);
			sendTo.receiveCommand("bestLoc:"+locSelector.getBestLoc(command));
		}
	}
	
	private Mission getTaskMission()
	{
		Mission toReturn = null;
		String accountType = PARAMS.get("type");
		if(accountType == "1") //MULE
		{
			//return mule mission
		}
		else if(accountType == "0") // SLAVE
		{
			String task = PARAMS.get("task");
			String spec = PARAMS.get("spec");
			
			if(task.equals("wc"))
			{
				TreeType target = TreeType.valueOf(spec.toUpperCase());
				return new OrionWoodcutter(this, target);
			}
			else if(task.equals("fish"))
			{
				FishType target = FishType.valueOf(spec.toUpperCase());
				return new OrionFisher(this, target);
			}
			else if(task.equals("mine"))
			{
				RockType target = RockType.valueOf(spec.toUpperCase());
				return new OrionMiner(this, target);
			}
		}
		
		return toReturn;
	}
}
