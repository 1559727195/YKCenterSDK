package com.hiflying.commons.log;

import android.util.Log;

public class HFLog {

   public static int level = 2;
   public static String LOG_PRE = "";


   public static void v(Object obj, String log) {
      if(level <= 2) {
         if(log == null) {
            log = "";
         }

         if(obj instanceof Class) {
            Log.v(LOG_PRE + ((Class)obj).getSimpleName(), log);
         } else {
            Log.v(LOG_PRE + obj.getClass().getSimpleName(), log);
         }

      }
   }

   public static void v(String tag, String msg) {
      Log.v(LOG_PRE + tag, msg);
   }

   public static void d(Object obj, String log) {
      if(level <= 3) {
         if(log == null) {
            log = "";
         }

         if(obj instanceof Class) {
            Log.d(LOG_PRE + ((Class)obj).getSimpleName(), log);
         } else {
            Log.d(LOG_PRE + obj.getClass().getSimpleName(), log);
         }

      }
   }

   public static void d(String tag, String msg) {
      Log.d(LOG_PRE + tag, msg);
   }

   public static void i(Object obj, String log) {
      if(level <= 4) {
         if(log == null) {
            log = "";
         }

         if(obj instanceof Class) {
            Log.i(LOG_PRE + ((Class)obj).getSimpleName(), log);
         } else {
            Log.i(LOG_PRE + obj.getClass().getSimpleName(), log);
         }

      }
   }

   public static void i(String tag, String msg) {
      Log.i(LOG_PRE + tag, msg);
   }

   public static void w(Object obj, String log) {
      if(level <= 5) {
         if(log == null) {
            log = "";
         }

         if(obj instanceof Class) {
            Log.w(LOG_PRE + ((Class)obj).getSimpleName(), log);
         } else {
            Log.w(LOG_PRE + obj.getClass().getSimpleName(), log);
         }

      }
   }

   public static void w(String tag, String msg) {
      Log.w(LOG_PRE + tag, msg);
   }

   public static void e(Object obj, String log) {
      if(level <= 6) {
         if(log == null) {
            log = "";
         }

         if(obj instanceof Class) {
            Log.e(LOG_PRE + ((Class)obj).getSimpleName(), log);
         } else {
            Log.e(LOG_PRE + obj.getClass().getSimpleName(), log);
         }

      }
   }

   public static void e(String tag, String msg) {
      Log.e(LOG_PRE + tag, msg);
   }

}
