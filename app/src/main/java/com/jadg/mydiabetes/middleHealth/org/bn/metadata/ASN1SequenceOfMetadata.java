/*
 * Copyright 2006 Abdulla G. Abdurakhmanov (abdulla.abdurakhmanov@gmail.com).
 *
 * Licensed under the LGPL, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/copyleft/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * With any your questions welcome to my e-mail
 * or blog at http://abdulla-a.blogspot.com.
 */

package com.jadg.mydiabetes.middleHealth.org.bn.metadata;

import com.jadg.mydiabetes.middleHealth.org.bn.annotations.ASN1SequenceOf;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.ASN1PreparedElementData;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.CoderUtils;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.DecodedObject;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.ElementInfo;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.IASN1TypesDecoder;
import com.jadg.mydiabetes.middleHealth.org.bn.coders.IASN1TypesEncoder;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class ASN1SequenceOfMetadata extends ASN1FieldMetadata
{
    private boolean isSetOf = false;
    private Type itemClass;
    private ASN1PreparedElementData itemClassMetadata;

    public ASN1SequenceOfMetadata(String name, boolean isSetOf, Type itemClass, AnnotatedElement seqFieldAnnotatedElem)
    {
        super(name);
        this.isSetOf = isSetOf;
        this.itemClass = itemClass;
        //ParameterizedType tp = (ParameterizedType)itemClass;
        //Class paramType = (Class)tp.getActualTypeArguments()[0];
	Class paramType = CoderUtils.getCollectionType((ParameterizedType)itemClass);

        this.itemClassMetadata = new ASN1PreparedElementData(paramType);
        if(this.itemClassMetadata.getTypeMetadata()!=null)
            this.itemClassMetadata.getTypeMetadata().setParentAnnotated(seqFieldAnnotatedElem);
    }

    public ASN1SequenceOfMetadata(ASN1SequenceOf annotation, Type itemClass, AnnotatedElement seqFieldAnnotatedElem) {
        this(annotation.name(), annotation.isSetOf(), itemClass, seqFieldAnnotatedElem);
    }

    public boolean isSetOf() {
        return this.isSetOf;
    }

    public ASN1PreparedElementData getItemClassMetadata() {
        return itemClassMetadata;
    }

    public int encode(IASN1TypesEncoder encoder, Object object, OutputStream stream,
               ElementInfo elementInfo) throws Exception {
        return encoder.encodeSequenceOf(object, stream, elementInfo);
    }

    public DecodedObject decode(IASN1TypesDecoder decoder, DecodedObject decodedTag, Class objectClass, ElementInfo elementInfo, InputStream stream) throws Exception {
        return decoder.decodeSequenceOf(decodedTag,objectClass,elementInfo,stream);
    }



}
