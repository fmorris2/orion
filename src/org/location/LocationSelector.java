package org.location;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.Orion;
import org.mission.data.enums.ChoppingLocation;

import viking.api.skills.woodcutting.enums.TreeType;

public class LocationSelector
{
	private Orion orion;
	private Map<String, String> dbLocMap;
	
	public LocationSelector(Orion o)
	{
		orion = o;
	}
	
	public String getBestLoc(String command)
	{
		dbLocMap = getDBLocInfo();
		if(dbLocMap == null)
			return null;
		
		String[] parts = command.split(":");
		String skill = parts[1];
		if(skill.equals("wc"))
			return getBestWcLoc(parts[2], parts[3]);
		
				
		return null;
	}
	
	private String getBestWcLoc(String members, String treeType)
	{
		orion.log(this, false, "getBestWcLoc");
		Map<ChoppingLocation, Integer> locMap = new HashMap<>();
		
		//parse the returned map of strings from the db into a map we can actually use
		Iterator<Entry<String, String>> it = dbLocMap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
			if(entry.getKey().startsWith("W_"))
			{
				orion.log(this, false, "Parsing bot amount of " + entry.getValue() + " for " + entry.getKey() + " as an integer");
				locMap.put(ChoppingLocation.valueOf(entry.getKey()), Integer.parseInt(entry.getValue()));
			}
		}
		
		//now figure out what is actually the best loc
		List<ChoppingLocation> shuffled = Arrays.asList(ChoppingLocation.values());
		Collections.shuffle(shuffled);
		
		ChoppingLocation[] vals = shuffled.toArray(new ChoppingLocation[shuffled.size()]);
		TreeType type = TreeType.valueOf(treeType);
		ChoppingLocation best = null;
		boolean lookingForMem = !members.equals("free");
		for(ChoppingLocation loc : vals)
		{
			if(loc.isMembers() && !lookingForMem) //skip over this loc if it's a mem loc and we aren't members
				continue;
			
			//skip over this loc if it doesn't contain the target tree or is at max capacity
			Integer locCap = locMap.get(loc) == null ? 0 : locMap.get(loc);
			Integer bestCap = best == null ? 0 : locMap.get(best);
			
			if(!loc.containsTreeType(type) || locCap >= loc.getCapacity())
				continue;
			
			//update the best loc if this one is better than our current best
			if(best == null || locCap < bestCap)
				best = loc;
			
			if(bestCap == 0) //we've found an optimal location, no need to continue the search
				break;
		}
		
		orion.log(this, false, "best: " + best);
		return best.toString();
	}
	
	private Map<String, String> getDBLocInfo()
	{
		Map<String, String> toReturn = new HashMap<>();
		
		String locString = orion.occClient.sendAndListen("getLocInfo", true);
		if(locString == null)
			return null;
		
		orion.log(this, false, "locString: " + locString);
		
		if(locString.length() > 0)
		{
			String[] parts = locString.split(","); //EXAMPLE: W_VARROCK_EAST:4,M_BARBARIAN_VILLAGE:5
			for(String part : parts)
			{
				String[] locInfoParts = part.split(":");
				toReturn.put(locInfoParts[0], locInfoParts[1]);
			}
		}
		
		return toReturn;
	}
}
