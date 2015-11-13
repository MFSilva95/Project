/*
Copyright (C) 2010 GSyC/LibreSoft, Universidad Rey Juan Carlos.

Author: Jose Antonio Santos Cadenas <jcaden@libresoft.es>

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

package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.application;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Attribute;

public class SetEvent extends ExternalEvent {

	Attribute attribute;
	HANDLE handle;

	public SetEvent(HANDLE handle, Attribute attr) {
		super(EventType.REQ_SET);

		attribute = attr;
		this.handle = handle;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public HANDLE getObjectHandle() {
		return handle;
	}
}
