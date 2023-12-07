package com.hiflying.smartlink.v7;

import com.hiflying.smartlink.AbstractSmartLinkerActivity;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.v7.MulticastSmartLinker;

public class MulticastSmartLinkerActivity extends AbstractSmartLinkerActivity {

   public ISmartLinker setupSmartLinker() {
      return MulticastSmartLinker.getInstance();
   }
}
