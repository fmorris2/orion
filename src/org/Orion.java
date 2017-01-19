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
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.ScriptManifest;
import org.socket.OccClient;

import viking.api.Timing;
import viking.api.skills.fishing.enums.FishType;
import viking.api.skills.mining.enums.RockType;
import viking.api.skills.woodcutting.enums.TreeType;
import viking.framework.command.CommandReceiver;
import viking.framework.mission.Mission;
import viking.framework.mule.MuleManagement;
import viking.framework.mule.MuleOrderEvent;
import viking.framework.paint.VikingPaint;
import viking.framework.script.VikingScript;

@ScriptManifest(author = "The Viking", name = "Orion", info = "WE ROLLIN'", version = 0, logo = "")
public class Orion extends VikingScript implements CommandReceiver
{
	private static final int MULE_EVENT_TIMER = 60000 * 2;
	
	public final BreakManager BREAK_MANAGER = new BreakManager();
	
	public OccClient occClient;
	
	private StatusChecks statusChecks;
	private LocationSelector locSelector;
	
	private Mission muleOrderMission;
	private MuleOrderEvent muleOrderEvent;
	private long lastMuleEventUpdate;
	
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
		Mission current = getMissionHandler().getCurrent();
		
		//check account status
		statusChecks.perform();
		
		//check if slave has mule order
		handleMuleOrder(current);
		
		//if the client is not logged in, do not proceed to execute the missions
		//TODO should change this.... Missions should handle what should happen if the account is not logged in
		if(!client.isLoggedIn() && !(current instanceof OrionMule))
			return 600;
		
		int primary = super.onLoop();
		
		return primary == 0 ? 100 : primary;
	}
	
	private void handleMuleOrder(Mission current)
	{
		if(current instanceof MuleManagement)
		{
			if(muleOrderEvent == null || muleOrderMission != current)
			{
				log(this, false, "Updating mule order event");
				muleOrderEvent = new MuleOrderEvent(this, ((MuleManagement)current).getOrder());
				muleOrderEvent.getOrder().muleAt = Integer.parseInt(occClient.sendAndListen("SLAVE " + occClient.getInstanceId() + " MULE_AT", false));
				muleOrderMission = current;
			}
			else if(!muleOrderEvent.getOrder().equals(((MuleManagement)current).getOrder()))
			{
				log(this, false, "Mule order has changed since the start of the mission... updating...");
				muleOrderEvent = new MuleOrderEvent(this, ((MuleManagement)current).getOrder());
			}
			else if(muleOrderEvent.shouldExecute() && updateEvent())
			{
				while(!muleOrderEvent.hasFinished())
				{
					statusChecks.perform();
					updateEvent();
					muleOrderEvent.execute();
					current.waitMs(400);
				}
				
				muleOrderEvent.setHasFinished(false);
			}
		}
	}
	
	private boolean updateEvent()
	{
		if(Timing.timeFromMark(lastMuleEventUpdate) > MULE_EVENT_TIMER)
		{
			lastMuleEventUpdate = Timing.currentMs();
			log(this, false, "Updating mule event");
			String info = occClient.sendAndListen("SLAVE " + occClient.getInstanceId() + " REQUEST", false);
			String[] parts = info.split(";");
			if(parts.length < 3)
				return false;
			
			int x = -1, y = -1, z = -1;
			for(String part : parts)
			{
				String[] pair = part.split(":");
				String key = pair[0], val = pair[1];
				if(key.equals("name")) muleOrderEvent.setMuleName(val);
				else if(key.equals("x")) x = Integer.parseInt(val);
				else if(key.equals("y")) y = Integer.parseInt(val);
				else if(key.equals("z")) z = Integer.parseInt(val);
			}
			
			muleOrderEvent.setMulePos(new Position(x, y, z));
			
			return true;
		}
		
		return false;
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
		CommandReceiver sendTo = (CommandReceiver)getMissionHandler().getCurrent();
		
		if(parts[0].equals("getLoc"))
		{
			String bestLoc = locSelector.getBestLoc(command);
			occClient.set("location", bestLoc, false);
			sendTo.receiveCommand("bestLoc:"+locSelector.getBestLoc(command));
		}
		else if(parts[0].equals("mule"))
		{
			if(parts[1].equals("poll"))
			{
				log(this, false, "Sending mule poll command to OCC");
				sendTo.receiveCommand(occClient.sendAndListen("MULE " + occClient.getInstanceId() + " POLL", false));
			}
			else if(parts[1].equals("reset"))
				occClient.send("MULE " + occClient.getInstanceId() + " RESET", false);
			else if(parts[1].equals("complete"))
				occClient.send("MULE " + occClient.getInstanceId() + " COMPLETE " + parts[2], false);
		}
	}
	
	private Mission getTaskMission()
	{
		Mission toReturn = null;
		String accountType = PARAMS.get("type");
		if(accountType.equals("1")) //MULE
		{
			log(this, false, "Add mule mission");
			return new OrionMule(this);
		}
		else if(accountType.equals("0")) // SLAVE
		{
			log(this, false, "Add slave mission");
			String task = PARAMS.get("task");
			String spec = PARAMS.get("spec");
			
			log(this, false, "Task: " + task + ", Spec: " + spec);
			
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
