package moe.henry_zhr.firefox_no_home_button;

import androidx.annotation.Keep;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

@Keep
public class MainHook implements IXposedHookLoadPackage {
  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) {
    XposedHelpers.findAndHookMethod(
        "mozilla.components.browser.toolbar.BrowserToolbar",
        lpparam.classLoader,
        "addNavigationAction",
        "mozilla.components.concept.toolbar.Toolbar.Action",
        new XC_MethodHook() {
          @Override
          protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            final Object action = param.args[0];
            final Object listener = XposedHelpers.findField(action.getClass(), "listener").get(action);
            // org.mozilla.fenix.browser.BrowserFragment$initializeUI$homeAction$1
            if (listener.getClass().getName().contains("homeAction")) {
              param.setResult(null);
            }
          }
        }
    );
  }
}