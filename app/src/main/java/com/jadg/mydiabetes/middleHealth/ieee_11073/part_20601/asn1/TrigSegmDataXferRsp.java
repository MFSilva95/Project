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
import com.jadg.mydiabetes.middleHealth.org.bn.coders.*;


@ASN1PreparedElement
    @ASN1Sequence ( name = "TrigSegmDataXferRsp", isSet = false )
    public class TrigSegmDataXferRsp implements IASN1PreparedElement {

        @ASN1Element ( name = "seg-inst-no", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private InstNumber seg_inst_no = null;


        @ASN1Element ( name = "trig-segm-xfer-rsp", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private TrigSegmXferRsp trig_segm_xfer_rsp = null;



        public InstNumber getSeg_inst_no () {
            return this.seg_inst_no;
        }



        public void setSeg_inst_no (InstNumber value) {
            this.seg_inst_no = value;
        }



        public TrigSegmXferRsp getTrig_segm_xfer_rsp () {
            return this.trig_segm_xfer_rsp;
        }



        public void setTrig_segm_xfer_rsp (TrigSegmXferRsp value) {
            this.trig_segm_xfer_rsp = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TrigSegmDataXferRsp.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
