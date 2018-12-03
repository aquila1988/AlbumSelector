package com.aquila.lib.album.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.aquila.lib.album.R;

/***
 *@date 创建时间 2018/10/30 15:47
 *@author 作者: W.YuLong
 *@description 新版的自定义RecyclerView
 */
public class CustomRecyclerView extends RecyclerView {

    public static final int OTHER = 0;
    public static final int LIST_VERTICAL = 1;
    public static final int LIST_HORIZONTAL = 1 << 1;
    public static final int GRID_VERTICAL = 1 << 2;
    public static final int GRID_HORIZONTAL = 1 << 3;
    public static final int STAGGERED_VERTICAL = 1 << 4;
    public static final int STAGGERED_HORIZONTAL = 1 << 5;

    public static final int TYPE_ITEM_DECORATION_NONE = 0;
    public static final int TYPE_ITEM_DECORATION_DIVIDE_LINE = 1;
    public static final int TYPE_ITEM_DECORATION_SPACE_MARGIN = 1 << 1;

    private boolean isExpanded = false;
    private int maxHeight = -1;


    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomRecyclerView);
        int layoutStyle = a.getInteger(R.styleable.CustomRecyclerView_app_layout_style, 0);
        int itemDecorationType = a.getInteger(R.styleable.CustomRecyclerView_app_item_decoration_type, 0);

        int spanCount = a.getInt(R.styleable.CustomRecyclerView_app_span_count, 1);

        int itemSpace = a.getDimensionPixelOffset(R.styleable.CustomRecyclerView_app_item_margin, 0);
        int startItemMargin = a.getDimensionPixelOffset(R.styleable.CustomRecyclerView_app_start_item_margin, 0);
        int endItemMargin = a.getDimensionPixelOffset(R.styleable.CustomRecyclerView_app_end_item_margin, 0);

        int divideLineColor = a.getColor(R.styleable.CustomRecyclerView_app_item_divide_line_color, -1);
        int divideLineSize = a.getDimensionPixelSize(R.styleable.CustomRecyclerView_app_item_divide_line_size, -1);
        maxHeight = a.getDimensionPixelOffset(R.styleable.CustomRecyclerView_app_recycler_view_max_height, -1);
        isExpanded = a.getBoolean(R.styleable.CustomRecyclerView_app_is_expanded, false);
        a.recycle();

        setLayoutStyle(layoutStyle, spanCount);

        //设置了Item的间距的话就画间距
        if (itemSpace > 0 || startItemMargin > 0 || endItemMargin > 0) {
            CustomItemDecoration itemDecoration = CustomItemDecoration.with().setDivideLineColor(divideLineColor)
                    .setOrientation(layoutStyle).setSpanCount(spanCount)
                    .setDivideLineWidth(divideLineSize).setItemDecorationType(TYPE_ITEM_DECORATION_SPACE_MARGIN)
                    .setStartItemMargin(startItemMargin).setEndItemMargin(endItemMargin)
                    .setItemMargin(itemSpace).builder();
            addItemDecoration(itemDecoration);
        }

        //设置了划线的话就画分割线
        if (itemDecorationType == TYPE_ITEM_DECORATION_DIVIDE_LINE) {
            CustomItemDecoration itemDecoration = CustomItemDecoration.with().setDivideLineColor(divideLineColor)
                    .setOrientation(layoutStyle).setSpanCount(spanCount)
                    .setDivideLineWidth(divideLineSize).setItemDecorationType(itemDecorationType)
                    .setStartItemMargin(startItemMargin).setEndItemMargin(endItemMargin)
                    .setItemMargin(itemSpace)
                    .builder();
            addItemDecoration(itemDecoration);
        }

    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (maxHeight != -1) {
            int width = getDefaultSize(getWidth(), widthSpec);
            int height = getDefaultSize(getHeight(), heightSpec);
            height = Math.min(height, maxHeight);
            setMeasuredDimension(width, height);
        } else {
            if (isExpanded) {
                int measureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                        MeasureSpec.AT_MOST);
                super.onMeasure(widthSpec, measureSpec);
            } else {
                super.onMeasure(widthSpec, heightSpec);
            }
        }

    }


    public void setLayoutStyle(int layoutStyle, int spanCount) {
        LayoutManager layoutManager;
        switch (layoutStyle) {
            case LIST_HORIZONTAL:
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                break;
            case GRID_VERTICAL:
                layoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false);
                break;
            case GRID_HORIZONTAL:
                layoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.HORIZONTAL, false);
                break;
            case STAGGERED_VERTICAL:
                layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
                break;
            case STAGGERED_HORIZONTAL:
                layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL);
                break;
            case LIST_VERTICAL:
            default: //默认的就是列表模式
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                break;
        }
        setLayoutManager(layoutManager);
    }


    /***
     *@date 创建时间 2018/10/30 14:08
     *@author 作者: W.YuLong
     *@description
     */
    public static class CustomItemDecoration extends ItemDecoration {

        private Paint paint;
        private Builder builder;

        public static Builder with() {
            return new Builder();
        }

        public CustomItemDecoration(Builder builder) {
            this.builder = builder;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(builder.divideLineColor);
            paint.setStyle(Paint.Style.FILL);
        }


        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            if (builder.itemDecorationType == TYPE_ITEM_DECORATION_DIVIDE_LINE) {
                drawDivideLine(c, parent, builder.orientation);
            }

        }

        // 根据Item布局画分割线，目前主要针对水平和列表的list
        protected void drawDivideLine(Canvas c, RecyclerView parent, int orientation) {
            final int left = parent.getPaddingTop();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            int itemSpace = (builder.itemMargin + 1) / 2;
            for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
                final View child = parent.getChildAt(i);
                if (orientation == LIST_VERTICAL) {
                    c.drawRect(left, child.getBottom() + itemSpace, right, child.getBottom() + itemSpace + builder.divideLineWidth, paint);
                } else if (orientation == LIST_HORIZONTAL) {
                    c.drawRect(child.getRight() + itemSpace, top, child.getRight() + itemSpace + builder.divideLineWidth, bottom, paint);

                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {

            if (builder.itemDecorationType == TYPE_ITEM_DECORATION_NONE) {
                return;
            }

            int position = parent.getChildAdapterPosition(view);
            int itemCount = parent.getAdapter().getItemCount();


            if (itemCount <= 1) {
                return;
            }

            if (builder.itemDecorationType == TYPE_ITEM_DECORATION_DIVIDE_LINE) {
                setDivideLineOffset(outRect, position, itemCount);
            } else if (builder.itemDecorationType == TYPE_ITEM_DECORATION_SPACE_MARGIN) {
                setItemMarginOffset(outRect, position, itemCount);
            }
        }

        /*设置每个Item的间距*/
        private void setItemMarginOffset(Rect outRect, int position, int itemCount) {
            int lastRemainCount = itemCount % builder.spanCount;
            if (lastRemainCount == 0) {
                lastRemainCount = builder.spanCount;
            }

            int startItemSpace = builder.startItemMargin;
            int itemSpace = (builder.itemMargin + 1) / 2;
            int endItemSpace = builder.endItemMargin;
            int spanCount = builder.spanCount;

            switch (builder.orientation) {
                case LIST_HORIZONTAL:
                    if (position == 0) {
                        outRect.left = startItemSpace;
                        outRect.right = itemSpace;
                    } else if (position == itemCount - 1) {
                        outRect.right = endItemSpace;
                        outRect.left = itemSpace;
                    } else {
                        outRect.left = itemSpace;
                        outRect.right = itemSpace;
                    }
                    break;
                case LIST_VERTICAL:
                    if (position == 0) {
                        outRect.top = startItemSpace;
                        outRect.bottom = itemSpace;
                    } else if (position == itemCount - 1) {
                        outRect.top = itemSpace;
                        outRect.bottom = endItemSpace;
                    } else {
                        outRect.top = itemSpace;
                        outRect.bottom = itemSpace;
                    }
                    break;
                case GRID_VERTICAL:
                case STAGGERED_VERTICAL:
                    //顶部一排的item
                    if (position < spanCount) {
                        outRect.top = startItemSpace;
                        outRect.bottom = itemSpace;
                    } else if (itemCount - position <= lastRemainCount) {
                        //底部最后一排的下边距
                        outRect.bottom = endItemSpace;
                        outRect.top = itemSpace;
                    } else {
                        outRect.top = itemSpace;
                        outRect.bottom = itemSpace;
                    }

                    //最左边一列Item不需要左边距
                    if (position % spanCount == 0) {
//                        outRect.right = itemSpace ;
                    } else if ((position + 1) % spanCount == 0) { //最右边一列
                        outRect.left = itemSpace;
                    } else {
                        outRect.left = itemSpace;
                        outRect.right = itemSpace;
                    }
                    break;
                case GRID_HORIZONTAL:
                case STAGGERED_HORIZONTAL:
                    //最左边一排的item左边距
                    if (position < spanCount) {
                        outRect.left = startItemSpace;
                        outRect.right = itemSpace;

                    } else if (itemCount - position <= lastRemainCount) {
                        //最右边一排的右边距
                        outRect.right = endItemSpace;
                        outRect.left = itemSpace;
                    } else {
                        outRect.left = itemSpace;
                        outRect.right = itemSpace;
                    }

                    //顶部一排
                    if (position % spanCount == 0) {
                        outRect.bottom = itemSpace;
                    } else if ((position + 1) % spanCount == 0) {
                        //底部一排
                        outRect.top = itemSpace;
                    } else {
                        outRect.top = itemSpace;
                        outRect.bottom = itemSpace;
                    }
                    break;
                case OTHER:
                    break;
            }
        }

        /*设置分割线的Item位置*/
        private void setDivideLineOffset(Rect outRect, int position, int itemCount) {
            //这个divideLineWidth + 1是为了如果divideLineWidth=1的时候除以2就为0了，
            int divideLineWidth = (builder.divideLineWidth + 1) / 2;
            switch (builder.orientation) {
                case LIST_HORIZONTAL:
                    if (itemCount > 1) {

                        if (position == 0) {
                            outRect.left = 0;
                            outRect.right = divideLineWidth;
                        } else if (position == itemCount - 1) {
                            outRect.left = divideLineWidth;
                            outRect.right = 0;
                        } else {
                            outRect.right = divideLineWidth;
                            outRect.left = divideLineWidth;
                        }
                    }
                    break;
                case LIST_VERTICAL:
                    if (itemCount > 0) {
                        if (position == 0) {
                            outRect.top = 0;
                            outRect.bottom = divideLineWidth;
                        } else if (position == itemCount - 1) {
                            outRect.top = divideLineWidth;
                            outRect.bottom = 0;
                        } else {
                            outRect.top = divideLineWidth;
                            outRect.bottom = divideLineWidth;
                        }
                    }
                    break;
            }
        }

        /***
         *@date 创建时间 2018/10/30 15:47
         *@author 作者: W.YuLong
         *@description
         */
        public static class Builder {
            private int orientation;

            private int startItemMargin = 0;
            private int itemMargin = 0;
            private int endItemMargin = 0;
            private int spanCount = 1;

            private int divideLineColor = Color.LTGRAY;

            private int divideLineWidth = 2;
            private int itemDecorationType = TYPE_ITEM_DECORATION_DIVIDE_LINE;

            public Builder setDivideLineColor(int divideLineColor) {
                if (divideLineColor == -1) {
                    this.divideLineColor = Color.LTGRAY;
                } else {
                    this.divideLineColor = divideLineColor;
                }
                return this;
            }

            public Builder setOrientation(int orientation) {
                this.orientation = orientation;
                return this;
            }

            public Builder setStartItemMargin(int startItemMargin) {
                this.startItemMargin = startItemMargin;
                return this;
            }

            public Builder setItemMargin(int itemMargin) {
                this.itemMargin = itemMargin;
                return this;
            }

            public Builder setEndItemMargin(int endItemMargin) {
                this.endItemMargin = endItemMargin;
                return this;
            }

            public Builder setSpanCount(int spanCount) {
                this.spanCount = spanCount;
                return this;
            }

            public Builder setDivideLineWidth(int divideLineWidth) {
                this.divideLineWidth = divideLineWidth;
                return this;
            }

            public Builder setItemDecorationType(int type) {
                this.itemDecorationType = type;
                return this;
            }


            public CustomItemDecoration builder() {
                return new CustomItemDecoration(this);
            }
        }

    }
}
