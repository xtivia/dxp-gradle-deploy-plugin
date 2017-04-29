/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.speedray.gogo.deploy

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Jar

import java.util.jar.Attributes
import java.util.jar.Manifest;

/**
 * Created by 91040 on 2/5/2017.
 */
class Bundle {
    boolean isFragment = false;
    boolean isSnapshot = false;
    String fragmentHost = null;
    String bsn = null;
    String hostBSN = null;
    Jar bundle = null

    Bundle(File jarFile) {
        bundle = new Jar(jarFile)

        try {
            Manifest manifest = bundle.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();

            fragmentHost = mainAttributes.getValue("Fragment-Host");

            isFragment = fragmentHost != null;

            bsn = bundle.getBsn();

            if(isFragment) {
                hostBSN = new Parameters(fragmentHost).keySet().iterator().next();
            }
        } finally {

        }
    }
}
