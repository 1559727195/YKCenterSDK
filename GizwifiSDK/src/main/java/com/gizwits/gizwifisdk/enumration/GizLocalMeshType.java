package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizLocalMeshType implements Serializable {

   GizLocalMeshUnSupport("GizLocalMeshUnSupport", 0),
   GizLocalMeshSub("GizLocalMeshSub", 1),
   GizLocalMeshGateway("GizLocalMeshGateway", 2);
   // $FF: synthetic field
   private static final GizLocalMeshType[] $VALUES = new GizLocalMeshType[]{GizLocalMeshUnSupport, GizLocalMeshSub, GizLocalMeshGateway};


   private GizLocalMeshType(String var1, int var2) {}

   public static GizLocalMeshType valueOf(int value) {
      switch(value) {
      case 0:
         return GizLocalMeshUnSupport;
      case 1:
         return GizLocalMeshSub;
      case 2:
         return GizLocalMeshGateway;
      default:
         return GizLocalMeshUnSupport;
      }
   }

}
