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
package com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim;

public interface EpiCfgScanner_Events {

		/**
		 * This event style reports summary data about any objects and attributes that the scanner
		 * monitors. The event is triggered whenever data values change and the variable message
		 * format (type/length/value) is used when reporting data that changed.
		 */
		public void Unbuf_Scan_Report_Var();

		/**
		 * This event style is used whenever data values change and the fixed message format of
		 * each object is used to report data that changed.
		 */
		public void Unbuf_Scan_Report_Fixed();

		/**
		 * This style is used when the scanner object is used to send the data in its most compact
		 * format. The Handle-Attr-Val-Map attribute describes the objects and attributes that are
		 * included and the format of the message.
		 */
		public void Unbuf_Scan_Report_Grouped();

		/**
		 * This is the same as Unbuf-Scan-Report-Var, but allows inclusion of data from multiple
		 * persons.
		 */
		public void Unbuf_Scan_Report_MP_Var();

		/**
		 * This is the same as Unbuf-Scan-Report-Fixed, but allows inclusion of data from multiple
		 * persons.
		 */
		public void Unbuf_Scan_Report_MP_Fixed();

		/**
		 * This is the same as Unbuf-Scan-Report-Grouped, but allows inclusion of data from multiple
		 *  persons.
		 */
		public void Unbuf_Scan_Report_MP_Grouped();
}
