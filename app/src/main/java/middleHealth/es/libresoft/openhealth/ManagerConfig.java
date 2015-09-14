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
package middleHealth.es.libresoft.openhealth;

public interface ManagerConfig {

	public static byte[] HEX_80000000 = {(byte)128, (byte)0, (byte)0, (byte)0};

	/* The unique system ID of the manager (EUI-64) Copied from CESL
	 * TODO: Change it for our (EUI-64)
	 */
	public static byte[] Manager_Id = {(byte)76, (byte)78, (byte)73, (byte)95, (byte)77, (byte)71, (byte)82, (byte)48};

	/* The version of the association procedure supported by the manager */
	public static byte[] assoc_version = HEX_80000000; /*0x80000000 (assoc-version1=0)*/

	/* Version of the protocol supported by the manager */
	public static byte[] protocol_version = HEX_80000000; /*0x80000000 (protocol-version1=0) */

	/* Nomenclature supported by the manager*/
	public static byte[] nomenclature_version = HEX_80000000; /*0x80000000 (version=0) */

	/* Supported encoding rules */
	public static byte[] enc_mder = {(byte)128, (byte)0}; /*0x8000*/
	//public byte[] enc_xer = {(byte)64, (byte)0}; /*0x4000*/
	public static byte[] enc_per = {(byte)32, (byte)0}; /*0x2000*/

	/* System type */
	public static byte[] syste_type = HEX_80000000; /*0x80000000 (manager=0) */
}
