package com.gizwits.gizwifisdk.enumration;


public enum GizLogicalOperator {

   GizLogicalAnd("GizLogicalAnd", 0),
   GizLogicalOr("GizLogicalOr", 1);
   // $FF: synthetic field
   private static final GizLogicalOperator[] $VALUES = new GizLogicalOperator[]{GizLogicalAnd, GizLogicalOr};


   private GizLogicalOperator(String var1, int var2) {}

   public static GizLogicalOperator valueOf(int value) {
      switch(value) {
      case 0:
         return GizLogicalAnd;
      case 1:
         return GizLogicalOr;
      default:
         return null;
      }
   }

}
