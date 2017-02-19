package org;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.osbot.rs07.script.ScriptManifest;

import viking.framework.mission.Mission;
import viking.framework.paint.VikingPaint;
import viking.framework.script.VikingScript;

@ScriptManifest(author = "The Viking", name = "Test", info = "WE ROLLIN'", version = 0, logo = "")
public class Test extends VikingScript
{	
	@Override
	public Queue<Mission> generateMissions()
	{
		return new LinkedList<>(Arrays.asList(new ReactionMission(this)));
	}

	@Override
	public VikingPaint<?> getVikingPaint()
	{
		return null;
	}

	@Override
	public boolean isDevBuild()
	{
		return false;
	}

}
