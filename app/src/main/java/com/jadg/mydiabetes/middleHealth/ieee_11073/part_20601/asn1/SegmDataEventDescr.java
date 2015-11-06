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
    @ASN1Sequence ( name = "SegmDataEventDescr", isSet = false )
    public class SegmDataEventDescr implements IASN1PreparedElement {

        @ASN1Element ( name = "segm-instance", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private InstNumber segm_instance = null;


        @ASN1Element ( name = "segm-evt-entry-index", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private INT_U32 segm_evt_entry_index = null;


        @ASN1Element ( name = "segm-evt-entry-count", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 2  )

	private INT_U32 segm_evt_entry_count = null;


        @ASN1Element ( name = "segm-evt-status", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 3  )

	private SegmEvtStatus segm_evt_status = null;



        public InstNumber getSegm_instance () {
            return this.segm_instance;
        }



        public void setSegm_instance (InstNumber value) {
            this.segm_instance = value;
        }



        public INT_U32 getSegm_evt_entry_index () {
            return this.segm_evt_entry_index;
        }



        public void setSegm_evt_entry_index (INT_U32 value) {
            this.segm_evt_entry_index = value;
        }



        public INT_U32 getSegm_evt_entry_count () {
            return this.segm_evt_entry_count;
        }



        public void setSegm_evt_entry_count (INT_U32 value) {
            this.segm_evt_entry_count = value;
        }



        public SegmEvtStatus getSegm_evt_status () {
            return this.segm_evt_status;
        }



        public void setSegm_evt_status (SegmEvtStatus value) {
            this.segm_evt_status = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmDataEventDescr.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
