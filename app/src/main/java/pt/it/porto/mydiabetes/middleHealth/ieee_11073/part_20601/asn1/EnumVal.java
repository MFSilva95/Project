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

package pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_20601.asn1;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net
// Any modifications to this file will be lost upon recompilation of the source ASN.1.
//

import pt.it.porto.mydiabetes.middleHealth.org.bn.CoderFactory;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1BitString;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Choice;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Element;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1OctetString;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1PreparedElement;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.constraints.ASN1SizeConstraint;
import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElement;
import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElementData;
import pt.it.porto.mydiabetes.middleHealth.org.bn.types.BitString;




    @ASN1PreparedElement
    @ASN1Choice ( name = "EnumVal" )
    public class EnumVal implements IASN1PreparedElement {

        @ASN1Element ( name = "enum-obj-id", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private OID_Type enum_obj_id = null;

  @ASN1OctetString( name = "" )

        @ASN1Element ( name = "enum-text-string", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private byte[] enum_text_string = null;

  @ASN1BitString( name = "" )

            @ASN1SizeConstraint ( max = 16L )

        @ASN1Element ( name = "enum-bit-str", isOptional =  false , hasTag =  true, tag = 16 , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 2  )

	private BitString enum_bit_str = null;



        public OID_Type getEnum_obj_id () {
            return this.enum_obj_id;
        }

        public boolean isEnum_obj_idSelected () {
            return this.enum_obj_id != null;
        }

        private void setEnum_obj_id (OID_Type value) {
            this.enum_obj_id = value;
        }


        public void selectEnum_obj_id (OID_Type value) {
            this.enum_obj_id = value;

                    //setEnum_obj_id(null);

                    setEnum_text_string(null);

                    setEnum_bit_str(null);

        }




        public byte[] getEnum_text_string () {
            return this.enum_text_string;
        }

        public boolean isEnum_text_stringSelected () {
            return this.enum_text_string != null;
        }

        private void setEnum_text_string (byte[] value) {
            this.enum_text_string = value;
        }


        public void selectEnum_text_string (byte[] value) {
            this.enum_text_string = value;

                    setEnum_obj_id(null);

                    //setEnum_text_string(null);

                    setEnum_bit_str(null);

        }




        public BitString getEnum_bit_str () {
            return this.enum_bit_str;
        }

        public boolean isEnum_bit_strSelected () {
            return this.enum_bit_str != null;
        }

        private void setEnum_bit_str (BitString value) {
            this.enum_bit_str = value;
        }


        public void selectEnum_bit_str (BitString value) {
            this.enum_bit_str = value;

                    setEnum_obj_id(null);

                    setEnum_text_string(null);

                    //setEnum_bit_str(null);

        }




	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EnumVal.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
