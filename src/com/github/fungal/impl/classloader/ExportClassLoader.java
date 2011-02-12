/*
 * The Fungal kernel project
 * Copyright (C) 2010
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

package com.github.fungal.impl.classloader;

import com.github.fungal.api.classloading.KernelClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Export class loader (OSGi like)
 * @author <a href="mailto:jesper.pedersen@comcast.net">Jesper Pedersen</a>
 */
public class ExportClassLoader extends KernelClassLoader
{
   /** Export class loader repository */
   private ExportClassLoaderRepository eclr;

   /** Class Loaders */
   private Set<Integer> classLoaders;

   /**
    * Constructor
    * @param urls The URLs for JAR archives or directories
    * @param parent The parent class loader
    */
   public ExportClassLoader(URL[] urls, ClassLoader parent)
   {
      super(new URL[0], parent);

      this.eclr = new ExportClassLoaderRepository();

      if (urls != null)
      {
         classLoaders = eclr.register(urls);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> loadClass(String name) throws ClassNotFoundException
   {
      Class<?> result = super.loadClass(name);

      if (result != null)
         return result;

      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               try
               {
                  result = acl.loadClass(name);

                  if (result != null)
                     return result;
               }
               catch (ClassNotFoundException cnfe)
               {
                  // Ignore
               }
            }
         }
      }

      try
      {
         result = eclr.getNonExportClassLoader().loadClass(name);

         if (result != null)
            return result;
      }
      catch (ClassNotFoundException cnfe)
      {
         // Ignore
      }

      return loadClass(name, false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URL getResource(String name)
   {
      URL resource = null;

      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               resource = acl.getResource(name);

               if (resource != null)
                  return resource;
            }
         }
      }

      resource = eclr.getNonExportClassLoader().getResource(name);

      if (resource != null)
         return resource;

      return super.getResource(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getResourceAsStream(String name)
   {
      InputStream is = null;

      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               is = acl.getResourceAsStream(name);

               if (is != null)
                  return is;
            }
         }
      }

      is = eclr.getNonExportClassLoader().getResourceAsStream(name);

      if (is != null)
         return is;

      return super.getResourceAsStream(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Enumeration<URL> getResources(String name)
      throws IOException
   {
      Vector<URL> v = new Vector<URL>();
      Enumeration<URL> e = null;

      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               e = acl.getResources(name);

               if (e != null)
               {
                  while (e.hasMoreElements())
                  {
                     v.add(e.nextElement());
                  }
               }
            }
         }
      }

      e = eclr.getNonExportClassLoader().getResources(name);

      if (e != null)
      {
         while (e.hasMoreElements())
         {
            v.add(e.nextElement());
         }
      }

      e = super.getResources(name);

      if (e != null)
      {
         while (e.hasMoreElements())
         {
            v.add(e.nextElement());
         }
      }

      return v.elements();
   }

   /**
    * {@inheritDoc}
    */
   @Override 
   public void clearAssertionStatus()
   {
      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               acl.clearAssertionStatus();
            }
         }
      }

      eclr.getNonExportClassLoader().clearAssertionStatus();

      super.clearAssertionStatus();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setClassAssertionStatus(String className, boolean enabled)
   {
      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               acl.setClassAssertionStatus(className, enabled);
            }
         }
      }

      eclr.getNonExportClassLoader().setClassAssertionStatus(className, enabled);

      super.setClassAssertionStatus(className, enabled);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void setDefaultAssertionStatus(boolean enabled)
   {
      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               acl.setDefaultAssertionStatus(enabled);
            }
         }
      }

      eclr.getNonExportClassLoader().setDefaultAssertionStatus(enabled);

      super.setDefaultAssertionStatus(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPackageAssertionStatus(String packageName, boolean enabled)
   {
      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               acl.setPackageAssertionStatus(packageName, enabled);
            }
         }
      }

      eclr.getNonExportClassLoader().setPackageAssertionStatus(packageName, enabled);

      super.setPackageAssertionStatus(packageName, enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URL[] getURLs()
   {
      List<URL> result = null;
      URL[] urls = null;

      if (classLoaders != null)
      {
         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               urls = acl.getURLs();

               if (urls != null)
               {
                  result = new ArrayList<URL>(urls.length);
                  for (URL u : urls)
                  {
                     result.add(u);
                  }
               }
            }
         }
      }

      urls = eclr.getNonExportClassLoader().getURLs();

      if (urls != null)
      {
         if (result == null)
            result = new ArrayList<URL>(urls.length);

         for (URL u : urls)
         {
            result.add(u);
         }
      }

      urls = super.getURLs();

      if (urls != null)
      {
         if (result == null)
            result = new ArrayList<URL>(urls.length);

         for (URL u : urls)
         {
            result.add(u);
         }
      }

      if (result == null)
         return new URL[0];

      return result.toArray(new URL[result.size()]);
   }

   /**
    * String representation
    * @return The string
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      sb.append("ExportClassLoader@").append(Integer.toHexString(System.identityHashCode(this)));
      sb.append("[ExportClassLoaderRepository=").append(eclr);
      sb.append(", ClassLoaders=").append(classLoaders);
      sb.append("]");

      return sb.toString();
   }
}
