package com.hiflying.smartlink;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import java.lang.reflect.Field;

public class R1 {

   private static Context context;


   public static void initContext(Context _context) {
      context = _context;
   }

   public static int anim(String name) {
      return getIdentifier(context, name, "anim");
   }

   public static int animator(String name) {
      return getIdentifier(context, name, "animator");
   }

   public static int array(String name) {
      return getIdentifier(context, name, "array");
   }

   public static int attr(String name) {
      return getIdentifier(context, name, "attr");
   }

   public static int color(String name) {
      return getIdentifier(context, name, "color");
   }

   public static int dimen(String name) {
      return getIdentifier(context, name, "dimen");
   }

   public static int drawable(String name) {
      return getIdentifier(context, name, "drawable");
   }

   public static int id(String name) {
      return getIdentifier(context, name, "id");
   }

   public static int integer(String name) {
      return getIdentifier(context, name, "integer");
   }

   public static int layout(String name) {
      return getIdentifier(context, name, "layout");
   }

   public static int raw(String name) {
      return getIdentifier(context, name, "raw");
   }

   public static int string(String name) {
      return getIdentifier(context, name, "string");
   }

   public static int style(String name) {
      return getIdentifier(context, name, "style");
   }

   public static int[] styleable(String name) {
      return (int[])getFieldFromStyleable(context, name);
   }

   public static Object styleable(String name, Class clazz) {
      return getFieldFromStyleable(context, name);
   }

   private static int getIdentifier(Context context, String name, String type) {
      if(context == null) {
         new NullPointerException("Must call initContext(Context _context), recommend application context");
      }

      int resource = context.getResources().getIdentifier(name, type, context.getPackageName());
      if(resource == 0) {
         throw new NotFoundException(String.format("Resource for id R.%s.%s not found!", new Object[]{type, name}));
      } else {
         return resource;
      }
   }

   public static final Object getFieldFromStyleable(Context context, String name) {
      try {
         Field t = Class.forName(context.getPackageName() + ".R$styleable").getField(name);
         if(null != t) {
            return t.get((Object)null);
         }
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

      return null;
   }
}
