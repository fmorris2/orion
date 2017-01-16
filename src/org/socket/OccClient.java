package org.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.Orion;

public class OccClient
{
	final String OCC_IP = "127.0.0.1";
	final int OCC_PORT = 43594;
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Orion orion;
	private String instanceId;
	private Map<String, String> vals;
	
	public OccClient(Orion o, String instanceId)
	{
		orion = o;
		this.instanceId = instanceId;
		vals = new HashMap<>();
	}
	
	public boolean init()
	{
		try
		{
			socket = new Socket(OCC_IP, OCC_PORT);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			orion.log(this, false, "Socket bound to cluster controller");
			return true;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void set(String key, String val, boolean accountUpdate)
	{
		String cachedVal = vals.get(key);
		
		if(cachedVal == null || !cachedVal.equals(val))
		{
			orion.log(this, false, "SET " + instanceId + " " + (accountUpdate ? "account" : "instance") + " " + key+":"+val);
			vals.put(key, val);
			out.println("SET " + instanceId + " " + (accountUpdate ? "account" : "instance") + " " + key+":"+val);
		}
	}
	
	public String sendAndListen(String command, boolean query)
	{
		try
		{
			out.println(query ? ("QUERY " + instanceId + " " + command) : command);
			String fromOcc = in.readLine();
			orion.log(this, false, "Response from OCC: " + fromOcc);
			return fromOcc;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void send(String command, boolean query)
	{
		try
		{
			out.println(query ? ("QUERY " + instanceId + " " + command) : command);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void killInstance()
	{
		out.println("KILL " + instanceId);
	}
	
	public String getInstanceId()
	{
		return instanceId;
	}
}
