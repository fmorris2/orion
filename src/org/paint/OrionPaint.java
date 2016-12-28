package org.paint;

import org.Orion;

import viking.framework.paint.VikingPaint;
import viking.framework.paint.plugin.VikingPaintPlugin;
import viking.framework.paint.plugin.impl.basic_paints.impl.VikingDevPlugin;

public class OrionPaint extends VikingPaint<Orion>
{

	public OrionPaint(Orion script)
	{
		super(script);
	}

	@Override
	protected VikingPaintPlugin[] generatePlugins(Orion script)
	{
		return new VikingPaintPlugin[]
		{
				new VikingDevPlugin(script, this)
		};
	}

}
