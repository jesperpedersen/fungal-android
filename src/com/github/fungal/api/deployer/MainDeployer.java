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

package com.github.fungal.api.deployer;

import com.github.fungal.spi.deployers.Deployment;

import java.net.URL;

/**
 * The main deployer for Fungal
 * @author <a href="mailto:jesper.pedersen@comcast.net">Jesper Pedersen</a>
 */
public interface MainDeployer
{
   /**
    * Deploy uses the kernel class loader as the parent class loader
    * @param url The URL for the deployment
    * @exception Throwable If an error occurs
    */
   public void deploy(URL url) throws Throwable;

   /**
    * Undeploy
    * @param url The URL for the deployment
    * @exception Throwable If an error occurs
    */
   public void undeploy(URL url) throws Throwable;

   /**
    * Register a deployment -- advanced usage
    * @param deployment The deployment
    */
   public void registerDeployment(Deployment deployment);

   /**
    * Unregister a deployment -- advanced usage
    * @param deployment The deployment
    * @exception Throwable If an error occurs
    */
   public void unregisterDeployment(Deployment deployment) throws Throwable;
}
