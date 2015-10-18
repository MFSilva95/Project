/*
Copyright (C) 2010 GSyC/LibreSoft, Universidad Rey Juan Carlos.

Author: Jose Antonio Santos Cadenas <jcaden@libresoft.es>

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

package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Scanner implements Parcelable {
	private int handler;
	private String systId;

	public static final Creator<Scanner> CREATOR =
		new Creator<Scanner>() {
			public Scanner createFromParcel(Parcel in) {
				return new Scanner(in);
			}

			public Scanner[] newArray(int size) {
				return new Scanner[size];
			}
		};

	protected Scanner (Parcel in) {
		handler = in.readInt();
		systId = in.readString();
	}

	public Scanner(int pmHandler, String systemId) {
		handler = pmHandler;
		systId = systemId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(handler);
		dest.writeString(systId);
	}

	public int getHandler() {
		return handler;
	}

	public String getSystemId() {
		return systId;
	}

}
