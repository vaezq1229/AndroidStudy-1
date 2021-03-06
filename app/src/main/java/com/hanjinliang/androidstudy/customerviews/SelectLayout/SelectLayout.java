package com.hanjinliang.androidstudy.customerviews.SelectLayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.hanjinliang.androidstudy.R;

/**
 * Created by HanJinLiang on 2017-08-16.
 * 选择容器
 */
public class SelectLayout extends LinearLayout implements View.OnClickListener {
    private boolean isAniming=false;//是否在动画中
    View viewLeft;
    View viewCenter;
    View viewRight;

    View space;

    private float mMinWidth;//小视图宽度
    private float mMaxWidth;//大视图宽度
    private float mSpaceWidth;//大小视图中间间隔

    private int mAnimTime=500;//动画时长

    private boolean isMoveScrollable=true;//是否相应滑动事件

    private float mScale=0.3f;//缩放的比例

    /**
     * 当前选中的
     */
    public enum CurrentSelect{
        left,center,right
    }

    CurrentSelect currentSelect=CurrentSelect.center;

    public SelectLayout(Context context) {
        this(context,null);
    }

    public SelectLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelectLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setFocusable(true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize();
    }

    /**
     * 初始化View大小 缩放比例
     */
    private void initSize() {
        mMinWidth=viewLeft.getMeasuredWidth();
        mMaxWidth=mMinWidth*(1+mScale);
        mSpaceWidth=space.getMeasuredWidth()-mMinWidth*mScale/2;

        viewCenter.setScaleX(1+mScale);
        viewCenter.setScaleY(1+mScale);

        if(mMaxWidth>getMeasuredHeight()){//缩放法高度大于Layout高度  重新设置高度 自适应
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)mMaxWidth));
        }
    }

    /**
     * 初始化View
     */
    public void initView() {
        viewLeft=findViewById(R.id.viewLeft);
        viewCenter=findViewById(R.id.viewCenter);
        viewRight=findViewById(R.id.viewRight);
        space=findViewById(R.id.space);

        viewLeft.setOnClickListener(this);
        viewCenter.setOnClickListener(this);
        viewRight.setOnClickListener(this);
    }


    float downX=0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(isMoveScrollable) {//响应滑动事件  事件分发
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX();
                    if (Math.abs(moveX - downX) > 50 && moveX > downX) {//右滑
                        downX = moveX;
                        if (currentSelect == CurrentSelect.left) {
                            onItemClick(false,3);
                        } else if (currentSelect == CurrentSelect.center) {
                            onItemClick(false,1);
                        } else {
                            onItemClick(false,2);
                        }
                        return true;
                    } else if (Math.abs(moveX - downX) > 50 && moveX < downX) {//左滑
                        downX = moveX;
                        if (currentSelect == CurrentSelect.left) {
                            onItemClick(false,2);
                        } else if (currentSelect == CurrentSelect.center) {
                            onItemClick(false,3);
                        } else {
                            onItemClick(false,1);
                        }
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewLeft:
                onItemClick(true,1);
                break;
            case R.id.viewCenter:
                onItemClick(true,2);
                break;
            case R.id.viewRight:
                onItemClick(true,3);
                break;
        }
    }


    /**
     *
     * @param i  1是左边  2是右边
     */
    private void onItemClick(boolean isClick,int i) {
        if(isAniming){
            return;
        }
        if(i==1){//左边的
            if(currentSelect== CurrentSelect.center) {//左边到中间
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewLeft, "translationX", 0, mMinWidth/2+mMaxWidth/2+mSpaceWidth);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewLeft, "scaleX", 1, 1+mScale);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(viewLeft, "scaleY", 1, 1+mScale);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(viewCenter, "translationX",0, mMinWidth/2+mMaxWidth/2+mSpaceWidth);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(viewCenter, "scaleX", 1+mScale,1f);
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(viewCenter, "scaleY", 1+mScale,1f);

                ObjectAnimator animator7 = ObjectAnimator.ofFloat(viewRight, "translationX",0, -(mMinWidth+mMaxWidth+mSpaceWidth*2));

                playAnim(CurrentSelect.left,animator1, animator2, animator3,animator4, animator5, animator6,animator7);
            }else if(currentSelect==CurrentSelect.left){//中间就是点击的  没有反应
                //选中点击回调
                if(isClick&&mOnSelectChangedListener!=null){
                    mOnSelectChangedListener.onSelectClick();
                }
            }else if(currentSelect== CurrentSelect.right){//右边到中间
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewLeft, "translationX", mMinWidth+mMaxWidth+mSpaceWidth*2,mMinWidth/2+mMaxWidth/2+mSpaceWidth);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewLeft, "scaleX", 1, 1+mScale);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(viewLeft, "scaleY", 1, 1+mScale);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(viewCenter, "translationX",-(mMinWidth/2+mMaxWidth/2+mSpaceWidth), mMinWidth/2+mMaxWidth/2+mSpaceWidth);

                ObjectAnimator animator7 = ObjectAnimator.ofFloat(viewRight, "translationX",-(mMinWidth/2+mMaxWidth/2+mSpaceWidth), -(mMinWidth+mMaxWidth+mSpaceWidth*2));
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(viewRight, "scaleX", 1+mScale, 1f);
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(viewRight, "scaleY", 1+mScale, 1f);

                playAnim(CurrentSelect.left,animator1, animator2, animator3, animator4, animator5, animator6, animator7);
            }
        }else if(i==2){//中间的
            if(currentSelect== CurrentSelect.center) {//中间的是中间  没有反应
                //选中点击回调
                if(isClick&&mOnSelectChangedListener!=null){
                    mOnSelectChangedListener.onSelectClick();
                }
            }else if(currentSelect== CurrentSelect.left){//本身在右边显示  移动到中间

                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewLeft, "translationX", mMinWidth/2+mMaxWidth/2+mSpaceWidth,0);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewLeft, "scaleX", 1+mScale, 1.0f);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(viewLeft, "scaleY", 1+mScale, 1.0f);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(viewCenter, "translationX", mMinWidth/2+mMaxWidth/2+mSpaceWidth,0);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(viewCenter, "scaleX",1,1+mScale);
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(viewCenter, "scaleY", 1,1+mScale);

                ObjectAnimator animator7 = ObjectAnimator.ofFloat(viewRight, "translationX", -(mMinWidth+mMaxWidth+mSpaceWidth*2),0);

                playAnim(CurrentSelect.center,animator1, animator2, animator3, animator4, animator5, animator6, animator7);
            }else if(currentSelect== CurrentSelect.right){//本身在左边    移动到中间
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewLeft, "translationX", mMinWidth+mMaxWidth+mSpaceWidth*2,0);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(viewCenter, "translationX", -(mMinWidth/2+mMaxWidth/2+mSpaceWidth),0);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(viewCenter, "scaleX",1,1+mScale);
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(viewCenter, "scaleY",1,1+mScale);

                ObjectAnimator animator7 = ObjectAnimator.ofFloat(viewRight, "translationX", -(mMinWidth/2+mMaxWidth/2+mSpaceWidth),0);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewRight, "scaleX", 1+mScale, 1.0f);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(viewRight, "scaleY", 1+mScale, 1.0f);

                playAnim(CurrentSelect.center,animator1, animator2, animator3, animator4, animator5, animator6, animator7);
            }
        }else if(i==3){//右边的
            if(currentSelect== CurrentSelect.center) {//本身显示在右  移动到中间
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewLeft, "translationX", 0,mMinWidth+mMaxWidth+mSpaceWidth*2);

                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewCenter, "translationX", 0,-(mMinWidth/2+mMaxWidth/2+mSpaceWidth));
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(viewCenter, "scaleX", 1+mScale,1f);
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(viewCenter, "scaleY", 1+mScale,1f);

                ObjectAnimator animator5 = ObjectAnimator.ofFloat(viewRight, "translationX", 0,-(mMinWidth/2+mMaxWidth/2+mSpaceWidth));
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(viewRight, "scaleX", 1,1+mScale);
                ObjectAnimator animator7 = ObjectAnimator.ofFloat(viewRight, "scaleY", 1,1+mScale);

                playAnim(CurrentSelect.right,animator1, animator2, animator3, animator4, animator5, animator6, animator7);
            }else if(currentSelect== CurrentSelect.left){//本身在左显示  左边移动到中间
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewLeft, "translationX",mMinWidth/2+mMaxWidth/2+mSpaceWidth,mMinWidth+mMaxWidth+mSpaceWidth*2);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewLeft, "scaleX", 1+mScale,1f);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(viewLeft, "scaleY", 1+mScale,1f);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(viewCenter, "translationX", mMinWidth/2+mMaxWidth/2+mSpaceWidth,-(mMinWidth/2+mMaxWidth/2+mSpaceWidth));


                ObjectAnimator animator5 = ObjectAnimator.ofFloat(viewRight, "translationX", -(mMinWidth+mMaxWidth+mSpaceWidth*2),-(mMinWidth/2+mMaxWidth/2+mSpaceWidth));
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(viewRight, "scaleX", 1,1+mScale);
                ObjectAnimator animator7 = ObjectAnimator.ofFloat(viewRight, "scaleY", 1,1+mScale);

                playAnim(CurrentSelect.right,animator1, animator2, animator3, animator4, animator5, animator6, animator7);
            }else if(currentSelect==CurrentSelect.right){//本身在左边显示  移动到中间
                //选中点击回调
                if(isClick&&mOnSelectChangedListener!=null){
                    mOnSelectChangedListener.onSelectClick();
                }
            }
        }
    }

    /**
     * 一起播放动画
     * @param select 点击的item
     * @param items 动画
     */
    private void playAnim(final CurrentSelect select,Animator... items) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(items);
        animatorSet.setDuration(mAnimTime);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAniming=true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentSelect = select;
                removeView(viewLeft);
                removeView(viewCenter);
                removeView(viewRight);
                if(currentSelect==SelectLayout.CurrentSelect.left){
                    addView(viewRight,1);
                    addView(viewLeft,3);
                    addView(viewCenter,5);
                }else if(currentSelect==SelectLayout.CurrentSelect.center){
                    addView(viewLeft,1);
                    addView(viewCenter,3);
                    addView(viewRight,5);
                }else if(currentSelect==SelectLayout.CurrentSelect.right){
                    addView(viewCenter,1);
                    addView(viewRight,3);
                    addView(viewLeft,5);
                }
                isAniming=false;
                //选中状态更改回调
                if(mOnSelectChangedListener!=null){
                    mOnSelectChangedListener.onSelectChange(select);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    OnSelectChangedListener mOnSelectChangedListener;

    public void setOnSelectChangedListener(OnSelectChangedListener onSelectChangedListener) {
        mOnSelectChangedListener = onSelectChangedListener;
    }

    /**
     * 选中状态更改回调
     */
    public interface OnSelectChangedListener{
        public void onSelectChange(CurrentSelect current);
        public void onSelectClick();
    }

    /**
     * 设置动画时间
     * @param animTime
     */
    public void setAnimTime(int animTime) {
        mAnimTime = animTime;
    }

    /**
     * 是否能够滑动
     * @param moveScrollable
     */
    public void setMoveScrollable(boolean moveScrollable) {
        isMoveScrollable = moveScrollable;
    }

    /**
     * 设置当前选中项
     * @param currentSelect
     */
    public void setCurrentSelect(CurrentSelect currentSelect) {
        if(currentSelect==CurrentSelect.left){
            onItemClick(false,1);
        }else if(currentSelect==CurrentSelect.center){
            onItemClick(false,2);
        }else if(currentSelect==CurrentSelect.right){
            onItemClick(false,3);
        }
    }
}
