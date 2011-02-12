/*
 * The Fungal kernel project
 * Copyright (C) 2011
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.github.fungal.impl.netboot;

import com.github.fungal.bootstrap.DependencyType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Unmarshaller for a Maven POM XML file
 * @author <a href="mailto:jesper.pedersen@comcast.net">Jesper Pedersen</a>
 */
public class MavenUnmarshaller
{
   /**
    * Constructor
    */
   public MavenUnmarshaller()
   {
   }

   /**
    * Unmarshal
    * @param url The URL
    * @return The result
    * @exception IOException If an I/O error occurs
    */
   public List<DependencyType> unmarshal(URL url) throws IOException
   {
      if (url == null)
         throw new IllegalArgumentException("File is null");

      InputStream is = null;
      try
      {
         List<DependencyType> result = new ArrayList<DependencyType>(1);

         if ("file".equals(url.getProtocol()))
         {
            File file = new File(url.toURI());
            is = new FileInputStream(file);
         }
         else if ("jar".equals(url.getProtocol()))
         {
            JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
            is = jarConnection.getInputStream();
         }
         else
         {
            throw new IOException("Unsupport protocol: " + url);
         }

         is = new BufferedInputStream(is, 4096);

         XmlPullParser parser = Xml.newPullParser();
         parser.setInput(is, null);

         int eventType = parser.getEventType();
         while (eventType != XmlPullParser.END_DOCUMENT)
         {
            switch (eventType)
            {
               case XmlPullParser.START_TAG :

                  if ("dependency".equals(parser.getName()))
                     result.add(readDependency(parser));

                  break;
               default :
            }
         }

         return result;
      }
      catch (Throwable t)
      {
         throw new IOException(t.getMessage(), t);
      }
      finally
      {
         try
         {
            if (is != null)
               is.close();
         }
         catch (IOException ioe)
         {
            // Ignore
         }
      }
   }

   /**
    * Read: <dependency>
    * @param parser The XML stream
    * @return The dependency
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private DependencyType readDependency(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      DependencyType result = new DependencyType();

      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();
               if ("groupId".equals(name))
               {
                  result.setOrganisation(readString(parser));
               }
               else if ("artifactId".equals(name))
               {
                  result.setArtifact(readString(parser));
               }
               else if ("version".equals(name))
               {
                  result.setRevision(readString(parser));
               }
               else if ("type".equals(name))
               {
                  result.setExt(readString(parser));
               }
               else
               {
                  ignoreTag(parser);
               }

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"dependency".equals(parser.getName()))
         throw new XmlPullParserException("dependency tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read a string
    * @param parser The XML stream
    * @return The value
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private String readString(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      String result = null;

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               if (!parser.getText().trim().equals(""))
                  result = parser.getText().trim();

               break;

            default :
         }

         eventType = parser.next();
      }

      return result;
   }

   /**
    * Ignore a tag
    * @param parser The XML stream
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private void ignoreTag(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }
   }
}
