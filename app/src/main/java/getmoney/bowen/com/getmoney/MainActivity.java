package getmoney.bowen.com.getmoney;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity {

	private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
	private TextView mServiceStatusView;
	private View mSuccessView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置屏幕不休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		mServiceStatusView = (TextView) findViewById(R.id.text_accessible);
		mSuccessView = findViewById(R.id.rl_success);
		updateServiceStatus();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateServiceStatus();
	}

	private void updateServiceStatus() {
		boolean serviceEnabled = false;

		AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
		List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(
			AccessibilityServiceInfo.FEEDBACK_GENERIC);
		for (AccessibilityServiceInfo info : accessibilityServices) {
			if (info.getId().equals(getPackageName() + "/.RobMoney")) {
				serviceEnabled = true;
			}
		}
		mServiceStatusView.setText(serviceEnabled ? "辅助服务已开启" : "辅助服务已关闭");
		mSuccessView.setVisibility(serviceEnabled ? View.VISIBLE : View.GONE );

	}

	public void onButtonClicked(View view) {
		startActivity(mAccessibleIntent);
	}

	//点击了操作手册
	public void onUserGuideClicked(View view) {
		Intent intent = new Intent(this, UserGuideActivity.class);
		startActivity(intent);
		overridePendingTransition(0,0);
	}


	//点击了操作手册
	public void aboutMe(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
		overridePendingTransition(0,0);
	}

}
