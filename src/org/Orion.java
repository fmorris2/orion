package org;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.manager.StatusChecks;
import org.manager.breaking.BreakManager;
import org.mission.OrionWoodcutter;
import org.osbot.rs07.script.ScriptManifest;
import org.socket.OccClient;

import viking.framework.mission.Mission;
import viking.framework.paint.VikingPaint;
import viking.framework.script.VikingScript;

@ScriptManifest(author = "The Viking", name = "Orion", info = "WE ROLLIN'", version = 0, logo = "")
public class Orion extends VikingScript
{
	public final OccClient OCC_CLIENT = new OccClient(this);
	public final BreakManager BREAK_MANAGER = new BreakManager();
	
	private StatusChecks statusChecks;
	
	@Override
	public Queue<Mission> generateMissions()
	{
		//EXAMPLE: return new LinkedList<>(Arrays.asList(new OrionTutorial, new OrionQuester))
		return new LinkedList<>(Arrays.asList(new OrionWoodcutter(this)));
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
		statusChecks = new StatusChecks(this);
		
		if(!OCC_CLIENT.init())
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

}
