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

package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils;

import com.jadg.mydiabetes.middleHealth.org.bn.*;
import com.jadg.mydiabetes.middleHealth.org.bn.annotations.*;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.*;
import com.jadg.mydiabetes.middleHealth.org.bn.types.*;

/* Wrapper class to encode/decode pure variable OctectString structures in Binary Notes */


    @ASN1PreparedElement
    @ASN1BoxedType ( name = "PrstApdu" )
    public class OctetStringASN1 implements IASN1PreparedElement {

            @ASN1OctetString( name = "PrstApdu" )

            private byte[] value = null;

            public OctetStringASN1() {
            }

            public OctetStringASN1(byte[] value) {
                this.value = value;
            }

            public OctetStringASN1(BitString value) {
                setValue(value);
            }

            public void setValue(byte[] value) {
                this.value = value;
            }

            public void setValue(BitString btStr) {
                this.value = btStr.getValue();
            }

            public byte[] getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(OctetStringASN1.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
