package org;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import viking.framework.antiban.reaction.ReactionEntry;
import viking.framework.antiban.reaction.ReactionEvent;
import viking.framework.antiban.reaction.events.impl.TreeEvent;
import viking.framework.goal.GoalList;
import viking.framework.goal.impl.InfiniteGoal;
import viking.framework.mission.Mission;
import viking.framework.script.VikingScript;

public class ReactionMission extends Mission implements MouseInputListener
{
	
	ReactionEvent reactionEvent = new TreeEvent(this, "Oak");
	ReactionEntry reactionEntry = new ReactionEntry();
	
	public ReactionMission(VikingScript script)
	{
		super(script);
		script.bot.addMouseListener(this);
	}

	@Override
	public boolean canEnd()
	{
		return false;
	}

	@Override
	public String getMissionName()
	{
		return "Reaction Mission";
	}

	@Override
	public String getCurrentTaskName()
	{
		return "Reaction";
	}

	@Override
	public String getEndMessage()
	{
		return "";
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
		if(reactionEvent.isDoing() && !reactionEntry.hasStartedEvent())
		{
			script.log(this, false, "Reaction entry started...");
			reactionEntry.startDoing();
		}
		else
			checkForReactionStart();
		
		return 20;
	}
	
	private void checkForReactionStart()
	{
		if(!reactionEvent.isDoing() && reactionEntry.hasStartedEvent() && !reactionEntry.hasStartedReaction())
		{
			script.log(this, false, "Started reaction timer...");
			reactionEntry.stopDoing();
		}
	}
	
	private void checkForReactionEnd()
	{
		if(reactionEntry.hasStartedReaction())
		{
			script.log(this, false, "Stopped reaction timer!");
			reactionEntry.end();
			reactionEvent.log(reactionEntry);
		}
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
	public void mouseDragged(MouseEvent arg0)
	{
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		checkForReactionEnd();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		checkForReactionEnd();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

}
