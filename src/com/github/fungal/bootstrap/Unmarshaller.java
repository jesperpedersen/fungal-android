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

package com.github.fungal.bootstrap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Unmarshaller for bootstrap.xml
 * @author <a href="mailto:jesper.pedersen@comcast.net">Jesper Pedersen</a>
 */
public class Unmarshaller
{
   /**
    * Constructor
    */
   public Unmarshaller()
   {
   }

   /**
    * Unmarshal
    * @param url The URL
    * @return The result
    * @exception IOException If an I/O error occurs
    */
   public Bootstrap unmarshal(URL url) throws IOException
   {
      if (url == null)
         throw new IllegalArgumentException("URL is null");

      InputStream is = null;
      try
      {
         Bootstrap bootstrap = new Bootstrap();

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

                  if ("url".equals(parser.getName()))
                  {
                     bootstrap.getUrl().add(readUrl(parser));
                  }
                  else if ("protocols".equals(parser.getName()))
                  {
                     bootstrap.setProtocols(readProtocols(parser));
                  }
                  else if ("servers".equals(parser.getName()))
                  {
                     bootstrap.setServers(readServers(parser));
                  }
                  else if ("dependencies".equals(parser.getName()))
                  {
                     bootstrap.setDependencies(readDependencies(parser));
                  }

                  break;
               default :
            }
         }

         return bootstrap;
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
    * Read: <url>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private String readUrl(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      String result = null;

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               result = parser.getText();
               break;
            default : 
         }
         eventType = parser.next();
      }

      if (!"url".equals(parser.getName()))
         throw new XmlPullParserException("url tag not completed");

      return result;
   }

   /**
    * Read: <protocols>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private ProtocolsType readProtocols(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ProtocolsType result = new ProtocolsType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("protocol".equals(name))
                  result.getProtocol().add(readProtocol(parser));

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"protocols".equals(parser.getName()))
         throw new XmlPullParserException("protocols tag not completed");

      return result;
   }

   /**
    * Read: <protocol>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private ProtocolType readProtocol(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ProtocolType result = new ProtocolType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("id".equals(name))
         {
            result.setId(parser.getAttributeValue(i));
         }
         else if ("class-name".equals(name))
         {
            result.setClassName(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("property".equals(name))
                  result.getProperty().add(readProperty(parser));

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"protocol".equals(parser.getName()))
         throw new XmlPullParserException("protocol tag not completed");

      return result;
   }

   /**
    * Read: <property>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private PropertyType readProperty(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      PropertyType result = new PropertyType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("name".equals(name))
         {
            result.setName(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               if (!parser.getText().trim().equals(""))
                  result.setValue(parser.getText().trim());

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"property".equals(parser.getName()))
         throw new XmlPullParserException("property tag not completed");

      return result;
   }

   /**
    * Read: <servers>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    */
   private ServersType readServers(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ServersType result = new ServersType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("server".equals(name))
                  result.getServer().add(readServer(parser));

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"servers".equals(parser.getName()))
         throw new XmlPullParserException("servers tag not completed");

      return result;
   }

   /**
    * Read: <server>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private ServerType readServer(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ServerType result = new ServerType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("protocol".equals(name))
         {
            result.setProtocol(parser.getAttributeValue(i));
         }
         else if ("pattern".equals(name))
         {
            result.setPattern(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               if (!parser.getText().trim().equals(""))
                  result.setValue(parser.getText().trim());

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"server".equals(parser.getName()))
         throw new XmlPullParserException("server tag not completed");

      return result;
   }

   /**
    * Read: <dependencies>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private DependenciesType readDependencies(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      DependenciesType result = new DependenciesType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("dependency".equals(name))
                  result.getDependency().add(readDependency(parser));

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"dependencies".equals(parser.getName()))
         throw new XmlPullParserException("dependencies tag not completed");

      return result;
   }

   /**
    * Read: <dependency>
    * @param parser The XML stream
    * @return The result
    * @exception XmlPullParserException Thrown if an error occurs
    * @exception IOException If an I/O error occurs
    */
   private DependencyType readDependency(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      DependencyType result = new DependencyType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("target".equals(name))
         {
            result.setTarget(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();
               if ("organisation".equals(name))
               {
                  result.setOrganisation(readString(parser));
               }
               else if ("module".equals(name))
               {
                  result.setModule(readString(parser));
               }
               else if ("artifact".equals(name))
               {
                  result.setArtifact(readString(parser));
               }
               else if ("revision".equals(name))
               {
                  result.setRevision(readString(parser));
               }
               else if ("classifier".equals(name))
               {
                  result.setClassifier(readString(parser));
               }
               else if ("ext".equals(name))
               {
                  result.setExt(readString(parser));
               }

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"dependency".equals(parser.getName()))
         throw new XmlPullParserException("dependency tag not completed");

      return result;
   }

   /**
    * Read a string
    * @param parser The XML stream
    * @return The parameter
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
}
