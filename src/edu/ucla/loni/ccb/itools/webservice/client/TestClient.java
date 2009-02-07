package edu.ucla.loni.ccb.itools.webservice.client;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import edu.ucla.loni.ccb.itools.webservice.ResourcesFinder;

import javax.xml.rpc.ParameterMode;

public class TestClient
{
   public static void main(String [] args) {
       try {

           String endpoint = 
                    "http://localhost:8080/itools/webservices/resourcesFinder";
     
           Service  service = new Service();
           Call     call    = (Call) service.createCall();

           call.setTargetEndpointAddress( new java.net.URL(endpoint) );
           call.setOperationName("findResources");
           call.addParameter( "reg", XMLType.XSD_STRING, ParameterMode.IN);
           call.addParameter( "category", XMLType.XSD_STRING, ParameterMode.IN);
           call.setReturnType(org.apache.axis.Constants.XSD_STRING);

           // Call to addParameter/setReturnType as described in user-guide.html
           //call.addParameter("testParam",
           //                  org.apache.axis.Constants.XSD_STRING,
           //                  javax.xml.rpc.ParameterMode.IN);
           

           String ret = (String) call.invoke( new Object[] { "Pipe", "Name" } );

           System.out.println("got '" + ret + "'");
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}

