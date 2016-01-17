package com.example.solveslidingconflict;

import com.nineoldandroids.view.ViewHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView{

    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mMenuWidth;

    private boolean once  = false;
    private boolean isOpen = false;

    private int mLastXInterception = 0;
    private int mLastYInterception = 0;

    /**
     * 单位为dp
     */
    private int mMenuRightPadding = 50;

    /**
     * 为自定义属性时调用
     * @param context
     * @param attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context,attrs,0);

    }

    public SlidingMenu(Context context) {
        super(context,null);
    }

    /**
     * 当使用了自定义属性时，会调用此构造方法
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SlidingMenu(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        /**
         * 得到屏幕的高度
         */
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mMenuRightPadding = (int) (mScreenWidth*0.3);
    }

    /**
     * 设置子View的宽和高
     * 设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once){
            mWapper = (LinearLayout)getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth-mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 通过设置偏移量将Menu隐藏
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        if(changed){
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch(action){
            case MotionEvent.ACTION_UP:
                //隐藏在左边的宽度
                int scrollX = getScrollX();
                if(scrollX >= mMenuWidth/2){
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                }else{
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){
        if(isOpen)
            return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(){
        if(!isOpen)
            return;
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    /**
     * 切换菜单
     */
    public void toggle(){
        if(isOpen)
            closeMenu();
        else {
            openMenu();
        }
    }

    /**
     * 滚动发生时
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = (float) (l*1.0/mMenuWidth);
        float rightScale = (float) (0.7+0.3*scale);		//右侧缩放
        float leftScale = (float) (1.0-scale*0.3f);		//左侧缩放
        float leftAlpha = (float) (0.6+0.4*(1-scale));//透明度
        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu, leftAlpha);
        ViewHelper.setTranslationX(mMenu, mMenuWidth*scale*0.7f);
        //设置mContent缩放的中心点
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight()/2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        super.onInterceptTouchEvent(ev);
        boolean interception = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                interception = false;
                mLastXInterception = x;
                mLastYInterception = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = Math.abs(x - mLastXInterception);
                int deltaY = Math.abs(y - mLastYInterception);
                if(deltaX>deltaY&&deltaY<10&&deltaX>20){
                    interception = true;
                }else{
                    interception = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                interception = false;
                break;
            default:
                break;
        }
        mLastYInterception = y;
        mLastXInterception = x;
        if(isOpen)
            interception = true;
        return interception;
    }
}
