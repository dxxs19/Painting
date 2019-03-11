package com.wei.painting.activity

import androidx.databinding.ViewDataBinding
import cn.jzvd.Jzvd
import com.wei.libbase.SimpleActivity
import com.wei.painting.R
import lib.videoplayer.JzvdStdExt

/**
 * 饺子（节操）视频播放器播放案例
 * @author XiangWei
 * @since 2019/2/14
 */
class VideoPlayerActivity : SimpleActivity() {

    companion object {
        private const val VIDEO_URL =
            "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
        private const val VIDEO_THUMB_IMAGE_URL =
            "http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"
    }

    private var jzVideo: JzvdStdExt? = null

    override fun createDataBinding(): ViewDataBinding {
        val b = com.wei.painting.databinding.ActivityVideoPlayerBinding.inflate(layoutInflater)
        b.owner = this
        return b
    }

    override fun initViews() {
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        jzVideo = findViewById(R.id.jz_video)
        jzVideo?.let { video ->
            video.setUp(VIDEO_URL, "网络视频", Jzvd.SCREEN_WINDOW_NORMAL)
            video.displayThumbImage(VIDEO_THUMB_IMAGE_URL)
        }
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }
}