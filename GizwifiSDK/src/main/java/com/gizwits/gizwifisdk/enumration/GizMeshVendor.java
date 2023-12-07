package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizMeshVendor implements Serializable {

   GizMeshTelink("GizMeshTelink", 0),
   GizMeshJingxun("GizMeshJingxun", 1);
   // $FF: synthetic field
   private static final GizMeshVendor[] $VALUES = new GizMeshVendor[]{GizMeshTelink, GizMeshJingxun};


   private GizMeshVendor(String var1, int var2) {}

   public static GizMeshVendor valueOf(int value) {
      switch(value) {
      case 0:
         return GizMeshTelink;
      case 1:
         return GizMeshJingxun;
      default:
         return GizMeshTelink;
      }
   }

}
