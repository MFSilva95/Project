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

import com.jadg.mydiabetes.middleHealth.org.bn.CoderFactory;
import com.jadg.mydiabetes.middleHealth.org.bn.annotations.ASN1Element;
import com.jadg.mydiabetes.middleHealth.org.bn.annotations.ASN1PreparedElement;
import com.jadg.mydiabetes.middleHealth.org.bn.annotations.ASN1Sequence;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElement;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElementData;


@ASN1PreparedElement
    @ASN1Sequence ( name = "AuthBodyAndStrucType", isSet = false )
    public class AuthBodyAndStrucType implements IASN1PreparedElement {

        @ASN1Element ( name = "auth-body", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private AuthBody auth_body = null;


        @ASN1Element ( name = "auth-body-struc-type", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private AuthBodyStrucType auth_body_struc_type = null;



        public AuthBody getAuth_body () {
            return this.auth_body;
        }



        public void setAuth_body (AuthBody value) {
            this.auth_body = value;
        }



        public AuthBodyStrucType getAuth_body_struc_type () {
            return this.auth_body_struc_type;
        }



        public void setAuth_body_struc_type (AuthBodyStrucType value) {
            this.auth_body_struc_type = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AuthBodyAndStrucType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
