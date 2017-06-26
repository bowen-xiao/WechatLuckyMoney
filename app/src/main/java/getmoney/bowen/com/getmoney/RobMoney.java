package getmoney.bowen.com.getmoney;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RobMoney extends AccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		int eventType = event.getEventType();
		switch (eventType) {
		//第一步：监听通知栏消息
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			List<CharSequence> texts = event.getText();
			if (!texts.isEmpty()) {
				for (CharSequence text : texts) {
					String content = text.toString();
					Log.e("onAccessibilityEvent", "text:"+content);
					if (content.contains("[微信红包]")) {
						//模拟打开通知栏消息
						if (event.getParcelableData() != null
								&& 
							event.getParcelableData() instanceof Notification) {
							Notification notification = (Notification) event.getParcelableData();
							final PendingIntent pendingIntent = notification.contentIntent;
							try {
							  //		如果是锁屏就去解锁
								/*KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
								if (km.inKeyguardRestrictedInputMode()) {
									//keyguard is on 屏幕是锁定状态的
									wakeUpAndUnlock(getApplicationContext());
									backToSystemHome();
									int max=3000;
									int min=1000;
									Random random = new Random();
									int s = random.nextInt(max)%(max-min+1) + min;
									isFromDetail = false;
									Handler handler = new Handler();
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											*//**
											 *要执行的操作*//*

											try {
												pendingIntent.send();
											} catch (CanceledException e) {
												e.printStackTrace();
											}
										}
									}, s);//3秒后执行Runnable中的run方法 500 - 1000毫秒随机值
									return;
								}else{
									//解锁屏幕
									pendingIntent.send();
								}*/
								pendingIntent.send();
							} catch (CanceledException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			break;
		//第二步：监听是否进入微信红包消息界面
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			String className = event.getClassName().toString();
			Log.i("className :",className + "::className");
			if (className.equals("com.tencent.mm.ui.LauncherUI")) {
				if(isFromDetail){
					int max=1000;
					int min=500;
					Random random = new Random();
					int s = random.nextInt(max)%(max-min+1) + min;
					isFromDetail = false;
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							/**
							 *要执行的操作
							 */
							backToSystemHome();
						}
					}, s);//3秒后执行Runnable中的run方法 500 - 1000毫秒随机值
					return;
				}else{
					//开始抢红包
					getPacket();
				}
			} else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
				//开始打开红包
				openPacket();
				//com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI
			}else if(className.contains("luckymoney.ui.LuckyMoneyDetailUI")){
				Log.e("LuckyMoneyDetailUI :",className + "::LuckyMoneyDetailUI");
//				AccessibilityEvent.getSource().performAction(AccessibilityService.GLOBAL_ACTION_BACK);
				//返回首页
//				getRootInActiveWindow().performAction(AccessibilityService.GLOBAL_ACTION_HOME);

				findBackButton(getRootInActiveWindow());

				/* 回到主界面的访求
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									 | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(mHomeIntent);*/
			}
			break;
		}
	}

	boolean isFromDetail = false;

	//返回到系统的主页面
	private void backToSystemHome(){
		/*Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							 | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(mHomeIntent);*/

		Intent intent = new Intent();
		PackageManager packageManager = this.getPackageManager();
		intent = packageManager.getLaunchIntentForPackage("getmoney.bowen.com.getmoney");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		this.startActivity(intent);


	}

	//打到返回按钮
	private void findBackButton(AccessibilityNodeInfo nodeInfo){
		if(nodeInfo == null){return;}

		if (nodeInfo != null) {

			int childCount = nodeInfo.getChildCount();
			for (int i = 0; i < childCount; i++) {
				AccessibilityNodeInfo child = nodeInfo.getChild(i);
				String className = child.getClassName().toString();

				//可以找到返回按钮
				CharSequence text = child.getContentDescription();

//				Log.w("findBackButton ClassName", className.toString() + "");
//				Log.w("findBackButton ResourceName",child.getViewIdResourceName() + "");
//				Log.w("findBackButton isClickable",child.isClickable() + "");
//				Log.w("findBackButton text",text + "");
//				Log.w("child",child.toString() + "");
				if(text!= null && text.toString().contains("返回")){
					if(!child.isClickable()){
						child = child.getParent();
					}
					if(child.isClickable()){
						isFromDetail = true;
						child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}
					return;
				}
				if(child.getChildCount() !=0 ){
					findBackButton(child);
				}
			}

		}
	}


	/**
	 * 查找到
	 */
	@SuppressLint("NewApi")
	private void openPacket() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

		if (nodeInfo != null) {
			Set<AccessibilityNodeInfo> paketList = new HashSet<>();
			int childCount = nodeInfo.getChildCount();
			for (int i = 0; i < childCount; i++) {
				AccessibilityNodeInfo child = nodeInfo.getChild(i);
				String className = child.getClassName().toString();

				Log.w("child ClassName", className.toString() + "");
				Log.w("child ResourceName",child.getViewIdResourceName() + "");
				//			Log.w("child",child.toString() + "");
				if(className.contains("Button")){
					paketList.add(child);
				}
			}

			for (AccessibilityNodeInfo n : paketList) {
				//Log.w("openPacket",n.getText().toString() + "");
				if(n.isClickable()){
					n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				}
			}
		}

	}

	@SuppressLint("NewApi")
	private void getPacket() {
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		recycle(rootNode);
	}
	
	/**
	 * 打印一个节点的结构
	 * @param info
	 */
	@SuppressLint("NewApi")
	public void recycle(AccessibilityNodeInfo info) {  
        if (info.getChildCount() == 0) { 
        	if(info.getText() != null){
        		if("领取红包".equals(info.getText().toString())){
        			//这里有一个问题需要注意，就是需要找到一个可以点击的View
                	Log.e("recycle", "Click"+",isClick:"+info.isClickable());
                	Log.e("recycle", "Click"+",getViewIdResourceName:"+info.getViewIdResourceName());
                	info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                	AccessibilityNodeInfo parent = info.getParent();
                	while(parent != null){
                		Log.e("recycle", "parent isClick:"+parent.isClickable());
                		if(parent.isClickable()){
                			parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                			break;
                		}
                		parent = parent.getParent();
                	}
                	
            	}
        	}
        	
        } else {  
            for (int i = info.getChildCount(); i >0; i--) {
                if(info.getChild(i -1 )!=null){
                    recycle(info.getChild(i -1 ));
                }  
            }  
        }  
    }  

	@Override
	public void onInterrupt() {
	}


	//唤醒屏幕
	public static void wakeUpAndUnlock(Context context){
/*		需要的用户权限
		<uses-permission android:name="android.permission.WAKE_LOCK" />
		<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />*/

		/*KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		//解锁
		kl.disableKeyguard();
		//获取电源管理器对象
		PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
		//获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
		//点亮屏幕
		wl.acquire();
		//释放
		wl.release();*/
	}

}
