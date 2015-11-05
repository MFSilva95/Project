/*
Copyright (C) 2008-2009  Santiago Carot Nemesio
email: scarot@libresoft.es

This program is a (FLOS) free libre and open source implementation
of a multiplatform manager device written in java according to the
ISO/IEEE 11073-20601. Manager application is designed to work in
DalvikVM over android platform.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.Event;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.FIFO;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.IFIFO;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ApduType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.channel.Channel;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.channel.VirtualChannel;

public abstract class Device11073 {

	public static final String MDER_DEFUALT = "MDER";

	private VirtualChannel vch;

	protected IFIFO<ApduType> inputQueue;
	protected IFIFO<ApduType> outputQueue;
	protected IFIFO<Event> eventQueue;


	public Device11073 ()
	{
		inputQueue = new FIFO<ApduType>();
		outputQueue = new FIFO<ApduType>();
		eventQueue = new FIFO<Event>();
		vch = new VirtualChannel(inputQueue, outputQueue, eventQueue);
	}

	public void addChannel (Channel channel)
	{
		vch.addChannel(channel);
	}

	public void delChannel (Channel channel)
	{
		vch.delChannel(channel);
	}

	public void freeResources (){
		vch.freeChannels();
	}
}
