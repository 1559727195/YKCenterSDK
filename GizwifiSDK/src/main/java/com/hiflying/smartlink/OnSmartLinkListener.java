package com.hiflying.smartlink;

import com.hiflying.smartlink.SmartLinkedModule;

public interface OnSmartLinkListener {

   void onLinked(SmartLinkedModule var1);

   void onCompleted();

   void onTimeOut();
}
