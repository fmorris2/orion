package org.manager;

import org.Orion;
import org.OrionMule;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;

import viking.api.Timing;
import viking.api.login.VLogin;
import viking.framework.mission.Mission;

public class StatusChecks
{	
	private static final long POS_SENT_THRESH = 10000;
	private static final int SKILLS_SENT_THRESH = 10000;
	
	private Orion orion;
	private boolean isBanned, isLocked, sentDisplayName;
	private Position playerPos;
	private long lastPosSent, lastSkillsSent;
	
	public StatusChecks(Orion o)
	{
		orion = o;
	}
	
	public void perform()
	{
		if(orion.client.getLoginState() == null)
		{
			orion.log(this, false, "Login state is null... returning");
			return;
		}
		
		switch(orion.client.getLoginState())
		{
			case LOGGED_OUT:
				orion.occClient.set("is_logged_in", "false", true);
				loggedOutChecks();
			break;
			case LOGGED_IN:
				orion.occClient.set("is_logged_in", "true", true);
				loggedInChecks();
			break;
			default:
				orion.log(this, false, "Unhandled login state: " + orion.client.getLoginState());
			break;
		}
	}
	
	private void loggedOutChecks()
	{
		Mission current = orion.getMissionHandler().getCurrent();
		OrionMule mule = (current instanceof OrionMule) ? (OrionMule)current : null;
		
		if(mule != null && !mule.shouldLogin)
			return;
		
		if(!isBanned && !isLocked && !orion.BREAK_MANAGER.isBreaking())
		{
			orion.log(this, false, "Attempting to login...");
			String pass = orion.PARAMS.get("pass");
			if(pass == null)
				return;
			
			VLogin login = orion.getUtils().login;
			if(!login.login(orion.bot.getUsername(), pass))
			{
				if(login.isBanned())
				{
					orion.log(this, false, "Banned account");
					orion.occClient.set("is_banned", "true", true);
					isBanned = true;
				}
				else if(login.isLocked())
				{
					orion.log(this, false, "Locked account");
					orion.occClient.set("is_locked", "true", true);
					isLocked = true;
				}
				else if(login.isInvalid())
					orion.occClient.set("script_status", "invalid user / pass", false);
				else if(login.isRSUpdate())
				{
					orion.log(this, false, "RS Update!");
					orion.occClient.killInstance();
				}
			}
			else
				orion.log(this, false, "Successfully logged in");
		}
	}
	
	private void loggedInChecks()
	{
		//send script status
		Mission current = orion.getMissionHandler().getCurrent();
		if(current != null && current.getCurrentTaskName() != null)
			orion.occClient.set("script_status", current.getCurrentTaskName().replace(" ", "_"), false);
		
		//check if we need to set the display name
		String name = orion.myPlayer().getName();
		if(!sentDisplayName && name != null && !name.equals("null"))
		{
			orion.occClient.set("display_name", name, true);
			sentDisplayName = true;
		}
		
		//check if welcome screen is still up
		if(orion.getUtils().login.getLobbyButton() != null)
			orion.getUtils().login.clickLobbyButton();
		
		checkForPosUpdate();
		checkForSkillsUpdate();
	}
	
	private void checkForPosUpdate()
	{
		//position update
		if(!orion.myPosition().equals(playerPos))
		{
			playerPos = orion.myPosition();
			
			if(Timing.timeFromMark(lastPosSent) > POS_SENT_THRESH)
			{
				orion.occClient.set("pos", playerPos.getX()+","+playerPos.getY()+","+playerPos.getZ(), true);
				lastPosSent = Timing.currentMs();
			}
		}
	}
	
	private void checkForSkillsUpdate()
	{
		if(Timing.timeFromMark(lastSkillsSent) > SKILLS_SENT_THRESH)
		{
			String skillString = "";
			Skill[] skills = Skill.values();
			for(Skill s : skills)
			{
				skillString += orion.skills.getStatic(s);
				if(s != skills[skills.length - 1])
					skillString += ",";
			}
			
			orion.occClient.set("stats", skillString, true);
			lastSkillsSent = Timing.currentMs();
		}
	}
}
