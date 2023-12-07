package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizScheduleWeekday implements Serializable {

   GizScheduleSunday("GizScheduleSunday", 0),
   GizScheduleMonday("GizScheduleMonday", 1),
   GizScheduleTuesday("GizScheduleTuesday", 2),
   GizScheduleWednesday("GizScheduleWednesday", 3),
   GizScheduleThursday("GizScheduleThursday", 4),
   GizScheduleFriday("GizScheduleFriday", 5),
   GizScheduleSaturday("GizScheduleSaturday", 6);
   // $FF: synthetic field
   private static final GizScheduleWeekday[] $VALUES = new GizScheduleWeekday[]{GizScheduleSunday, GizScheduleMonday, GizScheduleTuesday, GizScheduleWednesday, GizScheduleThursday, GizScheduleFriday, GizScheduleSaturday};


   private GizScheduleWeekday(String var1, int var2) {}

   public static GizScheduleWeekday valueOf(int value) {
      switch(value) {
      case 0:
         return GizScheduleSunday;
      case 1:
         return GizScheduleMonday;
      case 2:
         return GizScheduleTuesday;
      case 3:
         return GizScheduleWednesday;
      case 4:
         return GizScheduleThursday;
      case 5:
         return GizScheduleFriday;
      case 6:
         return GizScheduleSaturday;
      default:
         return null;
      }
   }

}
