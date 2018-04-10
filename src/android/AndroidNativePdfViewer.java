package xyz.guutong.androidnativepdfviewer;

import android.Manifest;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PermissionHelper;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.guutong.androidpdfviewer.PdfViewActivity;
import android.content.Intent;

public class AndroidNativePdfViewer extends CordovaPlugin {
    private String []permissions = { Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };

    private CallbackContext callbackContext;
    private JSONArray args;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        if (action.equals("openPdfUrl")) {
            this.callbackContext = callbackContext;
            this.args = args;

            if(!hasPermisssion()) {
                requestPermissions();
            }else {
                open();
            }
        } else {
            callbackContext.error("Invalid action: " + action);
            return false;
        }

        return true;
    }

    private void open() {
        try {
            final String fileUrl = this.args.getString(0);
            final String title = this.args.getString(1);
            final JSONObject options = this.args.getJSONObject(2);
            String headerColor = "#1191d5";            
            boolean showScroll = false;            
            boolean swipeHorizontal = false;            
            boolean showShareButton = true;            
            boolean showCloseButton = true;            

            if (options.has("headerColor")) {
                headerColor = options.getString("headerColor");    
            }
            
            if (options.has("showScroll")) {
                showScroll = options.getBoolean("showScroll");
            }

            if (options.has("swipeHorizontal")) {
                swipeHorizontal = options.getBoolean("swipeHorizontal");
            }

            if (options.has("showShareButton")) {
                showShareButton = options.getBoolean("showShareButton");
            }

            if (options.has("showCloseButton")) {
                showCloseButton = options.getBoolean("showCloseButton");
            }

            Intent intent = new Intent(cordova.getActivity(), PdfViewActivity.class);
            intent.putExtra(PdfViewActivity.EXTRA_PDF_URL, fileUrl);
            intent.putExtra(PdfViewActivity.EXTRA_PDF_TITLE, title);
            intent.putExtra(PdfViewActivity.EXTRA_TOOLBAR_COLOR, headerColor);
            intent.putExtra(PdfViewActivity.EXTRA_SHOW_SCROLL, showScroll);
            intent.putExtra(PdfViewActivity.EXTRA_SWIPE_HORIZONTAL, swipeHorizontal);
            intent.putExtra(PdfViewActivity.EXTRA_SHOW_SHARE_BUTTON, showShareButton);
            intent.putExtra(PdfViewActivity.EXTRA_SHOW_CLOSE_BUTTON, showCloseButton);

            cordova.startActivityForResult(this, intent, 0);
            this.callbackContext.success(fileUrl);
        } catch (JSONException e) {
            this.callbackContext.error(e.getMessage());
        }
    }

   public boolean hasPermisssion() {
       for(String p : permissions)
       {
           if(!PermissionHelper.hasPermission(this, p))
           {
               return false;
           }
       }
       return true;
   }

    public void requestPermissions()
    {
        PermissionHelper.requestPermissions(this, 0, permissions);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException
    {
        PluginResult result;
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                this.callbackContext.sendPluginResult(result);
                return;
            }
        }

        switch(requestCode)
        {
            case 0:
                open();
            break;
        }
    }
}
