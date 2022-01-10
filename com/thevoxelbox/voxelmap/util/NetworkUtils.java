package com.thevoxelbox.voxelmap.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public class NetworkUtils {
   private static ArrayList localAddresses;
   private static ArrayList netmasks;

   public static void enumerateInterfaces() throws SocketException {
      localAddresses = new ArrayList();
      netmasks = new ArrayList();
      Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

      while(true) {
         NetworkInterface networkInterface;
         do {
            do {
               do {
                  if (!interfaces.hasMoreElements()) {
                     return;
                  }

                  networkInterface = (NetworkInterface)interfaces.nextElement();
               } while(networkInterface == null);
            } while(networkInterface.isLoopback());
         } while(!networkInterface.isUp());

         Iterator var2 = networkInterface.getInterfaceAddresses().iterator();

         while(var2.hasNext()) {
            InterfaceAddress interfaceAddress = (InterfaceAddress)var2.next();
            if (interfaceAddress != null) {
               InetAddress inetAddress = interfaceAddress.getAddress();
               if (inetAddress instanceof Inet4Address) {
                  InetAddress subnetMask = convertNetPrefixToNetmask(interfaceAddress.getNetworkPrefixLength());
                  if (subnetMask != null) {
                     localAddresses.add(inetAddress);
                     netmasks.add(subnetMask);
                  }
               }
            }
         }
      }
   }

   private static InetAddress convertNetPrefixToNetmask(int netPrefix) {
      try {
         int shiftby = Integer.MIN_VALUE;

         for(int i = netPrefix - 1; i > 0; --i) {
            shiftby >>= 1;
         }

         String maskString = Integer.toString(shiftby >> 24 & 255) + "." + Integer.toString(shiftby >> 16 & 255) + "." + Integer.toString(shiftby >> 8 & 255) + "." + Integer.toString(shiftby & 255);
         return InetAddress.getByName(maskString);
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   private static boolean onSameNetwork(InetAddress ip1, InetAddress ip2, InetAddress mask) throws Exception {
      byte[] a1 = ip1.getAddress();
      byte[] a2 = ip2.getAddress();
      byte[] m = mask.getAddress();

      for(int i = 0; i < a1.length; ++i) {
         if ((a1[i] & m[i]) != (a2[i] & m[i])) {
            return false;
         }
      }

      return true;
   }

   public static boolean isOnLan(InetAddress serverAddress) {
      try {
         for(int t = 0; t < localAddresses.size(); ++t) {
            if (onSameNetwork((InetAddress)localAddresses.get(t), serverAddress, (InetAddress)netmasks.get(t))) {
               return true;
            }
         }
      } catch (Exception var2) {
         ;
      }

      return false;
   }
}
