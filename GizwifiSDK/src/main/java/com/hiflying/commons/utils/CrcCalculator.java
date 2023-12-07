package com.hiflying.commons.utils;


public class CrcCalculator {

   public CrcCalculator.AlgoParams Parameters;
   public byte HashSize = 8;
   private long _mask = -1L;
   private long[] _table = new long[256];
   public static final byte[] TestBytes = new byte[]{(byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57};


   public CrcCalculator(CrcCalculator.AlgoParams params) {
      this.Parameters = params;
      this.HashSize = (byte)params.HashSize;
      if(this.HashSize < 64) {
         this._mask = (1L << this.HashSize) - 1L;
      }

      this.CreateTable();
   }

   public long calc(byte[] data, int offset, int length) {
      long init = this.Parameters.RefOut?ReverseBits(this.Parameters.Init, this.HashSize):this.Parameters.Init;
      long hash = this.ComputeCrc(init, data, offset, length);
      return (hash ^ this.Parameters.XorOut) & this._mask;
   }

   private long ComputeCrc(long init, byte[] data, int offset, int length) {
      long crc = init;
      int toRight;
      if(this.Parameters.RefOut) {
         for(toRight = offset; toRight < offset + length; ++toRight) {
            crc = this._table[(int)((crc ^ (long)data[toRight]) & 255L)] ^ crc >>> 8;
            crc &= this._mask;
         }
      } else {
         toRight = this.HashSize - 8;
         toRight = toRight < 0?0:toRight;

         for(int i = offset; i < offset + length; ++i) {
            crc = this._table[(int)((crc >> toRight ^ (long)data[i]) & 255L)] ^ crc << 8;
            crc &= this._mask;
         }
      }

      return crc;
   }

   private void CreateTable() {
      for(int i = 0; i < this._table.length; ++i) {
         this._table[i] = this.CreateTableEntry(i);
      }

   }

   private long CreateTableEntry(int index) {
      long r = (long)index;
      if(this.Parameters.RefIn) {
         r = ReverseBits(r, this.HashSize);
      } else if(this.HashSize > 8) {
         r <<= this.HashSize - 8;
      }

      long lastBit = 1L << this.HashSize - 1;

      for(int i = 0; i < 8; ++i) {
         if((r & lastBit) != 0L) {
            r = r << 1 ^ this.Parameters.Poly;
         } else {
            r <<= 1;
         }
      }

      if(this.Parameters.RefOut) {
         r = ReverseBits(r, this.HashSize);
      }

      return r & this._mask;
   }

   static long ReverseBits(long ul, int valueLength) {
      long newValue = 0L;

      for(int i = valueLength - 1; i >= 0; --i) {
         newValue |= (ul & 1L) << i;
         ul >>= 1;
      }

      return newValue;
   }


   public static class Crc8 {

      public static CrcCalculator.AlgoParams Crc8 = new CrcCalculator.AlgoParams("CRC-8", 8, 7L, 0L, false, false, 0L, 244L);
      public static CrcCalculator.AlgoParams Crc8Cdma2000 = new CrcCalculator.AlgoParams("CRC-8/CDMA2000", 8, 155L, 255L, false, false, 0L, 218L);
      public static CrcCalculator.AlgoParams Crc8Darc = new CrcCalculator.AlgoParams("CRC-8/DARC", 8, 57L, 0L, true, true, 0L, 21L);
      public static CrcCalculator.AlgoParams Crc8DvbS2 = new CrcCalculator.AlgoParams("CRC-8/DVB-S2", 8, 213L, 0L, false, false, 0L, 188L);
      public static CrcCalculator.AlgoParams Crc8Ebu = new CrcCalculator.AlgoParams("CRC-8/EBU", 8, 29L, 255L, true, true, 0L, 151L);
      public static CrcCalculator.AlgoParams Crc8ICode = new CrcCalculator.AlgoParams("CRC-8/I-CODE", 8, 29L, 253L, false, false, 0L, 126L);
      public static CrcCalculator.AlgoParams Crc8Itu = new CrcCalculator.AlgoParams("CRC-8/ITU", 8, 7L, 0L, false, false, 85L, 161L);
      public static CrcCalculator.AlgoParams Crc8Maxim = new CrcCalculator.AlgoParams("CRC-8/MAXIM", 8, 49L, 0L, true, true, 0L, 161L);
      public static CrcCalculator.AlgoParams Crc8Rohc = new CrcCalculator.AlgoParams("CRC-8/ROHC", 8, 7L, 255L, true, true, 0L, 208L);
      public static CrcCalculator.AlgoParams Crc8Wcdma = new CrcCalculator.AlgoParams("CRC-8/WCDMA", 8, 155L, 0L, true, true, 0L, 37L);
      public static final CrcCalculator.AlgoParams[] Params = new CrcCalculator.AlgoParams[]{Crc8, Crc8Cdma2000, Crc8Darc, Crc8DvbS2, Crc8Ebu, Crc8ICode, Crc8Itu, Crc8Maxim, Crc8Rohc, Crc8Wcdma};


   }

   public static class AlgoParams {

      public long Check;
      public int HashSize;
      public long Init;
      public String Name;
      public long Poly;
      public boolean RefIn;
      public boolean RefOut;
      public long XorOut;


      public AlgoParams(String name, int hashSize, long poly, long init, boolean refIn, boolean refOut, long xorOut, long check) {
         this.Name = name;
         this.Check = check;
         this.Init = init;
         this.Poly = poly;
         this.RefIn = refIn;
         this.RefOut = refOut;
         this.XorOut = xorOut;
         this.HashSize = hashSize;
      }
   }
}
