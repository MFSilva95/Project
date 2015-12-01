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
package pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim;

import pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;

import java.util.Hashtable;

	/**
	 * The EpiCfgScanner class represents a class that can be instantiated. EpiCfgScanner
	 * objects are used to send reports containing episodic data, that is, data not having
	 * a fixed period between each data value. A report is sent whenever one of the observed
	 * attributes changes value; however, two consecutive event reports shall not have a
	 * time interval less than the value of the Min-Reporting-Interval attribute.
	 */

public abstract class EpiCfgScanner extends CfgScanner implements EpiCfgScanner_Events{

	public EpiCfgScanner(Hashtable<Integer,Attribute> attributeList) throws InvalidAttributeException {
		super(attributeList);
	}

	public int getNomenclatureCode (){
		return Nomenclature.MDC_MOC_SCAN_CFG_EPI;
	}

}
