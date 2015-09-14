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

package es.libresoft.openhealth.events;

import ieee_11073.part_10101.Nomenclature;

import java.util.List;

import es.libresoft.openhealth.Agent;
import es.libresoft.openhealth.HealthDevice;
import es.libresoft.openhealth.RecentDeviceInformation;
import es.libresoft.openhealth.Measure;


public class InternalEventReporter {

	private static EventManager iEvent;
	public static synchronized void setDefaultEventManager (EventManager handler){
		iEvent = handler;
	}
	
	public synchronized static void gotAgentInfoFromMDS(HealthDevice dev){
		System.out.println("MYSERVICE: GOT AGENT INFO FROM MDS!!");
		
		Agent a = RecentDeviceInformation.getAgent(dev.getSystId());
		if(a==null){
			System.out.println("MYSERVICE: I GOT A NULL AGENT!");
		}
		dev.setAddress(a.getAddress());
		RecentDeviceInformation.addDevice(dev);
		iEvent.deviceConnected(dev.getSystId(), dev);
	}
	
	public synchronized static void agentConnected(Agent agent) {
		if (iEvent!=null)
			System.out.println("IEREPORTER: Attempting to add Agent to recentDeviceInformation...");
			RecentDeviceInformation.addAgent(agent);
			System.out.println("Added agent to recentDeviceInformation!");
	}

	public synchronized static void agentDisconnected(String system_id) {
		if (iEvent!=null)
			RecentDeviceInformation.removeAgent(system_id);
			iEvent.deviceDisconnected(system_id);
	}

	public synchronized static void agentChangeStatus(String system_id, String prevState , String newState) {
		if (iEvent!=null)
			System.out.println("IEREPORTER: changing device status from " + prevState + " to " + newState);
		
			if((system_id != null) && (newState != null)){
				iEvent.deviceChangeStatus(system_id, prevState, newState);
				System.out.println("IEREPORTER: changed device status with success! ");
			}else{
				System.out.print("something went wrong with change status: ");
				if(system_id == null){
					System.out.println("system_ID was null");
				}
				if(newState == null){
					System.out.println("newState was null");
				}
				
			}
	}

	public synchronized static void receivedMeasure(String system_id, Measure m) {
		System.out.println("MYHELPER: I was informed of new measure: ");
		System.out.println(" measureId " + m.getMeasureId());
		System.out.println(" unitId " + m.getUnitId());
		System.out.println(" timestamp " + m.getTimestamp());
		System.out.print(" values: ");
		for(int i = 0; i < m.getValues().size();i++){
			System.out.print(m.getValues().get(i) + " ");
		}
		System.out.println( "metricIds: ");
		for(int i = 0; i < m.getMetricIds().size();i++){
			System.out.print(m.getMetricIds().get(i) + " ");
		}
		
		iEvent.receivedMeasure(system_id, m);
		
	
	}
}
