package org.manager;

import org.Orion;

import viking.api.login.VLogin;

public class StatusChecks
{	
	private Orion orion;
	private boolean isBanned, isLocked;
	
	public StatusChecks(Orion o)
	{
		orion = o;
	}
	
	public void perform()
	{
		if(orion.client.getLoginState() == null)
			return;
		
		switch(orion.client.getLoginState())
		{
			case LOGGED_OUT:
				orion.OCC_CLIENT.setLoggedIn(false);
				loggedOutChecks();
			break;
			case LOGGED_IN:
				orion.OCC_CLIENT.setLoggedIn(true);
				loggedInChecks();
			break;
			default:
				orion.log(this, false, "Unhandled login state: " + orion.client.getLoginState());
			break;
		}
	}
	
	private void loggedOutChecks()
	{
		if(!isBanned && !isLocked && !orion.BREAK_MANAGER.isBreaking())
		{
			orion.log(this, false, "Attempting to login...");
			String[] params = orion.getParameters().split(".");
			VLogin login = orion.getUtils().login;
			if(!login.login(params[0], params[1]))
			{
				if(login.isBanned())
				{
					orion.log(this, false, "Banned account");
					orion.OCC_CLIENT.setBanned(true);
					isBanned = true;
				}
				else if(login.isLocked())
				{
					orion.log(this, false, "Locked account");
					orion.OCC_CLIENT.setLocked(true);
					isLocked = true;
				}
				else if(login.isInvalid())
				{
					orion.log(this, false, "Invalid user / pass");
				}
			}
			else
				orion.log(this, false, "Successfully logged in");
		}
	}
	
	private void loggedInChecks()
	{
		//check if idle
	}
}
