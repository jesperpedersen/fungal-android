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

import java.net.URL;
import java.util.Set;

/**
 * Non export class loader
 * @author <a href="mailto:jesper.pedersen@comcast.net">Jesper Pedersen</a>
 */
class NonExportClassLoader extends KernelClassLoader
{
   /** The repository */
   private ExportClassLoaderRepository eclr;

   /**
    * Constructor
    * @param eclr The repository
    */
   NonExportClassLoader(ExportClassLoaderRepository eclr)
   {
      this(new URL[0], ClassLoader.getSystemClassLoader(), eclr);
   }

   /**
    * Constructor
    * @param urls The URLs
    * @param cl The parent class loader
    * @param eclr The repository
    */
   private NonExportClassLoader(URL[] urls, ClassLoader cl, ExportClassLoaderRepository eclr)
   {
      super(urls, cl);

      if (eclr == null)
         throw new IllegalArgumentException("ECLR is null");

      this.eclr = eclr;
   }

   /**
    * Load a class
    * @param name The fully qualified class name
    * @return The class
    * @throws ClassNotFoundException If the class could not be found 
    */
   @Override
   public Class<?> loadClass(String name) throws ClassNotFoundException
   {
      // Don't call super.loadClass(String) as it is done in ExportClassLoader

      try
      {
         return loadClass(name, false);
      }
      catch (ClassNotFoundException cnfe)
      {
         Set<Integer> classLoaders = eclr.getClassLoaders();

         for (Integer id : classLoaders)
         {
            try
            {
               ArchiveClassLoader acl = eclr.getClassLoader(id);
               return acl.loadClass(name);
            }
            catch (ClassNotFoundException ignore)
            {
               // Ignore
            }
         }

         throw cnfe;
      }
   }

   /**
    * Lookup a class
    * @param name The fully qualified class name
    * @return The class
    * @throws ClassNotFoundException If the class could not be found 
    */
   public Class<?> lookup(String name) throws ClassNotFoundException
   {
      try
      {
         return loadClass(name, false);
      }
      catch (ClassNotFoundException cnfe)
      {
         Set<Integer> cls = eclr.getClassLoaders(name);

         if (cls != null)
         {
            for (Integer id : cls)
            {
               try
               {
                  ArchiveClassLoader acl = eclr.getClassLoader(id);
                  return acl.loadClass(name);
               }
               catch (ClassNotFoundException ignore)
               {
                  // Ignore
               }
            }
         }

         throw cnfe;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> findClass(String name) throws ClassNotFoundException
   {
      try
      {
         return super.findClass(name);
      }
      catch (Throwable t)
      {
         Set<Integer> classLoaders = eclr.getClassLoaders();

         for (Integer id : classLoaders)
         {
            ArchiveClassLoader acl = eclr.getClassLoader(id);

            if (acl != null)
            {
               try
               {
                  return acl.findClass(name, false);
               }
               catch (ClassNotFoundException ignore)
               {
                  // Ignore
               }
            }
         }
      }

      throw new ClassNotFoundException("Unable to load class: " + name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void addURL(URL url)
   {
      super.addURL(url);
   }

   /**
    * String representation
    * @return The string
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      sb.append("NonExportClassLoader@").append(Integer.toHexString(System.identityHashCode(this)));
      sb.append("[Eclr=").append(Integer.toHexString(System.identityHashCode(eclr)));
      sb.append("]");

      return sb.toString();
   }
}
