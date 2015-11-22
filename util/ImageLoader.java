package com.example.selectpicture.util;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2015/10/5.
 */
public class ImageLoader {

    private static ImageLoader mInstance;
    /**
     * 图片缓存的核心对象
     */
    private LruCache<String,Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEFAULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQuene;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;
    private Semaphore mSemaphore = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;
    public enum Type{
        FIFO,LIFO
    }

    private ImageLoader(int threadCount,Type type){
        init(threadCount,type);
    }

    /**
     *  初始化操作
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        /**
         * 后台轮询线程
         */
        mPoolThread = new Thread(){
            @Override
            public void run(){
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        //线程池取出一个任务去执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };

        mPoolThread.start();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取应用的最大可用内存
        int cacheMemory = maxMemory/8;
        mLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value){
                return value.getRowBytes()*value.getHeight();
            }
        };
        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQuene = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);



    }

    /**
     * 从任务队列取出一个方法
     * @return
     */
    private Runnable getTask() {
        if(mType==Type.FIFO){
            return mTaskQuene.removeFirst();
        }else{
            return mTaskQuene.removeLast();
        }
    }

    /**
     * 根据path为ImageView设置图片
     * @param path
     * @param view
     */
    public void loadImage(final String path, final ImageView view){
        view.setTag(path);
        if(mUIHandler ==null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    //获取得到的图片，为imageview回调设置图片
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView view = holder.view;
                    String path = holder.path;
                    //将path与getTag的存储路径进行比较
                    if(view.getTag().toString().equals(path)){
                        view.setImageBitmap(bm);
                    }
                }
            };
        }
        //根据path在缓存中获取Bitmap
        Bitmap bm = getBitmapFromLrcCache(path);
        if(bm != null){
            refreshBitmap(bm,path,view);
        }else{
            addTasks(new Runnable(){
                @Override
                public void run(){
                    //加载图片
                    //图片的压缩
                    //1.获得图片需要显示的大小
                   ImageSize size =  getImageViewSize(view);
                    //2.压缩图片
                    Bitmap bm = decodeSampleBitmapFromPath(path,size);
                    //3.把图片加入到缓存
                    addBitmapToLrcCache(path,bm);
                    refreshBitmap(bm, path, view);
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    private void refreshBitmap(Bitmap bm, String path, ImageView view) {
        ImageBeanHolder holder = new ImageBeanHolder();
        holder.path = path;
        holder.bitmap = bm;
        holder.view = view;
        Message msg = Message.obtain();
        msg.obj = holder;
        mUIHandler.sendMessage(msg);
    }

    private void addBitmapToLrcCache(String path, Bitmap bm){

        if(getBitmapFromLrcCache(path)==null){
            try{
                mLruCache.put(path,bm);
            }catch (Exception e){}
        }
    }

    /**
     * 根据图片需要显示的宽和高进行压缩
     * @param path
     * @param size
     * @return
     */
    private Bitmap decodeSampleBitmapFromPath(String path, ImageSize size) {
        //获取图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = caculateSampleSize(options,size);
        //使用获取到的inSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     * @param options
     * @param size
     * @return
     */
    private int caculateSampleSize(BitmapFactory.Options options, ImageSize size) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if(width>size.width||height>size.height){
            int widthRadio = Math.round(width*1.0f/size.width);
            int heightradio = Math.round(height*1.0f/size.height);
            inSampleSize = Math.max(widthRadio, heightradio);

        }
        return inSampleSize;
    }

    /**
     * 根据Imageview获取适当的压缩的宽和高
     * @param view
     */
    private ImageSize getImageViewSize(ImageView view){
        ImageSize size = new ImageSize();
        DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int width = view.getWidth();
                 //获取Imageview的实际宽度
        if(width<=0){
            width = lp.width;//获取imageview在layout中声明的宽度
        }
        if(width<=0){
            width = getImageviewFieldValue(view,"mMaxWidth"); //检查最大值
        }
        if(width<=0){
            width = metrics.widthPixels;
        }

        int height = view.getHeight() ;
                 //获取Imageview的实际宽度
        if(height<=0){
            height = lp.height;//获取imageview在layout中声明的宽度
        }
        if(height<=0){
            height = getImageviewFieldValue(view,"mMaxHeight"); //检查最大值
        }
        if(height<=0){
            height = metrics.heightPixels;
        }
        size.height = height;
        size.width = width;
        return size;
    }

    /**
     * 通过反射获取ImageView的某个属性值
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageviewFieldValue(Object object,String fieldName){
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if(fieldValue>0&&fieldValue<Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private void addTasks(Runnable runnable) {
        mTaskQuene.add(runnable);
        try {
            if(mPoolThreadHandler==null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLrcCache(String path) {
        return mLruCache.get(path);
    }


    /**
     * 单例模式
     * @return
     */
    public static ImageLoader getInstance(){
        if(mInstance==null){
            synchronized (ImageLoader.class){
                if(mInstance==null){
                    mInstance = new ImageLoader(DEFAULT_THREAD_COUNT,Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private class ImageBeanHolder{
        Bitmap bitmap;
        ImageView view;
        String path;
    }

    private class ImageSize{
        int width,height;
    }
}
