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

package com.github.fungal.deployment;

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
 * Unmarshaller for a bean deployment XML file
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
   public Deployment unmarshal(URL url) throws IOException
   {
      if (url == null)
         throw new IllegalArgumentException("File is null");

      InputStream is = null;
      try
      {
         Deployment deployment = new Deployment();

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

                  if ("bean".equals(parser.getName()))
                     deployment.getBean().add(readBean(parser));

                  break;
               default :
            }

            eventType = parser.next();
         }

         return deployment;
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
    * Read: <bean>
    * @param parser The XML stream
    * @return The bean
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private BeanType readBean(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      BeanType result = new BeanType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("name".equals(name))
         {
            result.setName(parser.getAttributeValue(i));
         }
         else if ("class".equals(name))
         {
            result.setClazz(parser.getAttributeValue(i));
         }
         else if ("interface".equals(name))
         {
            result.setInterface(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();
               if ("constructor".equals(name))
               {
                  result.setConstructor(readConstructor(parser));
               }
               else if ("property".equals(name))
               {
                  result.getProperty().add(readProperty(parser));
               }
               else if ("depends".equals(name))
               {
                  result.getDepends().add(readDepends(parser));
               }
               else if ("install".equals(name))
               {
                  result.getInstall().add(readInstall(parser));
               }
               else if ("uninstall".equals(name))
               {
                  result.getUninstall().add(readUninstall(parser));
               }
               else if ("incallback".equals(name))
               {
                  result.getIncallback().add(readIncallback(parser));
               }
               else if ("uncallback".equals(name))
               {
                  result.getUncallback().add(readUncallback(parser));
               }
               else if ("ignoreCreate".equals(name))
               {
                  result.setIgnoreCreate(readIgnoreCreate(parser));
               }
               else if ("ignoreStart".equals(name))
               {
                  result.setIgnoreStart(readIgnoreStart(parser));
               }
               else if ("ignoreStop".equals(name))
               {
                  result.setIgnoreStop(readIgnoreStop(parser));
               }
               else if ("ignoreDestroy".equals(name))
               {
                  result.setIgnoreDestroy(readIgnoreDestroy(parser));
               }

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"bean".equals(parser.getName()))
         throw new XmlPullParserException("bean tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <constructor>
    * @param parser The XML stream
    * @return The constructor
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private ConstructorType readConstructor(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ConstructorType result = new ConstructorType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("factoryMethod".equals(name))
         {
            result.setFactoryMethod(parser.getAttributeValue(i));
         }
         else if ("factoryClass".equals(name))
         {
            result.setFactoryClass(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("parameter".equals(name))
               {
                  result.getParameter().add(readParameter(parser));
               }
               else if ("factory".equals(name))
               {
                  result.setFactory(readFactory(parser));
               }

               break;
            default :
         }

         eventType = parser.next();
      }

      if (!"constructor".equals(parser.getName()))
         throw new XmlPullParserException("constructor tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <parameter>
    * @param parser The XML stream
    * @return The parameter
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private ParameterType readParameter(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ParameterType result = new ParameterType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("class".equals(name))
         {
            result.setClazz(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("inject".equals(name))
               {
                  result.getContent().add(readInject(parser));
               }
               else if ("null".equals(name))
               {
                  result.getContent().add(readNull(parser));
               }

               break;

            case XmlPullParser.TEXT :
               if (!parser.getText().trim().equals(""))
                  result.getContent().add(parser.getText());

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"parameter".equals(parser.getName()))
         throw new XmlPullParserException("parameter tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <property>
    * @param parser The XML stream
    * @return The property
    * @exception XmlPullParserException Thrown if an exception occurs
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
         else if ("class".equals(name))
         {
            result.setClazz(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("inject".equals(name))
               {
                  result.getContent().add(readInject(parser));
               }
               else if ("set".equals(name))
               {
                  result.getContent().add(readSet(parser));
               }
               else if ("map".equals(name))
               {
                  result.getContent().add(readMap(parser));
               }
               else if ("list".equals(name))
               {
                  result.getContent().add(readList(parser));
               }
               else if ("null".equals(name))
               {
                  result.getContent().add(readNull(parser));
               }
               else if ("this".equals(name))
               {
                  result.getContent().add(readThis(parser));
               }

               break;

            case XmlPullParser.TEXT :
               if (!parser.getText().trim().equals(""))
                  result.getContent().add(parser.getText());

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"property".equals(parser.getName()))
         throw new XmlPullParserException("property tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <inject>
    * @param parser The XML stream
    * @return The inject
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private InjectType readInject(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      InjectType result = new InjectType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("bean".equals(name))
         {
            result.setBean(parser.getAttributeValue(i));
         }
         else if ("property".equals(name))
         {
            result.setProperty(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               result.setValue(parser.getText());
               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"inject".equals(parser.getName()))
         throw new XmlPullParserException("inject tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <depends>
    * @param parser The XML stream
    * @return The depends
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private DependsType readDepends(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      DependsType result = new DependsType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               result.setValue(parser.getText());
               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"depends".equals(parser.getName()))
         throw new XmlPullParserException("depends tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <install>
    * @param parser The XML stream
    * @return The install
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private InstallType readInstall(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      InstallType result = new InstallType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("method".equals(name))
         {
            result.setMethod(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"install".equals(parser.getName()))
         throw new XmlPullParserException("install tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <uninstall>
    * @param parser The XML stream
    * @return The install
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private UninstallType readUninstall(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      UninstallType result = new UninstallType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("method".equals(name))
         {
            result.setMethod(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"uninstall".equals(parser.getName()))
         throw new XmlPullParserException("uninstall tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <incallback>
    * @param parser The XML stream
    * @return The incallback
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private IncallbackType readIncallback(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      IncallbackType result = new IncallbackType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("method".equals(name))
         {
            result.setMethod(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"incallback".equals(parser.getName()))
         throw new XmlPullParserException("incallback tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <uncallback>
    * @param parser The XML stream
    * @return The uncallback
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private UncallbackType readUncallback(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      UncallbackType result = new UncallbackType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("method".equals(name))
         {
            result.setMethod(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"uncallback".equals(parser.getName()))
         throw new XmlPullParserException("uncallback tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <map>
    * @param parser The XML stream
    * @return The map
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private MapType readMap(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      MapType result = new MapType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("keyClass".equals(name))
         {
            result.setKeyClass(parser.getAttributeValue(i));
         }
         else if ("valueClass".equals(name))
         {
            result.setValueClass(parser.getAttributeValue(i));
         }
         else if ("class".equals(name))
         {
            result.setClazz(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("entry".equals(name))
                  result.getEntry().add(readEntry(parser));

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"map".equals(parser.getName()))
         throw new XmlPullParserException("map tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <set>
    * @param parser The XML stream
    * @return The set
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private SetType readSet(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      SetType result = new SetType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("elementClass".equals(name))
         {
            result.setElementClass(parser.getAttributeValue(i));
         }
         else if ("class".equals(name))
         {
            result.setClazz(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("value".equals(name))
                  result.getValue().add(readValue(parser));

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"set".equals(parser.getName()))
         throw new XmlPullParserException("set tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <list>
    * @param parser The XML stream
    * @return The list
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private ListType readList(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ListType result = new ListType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("elementClass".equals(name))
         {
            result.setElementClass(parser.getAttributeValue(i));
         }
         else if ("class".equals(name))
         {
            result.setClazz(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("value".equals(name))
                  result.getValue().add(readValue(parser));

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"list".equals(parser.getName()))
         throw new XmlPullParserException("list tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <entry>
    * @param parser The XML stream
    * @return The entry
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private EntryType readEntry(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      EntryType result = new EntryType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.START_TAG :
               String name = parser.getName();

               if ("key".equals(name))
               {
                  result.setKey(readKey(parser));
               }
               else if ("value".equals(name))
               {
                  result.setValue(readValue(parser));
               }

               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"entry".equals(parser.getName()))
         throw new XmlPullParserException("entry tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <key>
    * @param parser The XML stream
    * @return The key
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private KeyType readKey(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      KeyType result = new KeyType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               result.setValue(parser.getText());
               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"key".equals(parser.getName()))
         throw new XmlPullParserException("key tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <value>
    * @param parser The XML stream
    * @return The value
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private ValueType readValue(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ValueType result = new ValueType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         switch (eventType)
         {
            case XmlPullParser.TEXT :
               result.setValue(parser.getText());
               break;

            default :
         }

         eventType = parser.next();
      }

      if (!"value".equals(parser.getName()))
         throw new XmlPullParserException("value tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <null>
    * @param parser The XML stream
    * @return The null
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private NullType readNull(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      NullType result = new NullType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"null".equals(parser.getName()))
         throw new XmlPullParserException("null tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <this>
    * @param parser The XML stream
    * @return The this
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private ThisType readThis(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      ThisType result = new ThisType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"this".equals(parser.getName()))
         throw new XmlPullParserException("this tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <factory>
    * @param parser The XML stream
    * @return The factory
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private FactoryType readFactory(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      FactoryType result = new FactoryType();

      for (int i = 0; i < parser.getAttributeCount(); i++)
      {
         String name = parser.getAttributeName(i);
         if ("bean".equals(name))
         {
            result.setBean(parser.getAttributeValue(i));
         }
      }

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"factory".equals(parser.getName()))
         throw new XmlPullParserException("factory tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <ignoreCreate>
    * @param parser The XML stream
    * @return The ignoreCreate
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private IgnoreCreateType readIgnoreCreate(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      IgnoreCreateType result = new IgnoreCreateType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"ignoreCreate".equals(parser.getName()))
         throw new XmlPullParserException("ignoreCreate tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <ignoreStart>
    * @param parser The XML stream
    * @return The ignoreStart
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private IgnoreStartType readIgnoreStart(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      IgnoreStartType result = new IgnoreStartType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"ignoreStart".equals(parser.getName()))
         throw new XmlPullParserException("ignoreStart tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <ignoreStop>
    * @param parser The XML stream
    * @return The ignoreStop
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private IgnoreStopType readIgnoreStop(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      IgnoreStopType result = new IgnoreStopType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"ignoreStop".equals(parser.getName()))
         throw new XmlPullParserException("ignoreStop tag not completed" + parser.getPositionDescription());

      return result;
   }

   /**
    * Read: <ignoreDestroy>
    * @param parser The XML stream
    * @return The ignoreDestroy
    * @exception XmlPullParserException Thrown if an exception occurs
    * @exception IOException If an I/O error occurs
    */
   private IgnoreDestroyType readIgnoreDestroy(XmlPullParser parser) throws XmlPullParserException, IOException
   {
      IgnoreDestroyType result = new IgnoreDestroyType();

      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_TAG)
      {
         eventType = parser.next();
      }

      if (!"ignoreDestroy".equals(parser.getName()))
         throw new XmlPullParserException("ignoreDestroy tag not completed" + parser.getPositionDescription());

      return result;
   }
}
