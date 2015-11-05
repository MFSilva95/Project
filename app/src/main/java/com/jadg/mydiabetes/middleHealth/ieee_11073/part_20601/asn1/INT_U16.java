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

package com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net
// Any modifications to this file will be lost upon recompilation of the source ASN.1.
//

import com.jadg.mydiabetes.middleHealth.org.bn.*;
import com.jadg.mydiabetes.middleHealth.org.bn.annotations.*;
import com.jadg.mydiabetes.middleHealth.org.bn.annotations.constraints.*;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.*;


@ASN1PreparedElement
    @ASN1BoxedType ( name = "INT_U16" )
    public class INT_U16 implements IASN1PreparedElement {

            @ASN1Integer( name = "INT-U16" )
            @ASN1ValueRangeConstraint (

		min = 0L,

		max = 65535L

	   )

            private Integer value;

            public INT_U16() {
            }

            public INT_U16(Integer value) {
                this.value = value;
            }

            public void setValue(Integer value) {
                this.value = value;
            }

            public Integer getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(INT_U16.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
