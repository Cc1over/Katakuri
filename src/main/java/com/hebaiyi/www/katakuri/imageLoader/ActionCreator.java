package com.hebaiyi.www.katakuri.imageLoader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ActionCreator {

    private static final int FROM_MEMORY = 112;

    private MemoryCache mMemoryCache;
    private Dispatcher mDispatcher;
    private String mUri;
    private ImageView mImageView;
    private UIHandler mUIHandler;

    ActionCreator(String uri) {
        mUri = uri;
        // 创建内存缓存类
        mMemoryCache = MemoryCache.getInstance();
        mUIHandler = new UIHandler(this);
        // 初始化调配者
        mDispatcher = Dispatcher.getInstance(mUIHandler,Dispatcher.Type.LIFO);
    }

    /**
     * 加载图片
     *
     * @param imageView 对应的ImageView对象
     */
    public void into(ImageView imageView) {
        // 缓存ImageView
        mImageView = imageView;
        // 给imageView设置标签
        mImageView.setTag(mUri);
        // 从内存取出bitmap
        Bitmap bm = mMemoryCache.getBitmapFromCache(mUri);
        if (bm != null) {
            Message message = Message.obtain();
            message.what = FROM_MEMORY;
            message.obj = bm;
            mUIHandler.sendMessage(message);
        } else {
            // 获取宽高信息
            int width = ImageUtil.getWidth(imageView);
            int height = ImageUtil.getHeight(imageView);
            // 创建任务
            ImageAction action = new ImageAction(mUri, width, height, mMemoryCache, mDispatcher);
            // 执行任务
            mDispatcher.performExecute(action);
        }
    }


    private static class UIHandler extends Handler {

        private final WeakReference<ActionCreator> mCreator;

        UIHandler(ActionCreator creator) {
            mCreator = new WeakReference<>(creator);
        }

        @Override
        public void handleMessage(Message msg) {
            ActionCreator creator = mCreator.get();
            super.handleMessage(msg);
            if (msg.what == FROM_MEMORY) {
                creator.mImageView.setImageBitmap((Bitmap) msg.obj);
            }
            // 获取设置图片所需的参数
            ImageAction action = (ImageAction) msg.obj;
            Bitmap bm = action.getBitmap();
            String uri = action.getUri();
            // 设置图片
            if (creator.mImageView.getTag().equals(uri)) {
                creator.mImageView.setImageBitmap(bm);
            }
        }
    }

}
