package com.hiflying.smartlink.v3;

import com.hiflying.smartlink.AbstractSmartLinkerActivity;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.v3.SnifferSmartLinker;

public class SnifferSmartLinkerActivity extends AbstractSmartLinkerActivity {

   public ISmartLinker setupSmartLinker() {
      return SnifferSmartLinker.getInstance();
   }
}
