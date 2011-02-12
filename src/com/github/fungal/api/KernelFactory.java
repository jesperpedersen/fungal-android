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

package com.github.fungal.api;

import com.github.fungal.api.configuration.KernelConfiguration;

import java.lang.reflect.Constructor;

/**
 * The kernel factory
 * @author <a href="mailto:jesper.pedersen@comcast.net">Jesper Pedersen</a>
 * @see com.github.fungal.api.Kernel
 * @see com.github.fungal.api.configuration.KernelConfiguration
 */
public final class KernelFactory
{
   /** Kernel implementation */
   private static final String KERNEL_IMPL = "com.github.fungal.impl.KernelImpl";

   /**
    * Constructor
    */
   private KernelFactory()
   {
   }

   /**
    * Create a kernel instance
    * @param kc The kernel configuration
    * @return The kernel
    * @exception Exception Thrown if an error occurs
    */
   @SuppressWarnings("unchecked") 
   public static synchronized Kernel create(KernelConfiguration kc) throws Exception
   {
      if (kc == null)
         throw new IllegalArgumentException("KernelConfiguration is null");

      Class<?> clz = Class.forName(KERNEL_IMPL);
      Constructor<?> c = clz.getConstructor(KernelConfiguration.class);

      return (Kernel)c.newInstance(kc);
   }
}
