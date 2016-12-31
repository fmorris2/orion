package org.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.Orion;

public class OccClient
{
	final String OCC_IP = "127.0.0.1";
	final int OCC_PORT = 43594;
	
	private Socket socket;
	private PrintWriter out;
	private Orion orion;
	private boolean banned, locked, loggedIn, idle, breaking;
	
	public OccClient(Orion o)
	{
		orion = o;
	}
	
	public boolean init()
	{
		try
		{
			socket = new Socket(OCC_IP, OCC_PORT);
			out = new PrintWriter(socket.getOutputStream(), true);
			orion.log(this, false, "Socket bound to cluster controller");
			return true;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void setBanned(boolean b)
	{
		if(banned == b)
			return;
		
		banned = b;
		out.println("SET is_banned="+b);
	}
	
	public void setLocked(boolean b)
	{
		if(locked == b)
			return;
		
		locked = b;
		out.println("SET is_locked="+b);
	}
	
	public void setLoggedIn(boolean b)
	{
		if(loggedIn == b)
			return;
		
		loggedIn = b;
		out.println("SET is_logged_in="+b);
	}
	
	public void setBreaking(boolean b)
	{
		if(breaking == b)
			return;
		
		breaking = b;
		out.println("SET is_breaking="+b);
	}
	
	public void setIdle(boolean b)
	{
		if(idle == b)
			return;
		
		idle = b;
		out.println("SET is_idle="+b);
	}
}
