package cn.antke.bct.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.common.utils.StringUtil;

/**
 * Created by ${zhaoweiwei} on 2016/6/28.
 * 输入框
 */
public class InputUtil {
	/**
	 *当每个EditText不为空时，按钮才可用
	 * @param button  是否被点亮可用的按钮
	 * @param editTexts EditText数组
	 */
	public static void editIsEmpty (final View button, final EditText... editTexts) {

		for (int i = 0; i<editTexts.length; i++) {

			editTexts[i].addTextChangedListener(new TextWatcher() {

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}

				@Override
				public void afterTextChanged(Editable s) {
					for (EditText editText:editTexts){
						if (TextUtils.isEmpty(editText.getText())){
							button.setEnabled(false);
							return;
						}
					}
					button.setEnabled(true);
				}
			});
		}
	}

	/**
	 * 处理额度和价钱混合显示
	 * @param integral 额度价格
	 * @param price 金额价格
	 * @return 最终显示结果
	 */
	public static String formatPrice(String integral, String price) {
		String result = "";
		if (!TextUtils.isEmpty(price) && StringUtil.parseInt(price, 0) > 0) {
			result += ("<font color=\"#ff3b3b\">￥" + price + "</font>");
		}
		if (!TextUtils.isEmpty(integral) && StringUtil.parseInt(integral, 0) > 0) {
			if (TextUtils.isEmpty(result)) {
				result += ("<font color=\"#ff3b3b\">"+integral+"额度</font>");
			} else {
				result += ("<font color=\"#ff3b3b\">+"+integral+"额度</font>");
			}
		}
		return result;
	}
	public static String formatPriceTwo(String integral, String price) {
		String result = "";
		if (!TextUtils.isEmpty(price) && StringUtil.parseInt(price, 0) > 0) {
			result += ("￥"+price );
		}
		if (!TextUtils.isEmpty(integral) && StringUtil.parseInt(integral, 0) > 0) {

			if (TextUtils.isEmpty(result)) {
				result += (integral+"额度");
			} else {
				result += ("+" + integral+"额度" );
			}
		}
		return result;
	}

	public static String formatPrice(int integral, int price) {
		String result = "";
		if (price > 0) {
			result += ("￥"+price);
		}
		if (integral > 0) {
			if (TextUtils.isEmpty(result)) {
				result += (integral + "额度");
			} else {
				result += ("+" + integral + "额度");
			}
		}
		return result;
	}

	/**
	 * @return 邮费显示处理
	 */
	public static String formatLogistics(String integral, String price) {
		String result = formatPrice(integral, price);
		if (TextUtils.isEmpty(result)) {
			result = "免邮";
		}
		return result;
	}

	public static String formatLogistics(int integral, int price) {
		String result = formatPrice(integral, price);
		if (TextUtils.isEmpty(result)) {
			result = "免邮";
		}
		return result;
	}
}
