package com.espressif.iot.esptouch.gizwits.protocol;

import com.espressif.iot.esptouch.gizwits.protocol.DatumCode;
import com.espressif.iot.esptouch.gizwits.protocol.GuideCode;
import com.espressif.iot.esptouch.gizwits.security.ITouchEncryptor;
import com.espressif.iot.esptouch.gizwits.task.IEsptouchGenerator;
import com.espressif.iot.esptouch.gizwits.util.ByteUtil;
import java.net.InetAddress;

public class EsptouchGenerator implements IEsptouchGenerator {

   private final byte[][] mGcBytes2;
   private final byte[][] mDcBytes2;


   public EsptouchGenerator(byte[] apSsid, byte[] apBssid, byte[] apPassword, InetAddress inetAddress, ITouchEncryptor encryptor) {
      GuideCode gc = new GuideCode();
      char[] gcU81 = gc.getU8s();
      this.mGcBytes2 = new byte[gcU81.length][];

      for(int dc = 0; dc < this.mGcBytes2.length; ++dc) {
         this.mGcBytes2[dc] = ByteUtil.genSpecBytes(gcU81[dc]);
      }

      DatumCode var11 = new DatumCode(apSsid, apBssid, apPassword, inetAddress, encryptor);
      char[] dcU81 = var11.getU8s();
      this.mDcBytes2 = new byte[dcU81.length][];

      for(int i = 0; i < this.mDcBytes2.length; ++i) {
         this.mDcBytes2[i] = ByteUtil.genSpecBytes(dcU81[i]);
      }

   }

   public byte[][] getGCBytes2() {
      return this.mGcBytes2;
   }

   public byte[][] getDCBytes2() {
      return this.mDcBytes2;
   }
}
