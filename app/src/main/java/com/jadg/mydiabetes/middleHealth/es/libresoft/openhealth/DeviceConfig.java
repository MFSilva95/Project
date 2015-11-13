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

public class DeviceConfig {

	private int data_proto_id;
	private int phdid;
	private String enc_rules;
	private int protocol_version;
	private int nomenclature_version;
	private int data_req_init_agent_count;
	private int data_req_init_manager_count;
	private byte[] data_req_mode_flags;

	public DeviceConfig(int proto_id, int phdid, String enc_rules, int p_version, int n_version,
			int driac, int drimc, byte[] drmf)
	{
		this.data_proto_id = proto_id;
		this.phdid = phdid;
		this.enc_rules = enc_rules;
		this.protocol_version = p_version;
		this.nomenclature_version = n_version;
		this.data_req_init_agent_count = driac;
		this.data_req_init_manager_count = drimc;
		this.data_req_mode_flags = drmf;
	}

	public int getDataProtoId(){return this.data_proto_id;}

	public int getPhdId(){return this.phdid;}

	public String getEncondigRules(){return this.enc_rules;}

	public byte[] getEncondigRulesToArray(){
		if (enc_rules.equalsIgnoreCase("PER"))
			return ManagerConfig.enc_per;
		//else if (enc_rules.equalsIgnoreCase("XER"))
		//	return ManagerConfig.enc_xer;
		/* Default MDER encoding */
		return ManagerConfig.enc_mder;
	}
	public int getProtocolVersion(){return this.protocol_version;}
	public int getNomenclatureVersion(){return this.nomenclature_version;}
	public int getDataReqInitAgentCount(){return this.data_req_init_agent_count;}
	public int getDataReqInitManagerCount(){return this.data_req_init_manager_count;}
	public byte[] getDataReqModeFlags(){return this.data_req_mode_flags;}
}
