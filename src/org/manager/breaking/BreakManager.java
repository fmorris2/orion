package org.manager.breaking;

import java.time.LocalDateTime;

import org.Orion;
import org.osbot.rs07.script.MethodProvider;

import viking.api.Timing;

public class BreakManager
{
	private final int SLEEP_HOURS = MethodProvider.random(5, 9);
	
	private int bedtime;
	
	private long nextMiniBreak;
	private long miniBreakDur;
	
	private boolean isOnMiniBreak;
	private Orion script;
	
	public BreakManager(Orion o)
	{
		setNextMiniBreak();
		script = o;
	}
	
	
	public boolean isBreaking()
	{
		if(bedtime == -1)
			return false;
		
		if(isOnMiniBreak())
		{
			script.log(this, false, "On mini break");
			return true;
		}
		
		if(isOnMiniBreak)
		{
			isOnMiniBreak = false;
			setNextMiniBreak();
		}
		
		int hour = LocalDateTime.now().getHour();
		int wakeUp = getWakeUp();
		boolean onBreak = (wakeUp - hour < 0 ? (23 + wakeUp) - hour : wakeUp - hour) < SLEEP_HOURS;
		
		if(onBreak)
		{
			script.log(this, false, "On sleep break");
			script.occClient.set("is_breaking", "true", true);
		}
		else
			script.occClient.set("is_breaking", "false", true);
		
		return onBreak;
	}
	
	private boolean isOnMiniBreak()
	{
		long currentMs = Timing.currentMs();
		return currentMs >= nextMiniBreak && currentMs < nextMiniBreak + miniBreakDur;
	}
	
	public int getWakeUp()
	{
		int notAdjusted = bedtime + SLEEP_HOURS;
		int adjusted = notAdjusted;
		if(notAdjusted > 23)
			adjusted = notAdjusted - 23;
		
		return adjusted;
	}
	
	public void setNextMiniBreak()
	{
		int minute = 60000;
		
		nextMiniBreak = Timing.currentMs() + (minute * MethodProvider.random(60, 200));
		miniBreakDur = minute * MethodProvider.random(15, 60);
	}
	
	public void setBedtime(int i)
	{
		bedtime = i;
	}
}
