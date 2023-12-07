package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizAdapterType implements Serializable {

   GizAdapterNon("GizAdapterNon", 0),
   GizAdapterDataPointMap("GizAdapterDataPointMap", 1),
   GizAdapterDataPointFunc("GizAdapterDataPointFunc", 2),
   GizAdapterDataPointScript("GizAdapterDataPointScript", 3);
   // $FF: synthetic field
   private static final GizAdapterType[] $VALUES = new GizAdapterType[]{GizAdapterNon, GizAdapterDataPointMap, GizAdapterDataPointFunc, GizAdapterDataPointScript};


   private GizAdapterType(String var1, int var2) {}

   public static GizAdapterType valueOf(int value) {
      switch(value) {
      case 0:
         return GizAdapterNon;
      case 1:
         return GizAdapterDataPointMap;
      case 2:
         return GizAdapterDataPointFunc;
      case 3:
         return GizAdapterDataPointScript;
      default:
         return GizAdapterNon;
      }
   }

}
