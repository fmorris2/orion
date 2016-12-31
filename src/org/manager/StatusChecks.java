package org.manager;

import org.Orion;
import org.osbot.rs07.constants.ResponseCode;
import org.osbot.rs07.listener.LoginResponseCodeListener;

public class StatusChecks implements LoginResponseCodeListener
{
	private static final int BANNED_CODE = 4;
	
	private Orion orion;
	private boolean isBanned, isLocked;
	
	public StatusChecks(Orion o)
	{
		orion = o;
		o.bot.addLoginListener(this);
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
		}
	}
	
	private void loggedInChecks()
	{
		//check if idle
	}

	@Override
	public void onResponseCode(int code) throws InterruptedException
	{
		if(ResponseCode.isDisabledError(code))
		{
			orion.log(this, false, "Disabled error code: " + code);
			if(code == BANNED_CODE)
			{
				orion.OCC_CLIENT.setBanned(true);
				isBanned = true;
			}
			else
			{
				orion.OCC_CLIENT.setLocked(true);
				isLocked = true;
			}
		}
	}
}
