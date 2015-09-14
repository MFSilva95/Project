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
package middleHealth.es.libresoft.openhealth.events;

public interface EventType {

	/* A condition asserted by a low layer of software though a well-defined API							*/
	public static final int IND_TRANS_CONN						=	1;		/* Transport Connection			*/
	public static final int IND_TRANS_DESC						=	2;		/* Transport Desconnection		*/
	public static final int IND_TIMEOUT							=	3;		/* Timeout						*/

	/* A Request from the application software interfacing with the state machine							*/
	public static final int REQ_ASSOC_REL						= 	4;		/* Association release request	*/
	public static final int REQ_ASSOC_ABORT						= 	5;		/* Association abort request	*/


	/* Application defined events*/
	public static final int REQ_GET_PM_STORE					=	500;
	public static final int REQ_SET								=	501;

}
