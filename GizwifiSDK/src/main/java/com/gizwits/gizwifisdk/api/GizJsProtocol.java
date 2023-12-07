//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Base64;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(19)
public class GizJsProtocol {
   private static final String TAG = "GizJsProtocol";
   private ConcurrentHashMap<String, String> protocolJs = new ConcurrentHashMap();
   private ConcurrentHashMap<String, String> datapoints = new ConcurrentHashMap();
   private ConcurrentHashMap<String, WebView> mWebViews = new ConcurrentHashMap();
   private static final GizJsProtocol mInstance = new GizJsProtocol();

   public GizJsProtocol() {
   }

   public synchronized void setProtocolJsAndDataPoints(Context mContext, String pk, String js, String datapoint) {
      this.protocolJs.put(pk, js);
      this.datapoints.put(pk, datapoint);
      String finaljs = "<!DOCTYPE html>\n    <html>\n    \n      // JS代码\n      <script>\n" + (String)this.protocolJs.get(pk) + "\n    </script>\n    \n    </html>";
      if (this.mWebViews.get(pk) == null) {
         WebView mWebView = new WebView(mContext);
         mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
         mWebView.removeJavascriptInterface("accessibility");
         mWebView.removeJavascriptInterface("accessibilityTraversal");
         WebSettings webSettings = mWebView.getSettings();
         webSettings.setJavaScriptEnabled(true);
         mWebView.loadDataWithBaseURL((String)null, finaljs, "text/html", "utf-8", (String)null);
         this.mWebViews.put(pk, mWebView);
      }

   }

   public static synchronized GizJsProtocol sharedInstance() {
      return mInstance;
   }

   public boolean isHasProtocolJs(String pk) {
      Iterator var2 = this.protocolJs.keySet().iterator();

      String key;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         key = (String)var2.next();
      } while(!key.equals(pk));

      return true;
   }

   public boolean isHasDataPoint(String pk) {
      Iterator var2 = this.datapoints.keySet().iterator();

      String key;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         key = (String)var2.next();
      } while(!key.equals(pk));

      return true;
   }

   public void encodeProtocol(String pk, JSONObject cmdData, String meshId, final GizJsProtocol.GetResult getResult) {
      if (this.mWebViews.get(pk) != null) {
         String dataPoint = (String)this.datapoints.get(pk);
         String finaldataPoint = dataPoint.replace("\"", "\\\"");
         JSONObject jsonObject = new JSONObject();

         try {
            jsonObject.put("mesh_id", meshId);
         } catch (JSONException var10) {
            var10.printStackTrace();
         }

         SDKLog.e("encodeJson" + cmdData.toString());
         SDKLog.e("encodeJson" + finaldataPoint);
         String cmdStr = cmdData.toString().replace("\"", "\\\"");
         String exStr = jsonObject.toString().replace("\"", "\\\"");
         ((WebView)this.mWebViews.get(pk)).evaluateJavascript("encode_data(\"" + finaldataPoint + "\",\"" + cmdStr + "\",\"HEX\",\"" + exStr + "\")", new ValueCallback<String>() {
            public void onReceiveValue(String value) {
               SDKLog.e("encode_data:" + value);
               if (value != null && !value.equals("null")) {
                  byte[] cmd = Base64.decode(value.getBytes(), 0);
                  getResult.encode(cmd);
               } else {
                  getResult.encode((byte[])null);
               }
            }
         });
      }

   }

   public void decodeProtocol(String pk, String dataBase64, final GizJsProtocol.GetResult getResult) {
      if (this.mWebViews.get(pk) != null) {
         String dataPoint = (String)this.datapoints.get(pk);
         String finaldataPoint = dataPoint.replace("\"", "\\\"");
         SDKLog.e("decode_data:" + (String)this.protocolJs.get(pk));
         SDKLog.e("decode_data:" + dataBase64);
         ((WebView)this.mWebViews.get(pk)).evaluateJavascript("decode_data(\"" + finaldataPoint + "\",\"" + dataBase64 + "\")", new ValueCallback<String>() {
            public void onReceiveValue(String value) {
               SDKLog.e("decode_data:" + value);
               if (value != null && !value.equals("null")) {
                  getResult.decode(value);
               } else {
                  getResult.encode((byte[])null);
               }
            }
         });
      }

   }

   public interface GetResult {
      void encode(byte[] var1);

      void decode(String var1);
   }
}
