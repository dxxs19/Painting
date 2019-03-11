package com.wei.util.text

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.wei.ext.isChinese
import com.wei.ext.isChinesePoint
import com.wei.ext.isEnglishPoint
import com.wei.ext.isNumOrEnglish
import java.util.regex.Pattern

/**
 * 文本操作相关
 * @author XiangWei
 * @since 2018/12/10
 */
object TextUtil {

    /**
     * 关键字高亮变色
     *
     * @param color
     *            变化的色值
     * @param text
     *            文字
     * @param keyword
     *            文字中的关键字
     * @return 结果SpannableString
     */
    fun matcherSearchTitle(color: Int, text: String?, keyword: String?): SpannableString {
        var text = text
        var keyword = keyword
        val s = SpannableString(text)
        keyword = escapeExprSpecialWord(keyword)
        text = escapeExprSpecialWord(text)
        if (text != null && keyword != null) {
            if (text.contains(keyword) && keyword.isNotEmpty()) {
                try {
                    val p = Pattern.compile(keyword)
                    val m = p.matcher(s)
                    while (m.find()) {
                        val start = m.start()
                        val end = m.end()
                        s.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                } catch (e: Exception) {
                    Log.e("exception: ", e.toString())
                }
            }
        }
        return s
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return keyword
     */
    fun escapeExprSpecialWord(keyword: String?): String? {
        var keyword = keyword
        if (keyword?.isNotEmpty() == true) {
            val fbsArr = arrayOf("\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|")
            for (key in fbsArr) {
                if (keyword != null && keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key)
                }
            }
        }
        return keyword
    }

    /**
     *  关键字高亮显示
     */
    fun getHighlightText(color: Int, text: String, keyWord: String): SpannableString {
        return matcherSearchTitle(color, text, keyWord)
    }

    /**
     *  计算中英文混合字符串长度（以中文为单位）
     */
    fun calculatedLength(str: CharSequence?): Int {
        var len = 0f
        str?.let {
            for (index in 0 until str.length) {
                val temp = str.substring(index, index + 1)
                len += if (temp.isChinese() || temp.isChinesePoint()) {
                    // 中文或者中文标点符号长度为1
                    1f
                } else if (temp.isNumOrEnglish() || temp.isEnglishPoint()) {
                    // 数字、英文或者英文标点符号长度为0.5
                    0.5f
                } else {
                    // Emoji 表情长度为2
                    temp.length.toFloat()
                }
            }
        }
        return Math.round(len)
    }

    /**
     *  限制输入控件（如，EdiTextView）最大输入字数
     */
    fun getInputFilters(maxLength: Int, callBack: ((count: Int) -> Unit)? = null): Array<InputFilter?> {
        val inputFilters = arrayOfNulls<InputFilter>(1)
        inputFilters[0] = Filter(maxLength, callBack)
        return inputFilters
    }

    /**
     *  重写filter，当输入字符长度超过指定长度时，不让输入
     */
    class Filter(private val maxLength: Int, private val callBack: ((count: Int) -> Unit)? = null) : InputFilter.LengthFilter(maxLength) {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
//            L.e("source: $source, start: $start, end: $end, dest: $dest, dstart: $dstart, dend: $dend")
            // 计算已经显示的字符长度
            val destLen = calculatedLength(dest)
            // 计算刚输入的字符长度
            val sourceLen = calculatedLength(source)
//            // 计算已经显示的字符长度
//            val destLen = dest?.length ?: 0
//            // 计算刚输入的字符长度
//            val sourceLen = source?.length ?: 0
            // 比较 （ 已经显示的字符长度+刚输入的字符长度 ）与最大长度
            if (destLen + sourceLen > maxLength) {
                return ""
            }
            callBack?.invoke(destLen + sourceLen)
            return source ?: ""
        }
    }

    /**
     *  判断是否emoji
     */
    @JvmStatic
    fun isEmojiCharacter(codePoint: Char): Boolean {
        return !((codePoint.toInt() == 0x0) ||
                (codePoint.toInt() == 0x9) ||
                (codePoint.toInt() == 0xA) ||
                (codePoint.toInt() == 0xD) ||
                ((codePoint.toInt() >= 0x20) && (codePoint.toInt() <= 0xD7FF)) ||
                ((codePoint.toInt() >= 0xE000) && (codePoint.toInt() <= 0xFFFD)) ||
                ((codePoint.toInt() >= 0x10000) && (codePoint.toInt() <= 0x10FFFF)))
    }

    /**
     *  返回去除emoji表情后的字符串
     */
    @JvmStatic
    fun delEmoji(value: String): String {
        val sb = StringBuilder()
        val charArray = value.toCharArray()
        charArray.forEach { it ->
            if (!isEmojiCharacter(it)) {
                sb.append(it)
            }
        }
        return sb.toString()
    }

    /**
     *  替换手机号中间四位为“*”
     */
    fun replacePhoneWithStars(phone: String): String {
        if (phone.isNotEmpty() && phone.length >= 11) {
            return phone.substring(0, 3).plus("****").plus(phone.substring(7))
        }
        return ""
    }
}