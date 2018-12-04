# 动态修改activity里的所有继承子TextView控件的字体缩放的帮助类
特性:
 * 不需要重启，立即生效
 * 低侵入性，几行代码即可集成

```kotlin

class MainActivity : AppCompatActivity() {

    // 定义成员变量
    var textSizeHelper: TextSizeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
    // 初始化TextSizeHelper
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        textSizeHelper = TextSizeHelper(rootView)
        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            // 设置缩放比例
                textSizeHelper!!.onFontScaled(progress * 0.01f + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

 
    // 覆盖getResources方法，修改scaledDensity的值
    override fun getResources(): Resources {

        return super.getResources().apply {
            textSizeHelper?.apply {
                displayMetrics.scaledDensity = textSizeHelper!!.newScaledDensity
            }
        }
    }
}
```