package org;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.mission.OrionWoodcutter;
import org.paint.OrionPaint;

import viking.framework.mission.Mission;
import viking.framework.paint.VikingPaint;
import viking.framework.script.VikingScript;

public class Orion extends VikingScript
{

	@Override
	public Queue<Mission> generateMissions()
	{
		//EXAMPLE: return new LinkedList<>(Arrays.asList(new OrionTutorial, new OrionQuester))
		return new LinkedList<>(Arrays.asList(new OrionWoodcutter(this)));
	}

	@Override
	public VikingPaint<?> getVikingPaint()
	{
		return new OrionPaint(this);
	}

	@Override
	public boolean isDevBuild()
	{
		return false;
	}

}
