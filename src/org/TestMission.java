package org;

import viking.framework.goal.GoalList;
import viking.framework.goal.impl.InfiniteGoal;
import viking.framework.item_management.IMEntry;
import viking.framework.item_management.ItemManagement;
import viking.framework.mission.Mission;
import viking.framework.script.VikingScript;

public class TestMission extends Mission implements ItemManagement
{

	public TestMission(VikingScript script)
	{
		super(script);
	}

	@Override
	public boolean canEnd()
	{
		return false;
	}

	@Override
	public String getMissionName()
	{
		return "test";
	}

	@Override
	public String getCurrentTaskName()
	{
		return "test";
	}

	@Override
	public String getEndMessage()
	{
		return null;
	}

	@Override
	public GoalList getGoals()
	{
		return new GoalList(new InfiniteGoal());
	}

	@Override
	public String[] getMissionPaint()
	{
		return null;
	}

	@Override
	public int execute()
	{
		script.log(this, false, "Test");
		return 600;
	}

	@Override
	public void onMissionStart()
	{
	}

	@Override
	public void resetPaint()
	{
	}

	@Override
	public IMEntry[] itemsToBuy()
	{
		return new IMEntry[]{new IMEntry(this, 592, 1, 100, "Ashes")};
	}

	@Override
	public int[] itemsToSell()
	{
		return new int[]{};
	} 

}
